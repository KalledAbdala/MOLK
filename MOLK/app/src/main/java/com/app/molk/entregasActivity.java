package com.app.molk;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class entregasActivity extends AppCompatActivity {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entregas);

        // Configura o ícone do menu para abrir a tela de módulos
        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> {
            Intent intent = new Intent(entregasActivity.this, ModulosActivity.class);
            startActivity(intent);
        });

        // Inicializa o fragmento do mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;

                    // Lista de ecopontos
                    LatLng[] ecopontos = {
                            new LatLng(-23.540958356268504, -47.4503902499014), // ECOPONTO Votorantim
                            new LatLng(-23.492430981384373, -47.43922302091665), // Ecoponto Zona Leste
                            new LatLng(-23.50534368035998, -47.43339664057677), // CORESO - Cooperativa
                            new LatLng(-23.473727588536207, -47.49311703906051), // ECOPONTO VILA HELENA
                            new LatLng(-23.498219589116914, -47.51642256042001), // Ecoponto Zona Oeste
                            new LatLng(-23.487087425539993, -47.471753644480955), // Metareciclagem - PMS
                            new LatLng(-23.24837114612832, -47.312087697482724), // Ecoponto Padre Bento
                            new LatLng(-23.196612620055213, -47.28174196654586), // Ecoponto 9 - CEA
                            new LatLng(-23.19906714103168, -47.31038833655026) // Ecoponto São Pedro e São Paulo
                    };

                    String[] nomesEcopontos = {
                            "ECOPONTO Votorantim",
                            "Ecoponto Zona Leste",
                            "CORESO - Cooperativa de Reciclagem de Sorocaba",
                            "ECOPONTO VILA HELENA",
                            "Ecoponto Zona Oeste Pref. Sorocaba",
                            "Metareciclagem - PMS",
                            "Ecoponto Padre Bento",
                            "Ecoponto 9 - CEA",
                            "Ecoponto São Pedro e São Paulo"
                    };

                    // Adiciona todos os marcadores no mapa
                    for (int i = 0; i < ecopontos.length; i++) {
                        mMap.addMarker(new MarkerOptions().position(ecopontos[i]).title(nomesEcopontos[i]));
                    }

                    // Move a câmera para o primeiro ecoponto
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ecopontos[0], 11));

                    // Exemplo: conecta todos os ecopontos com uma linha
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .color(Color.BLUE)
                            .width(8);

                    for (LatLng ponto : ecopontos) {
                        polylineOptions.add(ponto);
                    }

                    mMap.addPolyline(polylineOptions);
                }
            });
        }
    }
}
