package com.example.aplicacionmovil;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FdaApiService {

    @GET("drug/label.json")
    Call<FdaResponse> getMedicamentos(
            @Query("limit") int limit
    );
}