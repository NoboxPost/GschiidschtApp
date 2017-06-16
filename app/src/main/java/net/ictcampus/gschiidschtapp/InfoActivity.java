package net.ictcampus.gschiidschtapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

/**
 * InfoActivity is an information screen about the mobbing-topic. (accessible via drawer menu)
 * It includes a list of links to websites.
 */

//TODO: there should be different links for each language

public class InfoActivity extends AppCompatActivity {

    private int easterEggCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ImageButton backButton = (ImageButton) findViewById(R.id.info_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent infoIntent = new Intent(getApplicationContext(), TeamActivity.class);
                startActivity(infoIntent);
            }
        });



        checkAndHandleEasterEgg();
        fillListViewWithInfoLinks();
    }

    private void fillListViewWithInfoLinks() {
        //Array of Strings displayed
        final ArrayList<String> informationLinkTitles = new ArrayList<>();
        informationLinkTitles.add("CH Mobbing-Zentrale");
        informationLinkTitles.add("CH Mobbing-Beratungsstelle");
        informationLinkTitles.add("Beobachter Artikel zum Thema Mobbing");

        //Array of URL-Strings not displayed - must be the same size as informationLinkTitles and must have the same order
        final ArrayList<String> informationLinks = new ArrayList<>();
        informationLinks.add("http://www.mobbing-zentrale.ch/de/");
        informationLinks.add("http://mobbing-beratungsstelle.ch/");
        informationLinks.add("https://www.beobachter.ch/arbeit/arbeitsrecht/mobbing-so-wehren-sie-sich-richtig");

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, informationLinkTitles);

        ListView informationListView = (ListView) findViewById(R.id.informationList);
        informationListView.setAdapter(itemsAdapter);

        informationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), informationLinks.get(position).toString(), Toast.LENGTH_SHORT).show();

                Uri uriUrl = Uri.parse(informationLinks.get(position));
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);

            }
        });
    }

    //changes only the title in the toolbar
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
