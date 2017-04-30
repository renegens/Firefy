package com.renegens.firefy.model.dto

import com.renegens.firefy.model.FireEvent
import java.util.*

class FireValidator {

    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var description: String? = null
    var image: ByteArray? = null
        set(value) {
            field = value
            imageName = UUID.randomUUID().toString() + ".jpg"
        }
    var imageName: String? = null
    var city: String? = null

    fun convert()= FireEvent(latitude, longitude, description, city, imageName)

}
