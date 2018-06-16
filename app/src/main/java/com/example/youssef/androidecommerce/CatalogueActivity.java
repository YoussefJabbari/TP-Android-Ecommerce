package com.example.youssef.androidecommerce;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youssef.androidecommerce.entities.Client;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CatalogueActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    public List<Produit> produits = new ArrayList<>();

    String adresse = null;
    String result = null;
    int catalogue;

    protected ListView mListView;

    public Client client = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Intent intent = getIntent();
        catalogue = intent.getIntExtra("id", 1);
        try {
            client = new Client(intent.getIntExtra("idclient", 21), intent.getStringExtra("loginclient"),
                    intent.getStringExtra("mdpclient"), intent.getStringExtra("nomclient"),
                    intent.getStringExtra("prenomclient"), intent.getStringExtra("telclient"),
                    intent.getStringExtra("adressecli"), sdf.parse(intent.getStringExtra("datenaissance")),
                    intent.getStringExtra("email"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        produits();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.catalogue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_logout) {
            Intent log_out = new Intent(CatalogueActivity.this, LoginActivity.class);
            startActivity(log_out);
            CatalogueActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void produits()
    {
        adresse = "http://"+ LoginActivity.IP +"/androidecommerce/produitscatalogue.php";
        try {
            String retrieve = new CatalogueActivity.RetrieveData().execute(adresse, ""+catalogue).get();
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
                text2.setText("" + produits.get(position).getPrix() + " MAD");
                return view;
            }
        };
        mListView = (ListView) findViewById(R.id.list_produits);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(CatalogueActivity.this, CommanderProduitActivity.class);
                        intent.putExtra("idclient", client.getIdclient());
                        intent.putExtra("nomclient", client.getNomclient());
                        intent.putExtra("prenomclient", client.getPrenomclient());
                        intent.putExtra("loginclient", client.getLoginclient());
                        intent.putExtra("email", client.getEmail());
                        intent.putExtra("mdpclient", client.getMdpclient());
                        intent.putExtra("telclient", client.getTelclient());
                        intent.putExtra("adressecli", client.getAdressecli());
                        intent.putExtra("datenaissance", client.getStrDateNaissance());
                        intent.putExtra("produit", ""+produits.get(position).getIdcatalogue());
                        startActivity(intent);

                    }
                });

    }

    @SuppressLint("StaticFieldLeak")
    public class RetrieveData extends AsyncTask<String,Void,String>
    {
        ProgressDialog pdLoading = new ProgressDialog(CatalogueActivity.this);
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
                        .appendQueryParameter("id", params[1]);
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
            Toast.makeText(CatalogueActivity.this,s,Toast.LENGTH_LONG).show();
        }
    }
}
