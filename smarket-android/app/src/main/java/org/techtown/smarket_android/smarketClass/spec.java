package org.techtown.smarket_android.smarketClass;

import android.os.Parcel;
import android.os.Parcelable;

public class spec implements Parcelable {
    String key;
    String spec;

    public spec(){

    }

    public spec(String receiveKey, String receiveSpec){
        key = receiveKey;
        spec = receiveSpec;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected spec(Parcel in){
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(spec);
    }

    public void readFromParcel(Parcel in){
        key = in.readString();
        spec = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public org.techtown.smarket_android.smarketClass.spec createFromParcel(Parcel in) {
            return new spec(in);
        }

        public org.techtown.smarket_android.smarketClass.spec[] newArray(int size) {
            return new spec[size];
        }
    };
}
