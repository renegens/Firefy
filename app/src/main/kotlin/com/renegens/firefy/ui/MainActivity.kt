package com.renegens.firefy.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.justupped.android.util.SimpleActionSubscriber
import com.justupped.android.util.asAndroidIoTask
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import com.miguelbcr.ui.rx_paparazzo2.entities.size.CustomMaxSize
import com.renegens.firefy.R
import com.renegens.firefy.di.getDagger
import com.renegens.firefy.model.dto.FireValidator
import com.renegens.firefy.service.ReportService
import com.renegens.firefy.utils.LocationHelper
import com.renegens.firefy.utils.centerCrop
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import rx.Subscription
import timber.log.Timber
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class MainActivity : AppCompatActivity(), OnMapReadyCallback, MainView {

    companion object {

        const val PROVIDER_PATH = "Firefy"
        const val PICTURE_WIDTH_IN_PIXEL = 900
        const val PICTURE_HEIGHT_IN_PIXEL = 900
        const val PICTURE_QUALITY = 80
        const val CHECK_LOCATION_SETTINGS = 401

    }

    @Inject lateinit var presenter: MainPresenter
    @Inject lateinit var database: DatabaseReference
    @Inject lateinit var storage: StorageReference
    @Inject lateinit var reportService : ReportService

    private lateinit var fireValidator : FireValidator
    private lateinit var locationHelper: LocationHelper
    private lateinit var subscription: Subscription
    private var map: GoogleMap? = null
    private val locationSubscriber = SimpleActionSubscriber<String> {}

    override fun onCreate(savedInstanceState: Bundle?) {
        getDagger().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        fireValidator = FireValidator()


        presenter.view = this

        locationHelper = LocationHelper(this)
        subscription = locationHelper.checkLocationEnabled(this)

        RxPermissions(this)
                .request(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it == true }
                .subscribe {
                    mapView.getMapAsync {
                        it.isMyLocationEnabled = true
                    }
                }

        takeImage.onClick { launchCamera() }
        sent.onClick { onSentClicked() }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Timber.d("On Activity result called")
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CHECK_LOCATION_SETTINGS -> if (resultCode == RESULT_OK) {
                Timber.d("User enabled location")
                getLocation()
            } else {
                Timber.d("User Cancelled enabling location")
                Snackbar.make(rootView, "Location disabled", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        MapsInitializer.initialize(this)
        this.map = map
    }

    private fun launchCamera(){
        RxPaparazzo.single(this)
                .size(CustomMaxSize(900))
                .setFileProviderPath(PROVIDER_PATH)
                .usingCamera()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    if (response.resultCode() != RESULT_OK) {
                        response.targetUI().showUserCanceled()
                    } else {
                        response.targetUI()
                                .showPictureDone(response.data().file.path)
                    }
                }, Throwable::printStackTrace)


    }

    private fun showPictureDone(path: String?) {
        Glide.with(this)
                .load(path)
                .into(fireImage)
        var bitmap = BitmapFactory.decodeFile(path)
        bitmap = centerCrop(bitmap, PICTURE_HEIGHT_IN_PIXEL, PICTURE_WIDTH_IN_PIXEL)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, PICTURE_QUALITY, baos)
        fireValidator.image = baos.toByteArray()

    }

    private fun showUserCanceled() = Snackbar.make(rootView, "Picture canceled", Snackbar.LENGTH_LONG).show()

    private fun reportSuccess() = Snackbar.make(rootView, "Report Sent", Snackbar.LENGTH_LONG).show()

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        getLocation()
    }

    private fun getLocation() {
        RxPermissions(this)
                .request(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe()

        locationHelper
                .location
                .doOnNext { location ->
                    onLocationChange(location)
                    fireValidator.latitude = location.latitude
                    fireValidator.longitude = location.longitude
                }
                .flatMap {
                    location ->
                    reportService.getCurrentCity(
                            location.latitude,
                            location.longitude)
                }
                .doOnNext { city -> fireValidator.city = city  }
                .asAndroidIoTask()
                .subscribe(locationSubscriber)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        subscription.unsubscribe()
        locationSubscriber.unsubscribe()

    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    @SuppressWarnings("MissingPermission")
    private fun onLocationChange(location: Location) {
        var latLng = LatLng(location.latitude, location.longitude)
        map?.addMarker(MarkerOptions().position(latLng))
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18F))
    }

    private fun onSentClicked() {
        fireValidator.description = descriptionView.text.toString()

        val fireEvent = fireValidator.convert()

        if (fireValidator.image != null) {
            val storageRef = storage.child(fireValidator.imageName!!).putBytes(fireValidator.image!!)
            storageRef.addOnCompleteListener { toast("Image uploaded") }
        }

        database.push().setValue(fireEvent).addOnCompleteListener { reportSuccess() }
    }


}

