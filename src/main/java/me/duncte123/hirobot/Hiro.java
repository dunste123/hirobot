package me.duncte123.hirobot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

import javax.measure.Measure;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Length;
import static javax.measure.unit.NonSI.*;
import static javax.measure.unit.SI.*;

public class Hiro implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hiro.class);
    private static final long OWNER_ID = 311769499995209728L;
    private static final String PREFIX = "-";
    private static final String DEGREE_SIGN = "\u00B0";


    public Hiro(String token) throws LoginException {
        new JDABuilder()
                .setToken(token)
                .addEventListeners(this)
                .setActivity(Activity.playing("with Keitaro"))
                .build();
    }

    public static void main(String[] args) throws LoginException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Haha yes this code wants token");
        }

        new Hiro(args[0]);
    }


    @Override
    public void onEvent(@Nonnull GenericEvent event) {
        if (event instanceof ReadyEvent) {
            this.onReady((ReadyEvent) event);
        } else if (event instanceof GuildMemberJoinEvent) {
            this.onGuildMemberJoin((GuildMemberJoinEvent) event);
        } else if (event instanceof GuildMessageReceivedEvent) {
            this.onGuildMessageReceived((GuildMessageReceivedEvent) event);
        }
    }

    private void onReady(@Nonnull ReadyEvent event) {
        LOGGER.info("Logged in as {}", event.getJDA().getSelfUser().getAsTag());
    }

    private void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        //
    }

    private void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        final Message message = event.getMessage();
        final TextChannel channel = event.getChannel();
        final String contentRaw = message.getContentRaw();
        final User author = message.getAuthor();

        if ((PREFIX + "shutdown").equals(contentRaw) && author.getIdLong() == OWNER_ID) {
            final JDA jda = event.getJDA();
            jda.shutdown();
            jda.getHttpClient().connectionPool().evictAll();
            jda.getHttpClient().dispatcher().executorService().shutdown();
            return;
        }

        if (contentRaw.startsWith(PREFIX + "cvt")) {
            int fahrenheit = 100;

            final UnitConverter fahrenheitToCelsius = FAHRENHEIT.getConverterTo(CELSIUS);
            final double measure = Measure.valueOf(fahrenheit, FAHRENHEIT).doubleValue(FAHRENHEIT);
            final double celsius = fahrenheitToCelsius.convert(measure);

            channel.sendMessageFormat(
                    "TEST: %d%sf is %f%sc",
                    fahrenheit,
                    DEGREE_SIGN,
                    celsius,
                    DEGREE_SIGN
            ).queue();
        }

    }
}
