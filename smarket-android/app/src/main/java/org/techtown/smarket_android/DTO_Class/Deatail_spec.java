package org.techtown.smarket_android.DTO_Class;

import android.os.Parcel;
import android.os.Parcelable;

public class Deatail_spec implements Parcelable {
    String key;
    String spec;

    public Deatail_spec(){

    }

    public Deatail_spec(String receiveKey, String receiveSpec){
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

    protected Deatail_spec(Parcel in){
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
        public Deatail_spec createFromParcel(Parcel in) {
            return new Deatail_spec(in);
        }

        public Deatail_spec[] newArray(int size) {
            return new Deatail_spec[size];
        }
    };
}
