package com.app.molk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.molk.network.ApiService;
import com.app.molk.network.RetrofitClient;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class cadastrarResiduoActivity extends AppCompatActivity {

    private Uri imageUri;
    private ImageView imagemSelecionada;
    private SharedPreferences sharedPreferences;
    private String token;
    private int usuarioId;
    private static final int REQUEST_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_residuo);

        // Verificar permissões
        verificarPermissoes();

        // Pegando token e id do usuário
        sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);
        usuarioId = sharedPreferences.getInt("id_usuario", -1);

        if (token == null || usuarioId == -1) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar componentes
        Button btnSelecionarImagem = findViewById(R.id.btnSelecionarImagem);
        imagemSelecionada = findViewById(R.id.imagemSelecionada);

        btnSelecionarImagem.setOnClickListener(v -> abrirGaleria());

        Button btnCadastrar = findViewById(R.id.btn_enviar_residuo);
        btnCadastrar.setOnClickListener(v -> {
            String tipoStr = ((EditText) findViewById(R.id.tipo)).getText().toString();
            String descStr = ((EditText) findViewById(R.id.descricao)).getText().toString();
            String qtdStr = ((EditText) findViewById(R.id.quantidade)).getText().toString();
            String formaStr = ((EditText) findViewById(R.id.formaDeDescarte)).getText().toString();

            if (imageUri != null) {
                enviarDadosParaBackend(tipoStr, descStr, qtdStr, formaStr);
            } else {
                Toast.makeText(this, "Selecione uma imagem.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verificarPermissoes() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSION_CODE);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão concedida!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissão negada. Não será possível acessar imagens da galeria.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Selecionar Imagem"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imagemSelecionada.setImageURI(imageUri);
        }
    }

    private void enviarDadosParaBackend(String tipoStr, String descStr, String qtdStr, String formaStr) {
        String realPath = getRealPathFromURI(imageUri);
        if (realPath == null) {
            Toast.makeText(this, "Erro ao processar a imagem.", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(realPath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part imagemPart = MultipartBody.Part.createFormData("imagem_residuo", file.getName(), requestFile);

        RequestBody tipo = RequestBody.create(MediaType.parse("text/plain"), tipoStr);
        RequestBody descricao = RequestBody.create(MediaType.parse("text/plain"), descStr);
        RequestBody quantidade = RequestBody.create(MediaType.parse("text/plain"), qtdStr);
        RequestBody formaDescarte = RequestBody.create(MediaType.parse("text/plain"), formaStr);
        RequestBody tipoEntrega = RequestBody.create(MediaType.parse("text/plain"), "retirada");
        RequestBody usuarioIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(usuarioId));

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        String authHeader = "Bearer " + token;

        Call<ResponseBody> call = apiService.cadastrarResiduo(
                authHeader,
                imagemPart,
                tipo,
                descricao,
                quantidade,
                formaDescarte,
                tipoEntrega,
                usuarioIdPart
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Erro ao cadastrar!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
}
