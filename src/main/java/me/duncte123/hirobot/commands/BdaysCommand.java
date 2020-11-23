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

package me.duncte123.hirobot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.duncte123.hirobot.database.Database;
import me.duncte123.hirobot.database.objects.Birthday;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import java.util.stream.Collectors;

public class BdaysCommand extends Command {
    private final Database database;

    public BdaysCommand(Database database) {
        this.name = "bdays";
        this.help = "Manage birthdays";
        this.arguments = "[add/delete] [userid] [month-day]";
        this.ownerCommand = true;

        this.database = database;
    }

    @Override
    protected void execute(CommandEvent event) {
        final String args = event.getArgs();

        if (args.isEmpty()) {
            listBdays(event);
            return;
        }

        final String[] split = args.split("\\s+", 3);

        switch (split[0]) {
            case "add":
                addBday(event, split);
                break;
            case "delete":
                removeBday(event, split);
                break;
            default:
                event.replyFormatted("I don't know what %s is", split[0]);
                break;
        }
    }

    private void listBdays(CommandEvent event) {
        final String bdays = this.database.getBirthdays()
            .stream()
            .map((b) -> mapBirthday(b, event.getJDA()))
            .collect(Collectors.joining("\n"));

        event.reply(
            new EmbedBuilder()
                .setTitle("Server bdays")
                .setDescription(bdays)
                .build()
        );
    }

    private void addBday(CommandEvent event, String[] args) {
        final Birthday birthday = new Birthday(
            Long.parseLong(args[1]),
            args[2]
        );

        this.database.addBirthday(birthday);
        event.reply("Birthday added");
    }

    private void removeBday(CommandEvent event, String[] args) {
        this.database.removeBirthday(Long.parseLong(args[1]));

        event.reply("Birthday removed");
    }

    private String mapBirthday(Birthday bday, JDA jda) {
        final User userById = jda.getUserById(bday.getUserId());
        final String tag = userById == null ? "" : '`' + userById.getAsTag() + '`';

        return "<@" + bday.getUserId() + "> " + tag + ": " + bday.getDate();
    }
}
