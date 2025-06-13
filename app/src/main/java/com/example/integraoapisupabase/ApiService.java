package com.example.integraoapisupabase;

import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;
import java.util.Map;

public interface ApiService {

    @GET("produtos")
    Call<List<Produto>> getProdutos(
            @Header("apikey") String apiKey,
            @Header("Authorization") String bearer
    );

    @POST("produtos")
    Call<Void> createProduto(
            @Header("apikey") String apiKey,
            @Header("Authorization") String bearer,
            @Body Map<String, Object> produto  // Map em vez de Produto
    );

    @DELETE("produtos")
    Call<Void> deleteProduto(
            @Header("apikey") String apiKey,
            @Header("Authorization") String bearer,
            @Query("id") String id
    );
}
