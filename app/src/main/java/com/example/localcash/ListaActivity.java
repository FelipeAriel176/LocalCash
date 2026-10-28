package com.example.localcash;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.example.localcash.database.AppDatabase;
import com.example.localcash.database.Transaccion;
import java.util.ArrayList;
import java.util.List;

public class ListaActivity extends AppCompatActivity {

    private AppDatabase db;
    private ListView listViewGastos;
    private List<Transaccion> listaTransacciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        listViewGastos = findViewById(R.id.listViewGastos);

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "localcash-db")
                .allowMainThreadQueries()
                .build();

        cargarLista();

        listViewGastos.setOnItemLongClickListener((parent, view, position, id) -> {
            Transaccion transaccionABorrar = listaTransacciones.get(position);

            new AlertDialog.Builder(this)
                    .setTitle("Cobrar Deuda")
                    .setMessage("¿Seguro que desea borrar el gasto: " + transaccionABorrar.descripcion + "?")
                    .setPositiveButton("Sí, borrar", (dialog, which) -> {
                        db.transaccionDao().borrar(transaccionABorrar);
                        Toast.makeText(ListaActivity.this, "Deuda eliminada", Toast.LENGTH_SHORT).show();
                        cargarLista();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
            return true;
        });
    }

    private void cargarLista() {
        listaTransacciones = db.transaccionDao().obtenerTodas();
        List<String> listaTextos = new ArrayList<>();
        String[] contactos = {"Solo yo", "Reichel", "Javiera", "Fabián"};

        for (Transaccion t : listaTransacciones) {
            String nombreContacto = contactos[t.contactoId];
            listaTextos.add(nombreContacto + " debe $" + t.monto + " por " + t.descripcion);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaTextos);
        listViewGastos.setAdapter(adapter);
    }
}