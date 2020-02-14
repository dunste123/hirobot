package me.duncte123.hirobot.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLiteDatabase implements Database {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteDatabase.class);

    public SQLiteDatabase() {
        //
    }

    @Override
    public void setValentine(long userId, int arrayIndex) {

    }

    @Override
    public int getValentine(long userId) {
        return -1;
    }
}
