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

public class ModifyAccountActivity extends AppCompatActivity {
    private Button updateButton;
    private EditText heightText, weightText, ageText;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_account);

        //get back4app objectId from main activity
        Bundle bd = getIntent().getExtras();
        if(bd != null)
        {
            username = (String) bd.get("username");
        }else{
            Toast.makeText(ModifyAccountActivity.this, "Unable to fetch objectID from HomeScreen.", Toast.LENGTH_LONG).show();
        }

        heightText = findViewById(R.id.height_text);
        weightText = findViewById(R.id.weight_text);
        ageText = findViewById(R.id.age_text);
        updateButton = findViewById(R.id.update_account_btn);

        updateButton.setOnClickListener(new View.OnClickListener() {
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
                            boolean needToUpdate = false;
                            if(!Objects.equals(heightEntered, ""))
                            {
                                user.put("Height", heightEntered);
                                needToUpdate = true;
                            }
                            if(!Objects.equals(weightEntered, ""))
                            {
                                user.put("Weight", weightEntered);
                                needToUpdate = true;
                            }
                            if(!Objects.equals(ageEntered, ""))
                            {
                                user.put("Age", ageEntered);
                                needToUpdate = true;
                            }
                            if(needToUpdate){
                                user.saveInBackground();
                            }
                            Intent goToHomeScreen = new Intent(ModifyAccountActivity.this, HomeScreen.class);
                            goToHomeScreen.putExtra("username", username);
                            startActivity(goToHomeScreen);
                        } else {
                            // Something is wrong
                            Toast.makeText(ModifyAccountActivity.this, "Unable to update account, please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
