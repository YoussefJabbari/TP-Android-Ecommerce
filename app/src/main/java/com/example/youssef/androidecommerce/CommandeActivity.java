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
import com.example.youssef.androidecommerce.entities.Livreur;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
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
public class CommandeActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    public Client client = null;

    // UI references.
    private EditText mLivreurView;
    private EditText mAdresseView;

    //For RetrieveDATA
    String adresse;
    String result = null;
    List<Livreur> livreurs = new ArrayList<>();
    Livreur livreurSelectionne;

    InputStream is = null;
    String line = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commande);

        // Set up the login form.
        mLivreurView = (EditText) findViewById(R.id.livreur);
        mAdresseView = (EditText) findViewById(R.id.adresseLivraison);

        mLivreurView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDialog();
            }
        });

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

        mAdresseView.setText(client.getAdressecli());

        Button mEmailSignInButton = (Button) findViewById(R.id.add_commande_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptAdd();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CommandeActivity.this, ClientActivity.class);
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
        CommandeActivity.this.finish();
    }

    public void selectDialog()
    {
        adresse = "http://"+ LoginActivity.IP +"/androidecommerce/selectLivreurs.php";
        try {
            String resul = new RetrieveData().execute(adresse).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(CommandeActivity.this);
        final ArrayAdapter arrayAdapter;

        builderSingle.setTitle("Selectionner un livreur");
        arrayAdapter = new ArrayAdapter<>(CommandeActivity.this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.addAll(livreurs);

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
                strName = livreurs.get(which).toString();

                AlertDialog.Builder builderInner = new AlertDialog.Builder(CommandeActivity.this);
                builderInner.setMessage(strName);
                builderInner.setTitle("Vous avez selectionné comme livreur");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog,int selected) {
                        dialog.dismiss();

                        livreurSelectionne = new Livreur(livreurs.get(which));
                        mLivreurView.setText(livreurSelectionne.getNomlivreur() + " " + livreurSelectionne.getPrenomlivreur());

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
        mLivreurView.setError(null);
        mAdresseView.setError(null);

        // Store values at the time of the login attempt.
        String livreur = mLivreurView.getText().toString();
        String adresse = mAdresseView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(adresse)) {
            mAdresseView.setError(getString(R.string.error_field_required));
            focusView = mAdresseView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(livreur)) {
            mLivreurView.setError(getString(R.string.error_field_required));
            focusView = mLivreurView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            new AsyncAddCommande().execute(""+livreurSelectionne.getIdlivreur(), adresse, ""+client.getIdclient());
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
        ProgressDialog pdLoading = new ProgressDialog(CommandeActivity.this);

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
                URL url = new URL(params[0]);
                HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
                cnx.setRequestMethod("GET");
                is = new BufferedInputStream(cnx.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                return e.getMessage();
            }

            try
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();

                while ((line = br.readLine()) != null)
                {
                    sb.append(line);
                }
                is.close();
                result = sb.toString();
            }
            catch (Exception e) {
                e.printStackTrace();
                return "exception2";
            }

            //PARSE JSON DATA
            try
            {
                JSONArray ja = new JSONArray(result);
                JSONObject jo;
                livreurs.clear();

                for (int i=0 ; i<ja.length() ; i++)
                {
                    jo = ja.getJSONObject(i);
                    livreurs.add(new Livreur(jo.getInt("idlivreur"), jo.getString("loginlivreur"), jo.getString("mdplivreur"), jo.getString("nomlivreur"), jo.getString("prenomlivreur"), jo.getString("tellivreur"), jo.getString("emaillivreur")));
                }

                return "done";

            } catch (JSONException e) {
                e.printStackTrace();
                return "exception3";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            pdLoading.dismiss();
            Toast.makeText(CommandeActivity.this,s,Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncAddCommande extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(CommandeActivity.this);
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
                url = new URL("http://"+LoginActivity.IP+"/androidecommerce/addcommande.php");
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
                        .appendQueryParameter("livreur", params[0])
                        .appendQueryParameter("adresse",params[1])
                        .appendQueryParameter("client",params[2]);

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
                Toast.makeText(CommandeActivity.this, "Ajout validé", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CommandeActivity.this, ClientActivity.class);
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
                CommandeActivity.this.finish();

            }else if (result.equalsIgnoreCase("false")){

                Toast.makeText(CommandeActivity.this, "Les données ne sont pas valides", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") ) {

                Toast.makeText(CommandeActivity.this, "Problème de: Exception. "+result, Toast.LENGTH_LONG).show();

            } else if ( result.equalsIgnoreCase("unsuccessful")){
                Toast.makeText(CommandeActivity.this, "Problème de: CNX. "+response_code, Toast.LENGTH_LONG).show();
            }
        }
    }

}