package com.example.youssef.androidecommerce;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youssef.androidecommerce.entities.Produit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LigneCommandeActivity extends AppCompatActivity {

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    public List<Produit> produits = new ArrayList<>();

    String adresse = null;
    String result = null;

    int commande;

    protected ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ligne_commande);

        Intent intent = getIntent();
        commande = intent.getIntExtra("commande", 1);

        produits();
    }

    public void produits()
    {
        adresse = "http://"+ LoginActivity.IP +"/androidecommerce/produitsCommande.php";
        try {
            String retrieve = new RetrieveData().execute(adresse, ""+commande).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        ArrayAdapter<Produit> mAdapter = new ArrayAdapter<Produit>(this, R.layout.list, R.id.Itemname, produits) {
            @SuppressLint("SetTextI18n")
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(R.id.Itemname);
                TextView text2 = (TextView) view.findViewById(R.id.prix);

                text1.setText(produits.get(position).toString());
                text2.setText("");
                return view;
            }
        };
        mListView = (ListView) findViewById(R.id.list_produits);
        mListView.setAdapter(mAdapter);

    }


    @SuppressLint("StaticFieldLeak")
    public class RetrieveData extends AsyncTask<String,Void,String>
    {
        ProgressDialog pdLoading = new ProgressDialog(LigneCommandeActivity.this);
        HttpURLConnection conn;
        URL url = null;
        int response_code;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try
            {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception 1";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("commande", params[1]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                e1.printStackTrace();
                return "exception 2";
            }
            try
            {
                response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder resultat = new StringBuilder();
                    String line;

                    if (reader.readLine().equals("exist"))
                    {
                        while ((line = reader.readLine()) != null) {
                            resultat.append(line);
                        }
                        result = resultat.toString();
                    }
                    else
                    {
                        return "empty";
                    }
                }
                else
                {
                    return "fail";
                }


            } catch (IOException e) {
                e.printStackTrace();
                return "exception 3";
            }

            //PARSE JSON DATA
            try
            {
                JSONArray ja = new JSONArray(result);
                JSONObject jo;
                produits.clear();

                for (int i=0 ; i<ja.length() ; i++)
                {
                    jo = ja.getJSONObject(i);
                    produits.add(new Produit(jo.getInt("idproduit"), jo.getString("nomproduit"), jo.getString("typeproduit"), jo.getInt("prix"), jo.getInt("idcatalogue")));
                }
                return "done";

            } catch (JSONException e) {
                e.printStackTrace();
                return "exception 4";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            pdLoading.dismiss();
            Toast.makeText(LigneCommandeActivity.this,s,Toast.LENGTH_LONG).show();
        }
    }

}
