package com.renegens.firefy.service;

import com.google.gson.JsonObject;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface CityService {

    @GET("json")
    Observable<JsonObject> reportAddress(@Query("latlng") String location, @Query("sensor") String var);

}
