package com.bakbijbel.bakbijbel;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity  {


    SearchView simpleSearchView;
    TextView textView;
    Button login_btn;
    private Recept recept;
    public User user;
    public boolean loggedIn = false;

    public void SetUser()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.getString("Name", "Guest");
        user = new User(prefs.getString("AccessToken", ""), prefs.getString("Name", "Guest"));

        if(user.accesToken != "")
            loggedIn = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SetUser();
        simpleSearchView = (SearchView) findViewById(R.id.searchBar);
        textView = (TextView) findViewById(R.id.textView);
        login_btn = (Button) findViewById(R.id.login_btn);
        if(user.name.isEmpty() || user.name == "")
            textView.setText("Welkom Guest!");
        else
            textView.setText("Welkom " + user.name.toString() + "!");

        if(loggedIn)
            login_btn.setText("Logout");

        GetHomepageRecepts();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loggedIn) {
                    LogOut();
                }
                else {
                    try {
                        Intent newActivity = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(newActivity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                EmtyScreen();
                SearchRecepts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                EmtyScreen();
                SearchRecepts(newText);

                return false;
            }
        });


        final Intent intent = new Intent(this, WatEtenWeVandaagWidget.class);
        final PendingIntent pending = PendingIntent.getService(this, 0, intent, 0);
        final AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pending);
        long interval = 5000;
        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),interval, pending);
    }

    void EmtyScreen()
    {
        for (int i = 0; i < 6; i++) {
            //Log.println(Log.DEBUG, "MINE", String.valueOf(i));
            String receptFotoID = "receptFoto" + (i + 1);
            String receptTxtID = "receptTxt" + (i + 1);
            int resID = getResources().getIdentifier(receptFotoID, "id", getPackageName());
            int resTxtID = getResources().getIdentifier(receptTxtID, "id", getPackageName());
            try {
                ImageButton temp = (ImageButton) findViewById(resID);
                TextView tempTxt = (TextView) findViewById(resTxtID);
                temp.setImageResource(android.R.color.transparent);
                tempTxt.setText("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    void SearchRecepts(String search)
    {
        final List<Recept> allRecepts = GetAllRecepts();
        final List<Recept> receptsFound = new ArrayList<>();

        for (int i = 0; i < allRecepts.size(); i++) {
            if (allRecepts.get(i).title.toLowerCase().contains(search.toLowerCase())) {
                receptsFound.add(allRecepts.get(i));
            }
        }

        for (int i = 0; i < receptsFound.size() && i <= 5; i++) {
            //Log.println(Log.DEBUG, "MINE", receptsFound.get(i).title);

            String receptFotoID = "receptFoto" +  (i + 1);
            String receptTxtID = "receptTxt" +  (i +1);
            int resID = getResources().getIdentifier(receptFotoID, "id", getPackageName());
            int resTxtID = getResources().getIdentifier(receptTxtID, "id", getPackageName());
            try {
                ImageButton temp = (ImageButton) findViewById(resID);
                TextView tempTxt = (TextView) findViewById(resTxtID);

                if(receptsFound.get(i).title != "")
                    tempTxt.setText(receptsFound.get(i).title);
                new DownloadImageTask(temp).execute(receptsFound.get(i).pictureURL);

                final int finalI = i;
                temp.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        try {
                            Intent newActivity = new Intent(MainActivity.this, ReceptActivity.class);
                            //Log.println(Log.DEBUG, "MINE", String.valueOf(finalI));

                            newActivity.putExtra("info", receptsFound.get(finalI).getArray());
                            startActivity(newActivity);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void LogOut()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Name", "");
        editor.putString("AccessToken", "");
        editor.apply();
        loggedIn = false;
        Intent newActivity = new Intent(MainActivity.this, MainActivity.class);
        startActivity(newActivity);
        finish();
    }

    List<Recept> GetAllRecepts()
    {
        List<Recept> AllRecepts = new ArrayList<Recept>();
        try {
            String output = new WatEtenWeVandaagWidget.MyTask().execute("http://10.0.2.2:8000/api/recept").get();



            //Krijg een random recept voor de widget
            JSONArray jsonArray = new JSONArray(output);
            Random random;
            random = new Random();
            int rand_int = random.nextInt(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String title = jsonObject1.optString("name");
                String pictureURL = jsonObject1.optString("url");
                pictureURL = pictureURL.replace("http://127.0.0.1:8000/", "http://10.0.2.2:8000/");

                //Log.println(Log.DEBUG, "MINE", pictureURL);

                String description = jsonObject1.optString("description");
                String ingredients = jsonObject1.optString("ingredients");
                int preparationTime = Integer.parseInt(jsonObject1.optString("preparation_time"));

                Recept tempRecept = new Recept(pictureURL, title, description, ingredients, preparationTime);
                AllRecepts.add(tempRecept);
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
            //Log.println(Log.DEBUG, "MINE", e.toString());

        }

        return AllRecepts;
    }

    void GetHomepageRecepts()
    {
        final List<Recept> allRecepts = GetAllRecepts();

        Collections.shuffle(allRecepts);
        ImageButton receptFoto1 = (ImageButton) findViewById(R.id.receptFoto1);
        //TextView receptTxt1 = (TextView) findViewById(R.id.receptTxt1);
        //ImageButton receptFoto2 = (ImageButton) findViewById(R.id.receptFoto2);

        for (int i = 0; i < allRecepts.size() && i <= 5; i++) {

            String receptFotoID = "receptFoto" +  (i + 1);
            String receptTxtID = "receptTxt" +  (i +1);
            int resID = getResources().getIdentifier(receptFotoID, "id", getPackageName());
            int resTxtID = getResources().getIdentifier(receptTxtID, "id", getPackageName());
            try {
                ImageButton temp = (ImageButton) findViewById(resID);
                TextView tempTxt = (TextView) findViewById(resTxtID);
                if(allRecepts.get(i).title != "")
                    tempTxt.setText(allRecepts.get(i).title);
                new DownloadImageTask(temp).execute(allRecepts.get(i).pictureURL);

                final int finalI = i;
                temp.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        try {
                            Intent newActivity = new Intent(MainActivity.this, ReceptActivity.class);

                            newActivity.putExtra("info", allRecepts.get(finalI).getArray());
                            startActivity(newActivity);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageButton bmImage;

        public DownloadImageTask(ImageButton bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            try {

                bmImage.setImageBitmap(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
