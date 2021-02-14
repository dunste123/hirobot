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
import me.duncte123.hirobot.objects.CBCharacter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.util.concurrent.ThreadLocalRandom;

import static me.duncte123.hirobot.Utils.getUserStaticAvatarUrl;
import static me.duncte123.hirobot.Utils.loadCharactersFromFile;

public class RouteCommand extends Command {
    private final CBCharacter[] characters;
    private final String[] endings = {
            "perfect",
            "good",
            "bad",
            "worst",
    };

    public RouteCommand() {
        this.name = "route";
        this.help = "Shows the next possible routes that you can take";
        this.cooldown = 10;
        this.cooldownScope = CooldownScope.GUILD; // USER is default

        this.characters = loadCharactersFromFile("./data/routes.json");
    }

    @Override
    protected void execute(CommandEvent event) {
        final CBCharacter randomCharacter = getRandomCharacter();
        final EmbedBuilder embed = generateEmbed(randomCharacter);
        final Member member = event.getMember();

        embed.setAuthor(member.getEffectiveName(), null, getUserStaticAvatarUrl(member.getUser()));

        event.reply(embed.build());
    }

    private EmbedBuilder generateEmbed(CBCharacter character) {
        return new EmbedBuilder()
                .setThumbnail(character.getEmoteUrlGif())
                .setColor(character.getColor())
                .setTitle(String.format("Next: %s, %s ending", character.getName(), getRandomEnding()))
                .setDescription(character.getDescription())
                .addField("Age", String.valueOf(character.getAge()), true)
                .addField("Birthday", character.getBirthday(), true)
                .addField("Animal Motif", character.getAnimal(), true)
                .setFooter(
                        String.format("Play %s's route next. All bois are best bois.", character.getFirstName())
                );
    }

    private CBCharacter getRandomCharacter() {
        return this.characters[ThreadLocalRandom.current().nextInt(this.characters.length)];
    }

    private String getRandomEnding() {
        return this.endings[ThreadLocalRandom.current().nextInt(this.endings.length)];
    }
}
