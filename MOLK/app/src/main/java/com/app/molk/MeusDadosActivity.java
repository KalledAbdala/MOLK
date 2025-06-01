package com.app.molk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.molk.data.models.EmpresaInteresseResponse;
import com.app.molk.data.models.PorcentagemConcluidosResponse;
import com.app.molk.data.models.ResiduosQuantidadeResponse;
import com.app.molk.data.models.TipoResiduo;
import com.app.molk.data.models.TiposQuantidadeResponse;
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

public class MeusDadosActivity extends AppCompatActivity {

    private static final String TAG = "MeusDadosDashboard";

    private PieChart pieChart;
    private HorizontalBarChart barChart;
    private TextView totalResiduosText;
    private TextView txtTituloDashboard;
    private ProgressBar loadingIndicator;

    private LinearLayout containerEmpresas;
    private ProgressBar loadingEmpresasInteresse;
    private TextView txtSemEmpresas;

    private TextView porcentagemConcluidosText;

    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_dados_activity);

        // Inicializar componentes da UI
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        totalResiduosText = findViewById(R.id.totalResiduosText);
        txtTituloDashboard = findViewById(R.id.txtTituloDashboard);
        loadingIndicator = findViewById(R.id.loadingIndicator);

        // Inicializar componentes do card de empresas interessadas
        containerEmpresas = findViewById(R.id.containerEmpresas);
        loadingEmpresasInteresse = findViewById(R.id.loadingEmpresasInteresse);
        txtSemEmpresas = findViewById(R.id.txtSemEmpresas);

        // Atualizar o título para indicar que são meus dados
        txtTituloDashboard.setText("MEUS DADOS");

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
        if (menuIcon != null) {
            menuIcon.setOnClickListener(v -> {
                Intent intent = new Intent(MeusDadosActivity.this, ModulosActivity.class);
                startActivity(intent);
            });
        }

        // Inicializar gráficos vazios para evitar erros
        setupEmptyPieChart();
        setupEmptyBarChart();

        // Carregar dados da dashboard
        loadDashboardData();
    }

    private boolean isActivityActive() {
        return !isFinishing() && !isDestroyed();
    }

    private void loadDashboardData() {
        showLoading(true);

        // Carregar tipos de resíduos para o gráfico de pizza
        loadMeusTiposResiduos();

        // Carregar dados de status dos resíduos para o gráfico de barras
        loadMeusStatusResiduos();

        // Carregar total de resíduos
        loadMeusTotalResiduos();

        // Carregar empresas com interesse nos resíduos
        loadEmpresasInteresse();

    }



    private void loadEmpresasInteresse() {
        if (loadingEmpresasInteresse != null) {
            loadingEmpresasInteresse.setVisibility(View.VISIBLE);
        }
        if (containerEmpresas != null) {
            containerEmpresas.setVisibility(View.GONE);
        }
        if (txtSemEmpresas != null) {
            txtSemEmpresas.setVisibility(View.GONE);
        }

        Call<EmpresaInteresseResponse> call = apiService.getEmpresasInteresseNosMeusResiduos(token);
        call.enqueue(new Callback<EmpresaInteresseResponse>() {
            @Override
            public void onResponse(Call<EmpresaInteresseResponse> call, Response<EmpresaInteresseResponse> response) {
                if (!isActivityActive()) return;

                if (loadingEmpresasInteresse != null) {
                    loadingEmpresasInteresse.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null && response.body().getEmpresas() != null) {
                    List<EmpresaInteresseResponse.EmpresaInteresse> empresas = response.body().getEmpresas();

                    if (empresas.isEmpty()) {
                        if (txtSemEmpresas != null) {
                            txtSemEmpresas.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (containerEmpresas != null) {
                            containerEmpresas.setVisibility(View.VISIBLE);
                            containerEmpresas.removeAllViews(); // Limpa views anteriores

                            for (EmpresaInteresseResponse.EmpresaInteresse empresa : empresas) {
                                addEmpresaView(empresa);
                            }
                        }
                    }
                } else {
                    if (txtSemEmpresas != null) {
                        txtSemEmpresas.setVisibility(View.VISIBLE);
                    }
                    Log.e(TAG, "Erro ao carregar empresas interessadas: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Erro body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao ler errorBody", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<EmpresaInteresseResponse> call, Throwable t) {
                if (!isActivityActive()) return;

                if (loadingEmpresasInteresse != null) {
                    loadingEmpresasInteresse.setVisibility(View.GONE);
                }
                if (txtSemEmpresas != null) {
                    txtSemEmpresas.setVisibility(View.VISIBLE);
                }
                Log.e(TAG, "Falha ao carregar empresas interessadas: " + t.getMessage(), t);
            }
        });
    }

    private void addEmpresaView(EmpresaInteresseResponse.EmpresaInteresse empresa) {
        // Criar layout para cada empresa
        LinearLayout empresaLayout = new LinearLayout(this);
        empresaLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        empresaLayout.setOrientation(LinearLayout.HORIZONTAL);
        empresaLayout.setPadding(0, 8, 0, 8);

        // Nome da empresa
        TextView txtNome = new TextView(this);
        LinearLayout.LayoutParams nomeParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 3f);
        txtNome.setLayoutParams(nomeParams);
        txtNome.setText(empresa.getNomeEmpresa());
        txtNome.setTextColor(Color.BLACK);
        txtNome.setTextSize(14);

        // Quantidade de interesse
        TextView txtQuantidade = new TextView(this);
        LinearLayout.LayoutParams qtdParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        txtQuantidade.setLayoutParams(qtdParams);
        txtQuantidade.setText(String.valueOf(empresa.getQuantidadeInteresse()));
        txtQuantidade.setTextColor(Color.BLACK);
        txtQuantidade.setTextSize(14);
        txtQuantidade.setGravity(Gravity.END);

        // Adicionar views ao layout da empresa
        empresaLayout.addView(txtNome);
        empresaLayout.addView(txtQuantidade);

        // Adicionar linha divisória
        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(Color.LTGRAY);

        // Adicionar ao container principal
        containerEmpresas.addView(empresaLayout);
        containerEmpresas.addView(divider);
    }

    private void loadMeusTiposResiduos() {
        Log.d(TAG, "Iniciando chamada para getMeusTiposQuantidade");

        Call<TiposQuantidadeResponse> call = apiService.getMeusTiposQuantidade(token);
        call.enqueue(new Callback<TiposQuantidadeResponse>() {
            @Override
            public void onResponse(Call<TiposQuantidadeResponse> call, Response<TiposQuantidadeResponse> response) {
                if (!isActivityActive()) return;

                Log.d(TAG, "Resposta recebida: " + response.code());
                Log.d(TAG, "URL chamada: " + call.request().url());

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getTipos() != null) {
                        List<TipoResiduo> tipos = response.body().getTipos();
                        Log.d(TAG, "Dados recebidos: " + tipos.size() + " tipos");

                        // Log detalhado de cada tipo recebido
                        for (TipoResiduo tipo : tipos) {
                            Log.d(TAG, "Tipo: " + tipo.getTipo() + ", Quantidade: " + tipo.getQuantidade());
                        }

                        if (tipos.isEmpty()) {
                            Log.d(TAG, "Lista de tipos vazia - usuário não tem resíduos cadastrados");
                            Toast.makeText(MeusDadosActivity.this, "Você não possui resíduos cadastrados", Toast.LENGTH_SHORT).show();
                            setupEmptyPieChart();
                        } else {
                            // Configurar o gráfico com os dados recebidos
                            setupPieChart(tipos);
                        }
                    } else {
                        Log.e(TAG, "Corpo da resposta é nulo ou não contém tipos");
                        Toast.makeText(MeusDadosActivity.this, "Erro ao carregar tipos de resíduos: resposta vazia", Toast.LENGTH_SHORT).show();
                        setupEmptyPieChart();
                    }
                } else {
                    Toast.makeText(MeusDadosActivity.this, "Erro ao carregar tipos de resíduos", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Erro: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Corpo vazio";
                        Log.e(TAG, "Erro body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao ler errorBody", e);
                    }
                    setupEmptyPieChart();
                }
                showLoading(false);
            }

            @Override
            public void onFailure(Call<TiposQuantidadeResponse> call, Throwable t) {
                if (!isActivityActive()) return;

                Log.e(TAG, "URL chamada: " + call.request().url());
                Log.e(TAG, "Falha: " + t.getMessage(), t);
                Toast.makeText(MeusDadosActivity.this, "Falha na conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                setupEmptyPieChart();
                showLoading(false);
            }
        });
    }

    // Método para configurar o gráfico de pizza com os dados recebidos
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

    // Método para configurar um gráfico de pizza vazio ou com dados de exemplo
    private void setupEmptyPieChart() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(1, "Sem dados"));

        PieDataSet dataSet = new PieDataSet(entries, "Sem dados disponíveis");
        dataSet.setColors(Color.GRAY);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.animate();

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        pieChart.invalidate();
    }

    // Método para configurar um gráfico de barras vazio
    private void setupEmptyBarChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 0));
        entries.add(new BarEntry(1, 0));
        entries.add(new BarEntry(2, 0));
        entries.add(new BarEntry(3, 0));

        BarDataSet dataSet = new BarDataSet(entries, "Status dos Resíduos");
        dataSet.setColors(Color.GRAY);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.7f);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMinimum(0f);

        barChart.getAxisRight().setEnabled(false);

        String[] labels = new String[]{"Disponíveis", "Negociando", "Concluídos", "Cancelados"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.length);

        barChart.setFitBars(true);
        barChart.getLegend().setEnabled(false);
        barChart.invalidate();
    }

    private void loadMeusStatusResiduos() {
        // Criar um contador para verificar quando todas as chamadas foram concluídas
        final int[] callsCompleted = {0};
        final int totalCalls = 4;

        final int[] disponiveis = {0};
        final int[] negociando = {0};
        final int[] concluidos = {0};
        final int[] cancelados = {0};

        // Meus resíduos disponíveis
        Call<ResiduosQuantidadeResponse> callDisponiveis = apiService.getMeusResiduosDisponiveis(token);
        callDisponiveis.enqueue(new Callback<ResiduosQuantidadeResponse>() {
            @Override
            public void onResponse(Call<ResiduosQuantidadeResponse> call, Response<ResiduosQuantidadeResponse> response) {
                if (!isActivityActive()) return;

                Log.d(TAG, "Resposta meus-residuos-disponiveis: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    disponiveis[0] = response.body().getQuantidade();
                }
                checkAndSetupBarChart(++callsCompleted[0], totalCalls, disponiveis[0], negociando[0], concluidos[0], cancelados[0]);
            }

            @Override
            public void onFailure(Call<ResiduosQuantidadeResponse> call, Throwable t) {
                if (!isActivityActive()) return;

                Log.e(TAG, "Falha ao carregar meus resíduos disponíveis: " + t.getMessage(), t);
                checkAndSetupBarChart(++callsCompleted[0], totalCalls, disponiveis[0], negociando[0], concluidos[0], cancelados[0]);
            }
        });

        // Meus resíduos negociando
        Call<ResiduosQuantidadeResponse> callNegociando = apiService.getMeusResiduosNegociando(token);
        callNegociando.enqueue(new Callback<ResiduosQuantidadeResponse>() {
            @Override
            public void onResponse(Call<ResiduosQuantidadeResponse> call, Response<ResiduosQuantidadeResponse> response) {
                if (!isActivityActive()) return;

                Log.d(TAG, "Resposta meus-residuos-negociando: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    negociando[0] = response.body().getQuantidade();
                }
                checkAndSetupBarChart(++callsCompleted[0], totalCalls, disponiveis[0], negociando[0], concluidos[0], cancelados[0]);
            }

            @Override
            public void onFailure(Call<ResiduosQuantidadeResponse> call, Throwable t) {
                if (!isActivityActive()) return;

                Log.e(TAG, "Falha ao carregar meus resíduos negociando: " + t.getMessage(), t);
                checkAndSetupBarChart(++callsCompleted[0], totalCalls, disponiveis[0], negociando[0], concluidos[0], cancelados[0]);
            }
        });

        // Meus resíduos concluídos
        Call<ResiduosQuantidadeResponse> callConcluidos = apiService.getMeusResiduosConcluidos(token);
        callConcluidos.enqueue(new Callback<ResiduosQuantidadeResponse>() {
            @Override
            public void onResponse(Call<ResiduosQuantidadeResponse> call, Response<ResiduosQuantidadeResponse> response) {
                if (!isActivityActive()) return;

                Log.d(TAG, "Resposta meus-residuos-concluidos: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    concluidos[0] = response.body().getQuantidade();
                }
                checkAndSetupBarChart(++callsCompleted[0], totalCalls, disponiveis[0], negociando[0], concluidos[0], cancelados[0]);
            }

            @Override
            public void onFailure(Call<ResiduosQuantidadeResponse> call, Throwable t) {
                if (!isActivityActive()) return;

                Log.e(TAG, "Falha ao carregar meus resíduos concluídos: " + t.getMessage(), t);
                checkAndSetupBarChart(++callsCompleted[0], totalCalls, disponiveis[0], negociando[0], concluidos[0], cancelados[0]);
            }
        });

        // Meus resíduos cancelados
        Call<ResiduosQuantidadeResponse> callCancelados = apiService.getMeusResiduosCancelados(token);
        callCancelados.enqueue(new Callback<ResiduosQuantidadeResponse>() {
            @Override
            public void onResponse(Call<ResiduosQuantidadeResponse> call, Response<ResiduosQuantidadeResponse> response) {
                if (!isActivityActive()) return;

                Log.d(TAG, "Resposta meus-residuos-cancelados: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    cancelados[0] = response.body().getQuantidade();
                }
                checkAndSetupBarChart(++callsCompleted[0], totalCalls, disponiveis[0], negociando[0], concluidos[0], cancelados[0]);
            }

            @Override
            public void onFailure(Call<ResiduosQuantidadeResponse> call, Throwable t) {
                if (!isActivityActive()) return;

                Log.e(TAG, "Falha ao carregar meus resíduos cancelados: " + t.getMessage(), t);
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

        BarDataSet dataSet = new BarDataSet(entries, "Status dos Meus Resíduos");

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
        if (labelCount <= 0) labelCount = 1; // Garantir pelo menos um rótulo
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

    private void loadMeusTotalResiduos() {
        Call<TotalResiduosResponse> call = apiService.getMeusTotalResiduos(token);
        call.enqueue(new Callback<TotalResiduosResponse>() {
            @Override
            public void onResponse(Call<TotalResiduosResponse> call, Response<TotalResiduosResponse> response) {
                if (!isActivityActive()) return;

                Log.d(TAG, "Resposta meus-residuos-total: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    int total = response.body().getTotal();
                    totalResiduosText.setText(String.valueOf(total));
                } else {
                    Log.e(TAG, "Erro ao carregar total de meus resíduos: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Erro body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao ler errorBody", e);
                    }
                    totalResiduosText.setText("0");
                }
            }

            @Override
            public void onFailure(Call<TotalResiduosResponse> call, Throwable t) {
                if (!isActivityActive()) return;

                Log.e(TAG, "Falha ao carregar total de meus resíduos: " + t.getMessage(), t);
                totalResiduosText.setText("0");
            }
        });
    }

    private void showLoading(boolean show) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancelar chamadas pendentes se necessário
    }
}