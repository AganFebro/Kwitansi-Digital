package com.kudig.kwitansidigital;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {KwitansiEntity.class}, version = 1)
public abstract class KwitansiDB extends RoomDatabase {
    public abstract KwitansiDAO getKwitansiDAO();
    private static KwitansiDB instance;
    public static synchronized KwitansiDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            KwitansiDB.class, "KwitansiDB")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }


}
