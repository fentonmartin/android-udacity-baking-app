package fen.code.bakingapp.fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import fen.code.bakingapp.R;
import fen.code.bakingapp.activity.RecipeActivity;
import fen.code.bakingapp.adapter.RecipeAdapter;
import fen.code.bakingapp.entity.Recipe;
import fen.code.bakingapp.rest.IRecipe;
import fen.code.bakingapp.rest.RetrofitBuilder;
import fen.code.bakingapp.util.StringUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeFragment extends Fragment {

    Call<ArrayList<Recipe>> recipe;
    RecipeAdapter recipesAdapter;
    boolean isConnected;

    public RecipeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView;
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);

        ConnectivityManager
                cm = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recipe_recycler);
        recipesAdapter = new RecipeAdapter((RecipeActivity) getActivity());
        recyclerView.setAdapter(recipesAdapter);

        if (rootView.getTag() != null && rootView.getTag().equals("phone-land")) {
            GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 4);
            recyclerView.setLayoutManager(mLayoutManager);
        } else {
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);
        }

        IRecipe iRecipe = RetrofitBuilder.Retrieve();
        recipe = iRecipe.getRecipe();

        connect();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        showConnectionSnack();
    }

    private void connect() {
        if (isConnected)
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
                }

                @Override
                public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                    Log.v("http fail: ", t.getMessage());
                    showSnackBar(t.getMessage());
                }
            });
        else
            showConnectionSnack();
    }

    private void showConnectionSnack() {
        String message = "Sorry! Not connected to internet";
        int color = Color.RED;
        if (!isConnected && getView() != null) {
            Snackbar snackbar = Snackbar
                    .make(getView(), message, Snackbar.LENGTH_LONG);

            View view = snackbar.getView();
            TextView textView = (TextView) view
                    .findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    private void showSnackBar(String message) {
        if (getView() != null) {
            Snackbar snackbar = Snackbar
                    .make(getView(), message, Snackbar.LENGTH_LONG)
                    .setAction("Refresh", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            connect();
                        }
                    });
            snackbar.show();
        }
    }
}