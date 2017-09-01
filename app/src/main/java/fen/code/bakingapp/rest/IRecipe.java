package fen.code.bakingapp.rest;

import java.util.ArrayList;

import fen.code.bakingapp.entity.Recipe;
import fen.code.bakingapp.util.StringUtils;
import retrofit2.Call;
import retrofit2.http.GET;

public interface IRecipe {

    @GET(StringUtils.BAKING_URL)
    Call<ArrayList<Recipe>> getRecipe();
}