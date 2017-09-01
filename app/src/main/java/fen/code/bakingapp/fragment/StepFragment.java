package fen.code.bakingapp.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import fen.code.bakingapp.R;
import fen.code.bakingapp.activity.RecipeDetailActivity;
import fen.code.bakingapp.entity.Recipe;
import fen.code.bakingapp.entity.Step;
import fen.code.bakingapp.util.StringUtils;

public class StepFragment extends Fragment {

    SimpleExoPlayer player;
    SimpleExoPlayerView playerView;
    BandwidthMeter bandwidthMeter;
    TextView textView;

    ArrayList<Step> steps = new ArrayList<>();
    ArrayList<Recipe> recipe = new ArrayList<>();
    Handler mainHandler;
    String recipeName;
    int selectedIndex;

    public StepFragment() {
    }

    private ListItemClickListener itemClickListener;

    public interface ListItemClickListener {
        void onListItemClick(List<Step> allSteps, int Index, String recipeName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainHandler = new Handler();
        bandwidthMeter = new DefaultBandwidthMeter();
        itemClickListener = (RecipeDetailActivity) getActivity();

        if (savedInstanceState != null) {
            steps = savedInstanceState.getParcelableArrayList(StringUtils.SELECTED_STEP);
            selectedIndex = savedInstanceState.getInt(StringUtils.SELECTED_INDEX);
            recipeName = savedInstanceState.getString(StringUtils.EXTRA_TITLE);
        } else {
            steps = getArguments().getParcelableArrayList(StringUtils.SELECTED_STEP);
            if (steps != null) {
                steps = getArguments().getParcelableArrayList(StringUtils.SELECTED_STEP);
                selectedIndex = getArguments().getInt(StringUtils.SELECTED_INDEX);
                recipeName = getArguments().getString(StringUtils.EXTRA_TITLE);
            } else {
                recipe = getArguments().getParcelableArrayList(StringUtils.SELECTED_RECIPE);
                if (recipe != null) {
                    steps = (ArrayList<Step>) recipe.get(0).getSteps();
                }
                selectedIndex = 0;
            }
        }

        View rootView = inflater.inflate(R.layout.fragment_recipe_step,
                container, false);
        textView = rootView.findViewById(R.id.recipe_step_detail_text);
        textView.setText(steps.get(selectedIndex).getDescription());
        textView.setVisibility(View.VISIBLE);

        playerView = rootView.findViewById(R.id.playerView);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

        String videoURL = steps.get(selectedIndex).getVideoURL();

        if (rootView.findViewWithTag("sw600dp-port-recipe_step_detail") != null) {
            recipeName = ((RecipeDetailActivity) getActivity()).recipeName;
            if (((RecipeDetailActivity) getActivity()).getSupportActionBar() != null)
                ((RecipeDetailActivity) getActivity()).setTitle(recipeName);
        }

        String imageUrl = steps.get(selectedIndex).getThumbnailURL();
        if (!imageUrl.isEmpty()) {
            Uri builtUri = Uri.parse(imageUrl).buildUpon().build();
            ImageView thumbImage = rootView.findViewById(R.id.thumbImage);
            thumbImage.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(builtUri).into(thumbImage);
        }
        if (!videoURL.isEmpty()) {
            initializePlayer(Uri.parse(steps.get(selectedIndex).getVideoURL()));
            playerView.setVisibility(View.VISIBLE);
            if (rootView.findViewWithTag("sw600dp-land-recipe_step_detail") != null) {
                getActivity().findViewById(R.id.fragment_container2).setLayoutParams(new
                        LinearLayout.LayoutParams(-1, -2));
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
            } else if (isInLandscapeMode(getContext())) {
                textView.setVisibility(View.GONE);
            }
        } else {
            player = null;
            playerView.setVisibility(View.GONE);
            playerView.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
        }

        Button mPrevStep = rootView.findViewById(R.id.previousStep);
        Button mNextstep = rootView.findViewById(R.id.nextStep);

        mPrevStep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (steps.get(selectedIndex).getId() > 0) {
                    if (player != null) {
                        player.stop();
                    }
                    itemClickListener.onListItemClick(steps, steps.get(selectedIndex)
                            .getId() - 1, recipeName);
                } else {
                    Toast.makeText(getActivity(), "You are in the first step of " +
                            recipeName, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mNextstep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int lastIndex = steps.size() - 1;
                if (steps.get(selectedIndex).getId() < steps.get(lastIndex).getId()) {
                    if (player != null) {
                        player.stop();
                    }
                    itemClickListener.onListItemClick(steps, steps
                            .get(selectedIndex).getId() + 1, recipeName);
                } else {
                    Toast.makeText(getContext(), "You are in the last step of " +
                            recipeName, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rootView;
    }

    private void initializePlayer(Uri mediaUri) {
        if (player == null) {
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection
                    .Factory(bandwidthMeter);
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(mainHandler,
                    videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();

            player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            playerView.setPlayer(player);

            String userAgent = Util.getUserAgent(getContext(), "Baking App");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri,
                    new DefaultDataSourceFactory(getContext(), userAgent),
                    new DefaultExtractorsFactory(), null, null);
            player.prepare(mediaSource);
            player.setPlayWhenReady(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putParcelableArrayList(StringUtils.SELECTED_STEP, steps);
        currentState.putInt(StringUtils.SELECTED_INDEX, selectedIndex);
        currentState.putString("Title", recipeName);
    }

    public boolean isInLandscapeMode(Context context) {
        return (context.getResources().getConfiguration().orientation == Configuration
                .ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (player != null) {
            player.stop();
            player.release();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null) {
            player.stop();
            player.release();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.stop();
            player.release();
        }
    }
}