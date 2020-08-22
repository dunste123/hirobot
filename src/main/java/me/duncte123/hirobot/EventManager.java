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

import com.jagrosh.jdautilities.command.CommandClient;
import me.duncte123.hirobot.database.Database;
import me.duncte123.hirobot.events.FanServerEventHandler;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.IEventManager;

import javax.annotation.Nonnull;
import java.util.List;

import static me.duncte123.hirobot.Hiro.FAN_GUILD_ID;

public class EventManager implements IEventManager {

    private final EventListener[] fanServerListeners;

    public EventManager(CommandClient commandClient, Hiro bot, Database database) {
        fanServerListeners = new EventListener[] {
                new FanServerEventHandler(bot, database),
                (EventListener) commandClient,
        };
    }

    @Override
    public void handle(@Nonnull GenericEvent e) {
        // If we are not in a guild we need to run all listeners
        if (!(e instanceof GenericGuildEvent)) {
            this.runFanServerListeners(e);
            return;
        }

        // check guild ids
        // With the guild id check we can make sure that
        // The bot only responds in our guild

        final GenericGuildEvent event = (GenericGuildEvent) e;
        final long guildId = event.getGuild().getIdLong();

        if (guildId == FAN_GUILD_ID) {
            this.runFanServerListeners(event);
        }
    }

    private void runFanServerListeners(GenericEvent event) {
        for (EventListener listener : this.fanServerListeners) {
            listener.onEvent(event);
        }
    }

    @Nonnull
    @Override
    public List<Object> getRegisteredListeners() {
        return List.of(this.fanServerListeners);
    }

    // Not needed
    @Override
    public void register(@Nonnull Object listener) {}

    @Override
    public void unregister(@Nonnull Object listener) {}
}
