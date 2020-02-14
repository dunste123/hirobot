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
        File input = new File("routes-raw.json");
        File output = new File("valentines.json");

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
