package fen.code.bakingapp.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import fen.code.bakingapp.FenProductions;
import fen.code.bakingapp.R;
import fen.code.bakingapp.adapter.RecipeDetailAdapter;
import fen.code.bakingapp.entity.Recipe;
import fen.code.bakingapp.entity.Step;
import fen.code.bakingapp.fragment.RecipeDetailFragment;
import fen.code.bakingapp.fragment.RecipeStepDetailFragment;
import fen.code.bakingapp.util.StringUtils;

public class RecipeDetailActivity extends FenProductions implements
        RecipeDetailAdapter.ListItemClickListener,
        RecipeStepDetailFragment.ListItemClickListener {

    public String recipeName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        if (savedInstanceState == null) {
            Bundle selectedRecipeBundle = getIntent().getExtras();

            ArrayList<Recipe> recipe = selectedRecipeBundle
                    .getParcelableArrayList(StringUtils.SELECTED_RECIPE);
            if (recipe != null) {
                recipeName = recipe.get(0).getName();
            }

            final RecipeDetailFragment fragment = new RecipeDetailFragment();
            fragment.setArguments(selectedRecipeBundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(StringUtils.STACK_RECIPE_DETAIL)
                    .commit();

            if (findViewById(R.id.recipe_linear_layout).getTag() != null &&
                    findViewById(R.id.recipe_linear_layout).getTag().equals("tablet-land")) {

                final RecipeStepDetailFragment fragment2 = new RecipeStepDetailFragment();
                fragment2.setArguments(selectedRecipeBundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container2, fragment2)
                        .addToBackStack(StringUtils.STACK_RECIPE_STEP_DETAIL)
                        .commit();
            }
        } else {
            recipeName = savedInstanceState.getString(StringUtils.EXTRA_TITLE);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setHomeEnabled(true);
        setTitle(recipeName);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                if (findViewById(R.id.fragment_container2) == null) {
                    if (fm.getBackStackEntryCount() > 1) {
                        fm.popBackStack(StringUtils.STACK_RECIPE_DETAIL, 0);
                    } else if (fm.getBackStackEntryCount() > 0) {
                        finish();
                    }
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public void onListItemClick(List<Step> stepsOut, int selectedItemIndex, String recipeName) {
        final RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        setTitle(recipeName);

        Bundle stepBundle = new Bundle();
        stepBundle.putParcelableArrayList(StringUtils.SELECTED_STEP, (ArrayList<Step>) stepsOut);
        stepBundle.putInt(StringUtils.SELECTED_INDEX, selectedItemIndex);
        stepBundle.putString(StringUtils.EXTRA_TITLE, recipeName);
        fragment.setArguments(stepBundle);

        if (findViewById(R.id.recipe_linear_layout).getTag() != null &&
                findViewById(R.id.recipe_linear_layout).getTag().equals("tablet-land")) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container2, fragment)
                    .addToBackStack(StringUtils.STACK_RECIPE_STEP_DETAIL)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(StringUtils.STACK_RECIPE_STEP_DETAIL)
                    .commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(StringUtils.EXTRA_TITLE, recipeName);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
}