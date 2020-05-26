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

package me.duncte123.hirobot.chat;

import com.google.cloud.dialogflow.v2.*;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatBot.class);
    private static final String PROJECT_ID = "hiro-akiba-burymj";
    private final ExecutorService executor = Executors.newSingleThreadExecutor((r) -> {
        final Thread t = new Thread(r, "chatbot-thread");

        // Don't prevent the jvm from shutting down
        t.setDaemon(true);

        return t;
    });

    /**
     * Handles the input from the chatbot
     *
     * @param input
     *         The contents of the message with the mention prefix stripped off
     * @param event
     *         the event as received from discord
     */
    public void handleInput(String input, GuildMessageReceivedEvent event) {
        executor.submit(() -> {
            try {
                event.getChannel().sendTyping().queue();
                this.handleInternally(input, event);
            } catch (Exception e) {
                LOGGER.error("Failed to run chatbot", e);
            }
        });
    }

    private void handleInternally(String input, GuildMessageReceivedEvent event) throws Exception {
        final User author = event.getAuthor();
        final String response = this.getResponse(input, author.getId());

        event.getChannel()
                .sendMessage(author.getAsMention())
                .append(", ")
                .append(response)
                .queue();
    }

    /**
     * Get the chatbot response for an input
     *
     * @param input
     *         the user input
     * @param userId
     *         the id of the user (used for the session id)
     *
     * @return the response from the bot
     *
     * @throws Exception when google did throw
     */
    private String getResponse(String input, String userId) throws Exception {
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            final SessionName session = SessionName.of(PROJECT_ID, userId);
            final TextInput.Builder textInput = TextInput.newBuilder().setText(input).setLanguageCode("en-US");
            // Build the query with the TextInput
            final QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();
            // Performs the detect intent request
            final DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);
            // Display the query result
            final QueryResult queryResult = response.getQueryResult();

            LOGGER.info("====================");
            LOGGER.info("Query Text: '{}'", queryResult.getQueryText());
            LOGGER.info("Detected Intent: {} (confidence: {})",
                    queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence());
            LOGGER.info("Detected action: {}", queryResult.getAction());
            LOGGER.info("Fulfillment Text: '{}'", queryResult.getFulfillmentText());

            return queryResult.getFulfillmentText();
        }
    }

}
