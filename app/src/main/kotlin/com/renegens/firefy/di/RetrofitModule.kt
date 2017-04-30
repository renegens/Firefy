package com.renegens.firefy.di

import com.renegens.firefy.service.CityService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
class RetrofitModule {

    companion object {

        val GOOGLE_MAPS_URL = "http://maps.googleapis.com/maps/api/geocode/"

    }

    @Provides
    fun provideClient() = OkHttpClient.Builder()
            .readTimeout(12, TimeUnit.SECONDS)
            .connectTimeout(12, TimeUnit.SECONDS)
            .build()

    @Provides
    fun provideRetrofit(baseURL: String, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseURL)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Provides
    fun provideMapService() = provideRetrofit(GOOGLE_MAPS_URL, provideClient()).create(CityService::class.java)

}
