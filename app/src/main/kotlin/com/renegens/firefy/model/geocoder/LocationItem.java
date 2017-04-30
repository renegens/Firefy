package com.renegens.firefy.model.geocoder;

public class LocationItem {

    private AddressComponent addressComponent;

    private String premise;

    private String address;

    private String addressNumber;

    private String city;

    private String area;

    private String state;

    private String country;

    private String postalCode;

    public LocationItem(AddressComponent addressComponent) {
        this.addressComponent = addressComponent;
    }

    public void findValues() {
        for (ComponentItem item : addressComponent.getComponentItems()) {
            switch (item.getTypes()) {
                case "postal_code":
                    postalCode = item.getLongName();
                    break;
                case "street_number":
                    addressNumber = item.getLongName();
                    break;
                case "route":
                    address = item.getLongName();
                    break;
                case "premise":
                    premise = item.getLongName();
                    break;
                case "locality":
                    area = item.getLongName();
                    if (city == null) {
                        city = area;
                    }
                    break;
                case "administrative_area_level_1":
                    state = item.getLongName();
                    break;
                case "administrative_area_level_3":
                    city = item.getLongName();
                    if (city == null) {
                        city = area;
                    }
                    break;
                case "country":
                    country = item.getLongName();
                    break;
            }
        }
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getArea() {
        return area;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getWholeAddress() {
        String wholeAddress = "";
        if (premise != null) {
            wholeAddress += premise;
        }

        if (premise != null && address != null) {
            wholeAddress += ", " + address;
        } else if (premise == null && address != null) {
            wholeAddress += address;
        }

        if (addressNumber != null) {
            wholeAddress += " " + addressNumber;
        }
        return wholeAddress;
    }
}

