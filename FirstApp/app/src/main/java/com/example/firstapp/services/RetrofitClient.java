package com.example.firstapp.services;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Creating a Retrofit Client with interceptors for cookies

public class RetrofitClient {

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .addInterceptor(new AddCookieInterceptor()) // adding cookies
            .addInterceptor(new ReceivedCookieInterceptor()); // receiving cookies

    private static final String API_BASE_URL = "http://172.20.10.3:3000";
            //"http://192.168.1.8:3000";
            //"https://secure-ravine-64168.herokuapp.com";
             //"http://192.168.0.134:3000";
            //"http://10.0.2.2:3000/";
            //"http://172.20.10.2:3000";
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }
}
