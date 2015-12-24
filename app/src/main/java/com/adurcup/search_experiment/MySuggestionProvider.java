package com.adurcup.search_experiment;

import android.app.SearchManager;
import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by shivang on 12/21/15.
 */
public class MySuggestionProvider extends SearchRecentSuggestionsProvider{
    public final static String AUTHORITY = "com.adurcup.search_experiment.MySuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES|DATABASE_MODE_2LINES;

    public MySuggestionProvider(){
        setupSuggestions(AUTHORITY,MODE);
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String sel,
                        String[] selArgs, String sortOrder) {
        Cursor recentCursor = super.query(uri, projection,sel, selArgs,
                sortOrder);
        String[] columns= {
                SearchManager.SUGGEST_COLUMN_FORMAT,
                SearchManager.SUGGEST_COLUMN_ICON_1,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_TEXT_2,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA,
                BaseColumns._ID
        };
        MatrixCursor customcursor = new MatrixCursor(columns);
        new AsyncHttpTask().execute("http://api.adurcup.com/v2/productCat?query=" + selArgs[0].replace(" ","+"));

 //       Toast.makeText(getContext(),customcursor.getColumnCount()+feedsList.get(0).getTitle()+feedsList.size(),Toast.LENGTH_SHORT).show();

        for (int i=0;i<feedsList.size();i++)
        {
            FeedItem tmpfeedList=feedsList.get(i);
            String[] tmp = {null,null,tmpfeedList.getTitle(),tmpfeedList.getCategory(),tmpfeedList.getUrl(),Integer.toString(i)};
            customcursor.addRow(tmp);
        }


        Cursor[] cursor={recentCursor,customcursor};
        feedsList.clear();
        return new MergeCursor(cursor);
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result =8;
            HttpURLConnection urlConnection;

            try {
                /* forming th java.net.URL object */
                URL url = new URL(params[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                /* for Get request */
                urlConnection.setRequestMethod("GET");

                int statusCode = urlConnection.getResponseCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }

                    parseResult(response.toString());
                    result = 1; // Successful
                } else{
                    result = 0; //"Failed to fetch data!";
                }

            } catch (Exception e) {
                Log.d("error in doInBackground", e.getLocalizedMessage());
            }

            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {

            // Download complete. Let us update UI

           if (result == 1) {
            }
            else {
                Toast.makeText(getContext(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    JSONArray posts;
    List<FeedItem> feedsList;

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            posts = response.optJSONArray("result");

            feedsList = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                FeedItem item = new FeedItem();
                item.setTitle(post.optString("name"));
                item.setUrl(post.optString("url"));
                item.setCategory(post.optString("category"));
                feedsList.add(item);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
