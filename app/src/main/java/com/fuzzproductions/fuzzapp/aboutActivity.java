package com.fuzzproductions.fuzzapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by oliverbud on 4/23/15.
 */
public class aboutActivity extends ActionBarActivity{

    TextView about;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        about = (TextView)findViewById(R.id.about);
        LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        alp.setMargins(20, 20, 20, 20);
        about.setLayoutParams(alp);


        about.setLinksClickable(true);
        about.setMovementMethod(LinkMovementMethod.getInstance());
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLink();
            }
        });

        about.setText(Html.fromHtml("<p>HELLO! thanks for checking out this app.</p> \n<p>I wrote it, my name is Oliver Budiardjo.</p> \n \n<p>I used some internet resources to help me along. " +
                "I used <a href=\\\"http://codehenge.net/blog/2011/06/android-development-tutorial-asynchronous-lazy-loading-and-caching-of-listview-images/\\\">this really helpful post</a> as a guide to implementing lazy loading</p>" +
                "\n<p>And I used the Google documentation to implement the image animation from list to activity.</p>" +
                "\n \n</p>One thing that I had trouble with was dealing with image sizes/memory and scaling. Some of the images sizes were really big" +
                "and others were really small. I tried to scale as best as I could to make things look alright, but had to" +
                "also contend with memory issues. Also if I had more time I would get more in depth with the lazy loading queue " +
                "and a try to speed up the load/display time</p>" +
                "<p>Also I did not spend much time styling things. That can be a rabbit hole which I did not want to go down </p>"));

        fixTextView(about);
    }
    private void fixTextView(TextView tv) {
        SpannableString current=(SpannableString)tv.getText();
        URLSpan[] spans=
                current.getSpans(0, current.length(), URLSpan.class);

        for (URLSpan span : spans) {
            int start=current.getSpanStart(span);
            int end=current.getSpanEnd(span);

            current.removeSpan(span);
            current.setSpan(new DefensiveURLSpan(span.getURL()), start, end,
                    0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showLink(){
        String url = "http://codehenge.net/blog/2011/06/android-development-tutorial-asynchronous-lazy-loading-and-caching-of-listview-images/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivityForResult(i, 0);
    }

    private static class DefensiveURLSpan extends URLSpan {
        public DefensiveURLSpan(String url) {
            super(url);
        }

        @Override
        public void onClick(View widget) {
            try {
                android.util.Log.d(getClass().getSimpleName(), "Got here!");
                super.onClick(widget);
            }
            catch (ActivityNotFoundException e) {
            }
        }
    }
}
