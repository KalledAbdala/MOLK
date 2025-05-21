package com.app.molk;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResiduosDeParceirosActivity extends AppCompatActivity {

    private LinearLayout scrollContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_residuos_de_parceiros);

        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ResiduosDeParceirosActivity.this, ModulosActivity.class);
            startActivity(intent);
        });

        scrollContent = findViewById(R.id.scrollContent);

        adicionarCardMockado("EcoLimp", "Plástico", "Restos de embalagens PET", R.drawable.img_plastico);
        adicionarCardMockado("RecicleMais", "Papel", "Papéis triturados", R.drawable.img_papel);
        adicionarCardMockado("GreenWay", "Vidro", "Garrafas de vidro quebradas", R.drawable.img_vidro);
        adicionarCardMockado("BioTerra", "Metal", "Latas de alumínio", R.drawable.img_metal);

    }

    private void adicionarCardMockado(String empresa, String tipo, String descricao, int imagemResId) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.card_residuo_parceiro, scrollContent, false);

        TextView nomeEmpresa = cardView.findViewById(R.id.nomeEmpresa);
        TextView tipoResiduo = cardView.findViewById(R.id.tipoResiduo);
        TextView descricaoResiduo = cardView.findViewById(R.id.descricaoResiduo);
        ImageView imagemResiduo = cardView.findViewById(R.id.imagemResiduo);
        Button btnConectar = cardView.findViewById(R.id.btnConectar);

        nomeEmpresa.setText(empresa);
        tipoResiduo.setText(tipo);
        descricaoResiduo.setText(descricao);
        imagemResiduo.setImageResource(imagemResId);

        btnConectar.setOnClickListener(v -> {
            btnConectar.setText("Solicitado");
            btnConectar.setEnabled(false);
        });

        scrollContent.addView(cardView);
    }

}