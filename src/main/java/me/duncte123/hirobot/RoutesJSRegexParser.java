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

package me.duncte123.hirobot;

import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RoutesJSRegexParser {

    public static void main(String[] args) throws Exception {
        File input = new File("./data/routes-raw.json");
        File output = new File("./data/valentines.json");

        String content = Files.readString(input.toPath());

        final DataArray objects = DataArray.fromJson(content);

        final DataArray outputData = DataArray.empty();

        for(Object obj : objects) {
            Map<String, Object> d = (Map<String, Object>) obj;

            final String emoteUrl = (String) d.get("thb");
            final String[] split = emoteUrl.split("/");
            final String id = split[split.length - 1];

            outputData.add(
                    DataObject.empty()
                    .put("name", d.get("name"))
                    .put("description", d.get("tagline"))
                    .put("age", d.get("age"))
                    .put("birthday", d.get("bday"))
                    .put("animal", d.get("motif"))
                    .put("color", d.get("color"))
                    .put("emoteId", id.split("\\.")[0])
            );
        }

        Files.write(
                output.toPath(),
                outputData.toString().getBytes(),
                StandardOpenOption.CREATE
        );
    }

}
