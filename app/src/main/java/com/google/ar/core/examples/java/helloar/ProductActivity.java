package com.google.ar.core.examples.java.helloar;


import static com.google.ar.core.examples.java.helloar.Menu.SHARED_PREFS;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private static final String TAG = "0";

    static List<String> list = new ArrayList<>();
    Button BtnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        BtnHome = findViewById(R.id.Btn_Home);


        Log.d(TAG, "List pour product list: ");
        for (String data : list) {
            System.out.println(data);
        }

        ListView listProduct = findViewById(R.id.listProduct);
        List<String> listproduct = getIntent().getStringArrayListExtra("maListe");


        for (String element : listproduct) {
            Log.d("MaListe", element);
        }



        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,listproduct);
        Log.d(TAG, "listproduct:"+ listproduct);
        listProduct.setAdapter(arrayAdapter);



        listProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object produit = adapterView.getAdapter().getItem(i);
                Launch_ARactivity((String) produit);
            }
        });

        BtnHome.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void Launch_ARactivity(String nom){
        Intent intent = new Intent(ProductActivity.this, HelloArActivity.class);
        intent.putExtra("name",nom);
        startActivity(intent);
        finish();
    }
}