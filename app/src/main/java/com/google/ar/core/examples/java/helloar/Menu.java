package com.google.ar.core.examples.java.helloar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Menu extends AppCompatActivity {

    private String username;
    public Boolean state = false;

    public Boolean valid;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";

    public static final String STATET = "state";


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
        state = statet;
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

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void openProductActivity() {
        Intent intent = new Intent(this, ProductActivity.class);
        startActivity(intent);
    }

    private void updateViews(String user) {
        TextView textView = (TextView) findViewById(R.id.Logged);
        textView.setText("User Connected : " + user);
        if (user == "Seller") {
            state = true;
            BtnProduct.setEnabled(state);
        }
        if (user == "No One") {
            state = false;
            BtnProduct.setEnabled(state);
        }
        Log.d(TAG, "Update do: " + user + " " + state);
    }

    private void LogOut(String user) {
        TextView textView = (TextView) findViewById(R.id.Logged);
        textView.setText("User Connected : " + user);
        if (user == "No One") {
            state = false;
            BtnProduct.setEnabled(state);
            saveData(user, state);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
        builder.setTitle("Disconnected");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }


    private void saveData(String log, Boolean logg) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        editor.putString(TEXT, log);
        editor.putBoolean(STATET, logg);

        editor.apply();
        if (log == "No One")
            Toast.makeText(this, "Seller DisLogged", Toast.LENGTH_SHORT).show();
        if (log == "Seller")
            Toast.makeText(this, "Seller Logged", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Content saved: " + log + " " + logg);

    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        text = sharedPreferences.getString(TEXT, "No One");
        statet = sharedPreferences.getBoolean(STATET, false);
        Log.d(TAG, "Contenu read: " + text + " " + statet);
    }

    private void askAPI(String token) {
        String urlString = "https://615f5fb4f7254d0017068109.mockapi.io/api/v1/products";
        // String urlString = "https://16.16.4.35:444/api/v1/products";
        int responseCode = 0;
        try {
            // désactive la vérification du certificat SSL car auto-signé
            // disableSSLCertificateChecking();
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // Timeout de connexion de 5 secondes
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.connect();
            responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "REQUEST: L'URL est valide et atteignable.");
                valid = true;
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                StringBuilder data = new StringBuilder();
                while (line != null) {
                    line = bufferedReader.readLine();
                    data.append(line);
                }
                List<Object> products = convertResponseToProducts(String.valueOf(data));

                // products contien la liste des produits
                // voici comment itérer sur cette liste
                for (Object product : products) {
                    System.out.println(product.toString());
                    Log.d(TAG, "PRODUCT: " + product.toString());
                }

            } else {
                Log.d(TAG, "REQUEST: L'URL est valide mais n'est pas atteignable code de réponse HTTP : " + responseCode + ").");
                valid = false;
            }
        } catch (MalformedURLException e) {
            Log.d(TAG, "REQUEST: L'URL est invalide. code de réponse HTTP : " + responseCode + ").");
            valid = false;
        } catch (IOException e) {
            Log.d(TAG, "REQUEST: L'URL est invalide ou n'est pas atteignable. code de réponse HTTP : " + responseCode + ").");
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

    private void disableSSLCertificateChecking() {
        // Create a trust manager that does not validate certificate chains

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = (hostname, session) -> true;

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

    }


    private List<Object> convertResponseToProducts(String response) {
        List<Object> products = new ArrayList<Object>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject product = jsonArray.getJSONObject(i);
                products.add(product);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return products;
    }

    private void startApiThread(final String token) {
        Thread apiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                askAPI(token);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (valid == true) {
                            username = "Seller";
                            state = true;
                            saveData(username, state);
                            updateViews(username);

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                            builder.setTitle("Not authenticated, Please contact administrator");

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
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

}