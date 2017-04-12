package edu.daniel.project7;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText et;
    TextView descriptionTv;
    TextView temperatureTv;
    TextView humidityTv;
    TextView windSpeedTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et = (EditText) findViewById(R.id.et);
        descriptionTv = (TextView) findViewById(R.id.descriptionTv);
        temperatureTv = (TextView) findViewById(R.id.temperatureTv);
        humidityTv = (TextView) findViewById(R.id.humidityTv);
        windSpeedTv = (TextView) findViewById(R.id.windSpeedTv);
    }

    public void getWeather(View v) {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        String s = "http://api.openweathermap.org/data/2.5/weather?q=";
        String city = et.getText().toString();
        String key = "ccfd47de0d2c7d32710250bfc8eec407";
        s += city + "&APPID=" + key;

        new NetworkingTask().execute(s);
    }

    class NetworkingTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                if (stream == null)
                    return null;
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                return buffer.toString();
            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            String s = result;
            try {
                JSONObject jsonObject = new JSONObject(s);

                JSONArray weatherArray = jsonObject.getJSONArray("weather");
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                String description = weatherObject.getString("description");
                descriptionTv.setText(description);

                JSONObject mainObject = jsonObject.getJSONObject("main");
                String temperature = mainObject.getString("temp");
                double celsius = Double.parseDouble(temperature) - 273.0;
                double fahrenheit = Math.round((((celsius * 9.0 / 5.0) + 32.0) * 100.0)) / 100.0;
                temperature = String.valueOf(fahrenheit);
                temperatureTv.setText(temperature + " °F");

                String humidity = mainObject.getString("humidity");
                humidityTv.setText(humidity + "%");

                JSONObject windObject = jsonObject.getJSONObject("wind");
                String windSpeed = windObject.getString("speed");
                windSpeedTv.setText(windSpeed + " mph");
            } catch (Exception ex) {
            }
        }
    }
}