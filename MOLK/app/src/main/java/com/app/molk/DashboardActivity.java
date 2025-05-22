package com.app.molk;

import android.os.Bundle;
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
    }
}
