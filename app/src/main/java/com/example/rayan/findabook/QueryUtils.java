package com.example.rayan.findabook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static com.example.rayan.findabook.MainActivity.LOG_TAG;

/**
 * Created by Rayan on 7/3/2017.
 */

public class QueryUtils {

    private QueryUtils()
    {}

    public static URL createURL(String stringUrl)
    {
        URL url = null;
        try {
            url = new URL(stringUrl);
        }
        catch(MalformedURLException e)
        {
            Log.e(LOG_TAG, "Cannot create URL from given query", e);
        }
        return url;
    }

    private static String makeHTTPQueryRequest(URL url) throws IOException
    {
        String jsonResponse = "";
        if(url == null) {return jsonResponse;}

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try{
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200)
            {
               inputStream = urlConnection.getInputStream();
               jsonResponse = readFromInputStream(inputStream);
            }
            else
            {
                Log.e(LOG_TAG, "Error Response Code" + urlConnection.getResponseCode());
            }
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, "Cannot retrieve JSON results", e);

        }
        finally
        {
            if(urlConnection != null) {urlConnection.disconnect();}
            if(inputStream != null){inputStream.close();}
        }

        return jsonResponse;
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException
    {
        StringBuilder jsonOutput = new StringBuilder();
        if(inputStream != null)
        {
            InputStreamReader streamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader buffer = new BufferedReader(streamReader);
            String line = buffer.readLine();

            while(line != null)
            {
                jsonOutput.append(line);
                line = buffer.readLine();
            }
        }

        return jsonOutput.toString();

    }

    public static List<Book> extractBooksFromJSON(String jsonResponse)
    {
        if(TextUtils.isEmpty(jsonResponse))
        {
            return null;
        }

        List<Book> books = new ArrayList<>();

        try {

            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray bookArray = jsonObject.getJSONArray("items");

            Log.e("ABC", Integer.toString(bookArray.length()));

            for(int i=0; i<bookArray.length(); i++)
            {

                JSONObject volumeInfo = bookArray.getJSONObject(i).getJSONObject("volumeInfo");

                String name = assignJSONSafe(volumeInfo, "title");
                String url = assignJSONSafe(volumeInfo, "infoLink");
                String[] authorNameArray;
                if(volumeInfo.has("authors"))
                {
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    authorNameArray = new String[authors.length()];
                    for(int j=0; j<authors.length(); j++)

                    {
                        authorNameArray[j] = authors.getString(j);
                    }
                }
                else {
                    authorNameArray = new String[1];
                    authorNameArray[0] = "";
                }

                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");

                String ImageURL = assignJSONSafe(imageLinks, "thumbnail");
                String publishDate = assignJSONSafe(volumeInfo, "publishedDate");
                String description = "";
                if(bookArray.getJSONObject(i).has("searchInfo"))
                {
                    JSONObject searchInfo = bookArray.getJSONObject(i).getJSONObject("searchInfo");
                    description = assignJSONSafe(searchInfo, "textSnippet");
                }
                else{
                    description = "No description found";
                }
                String publisher = assignJSONSafe(volumeInfo, "publisher");
                String subtitle = assignJSONSafe(volumeInfo, "subtitle");
                Bitmap bmp = downloadThumbnailImage(ImageURL);
                //extract image here and assign to bitmap?
                Book bookObj = new Book(name, authorNameArray, url, bmp, publishDate,
                        description, publisher, subtitle);
                books.add(bookObj);
                Log.e(LOG_TAG,"done");

            }
            Log.e(LOG_TAG,"whole done");

        }
        catch (JSONException e)
        {
            Log.e("QueryUtils", "Problem parsing the JSON results", e);

        }

        return books;
    }

    public static String assignJSONSafe(JSONObject obj, String tag)
    {
        try{

            if(tag == "subtitle")
            {
                return (obj.has(tag)) ? obj.getString(tag) : ("");
            }
            return (obj.has(tag)) ? obj.getString(tag) : ("No " + tag + " available");

        }catch(JSONException e)
        {
            Log.e(LOG_TAG, "Cannot parse JSON", e);
            return null;
        }
    }

    public static Bitmap downloadThumbnailImage(String url)
    {
        HttpURLConnection conn = null;
        Bitmap imgBmp = null;
        try{
            URL imageURL = createURL(url);
            conn = (HttpURLConnection)imageURL.openConnection();
            InputStream stream = conn.getInputStream();
            if(stream != null)
            {
                imgBmp = BitmapFactory.decodeStream(stream);
                return imgBmp;
            }

        } catch (Exception e)
        {
            conn.disconnect();
            Log.e(LOG_TAG, "Error while downloading image", e);
        }
        finally {

            if(conn != null)
            {
                conn.disconnect();
            }
        }
        return imgBmp;
    }

    public static List<Book> fetchDataFromURL(String requestedURL)
    {
        URL url = createURL(requestedURL);
        String jsonResponse = null;

        try{
            jsonResponse = makeHTTPQueryRequest(url);
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, "Cannot request data from URL", e);
        }

        return extractBooksFromJSON(jsonResponse);
    }

}
