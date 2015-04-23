package com.fuzzproductions.fuzzapp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listPager = (ViewPager) findViewById(R.id.pager);

        fullList = new ArrayList<String>();
        textList = new ArrayList<String>();
        imageList = new ArrayList<String>();

        try {
            downloadJson();
        }
        catch(IOException e){
            Log.d("..........", "IOException : " + e.getMessage());
        }

    }

    public void initializePaging(String httpResponse){
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
                                URL url = new URL((String) jObject.get("data"));
//                                Bitmap imageBitmap = getImage(url);
//                                if (imageBitmap == null){
//                                    continue;
//                                }
//                                if (imageBitmap.getWidth() > 400){
//                                    imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 400, 400, false);
//                                }
//
//                                if (imageBitmap.getWidth() < 170){
//                                    imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 170, 170, false);
//                                }
                                imageList.add((String) jObject.get("data"));
                                fullList.add((String) jObject.get("data"));
                            } catch (MalformedURLException e) {
                                textList.add((String)jObject.get("data"));
                                fullList.add((String)jObject.get("data"));

                            }

                        } else if (TYPE_IMAGE.equals(jObject.get(TYPE))) {
                            try {
                                URL url = new URL((String) jObject.get("data"));
//                                Bitmap imageBitmap = getImage(url);
//                                if (imageBitmap == null){
//                                    continue;
//                                }
//                                if (imageBitmap.getWidth() > 400){
//                                    imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 400, 400, false);
//                                }
//
//                                if (imageBitmap.getWidth() < 170){
//                                    imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 170, 170, false);
//                                }
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

        if(android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
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
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String responseString = response.body().string();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
