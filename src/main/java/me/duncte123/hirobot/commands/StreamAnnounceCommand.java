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
import me.duncte123.hirobot.Hiro;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import java.util.concurrent.TimeUnit;

public class StreamAnnounceCommand extends Command {
    public StreamAnnounceCommand() {
        this.name = "streamnotify";
        this.help = "Subscribe or unsubscribe to stream announcements";
    }

    @Override
    protected void execute(CommandEvent event) {
        this.deleteMsg(event.getMessage());

        final Guild guild = event.getGuild();
        final Role streamRole = guild.getRoleById(Hiro.STREAM_ROLE);

        if (streamRole == null) {
            event.reply("Stream role is missing?");
            return;
        }

        final Member member = event.getMember();

        if (member.getRoles().contains(streamRole)) {
            guild.removeRoleFromMember(member, streamRole).queue();
            event.reply("I removed the stream notification role from you", this::deleteMsg);
            return;
        }

        guild.addRoleToMember(member, streamRole).queue();
        event.reply("I added the stream notification role to you", this::deleteMsg);
    }

    private void deleteMsg(Message msg) {
        msg.delete().queueAfter(10, TimeUnit.SECONDS);
    }
}
