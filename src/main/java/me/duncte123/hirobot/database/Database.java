package me.duncte123.hirobot.database;

public interface Database {

    void setValentine(long userId, int buddyIndex);

    /**
     *
     * @param userId the id of a user
     * @return when no record is found this should return -1
     */
    int getValentine(long userId);

}
