package com.renegens.firefy.model.geocoder;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AddressComponent {

    @SerializedName("address_components")
    private List<ComponentItem> componentItems = new ArrayList<>();

    public List<ComponentItem> getComponentItems() {
        return componentItems;
    }

    public void setComponentItems(List<ComponentItem> componentItems) {
        this.componentItems = componentItems;
    }
}
