package org.wit.placemark.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_edit.*

import kotlinx.android.synthetic.main.fragment_edit.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.placemark.R
import org.wit.placemark.main.MainApp
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.utils.createLoader
import org.wit.placemark.utils.hideLoader
import org.wit.placemark.utils.showLoader

class EditFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var loader : AlertDialog
    lateinit var root: View
    var editPlacemark: PlacemarkModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            editPlacemark = it.getParcelable("editplacemark")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_edit, container, false)
        activity?.title = getString(R.string.action_edit)
        loader = createLoader(activity!!)

        root.editVisited.setChecked(editPlacemark!!.visited)
        root.editFavorite.setChecked(editPlacemark!!.favorite)
        root.editMessage.setText(editPlacemark!!.message)
        root.ratingBar1.setRating(editPlacemark!!.rating)

        root.editUpdateButton.setOnClickListener {
            showLoader(loader, "Updating Placemark on Server...")
            updatePlacemarkData()
            updateUserPlacemark(app.auth.currentUser!!.uid, editPlacemark!!.uid, editPlacemark!!)
        }
        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(placemark: PlacemarkModel) =
            EditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("editplacemark",placemark)
                }
            }
    }


    fun updatePlacemarkData() {
        editPlacemark!!.visited = root.editVisited.isChecked()
        editPlacemark!!.favorite = root.editFavorite.isChecked()
        editPlacemark!!.message = root.editMessage.text.toString()
        editPlacemark!!.rating = root.ratingBar1.getRating()
    }

    fun updateUserPlacemark(userId: String, uid: String?, placemark: PlacemarkModel) {
        app.database.child("users").child(userId).child("placemarks").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(placemark)
                        activity!!.supportFragmentManager.beginTransaction()
                            .replace(R.id.homeFrame, ReportFragment.newInstance())
                            .addToBackStack(null)
                            .commit()
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Placemark error : ${error.message}")
                    }
                })
    }

}
