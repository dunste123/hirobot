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

package me.duncte123.hirobot.objects;

import net.dv8tion.jda.api.utils.data.DataObject;

import static me.duncte123.hirobot.Utils.hexStringToInt;

public class CBCharacter {
    private final String name;
    private final String description;
    private final int age;
    private final String birthday;
    private final String animal;
    private final int color;
    private final String emoteId;

    public CBCharacter(String name, String description, int age, String birthday, String animal, String color, String emoteId) {
        this.name = name;
        this.description = description;
        this.age = age;
        this.birthday = birthday;
        this.animal = animal;
        this.color = hexStringToInt(color);
        this.emoteId = emoteId;
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return name.substring(0, name.indexOf(' '));
    }

    public String getDescription() {
        return description;
    }

    public int getAge() {
        return age;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getAnimal() {
        return animal;
    }

    public int getColor() {
        return color;
    }

    public String getEmoteId() {
        return emoteId;
    }

    public String getEmoteUrlGif() {
        return "https://cdn.discordapp.com/emojis/" + getEmoteId() + ".gif?v=1";
    }

    public String getEmoteUrlPng() {
        return "https://cdn.discordapp.com/emojis/" + getEmoteId() + ".png?v=1";
    }

    public static CBCharacter fromData(DataObject data) {
        return new CBCharacter(
                data.getString("name"),
                data.getString("description"),
                data.getInt("age"),
                data.getString("birthday"),
                data.getString("animal"),
                data.getString("color"),
                data.getString("emoteId")
        );
    }
}
