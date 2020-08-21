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
import me.duncte123.hirobot.database.SQLiteDatabase;
import me.duncte123.hirobot.objects.CBCharacter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.util.concurrent.ThreadLocalRandom;

import static me.duncte123.hirobot.Hiro.OWNER_ID;
import static me.duncte123.hirobot.Utils.getUserStaticAvatarUrl;
import static me.duncte123.hirobot.Utils.loadCharactersFromFile;

public class ValentineCommand extends Command {
    private final CBCharacter[] characters;

    private final Database database;

    public ValentineCommand() {
        this.name = "valentine";
        this.help = "Who from Camp Buddy is your valentine? Find out via this command";
        this.cooldown = 20;

        this.database = new SQLiteDatabase();
        this.characters = loadCharactersFromFile("valentines.json");
    }

    @Override
    protected void execute(CommandEvent event) {
        final Member member = event.getMember();
        final long userId = member.getIdLong();

        if (!event.getArgs().isEmpty() && "clear".equals(event.getArgs()) && userId == OWNER_ID) {
            this.database.clearValentines();
            event.reply("Cleared valentines");
            return;
        }

        int valentineIndex = this.database.getValentine(userId);

        if (valentineIndex == -1) {
            valentineIndex = this.getRandomValentineIndex();
            this.database.setValentine(userId, valentineIndex);
        }

        final CBCharacter valentine = this.characters[valentineIndex];
        final EmbedBuilder embed = this.generateEmbed(valentine);

        embed.setAuthor(member.getEffectiveName(), null, getUserStaticAvatarUrl(member.getUser()));

        event.reply(embed.build());
    }

    private EmbedBuilder generateEmbed(CBCharacter character) {
        return new EmbedBuilder()
                .setThumbnail(character.getEmoteUrlPng())
                .setColor(character.getColor())
                .setTitle(String.format("Your valentine is %s", character.getName()))
                .setDescription(character.getDescription())
                .addField("Age", String.valueOf(character.getAge()), true)
                .addField("Birthday", character.getBirthday(), true)
                .addField("Animal Motif", character.getAnimal(), true)
                .setFooter(String.format(
                        "Don't fret if %s isn't your type. Who knows, maybe it's time for a new favourite.",
                        character.getFirstName()
                ));
    }

    private int getRandomValentineIndex() {
        return ThreadLocalRandom.current().nextInt(this.characters.length);
    }
}
