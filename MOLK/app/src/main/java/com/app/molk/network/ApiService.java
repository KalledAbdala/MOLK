package com.app.molk.network;

import com.app.molk.data.models.CadastroResponse;
import com.app.molk.data.models.ConectarResiduoRequest;
import com.app.molk.data.models.DashboardResponse;
import com.app.molk.data.models.EmpresaInteresseResponse;
import com.app.molk.data.models.EmpresaResponse;
import com.app.molk.data.models.Entregas;
import com.app.molk.data.models.MeusResiduosResponse;
import com.app.molk.data.models.NovoStatusBody;
import com.app.molk.data.models.OutrosUsuariosResponse;
import com.app.molk.data.models.PorcentagemConcluidosResponse;
import com.app.molk.data.models.ResiduosParceirosResponse;
import com.app.molk.data.models.ResiduosQuantidadeResponse;
import com.app.molk.data.models.ResiduosResponse;
import com.app.molk.data.models.TipoResiduo;
import com.app.molk.data.models.TiposQuantidadeResponse;
import com.app.molk.data.models.TotalParceirosResponse;
import com.app.molk.data.models.TotalResiduosResponse;
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

    // Métodos existentes...
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

    @GET("usuarios/outsiders")
    Call<OutrosUsuariosResponse> getUsuariosParceiros(@Header("Authorization") String token);

    // Métodos atualizados da dashboard com os endpoints corretos
    @GET("dashboard/total-parceiros")
    Call<TotalParceirosResponse> getTotalParceiros(@Header("Authorization") String token);

    @GET("dashboard/residuos-disponiveis")
    Call<ResiduosQuantidadeResponse> getResiduosDisponiveis(@Header("Authorization") String token);

    @GET("dashboard/residuos-negociando")
    Call<ResiduosQuantidadeResponse> getResiduosNegociando(@Header("Authorization") String token);

    @GET("dashboard/residuos-concluidos")
    Call<ResiduosQuantidadeResponse> getResiduosConcluidos(@Header("Authorization") String token);

    @GET("dashboard/residuos-cancelados")
    Call<ResiduosQuantidadeResponse> getResiduosCancelados(@Header("Authorization") String token);

    @GET("dashboard/residuos-total")
    Call<TotalResiduosResponse> getTotalResiduos(@Header("Authorization") String token);

    @GET("dashboard/residuos-tipos-quantidade")
    Call<List<TipoResiduo>> getTiposQuantidade(@Header("Authorization") String token);

    @GET("dashboard/empresa-mais-residuos")
    Call<EmpresaResponse> getEmpresaMaisResiduos(@Header("Authorization") String token);

    @GET("dashboard/empresa-mais-interesse")
    Call<EmpresaResponse> getEmpresaMaisInteresse(@Header("Authorization") String token);

    @GET("dashboard/residuos-porcentagem-concluidos")
    Call<PorcentagemConcluidosResponse> getPorcentagemConcluidos(@Header("Authorization") String token);

    @GET("dashboard/interesses-residuos")
    Call<List<Object>> getInteressesResiduos(@Header("Authorization") String token);

    // Adicione estes métodos à sua interface ApiService

    // Rotas para dados do próprio usuário
    @GET("dashboard/meus-residuos-disponiveis")
    Call<ResiduosQuantidadeResponse> getMeusResiduosDisponiveis(@Header("Authorization") String token);

    @GET("dashboard/meus-residuos-negociando")
    Call<ResiduosQuantidadeResponse> getMeusResiduosNegociando(@Header("Authorization") String token);

    @GET("dashboard/meus-residuos-concluidos")
    Call<ResiduosQuantidadeResponse> getMeusResiduosConcluidos(@Header("Authorization") String token);

    @GET("dashboard/meus-residuos-cancelados")
    Call<ResiduosQuantidadeResponse> getMeusResiduosCancelados(@Header("Authorization") String token);

    @GET("dashboard/meus-residuos-total")
    Call<TotalResiduosResponse> getMeusTotalResiduos(@Header("Authorization") String token);
    @GET("dashboard/meus-residuos-tipos-quantidade")
    Call<TiposQuantidadeResponse> getMeusTiposQuantidade(@Header("Authorization") String token);

    @GET("dashboard/meus-residuos-porcentagem-concluidos")
    Call<PorcentagemConcluidosResponse> getMeusPorcentagemConcluidos(@Header("Authorization") String token);

    @GET("dashboard/empresas-interesse-meus-residuos")
    Call<EmpresaInteresseResponse> getEmpresasInteresseNosMeusResiduos(@Header("Authorization") String token);

    @GET("dashboard/meus-residuos")
    Call<MeusResiduosResponse> getMeusResiduos(@Header("Authorization") String token);


}
