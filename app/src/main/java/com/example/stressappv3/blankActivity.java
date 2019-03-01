package com.example.stressappv3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Objects;

public class blankActivity extends AppCompatActivity {
    private Button completeAccountbutton;
    private EditText heightText, weightText, ageText;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        completeAccountbutton = findViewById(R.id.complete_account_btn);
        heightText = findViewById(R.id.height_text);
        weightText = findViewById(R.id.weight_text);
        ageText = findViewById(R.id.age_text);

        //get back4app objectId from main activity
        Bundle bd = getIntent().getExtras();
        if(bd != null)
        {
            username = (String) bd.get("username");
        }else{
            Toast.makeText(blankActivity.this, "Unable to fetch objectID from MainActivity.", Toast.LENGTH_LONG).show();
        }

        completeAccountbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String heightEntered, weightEntered, ageEntered;
                heightEntered = heightText.getText().toString();
                weightEntered = weightText.getText().toString();
                ageEntered = ageText.getText().toString();
                ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("LoginCredentials");
                // Retrieve the object by id
                userQuery.whereEqualTo("Username", username);
                userQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject user, ParseException e) {
                        if (e == null) {
                            //update user characteristics
                            user.put("Height", heightEntered);
                            user.put("Weight", weightEntered);
                            user.put("Age", ageEntered);
                            user.saveInBackground();
                            Intent goToHomeScreen = new Intent(blankActivity.this, HomeScreen.class);
                            goToHomeScreen.putExtra("username", username);
                            startActivity(goToHomeScreen);
                        } else {
                            // Something is wrong
                            Toast.makeText(blankActivity.this, "Unable to finish creating account, please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
