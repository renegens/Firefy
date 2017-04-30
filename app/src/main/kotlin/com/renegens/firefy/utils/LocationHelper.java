package com.renegens.firefy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class LocationHelper {

    private final int CHECK_LOCATION_SETTINGS = 401;
    private final Context context;
    private ReactiveLocationProvider locationProvider;
    private Observable<Location> lastKnownLocationObservable;
    private Observable<Location> locationUpdatesObservable;
    private LocationRequest locationRequest;

    public LocationHelper(Context context) {
        this.context = context;
        locationInit();
    }

    @SuppressWarnings({"MissingPermission"})
    private void locationInit() {
        //Reactive Location
        locationProvider = new ReactiveLocationProvider(context);
        lastKnownLocationObservable = locationProvider.getLastKnownLocation();

        locationRequest = LocationRequest.create()
                                         .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                         .setNumUpdates(5)
                                         .setInterval(100);

        locationUpdatesObservable = locationProvider.getUpdatedLocation(locationRequest);
    }

    public Subscription checkLocationEnabled(final Activity activity) {
        return locationProvider
                .checkLocationSettings(
                        new LocationSettingsRequest.Builder()
                                .addLocationRequest(locationRequest)
                                .setAlwaysShow(true)  //Reference: http://stackoverflow.com/questions/29824408/google-play-services-locationservices-api-new-option-never
                                .build()
                )
                .doOnNext(new Action1<LocationSettingsResult>() {
                    @Override
                    public void call(LocationSettingsResult locationSettingsResult) {
                        Status status = locationSettingsResult.getStatus();
                        if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            try {
                                status.startResolutionForResult(activity, CHECK_LOCATION_SETTINGS);
                            } catch (IntentSender.SendIntentException th) {
                                Log.e("MainActivity", "Error opening settings activity.", th);
                            }
                        }
                    }
                })
                .subscribe(new LogExOnlySubscriber<>());

    }

    public Observable<Location> getLocation() {
        return lastKnownLocationObservable
                .switchIfEmpty(locationUpdatesObservable)
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static class LogExOnlySubscriber<T> extends Subscriber<T> {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable ex) {
            Timber.d(ex, "Your RX IZ FAILING YO!");
        }

        @Override
        public void onNext(T t) {
        }
    }

}

