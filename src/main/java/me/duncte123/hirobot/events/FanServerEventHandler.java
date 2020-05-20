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

package me.duncte123.hirobot.events;

import me.duncte123.hirobot.ReactionHelpers;
import me.duncte123.hirobot.chat.ChatBot;
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

    private final ChatBot chatbot = new ChatBot();

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

        if (guild.getIdLong() != FAN_GUILD_ID || event.getChannel().getIdLong() != ROLES_CHANNEL_ID) {
            return;
        }

        final long emoteId = event.getReactionEmote().getIdLong();

        ReactionHelpers.applyRole(emoteId, event.getUserIdLong(), guild);
    }

    private void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent event) {
        final Guild guild = event.getGuild();

        if (guild.getIdLong() != FAN_GUILD_ID || event.getChannel().getIdLong() != ROLES_CHANNEL_ID) {
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

            return;
        }

        final String selfMention = event.getGuild().getSelfMember().getAsMention();

        if (contentRaw.startsWith(selfMention)) {
            this.chatbot.handleInput(
                    contentRaw.replaceFirst(selfMention, ""),
                    event
            );
        }
    }
}
