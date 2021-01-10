package org.wit.placemark.views.map

import android.annotation.SuppressLint
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.placemark.helpers.createDefaultLocationRequest
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.BasePresenter
import org.wit.placemark.views.BaseView

class PlacemarkMapPresenter(view: BaseView) : BasePresenter(view) {

  var map: GoogleMap? = null
  var locationService: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view)
  val locationRequest = createDefaultLocationRequest()

  @SuppressLint("MissingPermission")
  fun doResartLocationUpdates(map: GoogleMap) {
    var locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult?) {
        if (locationResult != null && locationResult.locations != null) {
          val currentLocation = locationResult.locations.last()
          map.addMarker(MarkerOptions()
                  .title("You are Here")
                  .position(LatLng(currentLocation.latitude, currentLocation.longitude))
                  .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
        }
      }
    }
      locationService.requestLocationUpdates(locationRequest, locationCallback, null)
    }


  fun doPopulateMap(map: GoogleMap, placemarks: ArrayList<PlacemarkModel>) {
    map.uiSettings.setZoomControlsEnabled(true)

    placemarks.forEach {
      val loc = LatLng(it.location.lat, it.location.lng)
      val options = MarkerOptions().title(it.title).position(loc)
      map.addMarker(options).tag = it
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, it.location.zoom))
    }
  }

  fun doMarkerSelected(marker: Marker) {
    val placemark = marker.tag as PlacemarkModel

    doAsync {
      uiThread {
        if (placemark != null) { view?.showPlacemark(placemark) }
        }
      }
  }

  fun loadPlacemarks() {
    doAsync {
      val placemarks = app.placemarks.findAll()
      uiThread {
        view?.showPlacemarks(placemarks as ArrayList<PlacemarkModel>)
      }
    }
  }
}

