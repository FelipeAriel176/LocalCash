package com.example.localcash.database;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface TransaccionDao {
    @Insert
    void insertar(Transaccion transaccion);

    @Query("SELECT * FROM transacciones")
    List<Transaccion> obtenerTodas();

    @Query("DELETE FROM transacciones")
    void borrarTodas();

    @Delete
    void borrar(Transaccion transaccion);
}