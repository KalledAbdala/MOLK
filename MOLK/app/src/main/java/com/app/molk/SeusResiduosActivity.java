package com.app.molk;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.molk.data.models.NovoStatusBody;
import com.app.molk.data.models.Residuo;
import com.app.molk.data.models.ResiduosResponse;
import com.app.molk.network.ApiService;
import com.app.molk.network.RetrofitClient;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeusResiduosActivity extends AppCompatActivity {

    private String token;
    private LinearLayout containerResiduos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seus_residuos);

        containerResiduos = findViewById(R.id.containerSeusResiduos);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String rawToken = prefs.getString("auth_token", null);

        if (rawToken == null || rawToken.trim().isEmpty()) {
            Toast.makeText(this, "Token não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, tela_login.class));
            finish();
            return;
        }

        rawToken = rawToken.trim();
        token = "Bearer " + rawToken;
        Log.i("TOKEN_DEBUG", "Token usado: " + token);

        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> {
            Intent intent = new Intent(SeusResiduosActivity.this, ModulosActivity.class);
            startActivity(intent);
        });

        Button btnAdicionarResiduo = findViewById(R.id.btnAdicionarResiduo);
        btnAdicionarResiduo.setOnClickListener(v -> {
            Intent intent = new Intent(SeusResiduosActivity.this, cadastrarResiduoActivity.class);
            startActivity(intent);
        });

        carregarResiduosDoUsuario();
    }

    private void carregarResiduosDoUsuario() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<ResiduosResponse> call = apiService.getResiduosDoUsuario(token);
        call.enqueue(new Callback<ResiduosResponse>() {
            @Override
            public void onResponse(Call<ResiduosResponse> call, Response<ResiduosResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Residuo> residuos = response.body().getResiduos();
                    Log.d("API_RESPONSE", new Gson().toJson(residuos));

                    containerResiduos.removeAllViews();

                    for (Residuo residuo : residuos) {
                        adicionarCardResiduo(residuo);
                    }
                } else {
                    int codigo = response.code();
                    String erroBody = "";
                    try {
                        erroBody = response.errorBody() != null ? response.errorBody().string() : "null";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(SeusResiduosActivity.this,
                            "Erro ao carregar resíduos. Código: " + codigo,
                            Toast.LENGTH_LONG).show();
                    Log.i("ERRO_API", "Erro ao carregar resíduos. Código: " + codigo + " - Body: " + erroBody);
                }
            }

            @Override
            public void onFailure(Call<ResiduosResponse> call, Throwable t) {
                Toast.makeText(SeusResiduosActivity.this, "Falha na conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void adicionarCardResiduo(Residuo residuo) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.card_residuo, null);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 24);
        cardView.setLayoutParams(layoutParams);

        TextView tipoText = cardView.findViewById(R.id.tipoResiduo);
        TextView descricaoText = cardView.findViewById(R.id.descricaoResiduo);
        ImageView imagemView = cardView.findViewById(R.id.imagemResiduo);

        tipoText.setText(residuo.getTipo());
        descricaoText.setText(residuo.getDescricao());

        if (residuo.getImagem_residuo() != null && residuo.getImagem_residuo().getData() != null) {
            try {
                String nomeArquivo = residuo.getImagem_residuo().getNomeArquivo();
                String url = "http://10.0.2.2:3000/uploads/" + nomeArquivo;

                Glide.with(this)
                        .load(url)
                        .placeholder(R.drawable.carregando_img)
                        .error(R.drawable.erro_img)
                        .into(imagemView);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show();
            }
        }

        Button btnStatus = cardView.findViewById(R.id.btnStatus);

        String status = residuo.getStatus_residuo();
        if (status != null) {
            btnStatus.setText(status);
        } else {
            btnStatus.setText("Sem status");
        }

        btnStatus.setOnClickListener(v -> {
            final String[] opcoes = {"disponivel", "negociando", "cancelado", "concluido"};

            new android.app.AlertDialog.Builder(SeusResiduosActivity.this)
                    .setTitle("Selecione o novo status")
                    .setItems(opcoes, (dialog, which) -> {
                        String novoStatus = opcoes[which];
                        atualizarStatusResiduo(residuo.getId(), novoStatus, btnStatus);
                    })
                    .show();
        });

        // **Adicione esta linha para mostrar o card na tela:**
        containerResiduos.addView(cardView);
    }


    // ✅ Atualizar status via API com @Body
    private void atualizarStatusResiduo(int idResiduo, String novoStatus, Button btnStatus) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        NovoStatusBody statusBody = new NovoStatusBody(novoStatus);

        Call<Void> call = apiService.atualizarStatusResiduo(token, idResiduo, statusBody);


  // ✅ Passa como body
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SeusResiduosActivity.this, "Status atualizado!", Toast.LENGTH_SHORT).show();
                    btnStatus.setText(novoStatus);
                } else {
                    Toast.makeText(SeusResiduosActivity.this, "Erro ao atualizar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SeusResiduosActivity.this, "Falha na atualização: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
