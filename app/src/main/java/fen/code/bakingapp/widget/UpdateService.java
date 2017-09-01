package fen.code.bakingapp.widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import fen.code.bakingapp.util.StringUtils;

public class UpdateService extends IntentService {

    public UpdateService() {
        super("UpdateService");
    }

    public static void startBakingService(Context context, ArrayList<String> fromActivityIngredientsList) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra(StringUtils.WIDGET_INGREDIENTS_LIST, fromActivityIngredientsList);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            ArrayList<String> fromActivityIngredientsList = intent.getExtras()
                    .getStringArrayList(StringUtils.WIDGET_INGREDIENTS_LIST);
            handleActionUpdateBakingWidgets(fromActivityIngredientsList);
        }
    }


    private void handleActionUpdateBakingWidgets(ArrayList<String> fromActivityIngredientsList) {
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE2");
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE2");
        intent.putExtra(StringUtils.WIDGET_INGREDIENTS_LIST, fromActivityIngredientsList);
        sendBroadcast(intent);
    }
}