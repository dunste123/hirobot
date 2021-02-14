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

package me.duncte123.hirobot.database.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Birthday {
    private final long userId;
    private final LocalDate date;

    // day format is day-month
    @JsonCreator
    public Birthday(@JsonProperty("userId") long userId, @JsonProperty("date") @Nonnull String date) {
        this.userId = userId;
        this.date = LocalDate.parse("0000-" + date);
    }

    public long getUserId() {
        return userId;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Birthday{" +
            "userId=" + userId +
            ", date=" + date +
            '}';
    }
}
