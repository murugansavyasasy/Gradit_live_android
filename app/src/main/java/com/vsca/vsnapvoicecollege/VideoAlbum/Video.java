package com.vsca.vsnapvoicecollege.VideoAlbum;

import android.os.Parcel;
import android.os.Parcelable;


public class Video implements Parcelable {
    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
    public long id;
    public String name;
    public String path;
    public boolean isSelected;

    public Video(long id, String name, String path, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.isSelected = isSelected;
    }

    private Video(Parcel in) {
//        id = in.readLong();
//        name = in.readString();
        path = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeLong(id);
//        dest.writeString(name);
        dest.writeString(path);
    }
}
