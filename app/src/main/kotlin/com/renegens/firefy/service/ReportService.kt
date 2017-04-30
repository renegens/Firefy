package com.renegens.firefy.service

import rx.Observable

interface ReportService {

    fun getCurrentCity(latitude: Double, longitude: Double): Observable<String?>

}
