package com.app.molk;

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

import com.app.molk.data.models.Residuo;
import com.app.molk.data.models.ResiduosResponse;
import com.app.molk.network.ApiService;
import com.app.molk.network.RetrofitClient;
import com.bumptech.glide.Glide;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeusResiduosActivity extends AppCompatActivity {

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seus_residuos);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String rawToken = prefs.getString("auth_token", null);

        if (rawToken == null || rawToken.trim().isEmpty()) {
            Toast.makeText(this, "Token não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, tela_login.class));
            finish();
            return;
        }

        // Remove espaços em branco no início/fim do token para evitar erro de assinatura
        rawToken = rawToken.trim();

        // Monta o header Authorization corretamente
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

                // IMPORTANTE: "localhost" no Android NÃO funciona para acessar seu backend local.
                // Substitua "localhost" pelo IP da sua máquina na rede local, por exemplo:
                // String url = "http://192.168.0.100:3000/uploads/" + nomeArquivo;
                // Ou use "10.0.2.2" se estiver usando emulador Android Studio
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

        LinearLayout container = findViewById(R.id.scrollContent);
        container.addView(cardView);
    }
}
