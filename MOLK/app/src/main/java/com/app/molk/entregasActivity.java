package com.app.molk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class entregasActivity extends AppCompatActivity {

    private LinearLayout containerCards;
    private GoogleMap mMap;

    private String fazerRequisicaoGET(String urlString, String token) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        reader.close();
        conn.disconnect();
        return result.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entregas);

        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> {
            Intent intent = new Intent(entregasActivity.this, ModulosActivity.class);
            startActivity(intent);
        });

        inicializarMapa();
        containerCards = findViewById(R.id.containerCards);

        new Thread(() -> {
            try {
                // Pega token do SharedPreferences
                SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                String token = prefs.getString("auth_token", null);
                if (token == null) return;

                String urlBase = "http://10.0.2.2:3000/";

                String responseEmpresa = fazerRequisicaoGET(urlBase + "entregas/empresa", token);
                JSONObject jsonEmpresa = new JSONObject(responseEmpresa);
                String nomeEmpresa = jsonEmpresa.getString("nome_empresa");

                String responseNegociando = fazerRequisicaoGET(urlBase + "entregas/negociando", token);
                JSONArray jsonArray = new JSONArray(responseNegociando);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String tipo = obj.getString("tipo");
                    String tipoEntrega = obj.getString("tipo_entrega");
                    String dataEntrega = obj.getString("data_entrega").substring(0, 10);

                    runOnUiThread(() -> addCard(tipo, tipoEntrega, dataEntrega, nomeEmpresa));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void inicializarMapa() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                mMap = googleMap;

                LatLng[] ecopontos = {
                        new LatLng(-23.540958356268504, -47.4503902499014),
                        new LatLng(-23.492430981384373, -47.43922302091665),
                        new LatLng(-23.50534368035998, -47.43339664057677),
                        new LatLng(-23.473727588536207, -47.49311703906051),
                        new LatLng(-23.498219589116914, -47.51642256042001),
                        new LatLng(-23.487087425539993, -47.471753644480955),
                        new LatLng(-23.24837114612832, -47.312087697482724),
                        new LatLng(-23.196612620055213, -47.28174196654586),
                        new LatLng(-23.19906714103168, -47.31038833655026)
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

                for (int i = 0; i < ecopontos.length; i++) {
                    mMap.addMarker(new MarkerOptions().position(ecopontos[i]).title(nomesEcopontos[i]));
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ecopontos[0], 11));

                PolylineOptions polylineOptions = new PolylineOptions()
                        .color(Color.BLUE)
                        .width(8);

                for (LatLng ponto : ecopontos) {
                    polylineOptions.add(ponto);
                }

                mMap.addPolyline(polylineOptions);
            });
        }
    }

    private void addCard(String tipo, String tipoEntrega, String dataEntrega, String nomeEmpresa) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.card_entrega, containerCards, false);

        TextView tvNomeEmpresa = cardView.findViewById(R.id.tvNomeEmpresa);
        TextView tvTipo = cardView.findViewById(R.id.tvTipo);
        TextView tvTipoEntrega = cardView.findViewById(R.id.tvTipoEntrega);
        TextView tvDataEntrega = cardView.findViewById(R.id.tvDataEntrega);

        tvNomeEmpresa.setText(nomeEmpresa);
        tvTipo.setText("Tipo: " + tipo);
        tvTipoEntrega.setText("Entrega: " + tipoEntrega);
        tvDataEntrega.setText("Data: " + dataEntrega);

        containerCards.addView(cardView);
    }
}
