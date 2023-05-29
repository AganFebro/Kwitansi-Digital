package com.kudig.kwitansidigital;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {KwitansiEntity.class}, version = 1)
public abstract class KwitansiDB extends RoomDatabase {
    public abstract KwitansiDAO getKwitansiDAO();

}
