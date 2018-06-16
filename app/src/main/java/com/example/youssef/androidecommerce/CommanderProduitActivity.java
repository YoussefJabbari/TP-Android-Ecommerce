package com.example.youssef.androidecommerce;

import android.annotation.SuppressLint;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.youssef.androidecommerce.entities.Client;
import com.example.youssef.androidecommerce.entities.Commande;

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

/**
 * A login screen that offers login via email/password.
 */
public class CommanderProduitActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    public Client client = null;
    public String produit;
    
    // UI references.
    private EditText mCommandeView;
    private EditText mQuantiteView;

    //For RetrieveDATA
    String adresse;
    String result = null;
    List<Commande> commandes = new ArrayList<>();
    Commande commandeSelecionne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commander_produit);
        
        // Set up the login form.
        mCommandeView = (EditText) findViewById(R.id.commande);
        mQuantiteView = (EditText) findViewById(R.id.quantite);

        mCommandeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDialog();
            }
        });

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Intent intent = getIntent();
        produit = intent.getStringExtra("produit");
        try {
            client = new Client(intent.getIntExtra("idclient", 21), intent.getStringExtra("loginclient"),
                    intent.getStringExtra("mdpclient"), intent.getStringExtra("nomclient"),
                    intent.getStringExtra("prenomclient"), intent.getStringExtra("telclient"),
                    intent.getStringExtra("adressecli"), sdf.parse(intent.getStringExtra("datenaissance")),
                    intent.getStringExtra("email"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Button mEmailSignInButton = (Button) findViewById(R.id.add_ligne_commande_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptAdd();
            }
        });
    }


    public void selectDialog()
    {
        adresse = "http://"+ LoginActivity.IP +"/androidecommerce/selectCommandes.php";
        try {
            String resul = new RetrieveData().execute(adresse).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(CommanderProduitActivity.this);
        final ArrayAdapter arrayAdapter;

        builderSingle.setTitle("Selectionner une commande");
        arrayAdapter = new ArrayAdapter<>(CommanderProduitActivity.this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.addAll(commandes);

        builderSingle.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                String strName;
                strName = commandes.get(which).toString();

                AlertDialog.Builder builderInner = new AlertDialog.Builder(CommanderProduitActivity.this);
                builderInner.setMessage(strName);
                builderInner.setTitle("Vous avez selectionné");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog,int selected) {
                        dialog.dismiss();

                        commandeSelecionne = new Commande(commandes.get(which));
                        mCommandeView.setText(commandeSelecionne.toString() );

                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptAdd() {

        // Reset errors.
        mCommandeView.setError(null);
        mQuantiteView.setError(null);

        // Store values at the time of the login attempt.
        String commande = mCommandeView.getText().toString();
        String quantite = mQuantiteView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(commande)) {
            mQuantiteView.setError(getString(R.string.error_field_required));
            focusView = mQuantiteView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(quantite)) {
            mCommandeView.setError(getString(R.string.error_field_required));
            focusView = mCommandeView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            new AsyncAddLigneCommande().execute(""+commandeSelecionne.getIdcommande(), produit, quantite);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    @SuppressLint("StaticFieldLeak")
    private class RetrieveData extends AsyncTask<String,Void,String>
    {
        ProgressDialog pdLoading = new ProgressDialog(CommanderProduitActivity.this);
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
            try
            {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
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
                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter("client", ""+client.getIdclient());
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

                    while ((line = reader.readLine()) != null) {
                        resultat.append(line);
                    }
                    result = resultat.toString();
                }
                else
                {
                    return "fail";
                }


            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }

            //PARSE JSON DATA
            try
            {
                JSONArray ja = new JSONArray(result);
                JSONObject jo;
                commandes.clear();

                for (int i=0 ; i<ja.length() ; i++)
                {
                    jo = ja.getJSONObject(i);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    commandes.add(new Commande(jo.getInt("idcommande"), jo.getString("adresselivraison"), sdf.parse(jo.getString("datecommande")), jo.getInt("commandevalidee"), jo.getInt("idlivreur"), jo.getInt("idclient")));
                }

                return "done";

            } catch (JSONException e) {
                e.printStackTrace();
                return "exception3";
            } catch (ParseException e) {
                e.printStackTrace();
                return "exception4";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            pdLoading.dismiss();
            Toast.makeText(CommanderProduitActivity.this,s,Toast.LENGTH_LONG).show();
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class AsyncAddLigneCommande extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(CommanderProduitActivity.this);
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

                // Enter URL address where your php file resides
                url = new URL("http://"+ LoginActivity.IP +"/androidecommerce/addLigneCommande.php");
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
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
                        .appendQueryParameter("commande", params[0])
                        .appendQueryParameter("produit",params[1])
                        .appendQueryParameter("quantite",params[2]);

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
                // TODO Auto-generated catch block
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
                CommanderProduitActivity.this.finish();

            }else if (result.equalsIgnoreCase("false")){

                Toast.makeText(CommanderProduitActivity.this, "Les données ne sont pas valides", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") ) {

                Toast.makeText(CommanderProduitActivity.this, "Problème de: Exception. "+result, Toast.LENGTH_LONG).show();

            } else if ( result.equalsIgnoreCase("unsuccessful")){
                Toast.makeText(CommanderProduitActivity.this, "Problème de: CNX. "+response_code, Toast.LENGTH_LONG).show();
            }
        }
    }
}

