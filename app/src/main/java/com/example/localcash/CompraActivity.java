package com.example.localcash;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.example.localcash.database.AppDatabase;
import com.example.localcash.database.Transaccion;

public class CompraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compra);

        EditText etDescripcion = findViewById(R.id.etDescripcion);
        EditText etMonto = findViewById(R.id.etMonto);
        Spinner spinnerContacto = findViewById(R.id.spinnerContacto);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        String[] contactos = {"Solo yo", "Reichel", "Javiera", "Fabián"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, contactos);
        spinnerContacto.setAdapter(adapter);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "localcash-db")
                .allowMainThreadQueries()
                .build();

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descripcion = etDescripcion.getText().toString();
                String montoStr = etMonto.getText().toString();

                if (descripcion.isEmpty() || montoStr.isEmpty()) {
                    Toast.makeText(CompraActivity.this, "Faltan datos", Toast.LENGTH_SHORT).show();
                    return;
                }

                Transaccion nuevaCompra = new Transaccion();
                nuevaCompra.descripcion = descripcion;

                double montoTotal = Double.parseDouble(montoStr);
                int contactoId = spinnerContacto.getSelectedItemPosition();

                if (contactoId != 0) {
                    nuevaCompra.monto = montoTotal / 2.0;
                } else {
                    nuevaCompra.monto = montoTotal;
                }

                nuevaCompra.contactoId = contactoId;

                double variacionLat = (Math.random() - 0.5) * 0.05;
                double variacionLon = (Math.random() - 0.5) * 0.05;
                nuevaCompra.latitud = -33.4489 + variacionLat;
                nuevaCompra.longitud = -70.6693 + variacionLon;

                db.transaccionDao().insertar(nuevaCompra);

                Toast.makeText(CompraActivity.this, "Deuda dividida y guardada", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}