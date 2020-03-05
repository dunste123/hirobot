package me.duncte123.hirobot.events;

import me.duncte123.hirobot.ReactionHelpers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static me.duncte123.hirobot.Hiro.*;

public class FanServerEventHandler implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(FanServerEventHandler.class);
    public static final String[] WELCOME_MESSAGES = {
            "Hey what's up {user}, welcome to my fanclub <:HiroCheer:670239465259794442>",
            "Hey listen up, {user} just joined",
            "Hey {user}, did u see Keitaro?",
            "Someone tell bro Aiden to cook us a welcome feast for {user}",
            "I smell something fishy... Oh wait! {user} has joined us!",
            "What is that noise? Taiga making trouble again? Oh... It's just {user} joining us",
            "Hands where I can see them {user} <:HiroSpray:670954573002833941>",
            "That's my {user}"
    };

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

        final long emoteId = event.getReactionEmote().getIdLong();

        ReactionHelpers.applyRole(emoteId, event.getUserIdLong(), guild);
    }

    private void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent event) {
        final Guild guild = event.getGuild();

        if (guild.getIdLong() != FAN_GUILD_ID) {
            return;
        }

        final long emoteId = event.getReactionEmote().getIdLong();

        ReactionHelpers.removeRole(emoteId, event.getUserIdLong(), guild);
    }

    private void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        final Guild guild = event.getGuild();

        if (guild.getIdLong() != FAN_GUILD_ID) {
            return;
        }

        final Role stansRole = Objects.requireNonNull(guild.getRoleById(STANS_ROLE_ID));
        final Member member = event.getMember();
        final TextChannel channel = Objects.requireNonNull(guild.getTextChannelById(GENERAL_CHANNEL_ID));
        final int i = ThreadLocalRandom.current().nextInt(WELCOME_MESSAGES.length);

        channel.sendMessage(
                WELCOME_MESSAGES[i].replace("{user}", member.getUser().getAsMention())
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
}
