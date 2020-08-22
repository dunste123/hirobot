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

import com.fasterxml.jackson.core.type.TypeReference;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.duncte123.hirobot.commands.CVTCommand;
import me.duncte123.hirobot.commands.DialogCommand;
import me.duncte123.hirobot.commands.RouteCommand;
import me.duncte123.hirobot.commands.ValentineCommand;
import me.duncte123.hirobot.database.Database;
import me.duncte123.hirobot.database.SQLiteDatabase;
import me.duncte123.hirobot.database.objects.Birthday;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Hiro {
    public static final String PREFIX = "!!";
    public static final long OWNER_ID = 311769499995209728L;
    public static final long FAN_GUILD_ID = 670218976932134922L;
    public static final long STANS_ROLE_ID = 670368434017533962L;
    public static final long GENERAL_CHANNEL_ID = 670218976932134925L;
    public static final long ROLES_CHANNEL_ID = 672361818429325312L;
    public static final long DEV_CHANNEL_ID = 677954714825916427L;

    private static final Map<String, String> customEnv = new HashMap<>();

    public final JDA jda;

    private final Database database;

    public Hiro() throws LoginException, IOException {
        this.database = new SQLiteDatabase();

        final CommandClientBuilder builder = new CommandClientBuilder();

        ReactionHelpers.load();

        builder.setPrefix(PREFIX);
//        builder.setActivity(Activity.listening("Greatest Memories"));
//        builder.setActivity(Activity.streaming("Portal", "https://twitch.tv/super_hiro69"));
        builder.setActivity(null);
        builder.setOwnerId(String.valueOf(OWNER_ID));
        builder.setHelpConsumer(this::helpConsumer);

        builder.addCommands(
                new CVTCommand(),
                new RouteCommand(),
                new ValentineCommand(database),
                new DialogCommand()
        );

        final CommandClient commandClient = builder.build();
        final EventManager eventManager = new EventManager(commandClient, this, database);

        this.jda = JDABuilder.create(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS
        )
                .setToken(customEnv.get("TOKEN"))
                .setEventManager(eventManager)
                .setMemberCachePolicy(MemberCachePolicy.NONE)
                .disableCache(EnumSet.allOf(CacheFlag.class))
                .build();

        this.loadBirthdays();
    }

    // Modified code from CommandClientImpl.java of JDA Utils
    private void helpConsumer(CommandEvent event) {
        final CommandClient client = event.getClient();
        final List<Command> commands = client.getCommands();
        final String textPrefix = client.getTextualPrefix();
        final String prefix = client.getPrefix();

        final StringBuilder builder = new StringBuilder("So you want a taste of my commands?\nWell here they are:\n");
        Command.Category category = null;

        for (Command command : commands) {
            if (!command.isHidden() && (!command.isOwnerCommand() || event.isOwner())) {
                if (!Objects.equals(category, command.getCategory())) {
                    category = command.getCategory();
                    builder.append("\n\n  __").append(category == null ? "No Category" : category.getName()).append("__:\n");
                }
                builder.append("\n`").append(textPrefix).append(prefix == null ? " " : "").append(command.getName())
                        .append(command.getArguments() == null ? "`" : " " + command.getArguments() + "`")
                        .append(" - ").append(command.getHelp());
            }
        }

        event.reply(builder.toString(), (unused) -> {
            if (event.isFromType(ChannelType.TEXT)) {
                event.reactSuccess();
            }
        }, (t) -> event.replyWarning("Help cannot be sent because you are blocking Direct Messages."));
    }

    public static void main(String[] args) throws Exception {
        final Map<String, String> envFile = loadEnvironment();
        // Put stuff in the env at runtime so we don't have to worry about them cli command
        customEnv.putAll(envFile);

        new Hiro();
    }

    private static Map<String, String> loadEnvironment() throws IOException {
        final Map<String, String> env = new HashMap<>();
        final List<String> lines = Files.readAllLines(new File(".env").toPath());

        for (String line : lines) {
            final String[] kv = line.split("=");

            env.put(kv[0], kv[1]);
        }

        return env;
    }

    private void loadBirthdays() throws IOException {
        final var bdayInit = new File("bday.init.json5");

        // TODO: items are duped on boot, delete file?
        if (bdayInit.exists()) {
            final List<Birthday> dataArray = ReactionHelpers.MAPPER.readValue(bdayInit, new TypeReference<>() {});

            dataArray.forEach(this.database::addBirthday);
        }
    }
}
