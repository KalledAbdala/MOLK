package com.app.molk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.molk.network.ApiService;
import com.app.molk.network.RetrofitClient;
import com.google.gson.JsonObject;

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
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        Log.d("LOGIN", "Resposta: " + responseBody);

                        if (responseBody.contains("Usuário não encontrado")) {
                            Toast.makeText(tela_login.this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(tela_login.this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(tela_login.this, ModulosActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (Exception e) {
                        Log.e("LOGIN", "Erro ao ler resposta", e);
                    }
                } else {
                    Toast.makeText(tela_login.this, "Erro: " + response.code(), Toast.LENGTH_SHORT).show();
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
