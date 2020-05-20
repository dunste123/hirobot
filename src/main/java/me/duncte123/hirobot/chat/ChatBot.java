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

/**
 * java.io.IOException: The Application Default Credentials are not available. They are available if running in Google Compute Engine. Otherwise, the environment variable GOOGLE_APPLICATION_CREDENTIALS must be defined pointing to a file defining the credentials. See https://developers.google.com/accounts/docs/application-default-credentials for more information.
 * 	at com.google.auth.oauth2.DefaultCredentialsProvider.getDefaultCredentials(DefaultCredentialsProvider.java:134)
 * 	at com.google.auth.oauth2.GoogleCredentials.getApplicationDefault(GoogleCredentials.java:119)
 * 	at com.google.auth.oauth2.GoogleCredentials.getApplicationDefault(GoogleCredentials.java:91)
 * 	at com.google.api.gax.core.GoogleCredentialsProvider.getCredentials(GoogleCredentialsProvider.java:67)
 * 	at com.google.api.gax.rpc.ClientContext.create(ClientContext.java:135)
 * 	at com.google.cloud.dialogflow.v2.stub.GrpcSessionsStub.create(GrpcSessionsStub.java:78)
 * 	at com.google.cloud.dialogflow.v2.stub.SessionsStubSettings.createStub(SessionsStubSettings.java:108)
 * 	at com.google.cloud.dialogflow.v2.SessionsClient.<init>(SessionsClient.java:132)
 * 	at com.google.cloud.dialogflow.v2.SessionsClient.create(SessionsClient.java:114)
 * 	at com.google.cloud.dialogflow.v2.SessionsClient.create(SessionsClient.java:106)
 * 	at me.duncte123.hirobot.chat.ChatBot.getResponse(ChatBot.java:83)
 * 	at me.duncte123.hirobot.chat.ChatBot.handleInternally(ChatBot.java:62)
 * 	at me.duncte123.hirobot.chat.ChatBot.lambda$handleInput$1(ChatBot.java:53)
 * 	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:515)
 * 	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
 * 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
 * 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
 * 	at java.base/java.lang.Thread.run(Thread.java:834)
 */
public class ChatBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatBot.class);
    private static final String PROJECT_ID = "";
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
                this.handleInternally(input, event);
            } catch (Exception e) {
                LOGGER.error("Failed to run chatbot", e);
            }
        });
    }

    private void handleInternally(String input, GuildMessageReceivedEvent event) throws Exception {
        final User author = event.getAuthor();
        final String response = this.getResponse(input, "123456789");

        event.getChannel()
                .sendMessage(author.getAsMention())
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

            SessionName session = SessionName.of(PROJECT_ID, userId);
            System.out.println("Session Path: " + session.toString());

            TextInput.Builder textInput =
                    TextInput.newBuilder().setText(input).setLanguageCode("en-US");

            // Build the query with the TextInput
            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

            // Performs the detect intent request
            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

            // Display the query result
            QueryResult queryResult = response.getQueryResult();

            System.out.println("====================");
            System.out.format("Query Text: '%s'\n", queryResult.getQueryText());
            System.out.format("Detected Intent: %s (confidence: %f)\n",
                    queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence());
            System.out.format("Fulfillment Text: '%s'\n", queryResult.getFulfillmentText());

            return queryResult.getFulfillmentText();
        }
    }

}
