package fen.code.bakingapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fen.code.bakingapp.R;
import fen.code.bakingapp.activity.RecipeDetailActivity;
import fen.code.bakingapp.adapter.RecipeDetailAdapter;
import fen.code.bakingapp.entity.Ingredient;
import fen.code.bakingapp.entity.Recipe;
import fen.code.bakingapp.util.StringUtils;
import fen.code.bakingapp.widget.UpdateService;

import static fen.code.bakingapp.util.StringUtils.SELECTED_RECIPE;

public class RecipeDetailFragment extends Fragment {

    ArrayList<Recipe> recipe= new ArrayList<>();
    RecyclerView recyclerView;
    TextView textView;
    String recipeName;

    public RecipeDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            recipe = savedInstanceState.getParcelableArrayList(SELECTED_RECIPE);
        } else {
            recipe = getArguments().getParcelableArrayList(SELECTED_RECIPE);
        }

        List<Ingredient> ingredients = null;
        if (recipe != null) {
            recipeName = recipe.get(0).getName();
            ingredients = recipe.get(0).getIngredients();
        }

        View rootView = inflater.inflate(R.layout.recipe_detail_fragment_body_part,
                container, false);
        textView = rootView.findViewById(R.id.recipe_detail_text);

        ArrayList<String> recipeIngredientsForWidgets = new ArrayList<>();

        if (ingredients != null) {
            for (int i = 0; i < ingredients.size(); i++) {
                textView.append("\u2022 " + ingredients.get(i).getIngredient() + "\n");
                textView.append("\t\t\t Quantity: " + ingredients.get(i).getQuantity()
                        .toString() + "\n");
                textView.append("\t\t\t Measure: " + ingredients.get(i).getMeasure() + "\n\n");

                recipeIngredientsForWidgets.add(ingredients.get(i).getIngredient() + "\n" +
                        "Quantity: " + ingredients.get(i).getQuantity().toString() + "\n" +
                        "Measure: " + ingredients.get(i).getMeasure() + "\n");
            }
        }

        recyclerView = rootView.findViewById(R.id.recipe_detail_recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        RecipeDetailAdapter adapter = new RecipeDetailAdapter
                ((RecipeDetailActivity) getActivity());
        recyclerView.setAdapter(adapter);
        adapter.setMasterRecipeData(recipe, getContext());

        UpdateService.startBakingService(getContext(), recipeIngredientsForWidgets);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putParcelableArrayList(SELECTED_RECIPE, recipe);
        currentState.putString(StringUtils.EXTRA_TITLE, recipeName);
    }
}