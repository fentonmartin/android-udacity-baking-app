package fen.code.bakingapp.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fen.code.bakingapp.util.StringUtils;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitBuilder {

    public static IRecipe Retrieve() {

        Gson gson = new GsonBuilder().create();
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        return new Retrofit.Builder()
                .baseUrl(StringUtils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callFactory(httpClientBuilder.build())
                .build().create(IRecipe.class);
    }
}