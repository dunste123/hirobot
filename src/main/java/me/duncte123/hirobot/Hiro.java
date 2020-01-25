package me.duncte123.hirobot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Hiro {

    public static void main(String[] args) throws LoginException {
        new JDABuilder()
                .setToken("")
                .setActivity(Activity.playing("with Keitaro"))
                .build();
    }

}
