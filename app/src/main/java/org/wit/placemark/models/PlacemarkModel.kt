package org.wit.placemark.models

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*


@IgnoreExtraProperties
@Parcelize
@Entity
data class PlacemarkModel @RequiresApi(Build.VERSION_CODES.O) constructor(var uid: String = "",
                                                                          var title: String = "",
                                                                          var description: String = "",
                                                                          var image: String = "",
                                                                          var message: String = "write your notes",
                                                                          var visited: Boolean = false,
                                                                          var dateVisited: Date = Date(2000,1,1),
                                                                          var favorite: Boolean = false,
                                                                          var rating: Float = 1f,
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


