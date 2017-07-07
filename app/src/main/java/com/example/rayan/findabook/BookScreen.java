package com.example.rayan.findabook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;

import static com.example.rayan.findabook.R.id.authorNames;

public class BookScreen extends AppCompatActivity {

    Bitmap bookCoverImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_screen);

        String bitmapPath = getIntent().getStringExtra("filePath");
        if(bitmapPath != null)
        {
            Context context = this;
            try {
                bookCoverImage = BitmapFactory.decodeStream(context.openFileInput(bitmapPath));

            }catch (FileNotFoundException e)
            {
                Log.e("BookActivity", "Bitmap not being properly accessed", e);
                bookCoverImage = null;
                bitmapPath = null;
            }
        }

        final Book currentBook = (Book)getIntent().getParcelableExtra("book");
        ImageView bookCoverImageView = (ImageView)findViewById(R.id.BookScreenCover);
        if(bookCoverImage != null)
        {
            bookCoverImageView.setImageBitmap(bookCoverImage);
        }

        //add both title and subtitle
        TextView titleTextView = (TextView)findViewById(R.id.BookScreenTitle);
        String mainTitle = currentBook.getName();
        String subTitle = currentBook.getSubtitle();
        String newTitle = mainTitle + "\n" + subTitle;
        titleTextView.setText(newTitle);

        TextView authorsNameTextView = (TextView)findViewById(R.id.BookScreenAuthors);
        String[] authorNameArray = currentBook.getAuthors();
        String authorNames = "";

        for(int i=0; i<authorNameArray.length; i++)
        {
            authorNames += (authorNameArray[i] + ", ");
        }
        authorsNameTextView.setText(authorNames.substring(0, authorNames.length() - 2));

        TextView yearView = (TextView)findViewById(R.id.BookScreenYear);
        yearView.setText(currentBook.getPublishDate().substring(0, 4));

        TextView publisherView = (TextView)findViewById(R.id.BookScreenPublisher);
        publisherView.setText(currentBook.getPublisher());

        TextView description = (TextView)findViewById(R.id.BookScreenDescription);
        description.setText(currentBook.getDescription());

        Button openBook = (Button)findViewById(R.id.buttonOpenBook);
        openBook.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view)
            {
                Uri url = Uri.parse(currentBook.getUrl());
                Intent openBookURL = new Intent(Intent.ACTION_VIEW, url);
                startActivity(openBookURL);
            }
        });

    }
}
