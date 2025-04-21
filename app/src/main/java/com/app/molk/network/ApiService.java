package com.app.molk.network;
import com.app.molk.data.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/cadastrar") // Rota para o cadastro
    Call<Void> cadastrarUsuario(@Body User user);
}
