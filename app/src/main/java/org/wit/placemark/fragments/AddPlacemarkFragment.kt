package org.wit.placemark.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_addplacemark.view.*


import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import org.wit.placemark.R
import org.wit.placemark.main.MainApp
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.utils.createLoader
import org.wit.placemark.utils.hideLoader
import org.wit.placemark.utils.showLoader
import java.util.HashMap


class AddPlacemarkFragment : Fragment(), AnkoLogger {

    lateinit var app: MainApp
    lateinit var loader : AlertDialog
    lateinit var eventListener : ValueEventListener
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_addplacemark, container, false)
        loader = createLoader(activity!!)
        activity?.title = getString(R.string.action_addplacemark)

        setButtonListener(root)
        return root;
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AddPlacemarkFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    fun setButtonListener( layout: View) {
        layout.addPlacemarkButton.setOnClickListener {
            val title = layout.title.text.toString()
                writeNewPlacemark(PlacemarkModel(title = title))
        }
    }


    override fun onPause() {
        super.onPause()
        if(app.auth.uid != null)
            app.database.child("user-placemarks")
                .child(app.auth.currentUser!!.uid)
                .removeEventListener(eventListener)
    }

    fun writeNewPlacemark(placemark: PlacemarkModel) {
        // Create new donation at /donations & /donations/$uid
        showLoader(loader, "Adding Placemark to Firebase")
        info("Firebase DB Reference : $app.database")
        val uid = app.auth.currentUser!!.uid
        val key = app.database.child("placemarks").push().key
        if (key == null) {
            info("Firebase Error : Key Empty")
            return
        }
        placemark.uid = key
        val placemarkValues = placemark.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/placemarks/$key"] = placemarkValues
        childUpdates["/user-placemark/$uid/$key"] = placemarkValues

        app.database.updateChildren(childUpdates)
        hideLoader(loader)
    }
}
