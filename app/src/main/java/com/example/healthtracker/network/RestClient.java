package com.example.healthtracker.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;

public class RestClient {
    private static Retrofit retrofit;

    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null || !retrofit.baseUrl().toString().equals(baseUrl)) {
            OkHttpClient client = new OkHttpClient.Builder().build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
