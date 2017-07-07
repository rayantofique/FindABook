package com.example.rayan.findabook;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import static android.R.attr.author;

public class Book implements Parcelable{

    private String name;
    private String subtitle;
    private String[] authors;
    private String url;
    private Bitmap image;
    private String publishDate;
    private String description;
    private String publisher;



    public Book(String nName, String[] nAuthor, String nUrl, Bitmap nImg,
                String npublishDate, String ndescription, String nPublisher, String nSubtitle)
    {
        name = nName;
        authors = nAuthor;
        url = nUrl;
        image = nImg;
        publishDate = npublishDate;
        description = ndescription;
        publisher = nPublisher;
        subtitle = nSubtitle;

    }

    public Book(Parcel parcel)
    {
        this.name = parcel.readString();
        this.authors = parcel.createStringArray();
        this.url = parcel.readString();
        this.image = null;
        this.publishDate = parcel.readString();
        this.description = parcel.readString();
        this.publisher = parcel.readString();
        this.subtitle = parcel.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.name);
        dest.writeStringArray(this.authors);
        dest.writeString(this.url);
        dest.writeString(this.publishDate);
        dest.writeString(this.description);
        dest.writeString(this.publisher);
        dest.writeString(this.subtitle);

    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getName(){return name;}
    public String[] getAuthors(){return authors;}
    public String getUrl(){return url;}
    public Bitmap getImage(){return image;}
    public String getPublishDate(){return publishDate;}
    public String getDescription(){return description;}
    public String getPublisher(){return publisher;}
    public String getSubtitle(){return subtitle;}


}
