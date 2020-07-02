//Deels van stackoverflow
package com.bakbijbel.bakbijbel;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class AsyncTaskRunner extends AsyncTask<String, String, String> {
    JSONObject jsonObject;
    Context mcontext;

    public AsyncTaskRunner(Context mcontext) {
        this.mcontext = mcontext;
    }

    public AsyncResponse delegate = null;
    @Override
    protected String doInBackground(String... params) {
        String email = params[0];
        String password = params[1];
        InputStream inputStream = null;
        String result = "";
        String url = "http://10.0.2.2:8000/api/login";
        try {
            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(url);
            String json = "";

            jsonObject = new JSONObject();
            jsonObject.accumulate("email", email);
            jsonObject.accumulate("password", password);
            json = jsonObject.toString();
            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpPost);

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            return e.toString();
        }

        // 11. return result
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }


    @Override
    protected void onPostExecute(String result) {
        try {
            delegate.processFinish(result, mcontext);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPreExecute() {

    }


    @Override
    protected void onProgressUpdate(String... text) {

    }
}