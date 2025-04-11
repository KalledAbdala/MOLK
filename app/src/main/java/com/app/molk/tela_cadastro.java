package com.app.molk;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.app.molk.R;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;


public class tela_cadastro extends AppCompatActivity {
    private TextInputEditText nomeEditText, emailEditText, passwordEditText, atividadeEditText, empresaEditText, telefoneEditText;
    private MaterialButton cadastrarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);  // ou o nome correto do seu XML

        // Referência aos campos
        nomeEditText = findViewById(R.id.nomeEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        atividadeEditText = findViewById(R.id.atividadeEditText);
        empresaEditText = findViewById(R.id.empresaEditText);
        telefoneEditText = findViewById(R.id.telefoneEditText); // garanta que este ID está no XML

        // Botão (você pode adicionar no final do seu XML um botão com id cadastrarButton)
        Button cadastrarButton = findViewById(R.id.btn_enviar);

        // Clique no botão
        cadastrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = nomeEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String senha = passwordEditText.getText().toString().trim();
                String atividade = atividadeEditText.getText().toString().trim();
                String empresa = empresaEditText.getText().toString().trim();
                String telefone = telefoneEditText.getText().toString().trim();

                // Validação simples
                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(tela_cadastro.this, "Preencha os campos obrigatórios.", Toast.LENGTH_SHORT).show();
                } else {
                    // Aqui você pode salvar no banco de dados, enviar para API, etc.
                    Toast.makeText(tela_cadastro.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}