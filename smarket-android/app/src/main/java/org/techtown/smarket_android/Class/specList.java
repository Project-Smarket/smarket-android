package org.techtown.smarket_android.Class;

public class specList {
    String key;
    String spec;

    public specList(){

    }

    public specList(String receiveKey, String receiveSpec){
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
}
