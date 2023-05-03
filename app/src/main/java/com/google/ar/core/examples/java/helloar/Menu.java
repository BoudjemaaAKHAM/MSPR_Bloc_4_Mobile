package com.google.ar.core.examples.java.helloar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class Menu extends AppCompatActivity {

    String user_connected = "No One";
    public Boolean state = false;


    Button BtnLogOut;
    Button BtnProduct;
    Button BtnScan;

    TextView LoggedUser;

    private static final String TAG = "QRCodeScanner";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BtnLogOut = findViewById(R.id.Btn_LogOut);
        BtnProduct = findViewById(R.id.Btn_Product);
        BtnProduct.setEnabled(state);
        BtnScan = findViewById(R.id.Btn_Scan);
        LoggedUser = findViewById(R.id.Logged);


        TextView textView = (TextView) findViewById(R.id.Logged);
        textView.setText("User Connected : " + user_connected);


        BtnLogOut.setOnClickListener(v ->
        {
            LogOut("No One");
        });

        //BtnScan.setOnClickListener(v ->
        //{
            //LogIn("Test");

        //});
        BtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(
                        Menu.this
                );
                intentIntegrator.setPrompt("For flash use volume up");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(Capture.class);
                intentIntegrator.initiateScan();
            }
        });


        BtnProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProductActivity();
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Résultat du scan du QR code
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Log.d(TAG, "Scan annulé");
                Toast.makeText(this, "Scan annulé", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Contenu du code QR : " + result.getContents());
                Toast.makeText(this, "Contenu du code QR : " + result.getContents(), Toast.LENGTH_SHORT).show();
                String var=result.getContents();
                updateViews(var);
                // Utilisez le contenu du QR code comme vous le souhaitez dans votre application
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void openProductActivity(){
        Intent intent = new Intent (this,ProductActivity.class);
        startActivity(intent);
    }

    private void updateViews(String user) {
        TextView textView = (TextView) findViewById(R.id.Logged);
        textView.setText("User Connected : " + user);
        if (user != "No One") {
            state = true;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
        builder.setTitle("Disconnected");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private void LogIn(String user){
        TextView textView = (TextView) findViewById(R.id.Logged);
        textView.setText("User Connected : "+user);
        if (user != "No One"){
            state=true;
            BtnProduct.setEnabled(state);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
        builder.setTitle("Connected as" + user);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                updateViews(user);
                dialogInterface.dismiss();
            }
        }).show();
    }


}