package com.renegens.firefy.ui

import com.renegens.firefy.service.CityService
import dagger.Module
import dagger.Provides

@Module
class MainModule {

    @Provides fun provideMainPresenter(cityService: CityService) = MainPresenter(cityService)

}
