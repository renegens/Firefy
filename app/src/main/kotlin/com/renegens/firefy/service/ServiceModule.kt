package com.renegens.firefy.service

import com.renegens.firefy.service.impl.CityServiceEndpoint
import dagger.Module
import dagger.Provides

@Module
class ServiceModule {

    @Provides fun provideReportService(cityService: CityService):ReportService = CityServiceEndpoint(cityService)

}
