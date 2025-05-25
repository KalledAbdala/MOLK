package com.app.molk;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.molk.data.models.OutrosUsuariosResponse;
import com.app.molk.data.models.User;
import com.app.molk.network.ApiService;
import com.app.molk.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeusParceirosActivity extends AppCompatActivity {

    private String token;
    private LinearLayout containerParceiros;
    private TextInputEditText editTextFiltroParceiros;

    private List<User> todosParceiros = new ArrayList<>(); // lista completa para filtro

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seus_parceiros);

        containerParceiros = findViewById(R.id.scrollContent);
        editTextFiltroParceiros = findViewById(R.id.editTextFiltroParceiros);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String rawToken = prefs.getString("auth_token", null);

        if (rawToken == null || rawToken.trim().isEmpty()) {
            Toast.makeText(this, "Token não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, tela_login.class));
            finish();
            return;
        }

        token = "Bearer " + rawToken.trim();
        Log.i("TOKEN_DEBUG", "Token usado: " + token);

        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> {
            Intent intent = new Intent(SeusParceirosActivity.this, ModulosActivity.class);
            startActivity(intent);
        });

        carregarParceiros();

        // Listener para buscar dinamicamente
        editTextFiltroParceiros.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarParceiros(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void carregarParceiros() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        Call<OutrosUsuariosResponse> call = apiService.getUsuariosParceiros(token);
        call.enqueue(new Callback<OutrosUsuariosResponse>() {
            @Override
            public void onResponse(Call<OutrosUsuariosResponse> call, Response<OutrosUsuariosResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> parceiros = response.body().getUsuarios();
                    if (parceiros != null) {
                        todosParceiros.clear();
                        todosParceiros.addAll(parceiros);
                        filtrarParceiros(editTextFiltroParceiros.getText().toString()); // já filtra caso tenha algo digitado
                    }
                } else {
                    Toast.makeText(SeusParceirosActivity.this, "Erro ao carregar parceiros.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OutrosUsuariosResponse> call, Throwable t) {
                Toast.makeText(SeusParceirosActivity.this, "Erro de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filtrarParceiros(String texto) {
        containerParceiros.removeAllViews();
        String busca = texto.toLowerCase().trim();

        for (User parceiro : todosParceiros) {
            if (parceiro.getNome_empresa().toLowerCase().contains(busca)) {
                adicionarCardParceiro(parceiro);
            }
        }

        if (containerParceiros.getChildCount() == 0) {
            TextView vazio = new TextView(this);
            vazio.setText("Nenhum parceiro encontrado.");
            vazio.setTextSize(16);
            vazio.setTextColor(getResources().getColor(android.R.color.darker_gray));
            vazio.setPadding(16, 32, 16, 32);
            containerParceiros.addView(vazio);
        }
    }

    private void adicionarCardParceiro(User parceiro) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.card_parceiro, null);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 24);
        cardView.setLayoutParams(layoutParams);

        TextView nomeEmpresaText = cardView.findViewById(R.id.nomeParceiro);
        Button btnVerDetalhes = cardView.findViewById(R.id.btnVerDetalhes);

        nomeEmpresaText.setText(parceiro.getNome_empresa());

        btnVerDetalhes.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(parceiro.getNome_empresa())
                    .setMessage("Nome do responsável: " + parceiro.getNome() + "\n" +
                            "Email: " + parceiro.getEmail() + "\n" +
                            "Telefone: " + parceiro.getTelefone())
                    .setPositiveButton("Fechar", null)
                    .show();
        });

        containerParceiros.addView(cardView);
    }
}
