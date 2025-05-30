package com.example.findhdd.api;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;

import com.google.gson.Gson;

public class ApiClient {
    private static Retrofit publicRetrofit = null;
    private static Retrofit authRetrofit = null;

    public static Retrofit getClient() {
        if (publicRetrofit == null) {
            publicRetrofit = createRetrofitBuilder()
                    .client(createOkHttpClient(null))
                    .build();
        }
        return publicRetrofit;
    }

    public static Retrofit getClient(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalStateException("Attempt to create API client with null credentials");
        }

        Log.d("APICLIENT", "Creating client for user: " + username);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS); // Измените на HEADERS для проверки

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    // Используем Credentials.basic вместо ручного Base64
                    String authToken = Credentials.basic(username, password);
                    Request request = chain.request()
                            .newBuilder()
                            .header("Authorization", authToken)
                            .build();
                    return chain.proceed(request);
                })
                .build();

        return new Retrofit.Builder()
                .baseUrl("https://chief-suitably-falcon.ngrok-free.app/api/v1/")
                //.baseUrl("http://10.0.2.2:8888/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    private static Retrofit.Builder createRetrofitBuilder() {
        return new Retrofit.Builder()
                .baseUrl("https://chief-suitably-falcon.ngrok-free.app/api/v1/")
                //.baseUrl("http://10.0.2.2:8888/api/v1/")
                .addConverterFactory(GsonConverterFactory.create());
    }

    private static OkHttpClient createOkHttpClient(String credentials) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    Request request = chain.request();

                    okhttp3.Response response = chain.proceed(request);
                    if (!response.isSuccessful()) {
                        try {
                            String json = response.peekBody(2048).string();
                            ApiMessage error = new Gson().fromJson(json, ApiMessage.class);
                            if (error != null && error.getMessage() != null) {
                                throw new IOException(error.getMessage());
                            }
                        } catch (Exception e) {
                        }
                    }
                    return response;
                });

        if (credentials != null) {
            builder.addInterceptor(chain -> {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", credentials)
                        .build();
                return chain.proceed(newRequest);
            });
        }
        return builder.build();
    }
}