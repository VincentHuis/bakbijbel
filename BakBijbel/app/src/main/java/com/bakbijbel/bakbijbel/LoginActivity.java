package com.bakbijbel.bakbijbel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements AsyncResponse {
    public static TextView textView;
    public static JSONObject resultJson;
    EditText loginIF;
    EditText passwordIF;
    Button loginBtn;
    Button signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        textView = findViewById(R.id.textView2);
        loginBtn = findViewById(R.id.loginBtn);
        signUp = findViewById(R.id.SignUp);
        textView.setText("");

        loginIF = findViewById(R.id.loginInputField);
        passwordIF = findViewById(R.id.passwordInputField);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp();
            }
        });
    }

    void SignUp()
    {
        Toast.makeText(LoginActivity.this, "Een account aanmaken kan momenteel alleen via de website: Bakbijbel.nl",
                Toast.LENGTH_LONG).show();
    }

//Async methode wordt aangeroepen
    void Login()
    {
        AsyncTaskRunner runner = new AsyncTaskRunner(getApplicationContext());
        String userName = loginIF.getText().toString();
        String password = passwordIF.getText().toString();
        runner.delegate = this;
        runner.execute(userName, password);
    }

    @Override
    public void processFinish(String result, Context context) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
        JSONObject jsonObject1 = new JSONObject(jsonObject.get("user").toString());

        if(result.contains("error"))
            textView.setText("Foutieve combinatie, probeer het opnieuw!");

        if(jsonObject.isNull("error") == true)
        {
            User user = new User(jsonObject.get("access_token").toString(), jsonObject1.get("name").toString());
            //MainActivity.SetUser(user);


            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Name", user.name);
            editor.putString("AccessToken", user.accesToken);
            editor.apply();


            Intent newActivity = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(newActivity);
            finish();
        }
    }
}


