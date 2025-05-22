package com.app.molk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    PieChartView pieChart;
    LineChartView lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Corrija aqui o nome do layout para o XML correto
        setContentView(R.layout.activity_dashboard_acitivity);

        pieChart = findViewById(R.id.pieChart);
        lineChart = findViewById(R.id.lineChart);

        // Atualize os dados dos gráficos se quiser


        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ModulosActivity.class);
            startActivity(intent);
        });

    }
}
