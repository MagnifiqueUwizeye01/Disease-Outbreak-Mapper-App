package com.example.healthtracker.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("fhir/observation")
    Call<Void> sendObservation(@Body String json);
}
