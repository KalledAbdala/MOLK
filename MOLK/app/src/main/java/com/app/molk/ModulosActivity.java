package com.app.molk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ModulosActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modulos);



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
