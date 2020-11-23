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

import me.duncte123.hirobot.database.objects.Birthday;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;

public interface Database {

    void setValentine(long userId, int buddyIndex);

    /**
     *
     * @param userId the id of a user
     * @return when no record is found this should return -1
     */
    int getValentine(long userId);

    void clearValentines();

    List<Birthday> getBirthdays();

    void addBirthday(@Nonnull Birthday birthday);

    void removeBirthday(long userId);

    @Nullable
    Birthday getBirthday(LocalDate date);
}
