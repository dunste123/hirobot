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
        return name.split("\\s+")[0];
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

    public String getEmoteUrl() {
        return "https://cdn.discordapp.com/emojis/" + emoteId + ".gif?v=1";
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
