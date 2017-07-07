package com.example.rayan.findabook;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static com.example.rayan.findabook.MainActivity.LOG_TAG;

/**
 * Created by Rayan on 7/4/2017.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    private String urlToLoad;

    public BookLoader(Context context, String url)
    {
        super(context);
        urlToLoad = url;
    }

    @Override
    protected void onStartLoading(){forceLoad();}

    @Override
    public List<Book> loadInBackground(){

        if(!TextUtils.isEmpty(urlToLoad))
        {
            Log.e(LOG_TAG, "load in background");
            return QueryUtils.fetchDataFromURL(urlToLoad);

        }
        //image should be downloaded here?
        return null;
    }




}
