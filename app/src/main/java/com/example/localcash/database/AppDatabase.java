package com.example.localcash.database;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Transaccion.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TransaccionDao transaccionDao();
}