package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;


public class MainActivity extends AppCompatActivity {

    String user_connected = "No One";
    public Boolean state = false;




    Button BtnLogOut;
    Button BtnProduct;
    Button BtnScan;

    TextView LoggedUser;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BtnLogOut = findViewById(R.id.Btn_LogOut);
        BtnProduct = findViewById(R.id.Btn_Product);
        BtnProduct.setEnabled(state);
        BtnScan = findViewById(R.id.Btn_Scan);
        LoggedUser = findViewById(R.id.Logged);


        TextView textView = (TextView) findViewById(R.id.Logged);
        textView.setText("User Connected : "+user_connected);


        BtnScan.setOnClickListener(v->
        {
            scanCode();
        });

        BtnLogOut.setOnClickListener(v->
        {
            LogOut("No One");
        });

        BtnProduct.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openProductActivity();
            }
        });

    }

    void openProductActivity(){
        Intent intent = new Intent (this,ProductActivity.class);
        startActivity(intent);
    }

    private void updateViews(String user){
        TextView textView = (TextView) findViewById(R.id.Logged);
        textView.setText("User Connected : "+user);
        if (user != "No One"){
            state=true;
            BtnProduct.setEnabled(state);
        }

    }

    private void LogOut(String user){
        TextView textView = (TextView) findViewById(R.id.Logged);
        textView.setText("User Connected : "+user);
        if (user == "No One"){
            state=false;
            BtnProduct.setEnabled(state);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Disconnected");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
            }
        }).show();
    }


    void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result ->
    {
        if (result.getContents() !=null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Logged as : ");
            String var=result.getContents();
            builder.setMessage(var);


            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    updateViews(var);
                    dialogInterface.dismiss();


                }
            }).show();
        }
    });

}