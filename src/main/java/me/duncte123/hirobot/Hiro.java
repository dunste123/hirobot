package me.duncte123.hirobot;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.duncte123.hirobot.commands.CVTCommand;
import me.duncte123.hirobot.commands.RouteCommand;
import me.duncte123.hirobot.commands.ValentineCommand;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class Hiro {
    public static final String PREFIX = "-";
    public static final long OWNER_ID = 311769499995209728L;
    public static final long FAN_GUILD_ID = 670218976932134922L;
    public static final long STANS_ROLE_ID = 670368434017533962L;
    public static final long GENERAL_CHANNEL_ID = 670218976932134925L;

    public Hiro(String token) throws LoginException, IOException {
        final CommandClientBuilder builder = new CommandClientBuilder();

        ReactionHelpers.load();

        builder.setPrefix(PREFIX);
        builder.setActivity(Activity.listening("Greatest Memories"));
        builder.setOwnerId(String.valueOf(OWNER_ID));
        builder.setHelpConsumer(this::helpConsumer);

        builder.addCommands(
                new CVTCommand(),
                new RouteCommand(),
                new ValentineCommand()
        );

        final CommandClient commandClient = builder.build();
        final EventManager eventManager = new EventManager(commandClient);

        new JDABuilder()
                .setToken(token)
                .setEventManager(eventManager)
                .setEnabledCacheFlags(EnumSet.noneOf(CacheFlag.class))
                .build();
    }

    // Modified code from CommandClientImpl.java of JDA Utils
    private void helpConsumer(CommandEvent event) {
        final CommandClient client = event.getClient();
        final List<Command> commands = client.getCommands();
        final String textPrefix = client.getTextualPrefix();
        final String prefix = client.getPrefix();

        final StringBuilder builder = new StringBuilder("So you want a taste of my commands?\nWell here they are:\n");
        Command.Category category = null;

        for (Command command : commands) {
            if (!command.isHidden() && (!command.isOwnerCommand() || event.isOwner())) {
                if (!Objects.equals(category, command.getCategory())) {
                    category = command.getCategory();
                    builder.append("\n\n  __").append(category == null ? "No Category" : category.getName()).append("__:\n");
                }
                builder.append("\n`").append(textPrefix).append(prefix == null ? " " : "").append(command.getName())
                        .append(command.getArguments() == null ? "`" : " " + command.getArguments() + "`")
                        .append(" - ").append(command.getHelp());
            }
        }

        event.reply(builder.toString(), (unused) -> {
            if (event.isFromType(ChannelType.TEXT)) {
                event.reactSuccess();
            }
        }, (t) -> event.replyWarning("Help cannot be sent because you are blocking Direct Messages."));
    }

    public static void main(String[] args) throws LoginException, IOException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Haha yes this code wants token");
        }

        new Hiro(args[0]);
    }
}
