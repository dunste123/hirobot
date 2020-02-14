package me.duncte123.hirobot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.duncte123.hirobot.database.Database;
import me.duncte123.hirobot.database.SQLiteDatabase;
import me.duncte123.hirobot.objects.CBCharacter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.util.concurrent.ThreadLocalRandom;

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
