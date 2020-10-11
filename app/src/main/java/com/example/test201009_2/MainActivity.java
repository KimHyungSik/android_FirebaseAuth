package com.example.test201009_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ListData> arrayList;
    private MainAdapter mainAdapter;
    private FirebaseAuth mAuth;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.mainRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        //Firebase Instance reset
        mAuth = FirebaseAuth.getInstance();

        //List reset
        arrayList = new ArrayList<>();
        mainAdapter = new MainAdapter(arrayList, this);

        recyclerView.setAdapter(mainAdapter);

        Button button = findViewById(R.id.addItem_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListData listData = new ListData("mountains.jpg",i++);
                arrayList.add(listData);
                mainAdapter.notifyDataSetChanged();
            }
        });

       Button LogOut = findViewById(R.id.logOut);
       LogOut.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               FirebaseAuth.getInstance().signOut();
               startActivity(new Intent(getApplicationContext(), Login.class) );
           }
       });
    }
}