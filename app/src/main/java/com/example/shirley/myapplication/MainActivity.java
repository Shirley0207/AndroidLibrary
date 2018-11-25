package com.example.shirley.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shirley.animatedfloatexpandablelistview.AnimatedFloatELV;
import com.shirley.animatedfloatexpandablelistview.entity.GroupData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AnimatedFloatELV listView = findViewById(R.id.list_view);
        listView.setData(constructData());
    }

    private List<GroupData> constructData() {
        List<GroupData> data = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            GroupData temp = new GroupData();
            temp.setName("Group " + i);
            List<GroupData> children = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                GroupData child = new GroupData();
                child.setName("children " + i + "-" + j);
                children.add(child);
            }
            temp.setChildren(children);
            data.add(temp);
        }
        return data;
    }
}
