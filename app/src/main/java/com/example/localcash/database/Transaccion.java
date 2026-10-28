package com.example.localcash.database;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transacciones")
public class Transaccion {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String descripcion;
    public double monto;
    public double latitud;
    public double longitud;
    public int contactoId;
}