package com.example.stressappv3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class HomeScreen extends AppCompatActivity {
    String username;
    private Button beginSessionButton, modifyButton, dataButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        beginSessionButton = findViewById(R.id.begin_sesh_btn);
        modifyButton = findViewById(R.id.modify_btn);
        dataButton = findViewById(R.id.view_data_btn);
        logoutButton = findViewById(R.id.logout_btn);

        //get back4app objectId from main activity
        Bundle bd = getIntent().getExtras();
        if(bd != null)
        {
            username = (String) bd.get("username");
        }else{
            Toast.makeText(HomeScreen.this, "Unable to fetch username from blankActivity.", Toast.LENGTH_LONG).show();
        }

        beginSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToModScreen = new Intent(HomeScreen.this, ModifyAccountActivity.class);
                goToModScreen.putExtra("username", username);
                startActivity(goToModScreen);
            }
        });

        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToLoginScreen = new Intent(HomeScreen.this, MainActivity.class);
                goToLoginScreen.putExtra("username", username);
                startActivity(goToLoginScreen);
            }
        });
    }
}
