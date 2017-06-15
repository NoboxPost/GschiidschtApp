package net.ictcampus.gschiidschtapp;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class InfoActivity extends AppCompatActivity {

    private int easterEggCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        checkAndHandleEasterEgg();
    }

    private void checkAndHandleEasterEgg() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));
        final SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_file_key), getApplication().MODE_PRIVATE);

        boolean easterEggState = sharedPreferences.getBoolean(getString(R.string.easterEggStatus), false);
        final Button btn = (Button) findViewById(R.id.sneekyButton);
        if (easterEggState) {
            btn.setText(R.string.app_sneekyname);
        } else {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    easterEggCounter++;
                    if (easterEggCounter == 10) {
                        Toast.makeText(getApplicationContext(), getString(R.string.found_easterEgg), Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(getString(R.string.easterEggStatus), true).commit();
                        btn.setText(R.string.app_sneekyname);
                        btn.setOnClickListener(null);
                    }
                }
            });

        }
    }
}
