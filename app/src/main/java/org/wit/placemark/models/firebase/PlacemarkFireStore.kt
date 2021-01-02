package org.wit.placemark.models.firebase

import android.content.Context
import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.jetbrains.anko.AnkoLogger
import org.wit.placemark.helpers.readImageFromPath
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.models.PlacemarkStore
import java.io.ByteArrayOutputStream
import java.io.File

class PlacemarkFireStore(val context: Context) : PlacemarkStore, AnkoLogger {

  val placemarks = ArrayList<PlacemarkModel>()
  lateinit var userId: String
  lateinit var database: DatabaseReference
  lateinit var st: StorageReference

  override fun findAll(): ArrayList<PlacemarkModel> {
    return placemarks
  }

  override fun findById(uid: String): PlacemarkModel? {
    val foundPlacemark: PlacemarkModel? = placemarks.find { p -> p.uid == uid }
    return foundPlacemark
  }

  override fun create(placemark: PlacemarkModel) {
    val key = database.child("users").child(userId).child("placemarks").push().key
    key?.let {
      placemark.uid = key
      placemarks.add(placemark)
      database.child("users").child(userId).child("placemarks").child(key).setValue(placemark)
      updateImage(placemark)
    }
  }

  override fun update(placemark: PlacemarkModel) {
    var foundPlacemark: PlacemarkModel? = placemarks.find { p -> p.uid == placemark.uid }
    if (foundPlacemark != null) {
      foundPlacemark.title = placemark.title
      foundPlacemark.description = placemark.description
      foundPlacemark.image = placemark.image
      foundPlacemark.location = placemark.location
    }

    database.child("users").child(userId).child("placemarks").child(placemark.uid).setValue(placemark)
    if ((placemark.image.length) > 0 && (placemark.image[0] != 'h')) {
      updateImage(placemark)
    }
  }

  override fun delete(placemark: PlacemarkModel) {
    database.child("users").child(userId).child("placemarks").child(placemark.uid).removeValue()
    placemarks.remove(placemark)
  }

  override fun clear() {
    placemarks.clear()
  }

  fun updateImage(placemark: PlacemarkModel) {
    if (placemark.image != "") {
      val fileName = File(placemark.image)
      val imageName = fileName.getName()

      var imageRef = st.child(userId + '/' + imageName)
      val baos = ByteArrayOutputStream()
      val bitmap = readImageFromPath(context, placemark.image)

      bitmap?.let {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
          println(it.message)
        }.addOnSuccessListener { taskSnapshot ->
          taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
            placemark.image = it.toString()
            database.child("users").child(userId).child("placemarks").child(placemark.uid).setValue(placemark)
          }
        }
      }
    }
  }

  fun fetchPlacemarks(placemarksReady: () -> Unit) {
    val valueEventListener = object : ValueEventListener {
      override fun onCancelled(dataSnapshot: DatabaseError) {
      }
      override fun onDataChange(dataSnapshot: DataSnapshot) {
        dataSnapshot!!.children.mapNotNullTo(placemarks) { it.getValue<PlacemarkModel>(PlacemarkModel::class.java) }
        placemarksReady()
      }
    }
    userId = FirebaseAuth.getInstance().currentUser!!.uid
    database = FirebaseDatabase.getInstance().reference
    st = FirebaseStorage.getInstance().reference
    placemarks.clear()
    database.child("users").child(userId).child("placemarks").addListenerForSingleValueEvent(valueEventListener)
  }
}