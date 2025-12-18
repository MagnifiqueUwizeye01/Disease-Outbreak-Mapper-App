package com.example.healthtracker.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/fhir/Observation")
    Call<ResponseBody> postObservation(@Body RequestBody body);

    @POST("/fhir/Patient")
    Call<ResponseBody> postPatient(@Body RequestBody body);

    // add other endpoints as needed
}
