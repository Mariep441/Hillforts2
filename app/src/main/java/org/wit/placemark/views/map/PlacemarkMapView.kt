package org.wit.placemark.views.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_placemark.*
import kotlinx.android.synthetic.main.activity_placemark_maps.*
import kotlinx.android.synthetic.main.activity_placemark_maps.mapView
import org.wit.placemark.R

import org.wit.placemark.helpers.readImageFromPath
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.BaseView

class PlacemarkMapView : BaseView(), GoogleMap.OnMarkerClickListener {

  lateinit var presenter: PlacemarkMapPresenter
  lateinit var map : GoogleMap


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_placemark_maps)
    super.init(toolbar, true);

    presenter = initPresenter (PlacemarkMapPresenter(this)) as PlacemarkMapPresenter

    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync {
      map = it
      map.setOnMarkerClickListener(this)
      presenter.loadPlacemarks()
    }
  }

  override fun showPlacemark(placemark: PlacemarkModel) {
    currentTitle.text = placemark.title
    currentDescription.text = placemark.description
    Glide.with(this).load(placemark.image).into(currentImage);
  }

  override fun showPlacemarks(placemarks: ArrayList<PlacemarkModel>) {
    presenter.doPopulateMap(map, placemarks)
    presenter.doResartLocationUpdates(map)
  }

  override fun onMarkerClick(marker: Marker): Boolean {
    presenter.doMarkerSelected(marker)
    return true
  }

  override fun onDestroy() {
    super.onDestroy()
    mapView.onDestroy()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    mapView.onLowMemory()
  }

  override fun onPause() {
    super.onPause()
    mapView.onPause()
  }

  override fun onResume() {
    super.onResume()
    mapView.onResume()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    mapView.onSaveInstanceState(outState)
  }
}

