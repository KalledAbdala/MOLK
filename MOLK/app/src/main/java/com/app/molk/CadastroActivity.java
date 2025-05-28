package com.app.molk;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.molk.data.models.CadastroResponse;
import com.app.molk.data.models.User;
import com.app.molk.network.ApiService;
import com.app.molk.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroActivity extends AppCompatActivity {

    // Agora todos os campos são EditText
    private EditText nomeEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText atividadeEditText;
    private EditText empresaEditText;
    private EditText telefoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        // Inicializa os campos corretamente
        nomeEditText = findViewById(R.id.nomeEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        atividadeEditText = findViewById(R.id.atividadeEditText);
        empresaEditText = findViewById(R.id.empresaEditText);
        telefoneEditText = findViewById(R.id.telefoneEditText);

        Button enviarButton = findViewById(R.id.btn_enviar);
        enviarButton.setOnClickListener(v -> enviarDados());
    }

    private void enviarDados() {
        String nome = nomeEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String senha = passwordEditText.getText().toString();
        String empresa = empresaEditText.getText().toString().trim();
        String telefone = telefoneEditText.getText().toString().trim();

        User user = new User();
        user.setNome(nome);
        user.setEmail(email);
        user.setSenha(senha);
        user.setNome_empresa(empresa);
        user.setTelefone(telefone);

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<CadastroResponse> call = apiService.cadastrarUsuario(user);

        call.enqueue(new Callback<CadastroResponse>() {
            @Override
            public void onResponse(Call<CadastroResponse> call, Response<CadastroResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String mensagem = response.body().getMessage();
                    Toast.makeText(CadastroActivity.this, mensagem, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CadastroActivity.this, "Erro ao cadastrar. Código: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CadastroResponse> call, Throwable t) {
                Toast.makeText(CadastroActivity.this, "Falha na comunicação: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
