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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
    public static final String TTOKEN = "ttoken";
    public static final String STATET = "state";
    public static final String LIST = "list";
    public static final String NNOW = "nnow";

    List<Object> products;

    static List<String> list = new ArrayList<>();


    private String text;
    private Boolean statet;

    private String ttoken;

    private String tok;

    private String now;


    Button BtnLogOut;
    Button BtnProduct;
    Button BtnScan;

    Button BtnRefresh;

    TextView LoggedUser;


    private static final String TAG = "QRCodeScanner";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.activity_home);
        BtnLogOut = findViewById(R.id.Btn_LogOut);
        BtnProduct = findViewById(R.id.Btn_Product);
        BtnRefresh = findViewById(R.id.Btn_Refresh);

        BtnScan = findViewById(R.id.Btn_Scan);
        LoggedUser = findViewById(R.id.Logged);

        TextView textView = (TextView) findViewById(R.id.Logged);
        TextView textViewrefresh = (TextView) findViewById(R.id.LastRefresh);


        loadData();
        state = statet;
        textView.setText("User Connected : " + text);
        textViewrefresh.setText("Last Refresh : " + now);
        updateViews(text);
        BtnProduct.setEnabled(state);
        BtnRefresh.setEnabled(state);


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
                openProductActivity(tok);
            }
        });

        BtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textViewrefresh = (TextView) findViewById(R.id.LastRefresh);
                now = String.valueOf(new Date());
                TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
                textViewrefresh.setText("Last Refresh : " + now);
                startApiThread(tok);
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
                tok = result.getContents();

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void openProductActivity(String toke) {
        Intent intent = new Intent(this, ProductActivity.class);
        loadList();

        intent.putStringArrayListExtra("maListe", (ArrayList<String>) list);
        Bundle c = new Bundle();
        c.putString("key", toke); //PRODUCT ID
        intent.putExtras(c); //Put your id to your next Intent
        startActivity(intent);
    }

    private void updateViews(String user) {
        TextView textView = (TextView) findViewById(R.id.Logged);
        textView.setText("User Connected : " + user);
        if (user == "Seller") {
            state = true;
            BtnProduct.setEnabled(state);
            BtnRefresh.setEnabled(state);
            TextView textViewrefresh = (TextView) findViewById(R.id.LastRefresh);
            now = String.valueOf(new Date());
            TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
            textViewrefresh.setText("Last Refresh : " + now);

        }
        if (user == "No One") {
            state = false;
            BtnProduct.setEnabled(state);
            BtnRefresh.setEnabled(state);
            TextView textViewrefresh = (TextView) findViewById(R.id.LastRefresh);
            now = null;
            textViewrefresh.setText("Last Refresh : " + text);
        }
        Log.d(TAG, "Update do: " + user + " " + state);
    }

    private void LogOut(String user) {
        TextView textView = (TextView) findViewById(R.id.Logged);
        textView.setText("User Connected : " + user);
        TextView textViewrefresh = (TextView) findViewById(R.id.LastRefresh);
        textViewrefresh.setText("Last Refresh : " + user);
        if (user == "No One") {
            state = false;
            BtnProduct.setEnabled(state);
            BtnRefresh.setEnabled(state);
            saveData(user, state, null,user);
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


    @SuppressLint("SuspiciousIndentation")
    private void saveData(String log, Boolean logg, String tok, String nnow) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, log);
        editor.putBoolean(STATET, logg);
        editor.putString(TTOKEN, tok);
        editor.putString(NNOW, String.valueOf(nnow));

        editor.apply();
        if (log == "No One")
            Toast.makeText(this, "Seller DisLogged", Toast.LENGTH_SHORT).show();
        if (log == "Seller")
            //Toast.makeText(this, "Seller Logged", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Content saved: " + log + " " + logg + " " + tok + " " + nnow);

    }






    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        text = sharedPreferences.getString(TEXT, "No One");
        statet = sharedPreferences.getBoolean(STATET, false);
        ttoken = sharedPreferences.getString(TTOKEN, "No One");
        now = sharedPreferences.getString(NNOW, "No One");

        Log.d(TAG, "Contenu read: " + text + " " + statet + "" + ttoken);
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
                products = convertResponseToProducts(String.valueOf(data));

                // products contient la liste des produits
                // voici comment itérer sur cette liste
                for (Object product : products) {
                    System.out.println(product.toString());
                    Log.d(TAG, "PRODUCT: " + product.toString());
                }

                Convert(products);

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

    public class Product {
        private String id;
        private String createdAt;
        private String name;
        private String price;
        private String description;
        private String color;
        private int stock;


        public Product( String id, String name,String price ,String description , String color, int stock,String createdAt) {
            this.id = id;
            this.createdAt = createdAt;
            this.name = name;
            this.price = price;
            this.description = description;
            this.color = color;
            this.stock = stock;

        }

        public String getId() {
            return id;
        }
        public String getCreatedAt() {
            return createdAt;
        }
        public String getName() {
            return name;
        }
        public String getPrice() {
            return price;
        }
        public String getDescription() {
            return description;
        }
        public String getColor() {
            return color;
        }
        public int getStock() {
            return stock;
        }


    }

    public void Convert(List<Object> args) {
        Log.d(TAG, "List en tableau: ");
        String jsonString = String.valueOf(args);
        Gson gson = new Gson();
        Product[] products = gson.fromJson(jsonString, Product[].class);

        String[][] productData = new String[products.length][7];

        list.clear();

        for (int i = 0; i < products.length; i++) {
            Product product = products[i];
            productData[i][0] = product.getId();
            productData[i][1] = product.getCreatedAt();
            productData[i][2] = product.getName();
            productData[i][3] = product.getPrice();
            productData[i][4] = product.getDescription();
            productData[i][5] = product.getColor();
            productData[i][6] = Integer.toString(product.getStock());
            String id = product.getId();
            String name = product.getName();
            int stock = product.getStock();
            String data = id + ", " + name + ", " + stock;
            list.add(data);



        }
        SaveList(list);
        Log.d(TAG, "List en tableau: ");
        // Afficher le tableau 2 dimensions de données de produits
        for (String[] row : productData) {
            System.out.println(String.join(", ", row));
        }
        Log.d(TAG, "List send pour product list: ");
        for (String data : list) {
            System.out.println(data);
        }
    }

    private void SaveList(List<String> list){
        // Convertir la liste en une chaîne JSON
        Gson gson = new Gson();
        String json = gson.toJson(list);

        // Enregistrer la chaîne JSON dans les préférences partagées
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LIST, json);
        editor.apply();
        Log.d(TAG, "Content list saved: " + list);
    }

    private void loadList() {
        // Lire la chaîne JSON à partir des préférences partagées
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String json = sharedPreferences.getString(LIST, "");

        // Convertir la chaîne JSON en une liste
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> liste = gson.fromJson(json, type);
        list=liste;


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
                            now = String.valueOf(new Date());
                            saveData(username, state, token, String.valueOf(now));
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