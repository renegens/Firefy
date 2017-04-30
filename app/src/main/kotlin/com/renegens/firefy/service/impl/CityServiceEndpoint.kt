package com.renegens.firefy.service.impl

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.renegens.firefy.model.geocoder.LocationItem
import com.renegens.firefy.model.geocoder.VolunteerLocation
import com.renegens.firefy.service.CityService
import com.renegens.firefy.service.ReportService
import rx.Observable
import rx.schedulers.Schedulers


class CityServiceEndpoint(val cityService: CityService) : ReportService {

    override fun getCurrentCity(latitude: Double, longitude: Double): Observable<String?> =
            cityService
                    .reportAddress(latitude.toString() + "," + longitude.toString(), "true")
                    .flatMap { response -> findCity(response) }
                    .subscribeOn(Schedulers.io())

    private fun findCity(jsonObject: JsonObject): Observable<String?>  {
        val location = Gson().fromJson(jsonObject, VolunteerLocation::class.java)
        val locationItem = LocationItem(location.results?.first())
        locationItem.findValues()

        return Observable.just(locationItem.city)
    }

}
