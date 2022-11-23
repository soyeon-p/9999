package com.example.ssz.db.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class CameraLocation implements Parcelable {
    private ArrayList<String> location;

    protected CameraLocation(Parcel in) {
        location = in.createStringArrayList();
    }

    public static final Creator<CameraLocation> CREATOR = new Creator<CameraLocation>() {
        @Override
        public CameraLocation createFromParcel(Parcel in) {
            return new CameraLocation(in);
        }

        @Override
        public CameraLocation[] newArray(int size) {
            return new CameraLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeStringList(location);
    }
}
