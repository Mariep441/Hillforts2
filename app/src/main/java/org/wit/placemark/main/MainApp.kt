package org.wit.placemark.main

import android.app.Application
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.placemark.models.PlacemarkStore
import org.wit.placemark.models.firebase.PlacemarkFireStore
import org.wit.placemark.room.PlacemarkStoreRoom

class MainApp : Application(), AnkoLogger {

  lateinit var placemarks: PlacemarkStore
  lateinit var auth: FirebaseAuth
  lateinit var database: DatabaseReference

  override fun onCreate() {
    super.onCreate()
    //placemarks = PlacemarkStoreRoom(applicationContext)
    placemarks = PlacemarkFireStore(applicationContext)
    info("Placemark started")
  }
}