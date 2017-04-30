package com.renegens.firefy.model.geocoder;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ComponentItem {

    @SerializedName("long_name")
    private String longName;

    @SerializedName("types")
    private List<String> types;

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getTypes() {
        return types.get(0);
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
