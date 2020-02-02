package me.duncte123.hirobot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import me.duncte123.hirobot.commands.CVTCommand;
import me.duncte123.hirobot.commands.RouteCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Hiro implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hiro.class);
    private static final long OWNER_ID = 311769499995209728L;
    private static final long FAN_GUILD_ID = 670218976932134922L;
    private static final long STANS_ROLE_ID = 670368434017533962L;
    private static final long GENERAL_CHANNEL_ID = 670218976932134925L;
    public static final String PREFIX = "-";
//    private static final String DEGREE_SIGN = "\u00B0";
    private static final String[] WELCOME_MESSAGES = {
            "Hey what's up {user}, welcome to my fanclub <:HiroCheer:670239465259794442>",
            "Hey listen up, {user} just joined",
            "Hey {user}, did u see Keitaro?",
            "Someone tell bro Aiden to cook us a welcome feast for {user}",
            "I smell something fishy... Oh wait! {user} has joined us!",
            "What is that noise? Taiga making trouble again? Oh... It's just {user} joining us",
            "Hands where I can see them {user} <:HiroSpray:670954573002833941>"
    };


    public Hiro(String token) throws LoginException {

        final CommandClientBuilder builder = new CommandClientBuilder();

        builder.setPrefix(PREFIX);
        builder.setActivity(Activity.playing("with Keitaro"));
        builder.setOwnerId(String.valueOf(OWNER_ID));

        builder.addCommands(
                new CVTCommand(),
                new RouteCommand()
        );

        final CommandClient commandClient = builder.build();

        new JDABuilder()
                .setToken(token)
                .addEventListeners(this, commandClient)
                .setEnabledCacheFlags(EnumSet.noneOf(CacheFlag.class))
                .build();
    }

    @Override
    public void onEvent(@Nonnull GenericEvent event) {
        if (event instanceof ReadyEvent) {
            this.onReady((ReadyEvent) event);
        } else if (event instanceof GuildMemberJoinEvent) {
            this.onGuildMemberJoin((GuildMemberJoinEvent) event);
        } else if (event instanceof GuildMessageReceivedEvent) {
            this.onGuildMessageReceived((GuildMessageReceivedEvent) event);
        } else if (event instanceof GuildMessageReactionAddEvent) {
            this.onGuildMessageReactionAdd((GuildMessageReactionAddEvent) event);
        } else if (event instanceof GuildMessageReactionRemoveEvent) {
            this.onGuildMessageReactionRemove((GuildMessageReactionRemoveEvent) event);
        }
    }

    private void onReady(@Nonnull ReadyEvent event) {
        LOGGER.info("Logged in as {}", event.getJDA().getSelfUser().getAsTag());
    }

    private void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        final Guild guild = event.getGuild();

        if (guild.getIdLong() != FAN_GUILD_ID) {
            return;
        }

        final Member member = event.getMember();
        final long emoteId = event.getReactionEmote().getIdLong();

        ReactionHelpers.applyRole(emoteId, member);
    }

    private void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent event) {
        final Guild guild = event.getGuild();

        if (guild.getIdLong() != FAN_GUILD_ID) {
            return;
        }

        final Member member = event.getMember();
        final long emoteId = event.getReactionEmote().getIdLong();

        ReactionHelpers.removeRole(emoteId, member);
    }

    private void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        final Guild guild = event.getGuild();

        if (guild.getIdLong() != FAN_GUILD_ID) {
            return;
        }

        final Role stansRole = Objects.requireNonNull(guild.getRoleById(STANS_ROLE_ID));
        final Member member = event.getMember();
        final TextChannel channel = Objects.requireNonNull(guild.getTextChannelById(GENERAL_CHANNEL_ID));

        channel.sendMessage(
                WELCOME_MESSAGES[
                        ThreadLocalRandom.current().nextInt(WELCOME_MESSAGES.length)
                ].replace("{user}", member.getUser().getAsMention())
        ).queue();

        guild.addRoleToMember(member, stansRole).queue();
    }

    private void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        final Message message = event.getMessage();
        final String contentRaw = message.getContentRaw();
        final User author = message.getAuthor();

        if ((PREFIX + "shutdown").equals(contentRaw) && author.getIdLong() == OWNER_ID) {
            final JDA jda = event.getJDA();
            jda.shutdown();
            jda.getHttpClient().connectionPool().evictAll();
            jda.getHttpClient().dispatcher().executorService().shutdown();
        }

    }

    public static void main(String[] args) throws LoginException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Haha yes this code wants token");
        }

        new Hiro(args[0]);
    }
}
