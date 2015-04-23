package com.fuzzproductions.fuzzapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

/**
 * Created by oliverbud on 4/22/15.
 */
public class myArrayAdapter extends ArrayAdapter {

    Context context;
    String[] values;
    public ImageManager imageManager;
    ViewHolder holder;

    public static class ViewHolder{
        public TextView text;
        public ImageView image;
    }

    public myArrayAdapter(Context context, int resourceId, String[] values) {
        super(context, resourceId, values);
        this.context = context;
        this.values = values;
        imageManager =
                new ImageManager(((Activity)context).getApplicationContext());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.row_layout, parent, false);
            holder = new ViewHolder();

            holder.text = (TextView)rowView.findViewById(R.id.text);
            holder.image = (ImageView)rowView.findViewById(R.id.image);

            rowView.setTag(holder);
        }
        else{
            holder=(ViewHolder)rowView.getTag();
        }
        LinearLayout rowFrame = (LinearLayout) rowView.findViewById(R.id.rowFrame);




             try{
                 new URL(values[position]);
                 if (holder.image.getVisibility() != View.VISIBLE ){
                     holder.image.setVisibility(View.VISIBLE);
                 }
                 holder.text.setVisibility(View.GONE);
                 holder.image.setTag(values[position]);
                 final String name = "sharedElement_" + position;

                 if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                     if (holder.image.getTransitionName() == null) {
                         holder.image.setTransitionName(name);
                     }

                     Log.d("..........", "transitionName: " + holder.image.getTransitionName());

                 }

                 imageManager.displayImage(values[position], (Activity) this.context, holder.image);
             }
             catch(MalformedURLException e) {
                 holder.image.setVisibility(View.GONE);

                 if (holder.text.getVisibility() != View.VISIBLE ){
                     holder.text.setVisibility(View.VISIBLE);
                 }
                 holder.text.setText(Html.fromHtml(values[position]));

             }
        rowFrame.setTag(holder);


        return rowFrame;
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(30);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

}
