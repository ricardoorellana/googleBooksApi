package com.rorellanam.android.googlebookapi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ConnectivityManager mConnectivityManager;
    ArrayList<Books> books;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button searchButton = (Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText searchEditText = (EditText) findViewById(R.id.searchEditText);
                String [] searchString = new String[1];
                        searchString[0] = searchEditText.getText().toString();

                GoogleApiRequest request = (GoogleApiRequest) new GoogleApiRequest().execute(searchString);

            }
        });

    }

    public class GoogleApiRequest extends AsyncTask<String, Object, JSONObject>{

        @Override
        protected void onPreExecute() {
            // Check network connection.
            if(isNetworkConnected() == false){
                // Cancel request.
                Log.i(getClass().getName(), "Not connected to the internet");
                cancel(true);
                return;
            }
        }
        @Override
        protected JSONObject doInBackground(String... isbns) {
            // Stop if cancelled
            if(isCancelled()){
                return null;
            }

            String apiUrlString = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbns[0];
            try{
                HttpURLConnection connection = null;
                // Build Connection.
                try{
                    URL url = new URL(apiUrlString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000); // 5 seconds
                    connection.setConnectTimeout(5000); // 5 seconds
                } catch (MalformedURLException e) {
                    // Impossible: The only two URLs used in the app are taken from string resources.
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    // Impossible: "GET" is a perfectly valid request method.
                    e.printStackTrace();
                }
                int responseCode = connection.getResponseCode();
                if(responseCode != 200){
                    Log.w(getClass().getName(), "GoogleBooksAPI request failed. Response Code: " + responseCode);
                    connection.disconnect();
                    return null;
                }

                // Read data from response.
                StringBuilder builder = new StringBuilder();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = responseReader.readLine();
                while (line != null){
                    builder.append(line);
                    line = responseReader.readLine();
                }
                String responseString = builder.toString();
                Log.d(getClass().getName(), "Response String: " + responseString);
                JSONObject responseJson = new JSONObject(responseString);

                // Close connection and return response code.
                connection.disconnect();
                return responseJson;
            } catch (SocketTimeoutException e) {
                Log.w(getClass().getName(), "Connection timed out. Returning null");
                return null;
            } catch(IOException e){
                Log.d(getClass().getName(), "IOException when connecting to Google Books API.");
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                Log.d(getClass().getName(), "JSONException when connecting to Google Books API.");
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(JSONObject responseJson) {

            if(isCancelled()){
                // Request was cancelled due to no network connection.
//                showNetworkDialog();
            } else if(responseJson == null){
//                showSimpleDialog(getResources().getString(R.string.dialog_null_response));
            }
            else{
                try {
                    JSONArray results = responseJson.getJSONArray("items");
                    books = new ArrayList<Books>();
                    for(int i=0, total = results.length(); i < total ; i ++) {
                        JSONObject obj = results.getJSONObject(i).getJSONObject("volumeInfo");
                        String title = obj.optString("title");
                        String author = obj.optString("authors");
                        String url = obj.optString("previewLink");

                        books.add(new Books(title, author, url));
                    }

                    // All went well. Do something with your new JSONObject.
                    BooksAdapter adapter = new BooksAdapter(MainActivity.this, books);
                    ListView listView = (ListView) findViewById(R.id.list_books);
                    listView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected boolean isNetworkConnected(){

        // Instantiate mConnectivityManager if necessary
        if(mConnectivityManager == null){
            mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        // Is device connected to the Internet?
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        } else {
            return false;
        }
    }



}
