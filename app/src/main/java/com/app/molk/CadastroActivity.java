package com.app.molk;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.molk.R;
import com.app.molk.data.models.User;
import com.app.molk.network.ApiService;
import com.app.molk.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroActivity extends AppCompatActivity {

    // Declaração dos campos
    private TextInputEditText nomeEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText atividadeEditText;
    private TextInputEditText empresaEditText;
    private TextInputEditText telefoneEditText; // supondo que existe esse campo no final do XML

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro); // nome do seu layout XML

        // Inicializa os campos ligando ao XML
        nomeEditText = findViewById(R.id.nomeEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        atividadeEditText = findViewById(R.id.atividadeEditText);
        empresaEditText = findViewById(R.id.empresaEditText);
        telefoneEditText = findViewById(R.id.telefoneEditText);

        // Configura o botão de envio
        Button enviarButton = findViewById(R.id.btn_enviar);
        enviarButton.setOnClickListener(v -> enviarDados());

    }

    // Exemplo de função que pode ser usada para pegar os dados
    private void enviarDados() {
        String nome = nomeEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String senha = passwordEditText.getText().toString();
        String atividade = atividadeEditText.getText().toString().trim();
        String empresa = empresaEditText.getText().toString().trim();
        String telefone = telefoneEditText.getText().toString().trim();

        // Cria o objeto User com os dados coletados
        User user = new User();
        user.setNome(nome);
        user.setEmail(email);
        user.setSenha(senha);
        user.setResiduo(atividade);  // Aqui está o campo "atividade"
        user.setNome_empresa(empresa);
        user.setTelefone(telefone);

        // Faz a requisição à API para cadastrar o usuário
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<Void> call = apiService.cadastrarUsuario(user);

        // Envia os dados para a API
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Sucesso, faça algo, como mostrar uma mensagem ou redirecionar
                    Toast.makeText(CadastroActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    // Erro ao enviar dados
                    Toast.makeText(CadastroActivity.this, "Erro ao cadastrar, tente novamente.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Falha na requisição
                Toast.makeText(CadastroActivity.this, "Falha na comunicação com o servidor.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
