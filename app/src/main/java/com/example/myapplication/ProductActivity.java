package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    Button BtnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        BtnHome = findViewById(R.id.Btn_Home);

        ListView listProduct = findViewById(R.id.listProduct);

        List<String> list = new ArrayList<>();
        list.add("Produit 1");
        list.add("Produit 2");
        list.add("Produit 3");
        list.add("Produit 4");

        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,list);
        listProduct.setAdapter(arrayAdapter);

        listProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0){
                    //product 1
                    Launch_ARactivity(i+1);
                } else if (i==1){
                    //product 2
                    Launch_ARactivity(i+1);
                } else if (i==2){
                    //product 3
                    Launch_ARactivity(i+1);
                } else if (i==3){
                    //product 4
                    Launch_ARactivity(i+1);
                } else{

                }
            }
        });

        BtnHome.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    void Launch_ARactivity(int position){
        Intent intent = new Intent(ProductActivity.this, ARactivity.class);
        Bundle b = new Bundle();
        b.putInt("key", position); //PRODUCT ID
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
        finish();
    }

    void openMainActivity(){
        Intent intent = new Intent (this,MainActivity.class);
        startActivity(intent);
    }
}