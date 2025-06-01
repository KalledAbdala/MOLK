package com.app.molk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DashEscolha extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_escolha);

        // Configurar cliques nos cards
        CardView cardMeusDados = findViewById(R.id.cardMeusDados);
        CardView cardDadosParceiros = findViewById(R.id.cardDadosParceiros);



        // Configurar clique no card Meus Dados
        cardMeusDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar para a tela de Meus Dados
                Intent intent = new Intent(DashEscolha.this, MeusDadosActivity.class);
                startActivity(intent);
            }
        });


        // Configurar clique no card Dados dos Parceiros
        cardDadosParceiros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar para a tela de Dashboard
                Intent intent = new Intent(DashEscolha.this, DashboardActivity.class);
                startActivity(intent);
            }
        });


    }
}