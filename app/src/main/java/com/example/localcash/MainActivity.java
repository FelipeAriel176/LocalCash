package com.example.localcash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;
import org.maplibre.android.MapLibre;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.annotations.MarkerOptions;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.camera.CameraPosition;
import com.example.localcash.database.AppDatabase;
import com.example.localcash.database.Transaccion;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private AppDatabase db;
    private MapLibreMap mapaActual;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapLibre.getInstance(this);
        setContentView(R.layout.activity_main);

        tvTotal = findViewById(R.id.tvTotal);

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Acceso denegado", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Acceso concedido.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Huella no reconocida", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Seguridad LocalCash")
                .setSubtitle("Usa tu huella  para acceder")
                .setNegativeButtonText("Salir")
                .build();

        biometricPrompt.authenticate(promptInfo);

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "localcash-db")
                .allowMainThreadQueries()
                .build();

        mapView = new MapView(this);
        LinearLayout contenedor = findViewById(R.id.contenedorMapa);
        contenedor.addView(mapView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(map -> {
            mapaActual = map;
            map.setStyle("https://basemaps.cartocdn.com/gl/voyager-gl-style/style.json", style -> {
                map.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(-33.4489, -70.6693))
                        .zoom(10.0)
                        .build());
                actualizarPinesYTotal();
            });
        });

        Button btnIrACompras = findViewById(R.id.btnIrACompras);
        btnIrACompras.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CompraActivity.class);
            startActivity(intent);
        });

        Button btnLimpiar = findViewById(R.id.btnLimpiar);
        btnLimpiar.setOnClickListener(v -> {
            db.transaccionDao().borrarTodas();
            actualizarPinesYTotal();
            Toast.makeText(this, "Historial limpiado exitosamente", Toast.LENGTH_SHORT).show();
        });

        Button btnVerLista = findViewById(R.id.btnVerLista);
        btnVerLista.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListaActivity.class);
            startActivity(intent);
        });
    }

    private void actualizarPinesYTotal() {
        if (mapaActual != null) {
            mapaActual.clear();
            List<Transaccion> compras = db.transaccionDao().obtenerTodas();

            double total = 0;
            for (Transaccion compra : compras) {
                total += compra.monto;
                mapaActual.addMarker(new MarkerOptions()
                        .position(new LatLng(compra.latitud, compra.longitud))
                        .title(compra.descripcion + " ($" + compra.monto + ")"));
            }
            tvTotal.setText("Total Gastado: $" + total);
        }
    }

    @Override
    protected void onStart() { super.onStart(); mapView.onStart(); }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        actualizarPinesYTotal();
    }
    @Override
    protected void onPause() { super.onPause(); mapView.onPause(); }
    @Override
    protected void onStop() { super.onStop(); mapView.onStop(); }
    @Override
    public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
    @Override
    protected void onDestroy() { super.onDestroy(); mapView.onDestroy(); }
}