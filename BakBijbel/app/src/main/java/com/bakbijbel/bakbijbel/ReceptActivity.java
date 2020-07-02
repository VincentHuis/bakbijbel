package com.bakbijbel.bakbijbel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;


public class ReceptActivity extends AppCompatActivity {
    private static final int IO_BUFFER_SIZE = 100;
    ImageView recept_photo;
    TextView title;
    TextView preperationTime;
    TextView description;
     ProgressBar loading;
    String[] info;
    Timer timerWifi = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recept_activitiy);
        recept_photo = (ImageView) findViewById(R.id.recept_image);
        title = (TextView) findViewById(R.id.recept_title);
        preperationTime = (TextView) findViewById(R.id.textViewBereidingstijd);
        description = (TextView) findViewById(R.id.bereidingswijze);
        loading = (ProgressBar) findViewById(R.id.progressBar);
        info = getIntent().getExtras().getStringArray("info");
        loading.setVisibility(View.VISIBLE);

        GeneratePage();
    }
    //Log.d("LOG----LOG----LOG", test);

    void GeneratePage()
    {
        LoadPicture();
        title.setText(info[0]);
        description.setText(info[1]);
        preperationTime.setText("Bereidingstijd: " + info[3] + " minuten");
    }


    void LoadPicture() {

        if(isNetworkAvailable()) {
            new DownloadImageTask((ImageView) recept_photo)
                    .execute(info[4]);
        }
        else {
            timerWifi.schedule(new CheckInternet(), 0, 2000);
        }
    }
    class CheckInternet extends TimerTask {
        public void run() {
            Log.d("LOG----LOG----LOG", "Checking wifi");
            if(isNetworkAvailable()) {
                timerWifi.cancel();
                new DownloadImageTask((ImageView) recept_photo)
                        .execute(info[4]);
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    //Internet
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
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
            bmImage.setImageBitmap(result);
            if(result != null)
                loading.setVisibility(View.INVISIBLE);
        }
    }
}
