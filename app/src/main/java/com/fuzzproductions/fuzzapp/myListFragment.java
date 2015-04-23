package com.fuzzproductions.fuzzapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by oliverbud on 4/22/15.
 */
public class myListFragment extends Fragment {

    ListView list;
    ArrayList<String> data;
    String[] displayData;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("..........", "onCreateView Fragment");

        final View view = inflater.inflate(R.layout.fragment_layout, container, false);

        list = (ListView)view.findViewById(R.id.listView);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean result = ((MainActivity)getActivity()).clickedImage((ImageView) view.findViewById(R.id.image), position);
                if (!result){
                    ((MainActivity)getActivity()).clickedText();
                }
            }
        });

        displayData = new String[data.size()];
        for (int i = 0; i < data.size(); i ++){
            displayData[i] = data.get(i);
        }

        myArrayAdapter listAdapter = new myArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, displayData);
        list.setAdapter(listAdapter);

        return view;
    }

    public static myListFragment newInstance(ArrayList<String> data){
        myListFragment fragment = new myListFragment();
        fragment.data = data;
        Log.d("..........", "instantiate newFramgnet");
        return fragment;
    }
}
