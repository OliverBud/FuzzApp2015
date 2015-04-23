package com.fuzzproductions.fuzzapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
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

        about.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer hendrerit suscipit dolor nec hendrerit. Vivamus rutrum, turpis nec tincidunt ultricies, turpis orci facilisis nisl, a iaculis nisi enim eget nunc. Cras aliquet non turpis eu laoreet. Phasellus non suscipit leo. Etiam porta augue nec lectus porttitor euismod. Nunc et metus ante. Suspendisse consectetur sed nibh eget tempor. Nullam vestibulum lectus et efficitur rutrum. Nulla sapien ante, rutrum quis risus in, gravida finibus tortor. Aliquam fermentum ullamcorper justo iaculis pulvinar. Maecenas laoreet justo eu sagittis sodales. Sed id consequat nulla. Cras porttitor enim leo, faucibus porttitor ex dapibus id. Interdum et malesuada fames ac ante ipsum primis in faucibus. Sed efficitur lorem a neque auctor, eget malesuada lectus ultricies. Vivamus hendrerit dapibus purus, posuere blandit est ultrices eget.\n" +
                "\n" +
                "Morbi sit amet augue sollicitudin, laoreet justo sit amet, malesuada nisi. Nunc elementum tortor ut varius consequat. Ut sed ante felis. Sed mattis metus eget ante venenatis vehicula. Praesent metus dolor, volutpat et arcu quis, vulputate rutrum eros. Nam eget egestas augue. Fusce blandit mi non orci euismod, sit amet auctor purus dictum. Duis et augue felis. Aliquam in scelerisque odio. Nam elit enim, lobortis vel vestibulum eget, venenatis hendrerit justo. Donec molestie nibh turpis, vitae fringilla leo fermentum a. Aliquam faucibus mattis augue, eu hendrerit metus. Aliquam pharetra urna vitae consectetur sagittis. Maecenas vitae viverra est. Cras sit amet mauris elementum, imperdiet diam ut, vulputate dui. Vivamus vestibulum ultricies ex vitae sodales.\n" +
                "\n" +
                "Quisque porta, odio quis iaculis molestie, odio ipsum vulputate nulla, sit amet convallis orci purus non nisi. Mauris id viverra purus. Fusce interdum sem nec metus tincidunt tincidunt. Duis pretium, mi in rhoncus finibus, justo neque hendrerit ipsum, quis rutrum eros libero eu sapien. Nulla fringilla blandit orci ut vehicula. Integer ultricies, enim vel iaculis feugiat, tortor arcu gravida quam, id viverra diam ante malesuada diam. Sed auctor sollicitudin sem. Morbi tincidunt a sapien et mattis.\n" +
                "\n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras facilisis, urna ut iaculis porta, justo ante varius tortor, at euismod arcu ipsum sit amet turpis. Etiam vel pretium nisi. Praesent a accumsan est. Praesent eu nibh varius, tincidunt dolor nec, consectetur tortor. Suspendisse rutrum hendrerit turpis, id aliquet diam rhoncus et. Cras id mauris pulvinar, varius sem vitae, congue enim. Aenean euismod ex sed justo suscipit lacinia eu et enim. Sed pharetra vehicula neque id faucibus. Etiam id gravida lorem. Mauris vitae est non felis ullamcorper fringilla non a ex.\n" +
                "\n" +
                "Donec efficitur vestibulum varius. Donec sed tristique est, sodales feugiat odio. Phasellus ornare congue sapien, sed rutrum nisl consequat at. Integer sodales molestie est non fringilla. Donec id odio justo. In lobortis arcu quis justo commodo dictum. Duis non tortor at odio luctus efficitur at at nulla. Donec sed est varius, gravida massa non, lobortis risus. Aliquam malesuada iaculis molestie. Aenean elit lorem, ");

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
}
