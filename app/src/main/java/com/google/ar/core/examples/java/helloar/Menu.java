package com.google.ar.core.examples.java.helloar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Menu extends AppCompatActivity {

    private String username;
    public Boolean state = false;

    public Boolean valid;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT="text";
    public static final String STATET= "state";



    private String text;
    private Boolean statet;


    Button BtnLogOut;
    Button BtnProduct;
    Button BtnScan;
    TextView LoggedUser;


    private static final String TAG = "QRCodeScanner";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.activity_home);
        BtnLogOut = findViewById(R.id.Btn_LogOut);
        BtnProduct = findViewById(R.id.Btn_Product);

        BtnScan = findViewById(R.id.Btn_Scan);
        LoggedUser = findViewById(R.id.Logged);

        TextView textView = (TextView) findViewById(R.id.Logged);

        loadData();
        state=statet;
        textView.setText("User Connected : " + text);
        updateViews(text);
        BtnProduct.setEnabled(state);

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


                startApiThread(result.getContents());





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

    @SuppressLint("SetTextI18n")
    private void updateViews(String user) {
        TextView textView = (TextView) findViewById(R.id.Logged);
        textView.setText("User Connected : " + user);
        if (user == "Seller") {
            state = true;
            BtnProduct.setEnabled(state);
        }
        if(user == "No One"){
            state = false;
            BtnProduct.setEnabled(state);
        }
        Log.d(TAG, "Update do: " + user + " " + state);
    }

    private void LogOut(String user){
        TextView textView = (TextView) findViewById(R.id.Logged);
        textView.setText("User Connected : "+user);
        if (user == "No One"){
            state=false;
            BtnProduct.setEnabled(state);
            saveData(user,state);
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


    private void saveData(String log,Boolean logg){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT,log);
        editor.putBoolean(STATET,logg);

        editor.apply();
        if (log == "No One")
            Toast.makeText(this,"Seller DisLogged",Toast.LENGTH_SHORT).show();
        if (log == "Seller")
            Toast.makeText(this,"Seller Logged",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Contenu saved: " + log + " " + logg);

    };

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        text = sharedPreferences.getString(TEXT,"No One");
        statet = sharedPreferences.getBoolean(STATET,false);
        Log.d(TAG, "Contenu read: " + text + " " + statet);
    };

    private void askAPI(String token) {
        //String urlString = "http://13.53.243.55:444/api/v1/products";
        String urlString = "https://www.google.com";

        int responseCode = 0;
        String concat = null;
        URL url = null;
        concat = urlString + " " + token;
        try {
            url = new URL(urlString);
            Log.d(TAG, "REQUEST:test1 ");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Log.d(TAG, "REQUEST:test2 ");
            connection.setRequestMethod("GET");
            Log.d(TAG, "REQUEST:test3 ");
            connection.setConnectTimeout(5000); // Timeout de connexion de 5 secondes
            Log.d(TAG, "REQUEST:test4 ");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            Log.d(TAG, "REQUEST:test5 ");
            //trustSelfSignedSSL();
            Log.d(TAG, "REQUEST:test5.5 ");
            connection.connect();
            Log.d(TAG, "REQUEST:test6 "+connection.toString());
            Log.d(TAG, "CONNECTION URL: " + connection.getURL());
            Log.d(TAG, "REQUEST METHOD: " + connection.getRequestMethod());
            Log.d(TAG, "REQUEST PROPERTIES: " + connection.getRequestProperty("Authorization"));
            responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "REQUEST: L'URL est valide et atteignable.");
                valid = true;

            } else {
                Log.d(TAG, "REQUEST: L'URL est valide mais n'est pas atteignable (concat : " + concat + " code de réponse HTTP : " + responseCode + ").");
                valid = false;
            }
        } catch (MalformedURLException e) {
            Log.d(TAG, "REQUEST: L'URL est invalide.(concat : " + concat + " code de réponse HTTP : " + responseCode + ").");
            valid = false;
        } catch (IOException e) {
            Log.d(TAG, "REQUEST: L'URL est invalide ou n'est pas atteignable. (concat : " + concat + " code de réponse HTTP : " + responseCode + ").");
            valid = false;
        }
        //catch (NoSuchAlgorithmException e) {
        //    Log.d(TAG, "REQUEST sll no such");
        //    throw new RuntimeException(e);

        //}
        //catch (KeyManagementException e) {
        //    Log.d(TAG, "REQUEST sll key");
        //    throw new RuntimeException(e);
        //}
    }

    private void startApiThread(final String token) {
        Thread apiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                askAPI(token);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (valid == true){
                            username="Seller";
                            state=true;
                            saveData(username,state);
                            updateViews(username);

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                            builder.setTitle("Not authenticated, Please contact administrator");

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                        }
                    }
                });
            }
        });
        apiThread.start();
    }

    private static void trustSelfSignedSSL() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }


}