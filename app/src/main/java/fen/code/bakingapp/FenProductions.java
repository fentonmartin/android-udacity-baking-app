package fen.code.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class FenProductions extends AppCompatActivity {

    public Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState,
                         @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        setLog("ACTIVITY | onCreate");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        setLog("ACTIVITY | onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        setLog("ACTIVITY | onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        setLog("ACTIVITY | onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    public void setSubtitle(String subtitle) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(subtitle);
        }
    }

    public void setLog(String log) {
        Log.d("LOG " + getClass().getSimpleName(), log);
    }

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void setActivity(Class activity) {
        Intent intent = new Intent(getApplicationContext(), activity);
        startActivity(intent);
    }

    public void setActivity(Class activity, int flag) {
        Intent intent = new Intent(getApplicationContext(), activity);
        intent.setFlags(flag);
        startActivity(intent);
    }

    public void setActivity(Class activity, String value, String extra) {
        Intent intent = new Intent(getApplicationContext(), activity);
        intent.putExtra(value, extra);
        startActivity(intent);
    }

    public void setActivity(Class activity, String value, int extra) {
        Intent intent = new Intent(getApplicationContext(), activity);
        intent.putExtra(value, extra);
        startActivity(intent);
    }

    public void setHomeDisabled() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }
    }

    public void setToast(int id) {
        setToast(getString(id));
    }

    public void setToast(String message) {
        Toast.makeText(FenProductions.this, message, Toast.LENGTH_SHORT).show();
    }

    public void setToast(CharSequence message) {
        Toast.makeText(FenProductions.this, message, Toast.LENGTH_SHORT).show();
    }

    public void setShare(String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");
        startActivity(intent);
    }
}
