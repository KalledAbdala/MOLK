package com.app.molk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;

import com.app.molk.network.ApiService;
import com.app.molk.network.RetrofitClient;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class tela_login extends AppCompatActivity {

    private EditText edtEmail, edtSenha;
    private Button btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.editTextTextEmailAddress);
        edtSenha = findViewById(R.id.editTextTextPassword);
        btnEntrar = findViewById(R.id.button);

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fazerLogin();
            }
        });
    }

    private void fazerLogin() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        JsonObject loginData = new JsonObject();
        loginData.addProperty("email", email);
        loginData.addProperty("senha", senha);

        Call<ResponseBody> call = apiService.login(loginData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        Log.d("LOGIN", "Resposta: " + responseBody);

                        JSONObject json = new JSONObject(responseBody);

                        String tokenRecebido = json.getString("token");
                        int userId = json.getJSONObject("usuario").getInt("id");

                        // Salvar token e id_usuario usando as mesmas chaves e nome da SharedPreferences do SeusResiduosActivity
                        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("auth_token", tokenRecebido.trim());
                        editor.putInt("id_usuario", userId);
                        editor.apply();

                        Toast.makeText(tela_login.this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(tela_login.this, ModulosActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (Exception e) {
                        Log.e("LOGIN", "Erro ao processar resposta JSON", e);
                        Toast.makeText(tela_login.this, "Erro ao processar resposta do servidor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(tela_login.this, "Credenciais inválidas ou erro no servidor", Toast.LENGTH_SHORT).show();
                    Log.e("LOGIN", "Erro: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("LOGIN", "Falha na conexão: " + t.getMessage(), t);
                Toast.makeText(tela_login.this, "Erro de conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
