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

package me.duncte123.hirobot;

import me.duncte123.hirobot.objects.CBCharacter;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.data.DataArray;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Utils {
    /**
     * https://github.com/duncte123/ColorUtils/blob/master/src/main/java/me/duncte123/colorutils/ColorUtils.java
     *
     * Converts a hex string to an color int
     *
     * @param hex
     *         The hex string to convert to a color int
     *
     * @return The color as int
     */
    public static int hexStringToInt(String hex) {
        final String hexValue = "0x" + hex.replaceFirst("#", "");

        return Integer.decode(hexValue);
    }

    public static String getUserStaticAvatarUrl(User user) {
        final String avatarId = user.getAvatarId();

        if (avatarId == null) {
            return user.getDefaultAvatarUrl();
        }

        return String.format(User.AVATAR_URL, user.getId(), avatarId, "png");
    }

    public static CBCharacter[] loadCharactersFromFile(String fileName) {
        return loadCharactersFromFile(fileName, 0);
    }

    public static CBCharacter[] loadCharactersFromFile(String fileName, int what) {
        CBCharacter[] tmp = null;

        try {
            final File file = new File(fileName);
            final DataArray dataArray = DataArray.fromJson(Files.readString(file.toPath()));

            final int len = dataArray.length() - what;
            tmp = new CBCharacter[len];

            for (int i = 0; i < len; i++) {
                tmp[i] = CBCharacter.fromData(dataArray.getObject(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tmp;
    }
}
