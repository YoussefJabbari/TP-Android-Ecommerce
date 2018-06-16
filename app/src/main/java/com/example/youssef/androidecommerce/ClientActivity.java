package com.example.youssef.androidecommerce;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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

import com.example.youssef.androidecommerce.entities.Catalogue;
import com.example.youssef.androidecommerce.entities.Client;
import com.example.youssef.androidecommerce.entities.Commande;
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

public class ClientActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    public List<Catalogue> catalogues = new ArrayList<>();
    public List<Produit> produits = new ArrayList<>();
    public List<Commande> commandes = new ArrayList<>();

    protected String adresse;
    String result = null;

    protected ListView mListView;

    public Client client = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
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
        try {
            client = new Client(intent.getIntExtra("idclient", 21), intent.getStringExtra("loginclient"),
                    intent.getStringExtra("mdpclient"), intent.getStringExtra("nomclient"),
                    intent.getStringExtra("prenomclient"), intent.getStringExtra("telclient"),
                    intent.getStringExtra("adressecli"), sdf.parse(intent.getStringExtra("datenaissance")),
                    intent.getStringExtra("email"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        catalogues();

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
        getMenuInflater().inflate(R.menu.client, menu);
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
            Intent log_out = new Intent(ClientActivity.this, LoginActivity.class);
            startActivity(log_out);
            ClientActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.catalogues) {
            catalogues();
        } else if (id == R.id.produits) {
            produits();
        } else if (id == R.id.commandes) {
            commandes();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            Intent log_out = new Intent(ClientActivity.this, LoginActivity.class);
            startActivity(log_out);
            ClientActivity.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void catalogues()
    {
        adresse = "http://"+ LoginActivity.IP +"/androidecommerce/catalogues.php";
        try {
            String retrieve = new RetrieveData().execute("catalogues", adresse).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        ArrayAdapter<Catalogue> mAdapter = new ArrayAdapter<Catalogue>(this, R.layout.simple_list, R.id.Itemname, catalogues);
        mListView = (ListView) findViewById(R.id.list_items);
        mListView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_commande);
        fab.setVisibility(View.INVISIBLE);

        mListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(ClientActivity.this, CatalogueActivity.class);
                        intent.putExtra("idclient", client.getIdclient());
                        intent.putExtra("nomclient", client.getNomclient());
                        intent.putExtra("prenomclient", client.getPrenomclient());
                        intent.putExtra("loginclient", client.getLoginclient());
                        intent.putExtra("email", client.getEmail());
                        intent.putExtra("mdpclient", client.getMdpclient());
                        intent.putExtra("telclient", client.getTelclient());
                        intent.putExtra("adressecli", client.getAdressecli());
                        intent.putExtra("datenaissance", client.getStrDateNaissance());
                        intent.putExtra("id", (catalogues.get(position).getIdcatalogue()));
                        startActivity(intent);

                    }
                });

    }

    public void produits()
    {
        Toast.makeText(this, "Cliquez sur un produit pour l'ajouter à une commande!", Toast.LENGTH_LONG).show();
        adresse = "http://"+ LoginActivity.IP +"/androidecommerce/produits.php";
        try {
            String retrieve = new RetrieveData().execute("produits", adresse).get();
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
                text2.setText(""+produits.get(position).getPrix() + " MAD");
                return view;
            }
        };
        mListView = (ListView) findViewById(R.id.list_items);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(ClientActivity.this, CommanderProduitActivity.class);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_commande);
        fab.setVisibility(View.INVISIBLE);
    }

    public void commandes()
    {
        adresse = "http://"+ LoginActivity.IP +"/androidecommerce/commandes.php";
        try {
            String retrieve = new RetrieveData().execute("commandes", adresse).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        ArrayAdapter adapter = new ArrayAdapter(ClientActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, commandes) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(commandes.get(position).toString());
                text2.setText(commandes.get(position).getAdresselivraison());
                return view;
            }
        };

        mListView = (ListView) findViewById(R.id.list_items);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(ClientActivity.this, LigneCommandeActivity.class);
                        intent.putExtra("commande", commandes.get(position).getIdcommande());
                        startActivity(intent);

                    }
                });
        mListView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener(){
                    int choix = -1;

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ClientActivity.this);

                        String[] choices = new String[2];
                        choices[0] = "Valider la commande";
                        //choices[1] = "Modifier la commande";
                        choices[1] = "Visualiser les lignes de commande";
                        // set dialog message
                        alertDialogBuilder.setSingleChoiceItems(choices, -1, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                choix = whichButton;
                            }
                        });
                        alertDialogBuilder.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (choix == 0)
                                {
                                    new AsyncValiderCommande().execute(""+commandes.get(position).getIdcommande());
                                }
                                /*else if (choix == 1)
                                {
                                    //
                                }*/
                                else if (choix == 1)
                                {
                                    Intent intent = new Intent(ClientActivity.this, LigneCommandeActivity.class);
                                    intent.putExtra("idclient", client.getIdclient());
                                    intent.putExtra("nomclient", client.getNomclient());
                                    intent.putExtra("prenomclient", client.getPrenomclient());
                                    intent.putExtra("loginclient", client.getLoginclient());
                                    intent.putExtra("email", client.getEmail());
                                    intent.putExtra("mdpclient", client.getMdpclient());
                                    intent.putExtra("telclient", client.getTelclient());
                                    intent.putExtra("adressecli", client.getAdressecli());
                                    intent.putExtra("datenaissance", client.getStrDateNaissance());
                                    intent.putExtra("commande", commandes.get(position).getIdcommande());
                                    startActivity(intent);
                                }
                            }
                        });

                        alertDialogBuilder.setCancelable(true).setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                        return true;
                    }
                });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_commande);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClientActivity.this, CommandeActivity.class);
                intent.putExtra("idclient", client.getIdclient());
                intent.putExtra("nomclient", client.getNomclient());
                intent.putExtra("prenomclient", client.getPrenomclient());
                intent.putExtra("loginclient", client.getLoginclient());
                intent.putExtra("email", client.getEmail());
                intent.putExtra("mdpclient", client.getMdpclient());
                intent.putExtra("telclient", client.getTelclient());
                intent.putExtra("adressecli", client.getAdressecli());
                intent.putExtra("datenaissance", client.getStrDateNaissance());
                startActivity(intent);
                ClientActivity.this.finish();
            }
        });

    }



    @SuppressLint("StaticFieldLeak")
    public class RetrieveData extends AsyncTask<String,Void,String>
    {
        ProgressDialog pdLoading = new ProgressDialog(ClientActivity.this);
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
                url = new URL(params[1]);
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
                        .appendQueryParameter("id", ""+client.getIdclient());
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
                        return "EMPTY";
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
                catalogues.clear();
                produits.clear();
                commandes.clear();
                String str;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                for (int i=0 ; i<ja.length() ; i++)
                {
                    jo = ja.getJSONObject(i);

                    switch (params[0]) {
                        case "catalogues":
                            str = jo.getString("datecatalogue");
                            catalogues.add(new Catalogue(jo.getInt("idcatalogue"), jo.getString("nomcatalogue"), sdf.parse(str)));
                            break;
                        case "produits":
                            produits.add(new Produit(jo.getInt("idproduit"), jo.getString("nomproduit"), jo.getString("typeproduit"), jo.getInt("prix"), jo.getInt("idcatalogue")));
                            break;
                        case "commandes":
                            str = jo.getString("datecommande");
                            commandes.add(new Commande(jo.getInt("idcommande"), jo.getString("adresselivraison"), sdf.parse(str), jo.getInt("commandevalidee"), jo.getInt("idlivreur"), jo.getInt("idclient")));
                            break;
                    }
                }
                return "done";

            } catch (JSONException e) {
                e.printStackTrace();
                return "exception 4";
            } catch (ParseException e) {
                e.printStackTrace();
                return "exception 5";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            pdLoading.dismiss();
            Toast.makeText(ClientActivity.this,s,Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncValiderCommande extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(ClientActivity.this);
        HttpURLConnection conn;
        URL url = null;
        int response_code;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                url = new URL("http://"+LoginActivity.IP+"/androidecommerce/validerCommande.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("commande", params[0]);

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
                return "exception";
            }

            try {

                response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    return(result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();

            if(result.equalsIgnoreCase("true"))
            {

                Toast.makeText(ClientActivity.this, "Commande validée", Toast.LENGTH_LONG).show();
                commandes();

            }else if (result.equalsIgnoreCase("false")){

                Toast.makeText(ClientActivity.this, "Un problème est survenu!", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") ) {

                Toast.makeText(ClientActivity.this, "Problème de: Exception. "+result, Toast.LENGTH_LONG).show();

            } else if ( result.equalsIgnoreCase("unsuccessful")){
                Toast.makeText(ClientActivity.this, "Problème de: CNX. "+response_code, Toast.LENGTH_LONG).show();
            }
        }
    }
}
