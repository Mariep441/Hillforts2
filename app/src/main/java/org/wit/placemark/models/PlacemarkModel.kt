package org.wit.placemark.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
@Parcelize
@Entity
data class PlacemarkModel(var uid: String = "",
                          var title: String = "",
                          var description: String = "",
                          var image: String = "",
                          var message: String = "a message",
                          var visited: Boolean = false,
                          var favorite: Boolean = false,
                          var rating: Int = 0,
                          @Embedded var location : Location = Location()): Parcelable


{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
                "uid" to uid,
                "title" to title,
                "description" to description,
                "image" to image,
                "message" to message,
                "visited" to visited,
                "favorite" to favorite,
                "rating" to rating,

        )
    }
}



@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable


