package com.example.findhdd.api;

import android.util.Log;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;

import com.google.gson.Gson;

public class ApiClient {
    private static final String BASE_URL = "https://chief-suitably-falcon.ngrok-free.app/api/v1/";
    //"http://10.0.2.2:8888/api/v1/"
    private static Retrofit publicRetrofit;

    public static Retrofit getClient() {
        if (publicRetrofit == null) {
            publicRetrofit = createRetrofit(null);
        }
        return publicRetrofit;
    }

    public static Retrofit getClient(String username, String password) {
        if (username == null || password == null)
            throw new IllegalStateException("Username/password can't be null");
        String authToken = Credentials.basic(username, password);
        Log.d("APICLIENT", "Creating client for user: " + username);
        return createRetrofit(authToken);
    }

    private static Retrofit createRetrofit(String authHeader) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient(authHeader))
                .build();
    }

    private static OkHttpClient createOkHttpClient(String authHeader) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    okhttp3.Response response = chain.proceed(request);

                    if (!response.isSuccessful()) {
                        String json = response.peekBody(2048).string();
                        ApiMessage error = new Gson().fromJson(json, ApiMessage.class);
                        if (error != null && error.getMessage() != null)
                            throw new IOException(error.getMessage());
                    }
                    return response;
                });

        if (authHeader != null) {
            builder.addInterceptor(chain -> {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", authHeader)
                        .build();
                return chain.proceed(newRequest);
            });
        }

        return builder.build();
    }
}