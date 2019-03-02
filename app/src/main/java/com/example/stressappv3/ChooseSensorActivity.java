package com.example.stressappv3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

public class ChooseSensorActivity extends AppCompatActivity {
    Spinner dropDownList;
    Button goButton;
    TextView goCheck;
    int choicePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sensor);

        dropDownList = findViewById(R.id.spinner);
        goButton = findViewById(R.id.go_btn);
        goCheck = findViewById(R.id.text_check);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sensor_options, android.R.layout.simple_spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        dropDownList.setAdapter(adapter);

        dropDownList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                   @Override
                                                   public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                       Object item = parent.getItemAtPosition(position);
                                                       choicePosition = position;
                                                       goCheck.setText(Integer.toString(position));
                                                   }

                                                   @Override
                                                   public void onNothingSelected(AdapterView<?> parent) {

                                                   }
                                               }

        );

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToDataScreen = new Intent(ChooseSensorActivity.this, DataActivity.class);
                goToDataScreen.putExtra("sensor", choicePosition);
                startActivity(goToDataScreen);
            }
        });
    }
}
