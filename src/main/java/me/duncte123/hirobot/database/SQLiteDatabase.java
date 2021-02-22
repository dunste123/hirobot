/*
 * Custom bot for the Hiro Akiba fan server on discord
 * Copyright (C) 2021 Duncan "duncte123" Sterken
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
import me.duncte123.hirobot.database.objects.Birthday;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SQLiteDatabase implements Database {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteDatabase.class);
    private final HikariDataSource ds;

    public SQLiteDatabase() {
        final File dbFile = new File("./data/database.db");

        try {
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
        config.setJdbcUrl("jdbc:sqlite:" + dbFile);
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
                // language=SQLite
                statement.execute("CREATE TABLE IF NOT EXISTS birthdays (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id VARCHAR(20) NOT NULL UNIQUE," +
                    "date VARCHAR(5) NOT NULL" +
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

    @Override
    @SuppressWarnings("SqlWithoutWhere")
    public void clearValentines() {
        try (final Connection conn = ds.getConnection()) {
            try (final Statement smt = conn.createStatement()) {
                // language=SQLite
                smt.execute("DELETE FROM valentines");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Birthday> getBirthdays() {
        final List<Birthday> birthdays = new ArrayList<>();

        try (final Connection conn = ds.getConnection()) {
            try (final Statement smt = conn.createStatement()) {
                try (final ResultSet resultSet =
                         // language=SQLite
                         smt.executeQuery("SELECT * FROM birthdays")) {
                    while (resultSet.next()) {
                        birthdays.add(new Birthday(
                            resultSet.getLong("user_id"),
                            resultSet.getString("date")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return birthdays;
    }

    @Override
    public void addBirthday(@NotNull Birthday birthday) {
        try (final Connection conn = ds.getConnection()) {
            try (final PreparedStatement smt =
                     // language=SQLite
                     conn.prepareStatement("INSERT OR IGNORE INTO birthdays (user_id, date) VALUES(? , ?)")) {

                final LocalDate date = birthday.getDate();

                smt.setLong(1, birthday.getUserId());
                smt.setString(2, pre0(date.getMonthValue()) + '-' + pre0(date.getDayOfMonth()));

                smt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeBirthday(long userId) {
        try (final Connection conn = ds.getConnection()) {
            try (final PreparedStatement smt =
                     // language=SQLite
                     conn.prepareStatement("DELETE FROM birthdays WHERE user_id = ?")) {
                smt.setLong(1, userId);

                smt.executeUpdate();
                smt.closeOnCompletion();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Birthday> getBirthdays(LocalDate date) {
        final List<Birthday> birthdays = new ArrayList<>();

        try (final Connection conn = ds.getConnection()) {
            try (final PreparedStatement smt =
                     // language=SQLite
                     conn.prepareStatement("SELECT * FROM birthdays WHERE date = ?")) {
                smt.setString(1, pre0(date.getMonthValue()) + '-' + pre0(date.getDayOfMonth()));

                try (final ResultSet resultSet = smt.executeQuery()) {
                    if (resultSet.next()) {
                        birthdays.add(new Birthday(
                            resultSet.getLong("user_id"),
                            resultSet.getString("date")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return birthdays;
    }

    private String pre0(int in) {
        return in < 10 ? "0" + in : String.valueOf(in);
    }
}
