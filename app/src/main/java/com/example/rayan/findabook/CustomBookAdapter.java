package com.example.rayan.findabook;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.example.rayan.findabook.MainActivity.LOG_TAG;
import static com.example.rayan.findabook.R.id.bookName;
import static com.example.rayan.findabook.R.id.list;

/**
 * Created by Rayan on 7/3/2017.
 */

public class CustomBookAdapter extends ArrayAdapter<Book> {

    public CustomBookAdapter(Activity context, ArrayList<Book> books)
    {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View listItemView = convertView;
        if(listItemView == null)
        {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Book currentBook = getItem(position);
        TextView bookNameView = (TextView)listItemView.findViewById(bookName);
        TextView authorsView = (TextView)listItemView.findViewById(R.id.authorNames);
        ImageView bookImage = (ImageView)listItemView.findViewById(R.id.bookCover);

        String bookName = currentBook.getName();
        bookNameView.setText(bookName);

        String authorNames = "";
        String[] authorNameArray = currentBook.getAuthors();

        for(int i=0; i<authorNameArray.length; i++)
        {
            authorNames += (authorNameArray[i] + ", ");
        }
        authorsView.setText(authorNames.substring(0, authorNames.length() - 2));

        bookImage.setImageBitmap(currentBook.getImage());

        return listItemView;
    }

}
