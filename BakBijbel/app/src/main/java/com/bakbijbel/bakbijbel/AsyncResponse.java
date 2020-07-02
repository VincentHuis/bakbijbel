package com.bakbijbel.bakbijbel;

import android.content.Context;

import org.json.JSONException;

public interface AsyncResponse {
    void processFinish(String output, Context context) throws JSONException;
}