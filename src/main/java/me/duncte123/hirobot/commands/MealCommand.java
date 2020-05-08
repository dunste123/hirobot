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

package me.duncte123.hirobot.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.internal.utils.IOUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static me.duncte123.hirobot.ReactionHelpers.getJackson;

// https://www.themealdb.com/api/json/v1/1/random.php
// https://www.themealdb.com/meal/52913-Brie-wrapped-in-prosciutto-&-brioche
// https://www.themealdb.com/meal/{idMeal}-{strMeal replace space with dash}
public class MealCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(MealCommand.class);
    private static final int MAX_INGREDIENTS = 20;

    private final ObjectMapper jackson;

    public MealCommand() {
        this.name = "meal";
        this.help = "Get to see what Hiro and Aiden will be serving";
        this.cooldown = 60;
        this.jackson = getJackson();
    }

    @Override
    protected void execute(CommandEvent event) {
        final Request request = new Request.Builder()
                .get()
                .url("https://www.themealdb.com/api/json/v1/1/random.php")
                .header("Accept", "application/json")
                .header("User-Agent", "HiroBot (https://github.com/dunste123/hirobot)")
                .build();

        DialogCommand.client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LOGGER.error("Failed to get a meal", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final JsonNode meal = jackson.readTree(IOUtil.readFully(IOUtil.getBody(response))).get("meals").get(0);
                final MessageEmbed embed = buildEmbed(meal);
                final String mealOfDay = getMealOfDay();
                final String msg;

                if (mealOfDay == null) {
                    msg = "So you want a mid-night snack eh " + event.getAuthor().getAsMention() + "?\n" +
                            "I won't judge <:HiroSmug:670239478279045131>";
                } else {
                    msg = event.getAuthor().getAsMention() + ", here is what's for " + mealOfDay;
                }

                final Message message = new MessageBuilder()
                        .setContent(msg)
                        .setEmbed(embed)
                        .build();

                event.reply(message);
            }
        });
    }

    private MessageEmbed buildEmbed(JsonNode meal) {
        final String title = meal.get("strMeal").asText();
        final String url = String.format(
                "https://www.themealdb.com/meal/%s-%s",
                meal.get("idMeal").asText(),
                meal.get("strMeal").asText().replaceAll("\\s+", "-")
        );

        return new EmbedBuilder()
                .setTitle(title, url)
                .setThumbnail(meal.get("strMealThumb").asText())
                .addField("Ingredients", getIngredients(meal), false)
                .addField("Instructions", meal.get("strInstructions").asText(), false)
                .setFooter("Time used to determine what meal is next is in UTC.")
                .build();
    }

    private String getMealOfDay() {
        Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour < 12) {
            return "breakfast";
        } else if (hour < 18) {
            return "lunch";
        } else if (hour  < 22) {
            return "dinner";
        }

        // Midnight snack smh
        return null;
    }

    private String getIngredients(JsonNode meal) {
        final List<String> ingredients = new ArrayList<>();

        for (int i = 1; i <= MAX_INGREDIENTS; i++) {
            final String ingredientKey = "strIngredient" + i;
            final String measureKey = "strMeasure" + i;

            final String ingredient = meal.get(ingredientKey).asText("").trim();
            final String measure = meal.get(measureKey).asText("").trim();

            if (ingredient.isBlank() || measure.isBlank()) {
                continue;
            }

            ingredients.add(measure + ' ' + ingredient);
        }

        return '`' + String.join("`, `", ingredients) + '`';
    }
}
