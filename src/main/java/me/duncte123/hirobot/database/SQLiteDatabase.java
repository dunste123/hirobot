/*
 * Custom bot for the Hiro Akiba fan server on discord
 * Copyright (C) 2020 Duncan "duncte123" Sterken
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.duncte123.hirobot.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.concurrent.TimeUnit;

public class SQLiteDatabase implements Database {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteDatabase.class);
    private final HikariDataSource ds;

    public SQLiteDatabase() {
        try {
            final File dbFile = new File("database.db");

            if (!dbFile.exists()) {
                if (dbFile.createNewFile()) {
                    LOGGER.info("Created database file");
                } else {
                    LOGGER.info("Could not create database file");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:database.db");
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setIdleTimeout(TimeUnit.SECONDS.toMillis(30));
        config.setLeakDetectionThreshold(60 * 1000);
        ds = new HikariDataSource(config);

        try (final Connection conn = ds.getConnection()) {
            try (final Statement statement = conn.createStatement()) {
                // language=SQLite
                statement.execute("CREATE TABLE IF NOT EXISTS valentines (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id VARCHAR(20) NOT NULL," +
                        "buddy_index int(2) NOT NULL DEFAULT -1" +
                        ");");

                LOGGER.info("Table initialised");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setValentine(long userId, int buddyIndex) {
        try (final Connection conn = ds.getConnection()) {
            try (final PreparedStatement preparedStatement = conn.prepareStatement(
                    // language=SQLite
                    "INSERT INTO valentines(buddy_index, user_id) VALUES(? , ?)"
            )) {
                preparedStatement.setInt(1, buddyIndex);
                preparedStatement.setString(2, String.valueOf(userId));

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getValentine(long userId) {
        try (final Connection conn = ds.getConnection()) {
            try (final PreparedStatement preparedStatement = conn.prepareStatement(
                    // language=SQLite
                    "SELECT buddy_index FROM valentines WHERE user_id = ?"
            )) {
                preparedStatement.setString(1, String.valueOf(userId));

                try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("buddy_index");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
