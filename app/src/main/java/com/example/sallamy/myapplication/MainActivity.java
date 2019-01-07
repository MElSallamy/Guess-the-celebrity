package com.example.sallamy.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {


    Button bu0;
    Button bu1;
    Button bu2;
    Button bu3;
    ImageView viewer;
    ArrayList<String> imgsurls = new ArrayList<String>();
    ArrayList<String> names = new ArrayList<String>();
    String[] answers = new String[4];
    int choose_img;
    int locationOfCorrectAnswer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewer = (ImageView) findViewById(R.id.viewer);
        bu0 = (Button) findViewById(R.id.button0);
        bu1 = (Button) findViewById(R.id.button1);
        bu2 = (Button) findViewById(R.id.button2);
        bu3 = (Button) findViewById(R.id.button3);

        DownloadTask task = new DownloadTask();
        String result = null;

        try {
            //execute link
            result = task.execute("http://www.posh24.se/kandisar").get();
            // spilt the source page
            String[] splitResult = result.split("<div class=\"col-xs-12 col-sm-6 col-md-4\">");
            //pattern to find out img link
            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);
            while (m.find()) {
                // put links in arr of Strings
                imgsurls.add(m.group(1));
            }
            // pattern to find out name
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);
            while (m.find()) {
                //put names in arr of Strings
                names.add(m.group(1));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        // create first Q
        createNewQuestion();
    }

    //creating new Q
    public void createNewQuestion() {

        Random random = new Random();
        // creating random number to choose img
        choose_img = random.nextInt(imgsurls.size());
        ImageDownloader imgdownloader = new ImageDownloader();
        Bitmap imgsrc = null;
        try {
            imgsrc = imgdownloader.execute(imgsurls.get(choose_img)).get();
            viewer.setImageBitmap(imgsrc);
            locationOfCorrectAnswer = random.nextInt(4);
            int incorrectAnswerLocation;
            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers[i] = names.get(choose_img);
                } else {
                    incorrectAnswerLocation = random.nextInt(imgsurls.size());
                    while (incorrectAnswerLocation == choose_img) {
                        incorrectAnswerLocation = random.nextInt(imgsurls.size());
                    }
                    answers[i] = names.get(incorrectAnswerLocation);
                }

                bu0.setText(answers[0]);
                bu1.setText(answers[1]);
                bu2.setText(answers[2]);
                bu3.setText(answers[3]);

            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    //onClick method to choose answer
    public void choose(View view) {
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))) {
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Wrong! It was " + names.get(choose_img), Toast.LENGTH_LONG).show();
        }
        createNewQuestion();
    }


    // conn the internet and download task
    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    //download imgs task
    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

}