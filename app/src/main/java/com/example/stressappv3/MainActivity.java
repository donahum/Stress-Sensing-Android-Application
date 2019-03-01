package com.example.stressappv3;

import android.content.Intent;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Button loginButton, createAccountButton;
    private EditText usernameText, passwordText;

    @Override
    public void onBackPressed() {
        //prevent user from pressing back, if coming from logout phase
        //do nothing...
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.login_button);
        createAccountButton = findViewById(R.id.create_account_button);
        usernameText = findViewById(R.id.usernameEditText);
        passwordText = findViewById(R.id.passwordEditText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usernameEntered, passwordEntered;
                usernameEntered = usernameText.getText().toString();
                passwordEntered = passwordText.getText().toString();
                //create query for user credentials
                ParseQuery<ParseObject> loginQuery = ParseQuery.getQuery("LoginCredentials");
                loginQuery.whereEqualTo("Username", usernameEntered);
                loginQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject user, ParseException e) {
                        if (e == null) {
                            String userPassword = user.getString("Password");
                            //compare password from DB to pw entered
                            if(Objects.equals(userPassword, passwordEntered))
                            {
                                Intent goToHomeScreen = new Intent(MainActivity.this, HomeScreen.class);
                                goToHomeScreen.putExtra("username", usernameEntered);
                                startActivity(goToHomeScreen);
                            }else{
                                Toast.makeText(MainActivity.this, "Invalid password, please try again.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // Something is wrong
                            Toast.makeText(MainActivity.this, "Username not found, please create an account.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usernameEntered, passwordEntered;
                usernameEntered = usernameText.getText().toString();
                passwordEntered = passwordText.getText().toString();
                usernameText.setText("");
                passwordText.setText("");
                //first check that account doesn't exist yet
                ParseQuery<ParseObject> loginQuery = ParseQuery.getQuery("LoginCredentials");
                loginQuery.whereEqualTo("Username", usernameEntered);
                loginQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject user, ParseException e) {
                        if (e == null) {
                            // Account already exists
                            Toast.makeText(MainActivity.this, "Account already exists, please login.", Toast.LENGTH_LONG).show();
                        } else {
                            //if account doesn't exist, create it
                            final ParseObject account = new ParseObject("LoginCredentials");
                            account.put("Username", usernameEntered);
                            account.put("Password", passwordEntered);
                            account.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null){
                                        //success
                                        Intent goToAccountCreation = new Intent(MainActivity.this, blankActivity.class);
                                        goToAccountCreation.putExtra("username", usernameEntered);
                                        startActivity(goToAccountCreation);
                                    }else{
                                        //error
                                        Toast.makeText(MainActivity.this, "Error creating account.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                });

            }
        });
    }
}
