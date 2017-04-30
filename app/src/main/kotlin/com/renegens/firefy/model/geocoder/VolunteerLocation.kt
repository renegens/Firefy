package com.renegens.firefy.model.geocoder

import com.google.gson.annotations.SerializedName

class VolunteerLocation(
        @SerializedName("results")
        var results: List<AddressComponent>?
)
