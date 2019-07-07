package com.example.searchengine;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public ArrayList<SearchClass> searchClass=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView =findViewById(R.id.list_view);
        final Button button =findViewById(R.id.search_button);
        final EditText editText=findViewById(R.id.search_field);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Search().execute(editText.getText().toString());
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                intent.putExtra("name",searchClass.get(i).partNumber);
                intent.putExtra("desc",searchClass.get(i).description);
                intent.putExtra("data",searchClass.get(i).datasheet);
                intent.putExtra("image",searchClass.get(i).imagePath);
                intent.putExtra("link",searchClass.get(i).productLink);

                startActivity(intent);  // should start the Activity
            }
        });


    }

    //------------------------------------------------------------------------------------------------------------------------------
    //      API      API     API     API     API     API
    //------------------------------------------------------------------------------------------------------------------




    private  class Search extends AsyncTask<String, String, ArrayList<SearchClass>> {


        private static final String TAG = "Search";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<SearchClass> doInBackground(String... params) {
          //String urlString = "https://api.mouser.com/api/v1.0/order/options/query?apiKey=920addd1-94aa-4b53-8c86-be35d631806c"; // URL to call // Wrong URL (Osama)
            String urlString = "https://api.mouser.com/api/v1/search/keyword?apiKey=920addd1-94aa-4b53-8c86-be35d631806c"; // URL to call

            String data = "{\n" +      // Forgot to add { at the beginning of the request (Osama)
                    "  \"SearchByKeywordRequest\": {\n" +
                    "    \"keyword\": \"" + params[0] + "\",\n" + // params is an ArrayList you can't just make it string (Osama)
                    "    \"records\": 20,\n" +
                    "    \"startingRecord\": 0,\n" +
                    "    \"searchOptions\": \"None\",\n" +
                    "    \"searchWithYourSignUpLanguage\": \"false\"\n" +
                    "  }\n" +
                    " }"; //data to post

            Log.i(TAG, "doInBackground: request body \n " + data);
            OutputStream out;      // Don't need to initialize (Osama)
            InputStream stream;    // Don't need to initialize (Osama)
            String jsonData = null;
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);  // Should be called as there is a request body (Osama)
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");  // Should be specified as this is the Request method of the Server (Osama)
                urlConnection.setRequestProperty("Content-Type", "application/json");  //Should be Called to specify the content type (Osama)
                out = new BufferedOutputStream(urlConnection.getOutputStream());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();
                out.close();
                Log.i(TAG, "doInBackground: data was written");

                urlConnection.connect();
                Log.i(TAG, "doInBackground: Connection done");
                stream = urlConnection.getInputStream();
                jsonData = readDataFromStream(stream);
                Log.i(TAG, "doInBackground: Data returned \n " + jsonData);

            } catch (Exception e) {
                System.out.println(e.getMessage());
                Log.e(TAG, "doInBackground: exception", e);
            }
            if (TextUtils.isEmpty(jsonData)) return null;

            try {
                return getJSONData(jsonData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(ArrayList<com.example.searchengine.SearchClass> searchClasses) {
            if (searchClasses==null) return;
            searchClass.addAll(searchClasses);
            ListView listView =findViewById(R.id.list_view);
            myListAdapter myListAdapter=new myListAdapter();
            myListAdapter.addAll(searchClass);   // Should send your Array of data in the Super or use this Adapter addAll method
            listView.setAdapter(myListAdapter);
            Log.i(TAG, "onPostExecute: Data Ok ");
        }
    }

    String readDataFromStream(InputStream stream) throws IOException {
        StringBuilder builder = new StringBuilder();
        if (stream != null) {
            InputStreamReader reader = new InputStreamReader(stream, Charset.forName("UTF-8"));
            BufferedReader buffered = new BufferedReader(reader);
            String line;
            while ((line = buffered.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }

    private  ArrayList<SearchClass> getJSONData(String jsonData)throws JSONException {
        ArrayList<SearchClass> searches =new ArrayList<>();

        JSONObject root =new JSONObject(jsonData);         //getting all data as json object
        JSONObject searchResults = root.getJSONObject("SearchResults");
        JSONArray parts =searchResults.getJSONArray("Parts");//getting data in an array form  // it was Results not Parts (Osama)
        for (int i=0;i<parts.length();i++) {
            JSONObject part = parts.getJSONObject(i);        //get one by one
            //getting my details
            String partNumber = part.getString("ManufacturerPartNumber");
            String description = part.getString("Description");
            String imagePath = part.getString("ImagePath");
            String productLink = part.getString("ProductDetailUrl");
            String dataSheet = part.getString("DataSheetUrl");

            searches.add(new SearchClass(partNumber, description, productLink, imagePath, dataSheet));
        }
        return  searches;
    }

    //------------------------------------------------------------------------------------------------------------------------------
    //
    //------------------------------------------------------------------------------------------------------------------------------

    private class myListAdapter extends ArrayAdapter<SearchClass>{

        private static final String TAG = "MyListAdapter";

        public myListAdapter() {
            super(MainActivity.this, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView =convertView;
            if ( itemView ==null){                                              //making sure we have a view to work with
                itemView =getLayoutInflater().inflate(R.layout.list_item,parent,false);
            }
            SearchClass currentOne =searchClass.get(position);

            // you should use itemView to find your views (Osama)
            TextView name= itemView.findViewById(R.id.item_name);
            TextView description= itemView.findViewById(R.id.item_description); // it was item name that means the same view (Osama)
            //sett4ing text
            name.setText(currentOne.getPartNumber());
            description.setText(currentOne.getDescription());
            //------------------------>>>>>>>>>>>here
            Picasso.with(getApplicationContext())
                    .load(currentOne.getImagePath())
                    .into( (ImageView)itemView.findViewById(R.id.imageView));
            return itemView;
        }
    }
}
