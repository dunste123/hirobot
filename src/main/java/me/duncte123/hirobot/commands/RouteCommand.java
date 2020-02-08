package me.duncte123.hirobot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.duncte123.hirobot.objects.CBCharacter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ThreadLocalRandom;

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
        this.cooldown = 60;
        this.cooldownScope = CooldownScope.USER; // USER is default

        CBCharacter[] tmp = null;

        try {
            final File file = new File("routes.json");
            final DataArray dataArray = DataArray.fromJson(Files.readString(file.toPath()));

            // This little bit of code loads them all
            tmp = new CBCharacter[dataArray.length()];

            for (int i = 0; i < dataArray.length() - 4; i++) {
                tmp[i] = CBCharacter.fromData(dataArray.getObject(i));
            }

            // This just loads Hiro
            /*final DataObject dataObject = dataArray.getObject(0);

            tmp = new CBCharacter[] {
                    CBCharacter.fromData(dataObject)
            };*/

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.characters = tmp;
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
                .setThumbnail(character.getEmoteUrl())
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

    private String getUserStaticAvatarUrl(User user) {
        final String avatarId = user.getAvatarId();

        if (avatarId == null) {
            return user.getDefaultAvatarUrl();
        }

        return String.format(User.AVATAR_URL, user.getId(), avatarId, "png");
    }

    private CBCharacter getRandomCharacter() {
        return this.characters[ThreadLocalRandom.current().nextInt(this.characters.length)];
    }

    private String getRandomEnding() {
        return this.endings[ThreadLocalRandom.current().nextInt(this.endings.length)];
    }
}
