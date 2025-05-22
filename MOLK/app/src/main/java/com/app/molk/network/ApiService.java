package com.app.molk.network;

import com.app.molk.data.models.CadastroResponse;
import com.app.molk.data.models.ConectarResiduoRequest;
import com.app.molk.data.models.Entregas;
import com.app.molk.data.models.NovoStatusBody;
import com.app.molk.data.models.ResiduosParceirosResponse;
import com.app.molk.data.models.ResiduosResponse;
import com.app.molk.data.models.User;
import com.google.gson.JsonObject;


import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;


public interface ApiService {

    @POST("usuarios/cadastrar")
    Call<CadastroResponse> cadastrarUsuario(@Body User user);

    @POST("usuarios/login")
    Call<ResponseBody> login(@Body JsonObject loginData);

    @Multipart
    @POST("residuos/cadastrar")
    Call<ResponseBody> cadastrarResiduo(
            @Header("Authorization") String authHeader,
            @Part MultipartBody.Part imagem,
            @Part("tipo") RequestBody tipo,
            @Part("descricao") RequestBody descricao,
            @Part("quantidade") RequestBody quantidade,
            @Part("forma_descarte") RequestBody formaDescarte,
            @Part("tipo_entrega") RequestBody tipoEntrega,
            @Part("id_usuario") RequestBody usuarioId
    );

    @GET("residuos/seus")
    Call<ResiduosResponse> getResiduosDoUsuario(@Header("Authorization") String token);

    @PUT("residuos/atualizar/{id}")
    Call<Void> atualizarStatusResiduo(
            @Header("Authorization") String token,
            @Path("id") int idResiduo,
            @Body NovoStatusBody statusBody
    );


    @GET("/residuos/outsiders")
    Call<ResiduosParceirosResponse> getResiduosParceiros(@Header("Authorization") String token);


    @POST("residuos/conectar")
    Call<Void> conectarResiduo(
            @Header("Authorization") String authHeader,
            @Body ConectarResiduoRequest request
    );

    @GET("/entregas/empresa")
    Call<List<Entregas>> getEntregasEmpresa(@Header("Authorization") String token);

    @GET("/entregas/negociando")
    Call<List<Entregas>> getEntregasNegociando(@Header("Authorization") String token);

}
