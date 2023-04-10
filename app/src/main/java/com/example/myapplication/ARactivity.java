package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ARactivity extends AppCompatActivity {

    TextView productname;

    Button BtnReturn;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aractivity);
        productname = findViewById(R.id.product_name);
        BtnReturn = findViewById(R.id.Btn_Return);
        Bundle b = getIntent().getExtras();
        int value = -1; // or other values
        if(b != null)
            value = b.getInt("key");



        TextView textView = (TextView) findViewById(R.id.product_name);
        textView.setText("Product : "+value);

        BtnReturn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                openProductActivity();
            }
        });
    }

    void openProductActivity(){
        Intent intent = new Intent (this,ProductActivity.class);
        finish();
        startActivity(intent);
    }


}