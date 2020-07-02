package com.bakbijbel.bakbijbel;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of App Widget functionality.
 */
public class WatEtenWeVandaagWidget extends AppWidgetProvider implements AsyncResponse{

    public static class MyTask extends AsyncTask<String, Void, String> {
        public AsyncResponse delegate = null;


        @Override
        protected String doInBackground(String... urls)
        {
            try
            {
                URL url = new URL(urls[0]);
                URLConnection uc = url.openConnection();
                //String j = (String) uc.getContent();
                uc.setDoInput(true);
                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    a.append(inputLine);
                in.close();

                return a.toString();
            }
            catch (Exception e)
            {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
        }
    }

    @Override
    public void processFinish(String output, Context context) throws JSONException {


    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, RemoteViews views) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        //views = new RemoteViews(context.getPackageName(), R.layout.wat_eten_we_vandaag_widget);
        // Instruct the widget manager to update the widget


        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        try {
            String output = new MyTask().execute("http://10.0.2.2:8000/api/recept").get();
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.wat_eten_we_vandaag_widget);

            Log.println(Log.DEBUG, "MINE", output);


            //Krijg een random recept voor de widget
            JSONArray jsonArray = new JSONArray(output);
            Random random;
            random = new Random();
            int rand_int = random.nextInt(jsonArray.length());

                JSONObject jsonObject1 = jsonArray.getJSONObject(rand_int);
                String value = jsonObject1.optString("title");
                views.setTextViewText(R.id.appwidget_text2, value);

                Log.println(Log.DEBUG, "MINE", value);



            for (int appWidgetId : appWidgetIds) {

            updateAppWidget(context, appWidgetManager, appWidgetId, views);
        }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
            //Log.println(Log.DEBUG, "MINE", e.toString());

        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

