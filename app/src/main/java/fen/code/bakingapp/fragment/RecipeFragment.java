package fen.code.bakingapp.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import fen.code.bakingapp.R;
import fen.code.bakingapp.activity.RecipeActivity;
import fen.code.bakingapp.adapter.RecipeAdapter;
import fen.code.bakingapp.entity.Recipe;
import fen.code.bakingapp.rest.IRecipe;
import fen.code.bakingapp.rest.RetrofitBuilder;
import fen.code.bakingapp.test.SimpleIdlingResource;
import fen.code.bakingapp.util.StringUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeFragment extends Fragment {

    public RecipeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView;

        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);

        recyclerView = rootView.findViewById(R.id.recipe_recycler);
        final RecipeAdapter recipesAdapter = new RecipeAdapter((RecipeActivity) getActivity());
        recyclerView.setAdapter(recipesAdapter);

        if (rootView.getTag() != null && rootView.getTag().equals("phone-land")) {
            GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 4);
            recyclerView.setLayoutManager(mLayoutManager);
        } else {
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);
        }

        IRecipe iRecipe = RetrofitBuilder.Retrieve();
        Call<ArrayList<Recipe>> recipe = iRecipe.getRecipe();

        @SuppressWarnings("VisibleForTests")
        final SimpleIdlingResource idlingResource = (SimpleIdlingResource)
                ((RecipeActivity) getActivity()).getIdlingResource();
        idlingResource.setIdleState(false);

        recipe.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call,
                                   @NonNull Response<ArrayList<Recipe>> response) {
                Integer statusCode = response.code();
                Log.v("status code: ", statusCode.toString());

                ArrayList<Recipe> recipes = response.body();

                Bundle recipesBundle = new Bundle();
                recipesBundle.putParcelableArrayList(StringUtils.RECIPES, recipes);

                recipesAdapter.setRecipeData(recipes, getContext());
                idlingResource.setIdleState(true);
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Log.v("http fail: ", t.getMessage());
            }
        });

        return rootView;
    }
}