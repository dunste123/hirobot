package me.duncte123.hirobot;

import com.jagrosh.jdautilities.command.CommandClient;
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

    public EventManager(CommandClient commandClient) {
        fanServerListeners = new EventListener[] {
                new FanServerEventHandler(),
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