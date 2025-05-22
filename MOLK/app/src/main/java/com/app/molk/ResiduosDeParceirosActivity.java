package com.app.molk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.app.molk.data.models.ConectarResiduoRequest;
import com.app.molk.data.models.ResiduoParceiro;
import com.app.molk.data.models.ResiduosParceirosResponse;
import com.app.molk.network.ApiService;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResiduosDeParceirosActivity extends AppCompatActivity {

    EditText editTextFiltroParceiros;
    Button btnFiltrarParceiros;
    LinearLayout scrollContent;

    List<ResiduoParceiro> listaResiduosParceiros = new ArrayList<>();

    private ApiService apiService;

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

        editTextFiltroParceiros = findViewById(R.id.editTextFiltroParceiros);
        btnFiltrarParceiros = findViewById(R.id.btnFiltrarParceiros);
        scrollContent = findViewById(R.id.scrollContent);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().build())
                .build();

        apiService = retrofit.create(ApiService.class);

        buscarResiduosDoBackend();

        btnFiltrarParceiros.setOnClickListener(v -> {
            String filtro = editTextFiltroParceiros.getText().toString().trim();
            filtrarResiduosParceiros(filtro);
        });
    }

    private void buscarResiduosDoBackend() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", "");

        if (token.isEmpty()) {
            Log.e("API_TOKEN", "Token não encontrado!");
            return;
        }

        String authHeader = "Bearer " + token;

        Call<ResiduosParceirosResponse> call = apiService.getResiduosParceiros(authHeader);

        call.enqueue(new Callback<ResiduosParceirosResponse>() {
            @Override
            public void onResponse(Call<ResiduosParceirosResponse> call, Response<ResiduosParceirosResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaResiduosParceiros = response.body().residuos;
                    runOnUiThread(() -> exibirResiduos(listaResiduosParceiros));
                } else {
                    Log.e("API_RESPONSE", "Erro: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResiduosParceirosResponse> call, Throwable t) {
                Log.e("API_ERROR", "Falha: " + t.getMessage(), t);
            }
        });
    }

    private void filtrarResiduosParceiros(String filtro) {
        List<ResiduoParceiro> listaFiltrada = new ArrayList<>();

        for (ResiduoParceiro residuo : listaResiduosParceiros) {
            if (residuo.tipo != null && residuo.tipo.toLowerCase().contains(filtro.toLowerCase())) {
                listaFiltrada.add(residuo);
            }
        }

        exibirResiduos(listaFiltrada);
    }

    private void exibirResiduos(List<ResiduoParceiro> residuos) {
        scrollContent.removeAllViews();

        for (ResiduoParceiro residuo : residuos) {
            View cardView = LayoutInflater.from(this).inflate(R.layout.card_residuo_parceiro, scrollContent, false);

            TextView nomeEmpresa = cardView.findViewById(R.id.nomeEmpresa);
            TextView txtTipo = cardView.findViewById(R.id.tipoResiduo);
            ImageView imagemResiduo = cardView.findViewById(R.id.imagemResiduo);
            TextView txtDescricao = cardView.findViewById(R.id.descricaoResiduo);
            Button btnConectar = cardView.findViewById(R.id.btnConectar);

            nomeEmpresa.setText(residuo.usuario != null && residuo.usuario.nome_empresa != null
                    ? residuo.usuario.nome_empresa
                    : "Empresa não disponível");

            txtTipo.setText(residuo.tipo != null ? residuo.tipo : "");
            txtDescricao.setText(residuo.descricao != null ? residuo.descricao : "");

            if (residuo.imagem_residuo != null && residuo.imagem_residuo.data != null && !residuo.imagem_residuo.data.isEmpty()) {
                try {
                    StringBuilder nomeArquivoBuilder = new StringBuilder();
                    for (Integer b : residuo.imagem_residuo.data) {
                        nomeArquivoBuilder.append((char) b.intValue());
                    }
                    String nomeArquivo = nomeArquivoBuilder.toString();

                    String url = "http://10.0.2.2:3000/uploads/" + nomeArquivo;

                    Glide.with(this)
                            .load(url)
                            .placeholder(R.drawable.carregando_img)
                            .error(R.drawable.erro_img)
                            .into(imagemResiduo);

                } catch (Exception e) {
                    Log.e("IMAGEM", "Erro ao carregar imagem", e);
                    imagemResiduo.setImageResource(R.drawable.erro_img);
                }
            } else {
                imagemResiduo.setImageResource(R.drawable.erro_img);
            }

            btnConectar.setOnClickListener(v -> conectarComResiduo(residuo.id));

            scrollContent.addView(cardView);
        }
    }

    private void conectarComResiduo(int residuoId) {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", "");

        if (token.isEmpty()) {
            Log.e("API_TOKEN", "Token não encontrado!");
            return;
        }

        String authHeader = "Bearer " + token;

        ConectarResiduoRequest request = new ConectarResiduoRequest(residuoId);

        Call<Void> call = apiService.conectarResiduo(authHeader, request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i("API_CONNECT", "Conectado com sucesso ao resíduo " + residuoId);
                    runOnUiThread(() -> Toast.makeText(ResiduosDeParceirosActivity.this,
                            "Conectado com sucesso!", Toast.LENGTH_SHORT).show());
                } else {
                    Log.e("API_CONNECT", "Erro ao conectar: " + response.code() + " - " + response.message());
                    runOnUiThread(() -> Toast.makeText(ResiduosDeParceirosActivity.this,
                            "Erro ao conectar: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_CONNECT", "Falha: " + t.getMessage(), t);
                runOnUiThread(() -> Toast.makeText(ResiduosDeParceirosActivity.this,
                        "Falha na conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}
