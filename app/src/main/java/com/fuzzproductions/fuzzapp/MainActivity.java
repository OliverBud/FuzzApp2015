package com.fuzzproductions.fuzzapp;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final String JSON_ENDPOINT = "http://quizzes.fuzzstaging.com/quizzes/mobile/1/data.json";
    private static final String TYPE = "type";
    private static final String TYPE_TEXT = "text";
    private static final String TYPE_IMAGE = "image";

    String JsonString;
    JSONArray jsonList;

    ArrayList<String> fullList;
    ArrayList<String> imageList;
    ArrayList<String> textList;

    ViewPager listPager;
    myFragmentPagerAdapter listPagerAdapter;
    myListFragment fullListFragment;
    myListFragment textListFragment;
    myListFragment imageListFragment;

    List<myListFragment> fragments;

    FrameLayout displayLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listPager = (ViewPager) findViewById(R.id.pager);
        displayLayout = (FrameLayout) findViewById(R.id.displayLayout);

        fullList = new ArrayList<String>();
        textList = new ArrayList<String>();
        imageList = new ArrayList<String>();

        try {
            downloadJson();
        }
        catch(Exception e){
            Log.d("..........", "Exception : " + e.getMessage());
            displayNoConnection();
        }

    }

    ProgressBar spinner;
    TextView noConnectionImage;
    Button retry;
    View noConnectionLayout;

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void displayNoConnection(){
        Log.d("..........", "displayNoConnection");

        listPager.setVisibility(View.GONE);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);

                noConnectionLayout = inflater.inflate(R.layout.noconnection_layout,null);
                spinner = (ProgressBar) noConnectionLayout.findViewById(R.id.spinner);

                spinner.setIndeterminate(true);

                noConnectionImage = (TextView) noConnectionLayout.findViewById(R.id.noConnectionImage);

                Drawable drawable = getResources().getDrawable(android.R.drawable.btn_star);
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);

                noConnectionImage.setText(":/");
                noConnectionImage.setTextSize(150);
                noConnectionImage.setTextColor(Color.BLACK);
                retry = (Button) noConnectionLayout.findViewById(R.id.retryButton);
                retry.setText("No InternetConnection; Retry");

                spinner.setVisibility(View.GONE);

                retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            retry.setVisibility(View.GONE);
                            noConnectionImage.setVisibility(View.GONE);
                            spinner.setVisibility(View.VISIBLE);
                            downloadJson();
                        }
                        catch(IOException e){
                            retry.setVisibility(View.VISIBLE);
                            noConnectionImage.setVisibility(View.VISIBLE);
                            spinner.setVisibility(View.GONE);
                        }
                    }
                });


                FrameLayout.LayoutParams rlp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
                displayLayout.addView(noConnectionLayout, rlp);
                Log.d("..........", "noConnection layout added");

            }
        });



    }

    public void cleanupNoConnection(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (noConnectionLayout != null){
                    displayLayout.removeView(noConnectionLayout);
                    noConnectionLayout = null;
                }

            }
        });

    }

    public void initializePaging(String httpResponse){

        cleanupNoConnection();
        JsonString = httpResponse;
        try {
            jsonList = new JSONArray(JsonString);
        }
        catch (JSONException e){
            return;
        }


            for (int i = 0; i < jsonList.length(); i++) {
                try {
                    JSONObject jObject = jsonList.getJSONObject(i);

                    if (jObject.has(TYPE)) {

                        if (TYPE_TEXT.equals(jObject.get(TYPE))) {
                            try {
                                if (((String) jObject.get("data")).isEmpty()){
                                    continue;
                                }
                                URL url = new URL((String) jObject.get("data"));

                                imageList.add((String) jObject.get("data"));
                                fullList.add((String) jObject.get("data"));
                            } catch (MalformedURLException e) {
                                textList.add((String)jObject.get("data"));
                                fullList.add((String)jObject.get("data"));

                            }

                        } else if (TYPE_IMAGE.equals(jObject.get(TYPE))) {
                            try {
                                URL url = new URL((String) jObject.get("data"));

                                imageList.add((String) jObject.get("data"));
                                fullList.add((String) jObject.get("data"));
                            } catch (MalformedURLException e) {

                            }
                        }
                    }
                }
                catch(JSONException j){
                    continue;
                }
            }

            fullListFragment = myListFragment.newInstance(fullList);
            textListFragment = myListFragment.newInstance(textList);
            imageListFragment = myListFragment.newInstance(imageList);

            fragments = new ArrayList();

            fragments.add(fullListFragment);
            fragments.add(textListFragment);
            fragments.add(imageListFragment);


            listPagerAdapter = new myFragmentPagerAdapter(getSupportFragmentManager(), fragments);
            listPager.setOffscreenPageLimit(2);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("..........", "setListAdapter");
                    listPager.setAdapter(listPagerAdapter);
                }
            });




    }

    public void clickedText(){
        Intent intent = new Intent(this, webViewActivity.class);
        this.startActivityForResult(intent, 0);
    }

    public boolean clickedImage(ImageView image, int position){
        String transitionName;

        if (((BitmapDrawable)image.getDrawable()) == null){
            return false;
        }

        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        if (bitmap == null){
            return false;
        }

        if (image.getVisibility() == View.GONE){
            return false;
        }



        if(android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            if (image.getTransitionName() == null){
                return false;
            }
            transitionName = "sharedElement_" + position;
            getWindow().setSharedElementExitTransition(new Explode());

            Log.d("...........", "transition name from image: " + image.getTransitionName());

            Intent intent = new Intent(this, imageActivity.class);
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
            intent.putExtra("byteArray", bs.toByteArray());
            intent.putExtra("transitionName", transitionName);

            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, image, transitionName);
            startActivity(intent, options.toBundle());
        }
        else{
            Intent intent = new Intent(this, imageActivity.class);
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
            intent.putExtra("byteArray", bs.toByteArray());
            startActivityForResult(intent, 0);

        }
        return true;
    }

    public void downloadJson() throws IOException{

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(JSON_ENDPOINT)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                cleanupNoConnection();
                displayNoConnection();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String responseString = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listPager.setVisibility(View.VISIBLE);
                    }
                });
                initializePaging(responseString);
            }
    });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent aboutIntent = new Intent(this, aboutActivity.class);
            startActivityForResult(aboutIntent, 0);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


}
