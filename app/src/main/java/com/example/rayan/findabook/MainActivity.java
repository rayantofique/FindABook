package com.example.rayan.findabook;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.app.LoaderManager.LoaderCallbacks;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.R.attr.x;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Book>> {

    public static final String LOG_TAG = MainActivity.class.getName();

    private static final int BOOK_LOADER_ID = 1;


    private Button searchButton;
    private EditText searchField;
    private String searchedTerm;

    private CustomBookAdapter customAdapter;
    private String urlToCall;

    private ProgressBar progressBar;
    private TextView statusText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchedTerm = "";
        urlToCall = "";
        progressBar = (ProgressBar)findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.INVISIBLE);
        searchField = (EditText)findViewById(R.id.input_field);
        statusText = (TextView)findViewById(R.id.statusText);
        ListView listView = (ListView)findViewById(R.id.list);

        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean isConnected = netInfo != null && netInfo.isConnectedOrConnecting();

        if(!isConnected)
        {
            statusText.setText("Please connect to the internet");
        }
        else
        {
            statusText.setText("No books to show");
        }

        listView.setEmptyView(statusText);

        searchButton = (Button)findViewById(R.id.search_button);
        searchButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        customAdapter.clear();
                        customAdapter.notifyDataSetChanged();
                        searchedTerm = searchField.getText().toString();
                        //fix search term to properly search for titles with spaces
                        urlToCall = "https://www.googleapis.com/books/v1/volumes?q=" + fixMultipleWordURL(searchedTerm) + "&maxResults=10";
                        Log.e(LOG_TAG, "error" + urlToCall);
                        LoaderManager loaderManager = getLoaderManager();
                        statusText.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        loaderManager.restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                        Log.e(LOG_TAG, "loader started");
                    }
                });

        //the savedInstance stuff is done to preserve the data if activity is created anew e.g. when a screen is rotated
        if(savedInstanceState != null)
        {
            if(getLoaderManager().getLoader(BOOK_LOADER_ID)!= null)
            {
                //if instance exists then initialize loader with previous values
                getLoaderManager().initLoader(BOOK_LOADER_ID, null, this);
            }
            ArrayList<Book> booksSaved = savedInstanceState.getParcelableArrayList("booksSaved");
            if(booksSaved != null)
            {
                //display previous values before activity was destroyed
                customAdapter = new CustomBookAdapter(this, booksSaved);
            }
        }
        else
        {
            customAdapter = new CustomBookAdapter(this, new ArrayList<Book>());
        }


        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Book bookClicked = customAdapter.getItem(position);

                //pass whole class when starting new intent
                Intent bookActivity = new Intent(MainActivity.this, BookScreen.class);
                bookActivity.putExtra("book", bookClicked);
                String imageFilePath = createImageFromBitmap(bookClicked.getImage());
                bookActivity.putExtra("filePath", imageFilePath);
                startActivity(bookActivity);

            }
        });


    }

    private String fixMultipleWordURL(String rawURL)
    {
        String fixedURL = rawURL.trim().replace(' ', '+');
        return fixedURL;
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle bundle)
    {
        Log.e(LOG_TAG, "on create loader called too");
        return new BookLoader(this, urlToCall);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books)
    {
        Log.e(LOG_TAG, "on finished loader called too");

        customAdapter.clear();
        if(books != null && !books.isEmpty())
        {
            customAdapter.addAll(books);
            customAdapter.notifyDataSetChanged();
        }
        getLoaderManager().destroyLoader(BOOK_LOADER_ID);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader)
    {
        customAdapter.clear();
    }

    public String createImageFromBitmap(Bitmap bitmap)
    {
        //cant pass bitmap through parcel so will save image to disk and retrieve in other activity.
        String fileName = "bookCoverImage";
        if(bitmap == null)
        {
            return null;
        }
        try {
            //compressing the bitmap file into byteArrayOutputStream
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

            //writing to file
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();

        } catch (Exception e)
        {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    public void onSaveInstanceState(Bundle savedState)
    {
        //save the items in the list so we dont need to call the query again
        super.onSaveInstanceState(savedState);

        ArrayList<Book> books = new ArrayList<Book>();
        for(int i=0; i< customAdapter.getCount();i++)
        {
            books.add(customAdapter.getItem(i));
        }
        savedState.putParcelableArrayList("booksSaved", books);
    }

}
