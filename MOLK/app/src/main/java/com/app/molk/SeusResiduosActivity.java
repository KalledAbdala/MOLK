package com.app.molk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.molk.data.models.Residuo;
import com.app.molk.network.ApiService;
import com.app.molk.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        btnAdicionarResiduo.setOnClickListener(v -> {
            Intent intent = new Intent(SeusResiduosActivity.this, cadastrarResiduoActivity.class);
            startActivity(intent);
        });

        // Chamada da API para buscar resíduos
        carregarResiduosDoUsuario();

        // chamada do token
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("auth_token", "");

        if (token == null) {
            Toast.makeText(this, "Token não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        token = "Bearer " + token;

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Residuo>> call = apiService.getResiduosDoUsuario(token);

    }

    private void carregarResiduosDoUsuario() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<List<Residuo>> call = apiService.getResiduosDoUsuario(token);
        call.enqueue(new Callback<List<Residuo>>() {
            @Override
            public void onResponse(Call<List<Residuo>> call, Response<List<Residuo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Residuo> residuos = response.body();
                    for (Residuo residuo : residuos) {
                        adicionarCardResiduo(residuo);
                    }
                } else {
                    Toast.makeText(SeusResiduosActivity.this, "Erro ao carregar resíduos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Residuo>> call, Throwable t) {
                Toast.makeText(SeusResiduosActivity.this, "Falha na conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void adicionarCardResiduo(Residuo residuo) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.card_residuo, null);

        TextView tipoText = cardView.findViewById(R.id.tipoResiduo);
        TextView descricaoText = cardView.findViewById(R.id.descricaoResiduo);
        ImageView imagemView = cardView.findViewById(R.id.imagemResiduo);

        tipoText.setText(residuo.getTipo());
        descricaoText.setText(residuo.getDescricao());

        // Supondo que a imagem esteja como Base64
        if (residuo.getImagemBase64() != null && !residuo.getImagemBase64().isEmpty()) {
            byte[] imageBytes = Base64.decode(residuo.getImagemBase64(), Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imagemView.setImageBitmap(decodedImage);
        }

        LinearLayout container = findViewById(R.id.scrollContent);
        container.addView(cardView);
    }
}
