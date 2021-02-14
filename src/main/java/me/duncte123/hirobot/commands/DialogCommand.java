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

package me.duncte123.hirobot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.IOUtil;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.duncte123.hirobot.Hiro.PREFIX;

public class DialogCommand extends Command {
    private final List<String> backgrounds = new ArrayList<>();
    private final List<String> characters = new ArrayList<>();

    private final OkHttpClient client = new OkHttpClient();

    public DialogCommand() {
        this.name = "dialog";
        this.help = "Generate a dialog from your favorite characters";
        this.arguments = "[background] <character> <text>";
        this.cooldown = 10;
        this.cooldownScope = CooldownScope.GLOBAL;

        final Pair<List<String>, List<String>> items = this.fetchBackgroundsAndCharacters();

        characters.addAll(items.getLeft());
        backgrounds.addAll(items.getRight());
    }

    @Override
    protected void execute(CommandEvent event) {
        final String[] args = event.getArgs().split("\\s+");
        final List<String> test = new ArrayList<>(Arrays.asList(args));

        if (test.size() < 2) {
            event.reply("Too little arguments, correct usage is `" + PREFIX + "dialog [background] <character> <your message>`");
            return;
        }

        String character = test.remove(0).toLowerCase();
        final String background;

        if (characters.contains(character)) {
            background = "camp";
        } else {
            background = character;
            character = test.remove(0).toLowerCase();
        }

        if (!characters.contains(character)) {
            event.reply(String.format("I don't think that I know `%s` <:HiroConfused:670239474134810624>", character));
            return;
        }

        if (!backgrounds.contains(background)) {
            event.reply(String.format("Sorry but I don't know where `%s` is <:HiroSweat:670239468829147156>", background));
            return;
        }

        final String message = String.join(" ", test);

        if (message.length() > 120) {
            event.reply("I can't take that big of a load <:HiroShock:670239482179485717>\nkeep it below 120 characters please");
            return;
        }

        // Send typing here to start right before the request
        event.getChannel().sendTyping().queue();

        final Request request = makeRequest(background, character, message);

        this.client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                event.reply(String.format("Something went wrong: `%s`", e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final InputStream body = IOUtil.getBody(response);
                final byte[] bytes = IOUtil.readFully(body);

                // close the response because we are done with it
                response.close();

                event.getChannel().sendFile(
                        bytes,
                        "dialog.png"
                )
                        .append(event.getAuthor().getAsMention())
                        .append(", here is your requested dialog! <:HiroCheer:670239465259794442>")
                        .queue();
            }
        });
    }

    private Request makeRequest(String background, String character, String message) {
        final String json = DataObject.empty()
                .put("background", background)
                .put("character", character)
                .put("text", message)
                .toString();

        return new Request.Builder()
                .url("https://yuuto.dunctebot.com/dialog")
                .post(RequestBody.create(null, json))
                .header("Content-Type", "application/json")
                .header("User-Agent", "HiroBot")
                .build();
    }

    // Characters -> Backgrounds
    private Pair<List<String>, List<String>> fetchBackgroundsAndCharacters() {
        final Request request = new Request.Builder()
                .url("https://yuuto.dunctebot.com/info")
                .get()
                .header("User-Agent", "HiroBot")
                .build();

        try (final Response response = this.client.newCall(request).execute()) {
            final byte[] bytes = IOUtil.readFully(IOUtil.getBody(response));
            final DataObject json = DataObject.fromJson(bytes);

            final List<String> characters = json.getArray("characters")
                    .toList()
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());

            final List<String> backgrounds = json.getArray("backgrounds")
                    .toList()
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());

            return Pair.of(characters, backgrounds);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Pair.of(List.of(), List.of());
    }
}
