package com.example.swipedeletedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private List<String> mdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RecyclerView) findViewById(R.id.recyclerView);
        mdata = getData();
        setAdapter();
    }

    private List<String> getData() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            result.add("name：" + i);
        }
        return result;
    }

    private void setAdapter() {
        BaseRvAdapter adapter = new RvAdapter(this, mdata);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }
}
