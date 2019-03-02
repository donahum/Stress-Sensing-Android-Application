package com.example.stressappv3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.provider.ContactsContract;
import android.widget.Spinner;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.util.Random;

public class DataActivity extends AppCompatActivity {
    LineGraphSeries<DataPoint> series;
    Spinner dropDownList;
    int choicePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        //get back4app objectId from main activity
        Bundle bd = getIntent().getExtras();
        if(bd != null)
        {
            choicePosition = (int) bd.get("sensor");
        }else{
            Toast.makeText(DataActivity.this, "Unable to fetch sensor from previous screen.", Toast.LENGTH_LONG).show();
        }

        double x, y;
        double accumulator;
        x = 0;
        accumulator = 0;

        GraphView graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>();

        if(choicePosition == 0){
            //heart rate
            graph.removeAllSeries();
            x = 0;
            accumulator = 0;
            double noise = 0;
            for(int j = 0; j < 30; j++)
            {
                for(int i = 0; i < 3; i++)
                {
                    x += .1;
                    Random rand = new Random();
                    double max = .4;
                    double min = .2;
                    noise = min + (max - min) * rand.nextDouble();
                    y = noise;
                    accumulator += y;
                    series.appendData(new DataPoint(x, y), true, 200);
                }
                for(int i = 0; i < 4; i++)
                {
                    x += .1;
                    Random rand = new Random();
                    double max = 1.1;
                    double min = .9;
                    noise = min + (max - min) * rand.nextDouble();
                    y = noise;
                    accumulator += y;
                    series.appendData(new DataPoint(x, y), true, 200);
                }
                for(int i = 0; i < 3; i++)
                {
                    x += .1;
                    Random rand = new Random();
                    double max = .4;
                    double min = .2;
                    noise = min + (max - min) * rand.nextDouble();
                    y = noise;
                    accumulator += y;
                    series.appendData(new DataPoint(x, y), true, 200);
                }
            }
            graph.addSeries(series);
            double average = accumulator/300;
            Toast.makeText(DataActivity.this, "Your heart rate levels are normal.", Toast.LENGTH_LONG).show();
        }else if(choicePosition == 1){
            //power level
            graph.removeAllSeries();
            x = 0;
            accumulator = 0;
            for(int i = 0; i < 100; i++)
            {
                x += .1;
                y = 100 - x;
                accumulator += y;
                series.appendData(new DataPoint(x, y), true, 200);
            }
            for(int i = 100; i < 200; i++)
            {
                x += .1;
                y = 100 - 2*x;
                accumulator += y;
                series.appendData(new DataPoint(x, y), true, 200);
            }
            graph.addSeries(series);
        }else if(choicePosition == 2){
            //gsr
            graph.removeAllSeries();
            x = 0;
            accumulator = 0;
            double noise = 0;
            for(int i = 0; i < 200; i++)
            {
                x += .1;
                Random rand = new Random();
                double max = 2;
                double min = 1;
                noise = min + (max - min) * rand.nextDouble();
                y = 4.5*Math.sin(x) - noise + 6.5;
                accumulator += y;
                series.appendData(new DataPoint(x, y), true, 200);
            }
            graph.addSeries(series);
            double average = accumulator/300;
            Toast.makeText(DataActivity.this, "Your perspiration levels are high, and you may be more stressed out than normal.", Toast.LENGTH_LONG).show();
        }else if(choicePosition == 3){
            //O2
            graph.removeAllSeries();
            x = 0;
            accumulator = 0;
            for(int i = 0; i < 100; i++)
            {
                x += .1;
                y = 100 - x;
                accumulator += y;
                series.appendData(new DataPoint(x, y), true, 300);
            }
            for(int i = 100; i < 200; i++)
            {
                x += .1;
                y = 90 - .5*x;
                accumulator += y;
                series.appendData(new DataPoint(x, y), true, 300);
            }
            for(int i = 200; i < 300; i++)
            {
                x += .1;
                y = 85 - .25*x;
                accumulator += y;
                series.appendData(new DataPoint(x, y), true, 300);
            }
            graph.addSeries(series);
            double average = accumulator/300;
            Toast.makeText(DataActivity.this, "Average O2 reading is " + Double.toString(average) + ", your stress level is extremely high.", Toast.LENGTH_LONG).show();
        }else if(choicePosition == 4){
            //Body Temp
            graph.removeAllSeries();
            x = 0;
            accumulator = 0;
            for(int i = 0; i < 200; i++)
            {
                Random rand = new Random();
                int max = 96;
                int min = 91;
                int value = rand.nextInt((max - min) + 1) + min;
                x += .1;
                y = value;
                accumulator += y;
                series.appendData(new DataPoint(x, y), true, 300);
            }
            graph.addSeries(series);
            double average = accumulator/200;
            Toast.makeText(DataActivity.this, "Average temperature reading is " + Double.toString(average) + ", your stress level is normal.", Toast.LENGTH_LONG).show();
        }else{
            //EMG
            graph.removeAllSeries();
            x = 0;
            for(int j = 0; j < 20; j++)
            {
                for(double i = 0; i < 10; i++)
                {
                    Random rand = new Random();
                    double max = .2;
                    double min = 0;
                    double value = min + (max - min) * rand.nextDouble();
                    x += .1;
                    y = value;
                    series.appendData(new DataPoint(x, y), true, 300);
                }
                for(double i = 0; i < 5; i++)
                {
                    Random rand = new Random();
                    double max = 1.1;
                    double min = .9;
                    double value = min + (max - min) * rand.nextDouble();
                    x += .1;
                    y = value;
                    series.appendData(new DataPoint(x, y), true, 300);
                }
            }
            graph.addSeries(series);
            Toast.makeText(DataActivity.this, "Your muscle activity is at normal levels.", Toast.LENGTH_LONG).show();
        }
    }
}
