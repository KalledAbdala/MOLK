package com.app.molk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.molk.data.models.ResiduosQuantidadeResponse;
import com.app.molk.data.models.TipoResiduo;
import com.app.molk.data.models.TotalResiduosResponse;
import com.app.molk.network.ApiService;
import com.app.molk.network.RetrofitClient;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";

    private PieChart pieChart;
    private HorizontalBarChart barChart;
    private TextView totalResiduosText;
    private ProgressBar loadingIndicator;

    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_acitivity);

        // Inicializar componentes da UI
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        totalResiduosText = findViewById(R.id.totalResiduosText);
        loadingIndicator = findViewById(R.id.loadingIndicator);

        // Obter token de autenticação
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String rawToken = prefs.getString("auth_token", null);

        if (rawToken == null || rawToken.trim().isEmpty()) {
            Toast.makeText(this, "Token não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, tela_login.class));
            finish();
            return;
        }

        token = "Bearer " + rawToken.trim();
        Log.i(TAG, "Token usado: " + token);

        // Configurar Retrofit
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Configurar navegação
        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ModulosActivity.class);
            startActivity(intent);
        });

        // Carregar dados da dashboard
        loadDashboardData();
    }

    private void loadDashboardData() {
        showLoading(true);

        // Carregar tipos de resíduos para o gráfico de pizza
        loadTiposResiduos();

        // Carregar dados de status dos resíduos para o gráfico de barras
        loadStatusResiduos();

        // Carregar total de resíduos
        loadTotalResiduos();
    }

    private void loadTiposResiduos() {
        Log.d(TAG, "Iniciando chamada para getTiposQuantidade");

        Call<List<TipoResiduo>> call = apiService.getTiposQuantidade(token);
        call.enqueue(new Callback<List<TipoResiduo>>() {
            @Override
            public void onResponse(Call<List<TipoResiduo>> call, Response<List<TipoResiduo>> response) {
                Log.d(TAG, "Resposta recebida: " + response.code());
                Log.d(TAG, "URL chamada: " + call.request().url());

                if (response.isSuccessful() && response.body() != null) {
                    List<TipoResiduo> tipos = response.body();
                    Log.d(TAG, "Dados recebidos: " + tipos.size() + " tipos");
                    setupPieChart(tipos);
                } else {
                    Toast.makeText(DashboardActivity.this, "Erro ao carregar tipos de resíduos", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Erro: " + response.code());
                    try {
                        Log.e(TAG, "Erro body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao ler errorBody", e);
                    }
                }
                showLoading(false);
            }

            @Override
            public void onFailure(Call<List<TipoResiduo>> call, Throwable t) {
                Log.e(TAG, "URL chamada: " + call.request().url());
                Log.e(TAG, "Falha: " + t.getMessage(), t);
                Toast.makeText(DashboardActivity.this, "Falha na conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showLoading(false);
            }
        });
    }

    private void setupPieChart(List<TipoResiduo> tipos) {
        List<PieEntry> entries = new ArrayList<>();
        for (TipoResiduo tipo : tipos) {
            entries.add(new PieEntry(tipo.getQuantidade(), tipo.getTipo()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);

        // Adicione este formatador de valores para mostrar apenas números inteiros
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.animate();

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        pieChart.invalidate();
    }

    private void loadStatusResiduos() {
        // Criar um contador para verificar quando todas as chamadas foram concluídas
        final int[] callsCompleted = {0};
        final int totalCalls = 4;

        final int[] disponiveis = {0};
        final int[] negociando = {0};
        final int[] concluidos = {0};
        final int[] cancelados = {0};

        // Resíduos disponíveis
        Call<ResiduosQuantidadeResponse> callDisponiveis = apiService.getResiduosDisponiveis(token);
        callDisponiveis.enqueue(new Callback<ResiduosQuantidadeResponse>() {
            @Override
            public void onResponse(Call<ResiduosQuantidadeResponse> call, Response<ResiduosQuantidadeResponse> response) {
                Log.d(TAG, "Resposta residuos-disponiveis: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    disponiveis[0] = response.body().getQuantidade();
                }
                checkAndSetupBarChart(++callsCompleted[0], totalCalls, disponiveis[0], negociando[0], concluidos[0], cancelados[0]);
            }

            @Override
            public void onFailure(Call<ResiduosQuantidadeResponse> call, Throwable t) {
                Log.e(TAG, "Falha ao carregar resíduos disponíveis: " + t.getMessage(), t);
                checkAndSetupBarChart(++callsCompleted[0], totalCalls, disponiveis[0], negociando[0], concluidos[0], cancelados[0]);
            }
        });

        // Resíduos negociando
        Call<ResiduosQuantidadeResponse> callNegociando = apiService.getResiduosNegociando(token);
        callNegociando.enqueue(new Callback<ResiduosQuantidadeResponse>() {
            @Override
            public void onResponse(Call<ResiduosQuantidadeResponse> call, Response<ResiduosQuantidadeResponse> response) {
                Log.d(TAG, "Resposta residuos-negociando: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    negociando[0] = response.body().getQuantidade();
                }
                checkAndSetupBarChart(++callsCompleted[0], totalCalls, disponiveis[0], negociando[0], concluidos[0], cancelados[0]);
            }

            @Override
            public void onFailure(Call<ResiduosQuantidadeResponse> call, Throwable t) {
                Log.e(TAG, "Falha ao carregar resíduos negociando: " + t.getMessage(), t);
                checkAndSetupBarChart(++callsCompleted[0], totalCalls, disponiveis[0], negociando[0], concluidos[0], cancelados[0]);
            }
        });

        // Resíduos concluídos
        Call<ResiduosQuantidadeResponse> callConcluidos = apiService.getResiduosConcluidos(token);
        callConcluidos.enqueue(new Callback<ResiduosQuantidadeResponse>() {
            @Override
            public void onResponse(Call<ResiduosQuantidadeResponse> call, Response<ResiduosQuantidadeResponse> response) {
                Log.d(TAG, "Resposta residuos-concluidos: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    concluidos[0] = response.body().getQuantidade();
                }
                checkAndSetupBarChart(++callsCompleted[0], totalCalls, disponiveis[0], negociando[0], concluidos[0], cancelados[0]);
            }

            @Override
            public void onFailure(Call<ResiduosQuantidadeResponse> call, Throwable t) {
                Log.e(TAG, "Falha ao carregar resíduos concluídos: " + t.getMessage(), t);
                checkAndSetupBarChart(++callsCompleted[0], totalCalls, disponiveis[0], negociando[0], concluidos[0], cancelados[0]);
            }
        });

        // Resíduos cancelados
        Call<ResiduosQuantidadeResponse> callCancelados = apiService.getResiduosCancelados(token);
        callCancelados.enqueue(new Callback<ResiduosQuantidadeResponse>() {
            @Override
            public void onResponse(Call<ResiduosQuantidadeResponse> call, Response<ResiduosQuantidadeResponse> response) {
                Log.d(TAG, "Resposta residuos-cancelados: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    cancelados[0] = response.body().getQuantidade();
                }
                checkAndSetupBarChart(++callsCompleted[0], totalCalls, disponiveis[0], negociando[0], concluidos[0], cancelados[0]);
            }

            @Override
            public void onFailure(Call<ResiduosQuantidadeResponse> call, Throwable t) {
                Log.e(TAG, "Falha ao carregar resíduos cancelados: " + t.getMessage(), t);
                checkAndSetupBarChart(++callsCompleted[0], totalCalls, disponiveis[0], negociando[0], concluidos[0], cancelados[0]);
            }
        });
    }

    private void checkAndSetupBarChart(int completed, int total, int disponiveis, int negociando, int concluidos, int cancelados) {
        if (completed == total) {
            setupBarChart(disponiveis, negociando, concluidos, cancelados);
        }
    }

    private void setupBarChart(int disponiveis, int negociando, int concluidos, int cancelados) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        entries.add(new BarEntry(0, disponiveis));
        entries.add(new BarEntry(1, negociando));
        entries.add(new BarEntry(2, concluidos));
        entries.add(new BarEntry(3, cancelados));

        BarDataSet dataSet = new BarDataSet(entries, "Status dos Resíduos");

        dataSet.setColors(
                Color.rgb(76, 175, 80),   // Verde para disponíveis
                Color.rgb(255, 193, 7),   // Amarelo para negociando
                Color.rgb(33, 150, 243),  // Azul para concluídos
                Color.rgb(244, 67, 54)    // Vermelho para cancelados
        );

        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.7f);

        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMinimum(0f);

        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        int maxValue = Math.max(Math.max(disponiveis, negociando), Math.max(concluidos, cancelados));
        int labelCount = Math.min(maxValue + 1, 10);
        yAxis.setLabelCount(labelCount, true);

        barChart.getAxisRight().setEnabled(false);

        String[] labels = new String[]{"Disponíveis", "Negociando", "Concluídos", "Cancelados"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.length);

        barChart.setDrawValueAboveBar(true);
        barChart.setFitBars(true);
        barChart.getLegend().setEnabled(false);

        barChart.setExtraOffsets(50f, 10f, 10f, 10f);

        xAxis.setXOffset(10f);
        xAxis.setTextSize(10f);

        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void loadTotalResiduos() {
        Call<TotalResiduosResponse> call = apiService.getTotalResiduos(token);
        call.enqueue(new Callback<TotalResiduosResponse>() {
            @Override
            public void onResponse(Call<TotalResiduosResponse> call, Response<TotalResiduosResponse> response) {
                Log.d(TAG, "Resposta residuos-total: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    int total = response.body().getTotal();
                    totalResiduosText.setText(String.valueOf(total));
                } else {
                    Log.e(TAG, "Erro ao carregar total de resíduos: " + response.code());
                    try {
                        Log.e(TAG, "Erro body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao ler errorBody", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<TotalResiduosResponse> call, Throwable t) {
                Log.e(TAG, "Falha ao carregar total de resíduos: " + t.getMessage(), t);
            }
        });
    }

    private void showLoading(boolean show) {
        loadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}