package me.duncte123.hirobot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.MessageChannel;

import javax.measure.converter.ConversionException;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.duncte123.hirobot.ConvertHelpers.getConvertMethod;
import static me.duncte123.hirobot.ConvertHelpers.getUnitForInput;
import static me.duncte123.hirobot.Hiro.PREFIX;

public class CVTCommand extends Command {
    private static final Pattern INPUT_PATTERN = Pattern.compile("(\\d+)(\\D{1,2})");

    public CVTCommand() {
        this.name = "cvt";
        this.help = "Convert units";
        this.arguments = "<unit-to-convert-to> <value>";
    }

    @Override
    protected void execute(CommandEvent event) {

        if (event.getArgs().isEmpty()) {
            sendHelpForCommand(event);
            return;
        }

        // -cvt f 10c
        //-cvt (\D) (\d+)(\D)
        final String[] args = event.getArgs().split("\\s+");

        if (args.length < 2) {
            sendHelpForCommand(event);
            return;
        }

        final String input = args[1];
        final Matcher matcher = INPUT_PATTERN.matcher(input);

        if (!matcher.matches()) {
            sendHelpForCommand(event);
            return;
        }

        final String sourceUnit = matcher.group(2);
        final Unit<? extends Quantity> inputUnit = getUnitForInput(sourceUnit);

        if (inputUnit == null) {
            sendHelpForCommand(event);
            return;
        }

        final String targetUnit = args[0].toLowerCase();
        final var convertMethod = getConvertMethod(targetUnit);

        if (convertMethod == null) {
            sendHelpForCommand(event);
            return;
        }

        final MessageChannel channel = event.getChannel();

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
    }

    private void sendHelpForCommand(CommandEvent event) {
        event.getChannel().sendMessage("Correct usage for this command is `"+PREFIX+"cvt <unit-to-convert-to> <value>`\n" +
                "Available length units are: km, m, cm, in, ft\n" +
                "Available temperature units are: c, f, k\n" +
                "Some examples of this are `"+PREFIX+"cvt f 30c`\n" +
                "`"+PREFIX+"cvt c 100f`").queue();
    }
}
