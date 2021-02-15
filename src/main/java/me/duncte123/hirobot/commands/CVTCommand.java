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

import java.util.regex.Pattern;

public class CVTCommand extends Command {
    private static final Pattern INPUT_PATTERN = Pattern.compile("(\\d+)(\\D{1,2})");

    public CVTCommand() {
        this.name = "cvt";
        this.help = "Convert units";
        this.arguments = "<unit-to-convert-to> <value>";
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("ehhh");

        /*if (event.getArgs().isEmpty()) {
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
        }*/
    }
}
