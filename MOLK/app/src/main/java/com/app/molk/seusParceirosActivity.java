package com.app.molk;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SeusParceirosActivity extends AppCompatActivity {

    private LinearLayout scrollContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seus_parceiros);

        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> {
            Intent intent = new Intent(SeusParceirosActivity.this, ModulosActivity.class);
            startActivity(intent);
        });

        scrollContent = findViewById(R.id.scrollContent);

        // Adicionando parceiros mockados com todos os campos
        adicionarParceiro(
                "EcoLimp",
                "Mariana Silva",
                "mariana@ecolimp.com",
                "(11) 91234-5678"
        );
        adicionarParceiro(
                "RecicleMais",
                "Carlos Souza",
                "carlos@reciclemais.com",
                "(21) 99876-5432"
        );
        adicionarParceiro(
                "GreenWay",
                "Fernanda Lima",
                "fernanda@greenway.com",
                "(31) 93456-7890"
        );
        adicionarParceiro(
                "BioTerra",
                "João Mendes",
                "joao@bioterra.com",
                "(41) 97654-3210"
        );
    }

    private void adicionarParceiro(String empresa, String responsavel, String email, String telefone) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.card_parceiro, scrollContent, false);

        TextView nomeParceiro = cardView.findViewById(R.id.nomeParceiro);
        TextView nomeResponsavel = cardView.findViewById(R.id.nomeResponsavel);
        TextView emailResponsavel = cardView.findViewById(R.id.emailResponsavel);
        TextView telefoneResponsavel = cardView.findViewById(R.id.telefoneResponsavel);
        Button btnVerDetalhes = cardView.findViewById(R.id.btnVerDetalhes);

        nomeParceiro.setText("Empresa parceira: " + empresa);
        nomeResponsavel.setText("Responsável: " + responsavel);
        emailResponsavel.setText("Email: " + email);
        telefoneResponsavel.setText("Telefone: " + telefone);

        btnVerDetalhes.setOnClickListener(v -> {
            Toast.makeText(SeusParceirosActivity.this, "Mais informações sobre " + empresa, Toast.LENGTH_SHORT).show();
        });

        scrollContent.addView(cardView);
    }
}
