package com.app.molk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ModulosActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modulos);

        CardView btnResiduos = findViewById(R.id.cardResiduos);
        CardView btncardResiduosDeParceiros = findViewById(R.id.cardResiduosDeParceiros);
        CardView btnEntregas = findViewById(R.id.cardEntregas);
        CardView btnParceiros = findViewById(R.id.cardParceiros);
        CardView btnDashboard = findViewById(R.id.cardDashboard);
        CardView btnChatbot = findViewById(R.id.cardChatbot);



        btnResiduos.setOnClickListener(v -> {
            Intent intent = new Intent(this, SeusResiduosActivity.class);
            startActivity(intent);
        });

        btncardResiduosDeParceiros.setOnClickListener(v -> {
            Intent intent = new Intent(this, ResiduosDeParceirosActivity.class);
            startActivity(intent);
        });

        btnEntregas.setOnClickListener(v -> {
            Intent intent = new Intent(this, entregasActivity.class);
            startActivity(intent);
        });

        btnParceiros.setOnClickListener(v -> {
            Intent intent = new Intent(this, SeusParceirosActivity.class);
            startActivity(intent);
        });

        btnDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashEscolha.class);
            startActivity(intent);
        });

        btnChatbot.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatbotActivity.class);
            startActivity(intent);
        });


        ImageView btnIrSeusResiduos = findViewById(R.id.btnIrSeusResiduos);
        btnIrSeusResiduos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModulosActivity.this, SeusResiduosActivity.class);
                startActivity(intent);
            }
        });
    }
}
