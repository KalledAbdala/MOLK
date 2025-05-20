package com.app.molk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SeusResiduosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seus_residuos);

        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> {
            Intent intent = new Intent(SeusResiduosActivity.this, ModulosActivity.class);
            startActivity(intent);
        });


        Button btnAdicionarResiduo = findViewById(R.id.btnAdicionarResiduo);

        btnAdicionarResiduo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeusResiduosActivity.this, cadastrarResiduoActivity.class);
                startActivity(intent);
            }
        });


    }
}
