package me.duncte123.hirobot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.measure.converter.ConversionException;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;
import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.duncte123.hirobot.ConvertHelpers.*;

public class Hiro implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hiro.class);
    private static final Pattern INPUT_PATTERN = Pattern.compile("(\\d+)(\\D{1,2})");
    private static final long OWNER_ID = 311769499995209728L;
    private static final long FAN_GUILD_ID = 670218976932134922L;
    private static final long STANS_ROLE_ID = 670368434017533962L;
    private static final long GENERAL_CHANNEL_ID = 670218976932134925L;
    private static final String PREFIX = "-";
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
        new JDABuilder()
                .setToken(token)
                .addEventListeners(this)
                .setEnabledCacheFlags(EnumSet.noneOf(CacheFlag.class))
                .setActivity(Activity.playing("with Keitaro"))
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
        }
    }

    private void onReady(@Nonnull ReadyEvent event) {
        LOGGER.info("Logged in as {}", event.getJDA().getSelfUser().getAsTag());
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
            // -cvt f 10c
            //-cvt (\D) (\d+)(\D)
            final String[] split = contentRaw.split("\\s+");
            final List<String> args = List.of(split).subList(1, split.length);

            if (args.size() < 2) {
                sendHelpForCommand(channel);
                return;
            }

            final String input = args.get(1);
            final Matcher matcher = INPUT_PATTERN.matcher(input);

            if (!matcher.matches()) {
                sendHelpForCommand(channel);
                return;
            }

            final String sourceUnit = matcher.group(2);
            final Unit<? extends Quantity> inputUnit = getUnitForInput(sourceUnit);

            if (inputUnit == null) {
                sendHelpForCommand(channel);
                return;
            }

            final String targetUnit = args.get(0).toLowerCase();
            final var convertMethod = getConvertMethod(targetUnit);

            if (convertMethod == null) {
                sendHelpForCommand(channel);
                return;
            }

            try {
                final double inputVal = Double.parseDouble(matcher.group(1));

                final Double apply = convertMethod.apply(inputVal, inputUnit);

                channel.sendMessageFormat(
                        "%.2f%s is %.2f%s",
                        inputVal,
                        inputUnit,
                        apply,
                        getUnitForInput(targetUnit)
                ).queue();
            } catch (ConversionException e) {
                channel.sendMessage(e.getMessage()).queue();
            }

            /*double fahrenheit = 100;
            final double celsius = toCelsius(fahrenheit, FAHRENHEIT);

            channel.sendMessageFormat(
                    "TEST: %f%sf is %f%sc",
                    fahrenheit,
                    DEGREE_SIGN,
                    celsius,
                    DEGREE_SIGN
            ).queue();*/
        }

    }

    private void sendHelpForCommand(TextChannel channel) {
        channel.sendMessage("Correct usage for this command is `"+PREFIX+"cvt <unit-to-convert-to> <value>`\n" +
                "Available length units are: km, m, cm, in, ft\n" +
                "Available temperature units are: c, f, k\n" +
                "Some examples of this are `"+PREFIX+"cvt f 30c`\n" +
                "`"+PREFIX+"cvt c 100f`").queue();
    }

    public static void main(String[] args) throws LoginException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Haha yes this code wants token");
        }

        new Hiro(args[0]);
    }
}
