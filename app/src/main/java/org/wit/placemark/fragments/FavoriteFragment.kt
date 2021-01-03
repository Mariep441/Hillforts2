package org.wit.placemark.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_placemark_list.*

import org.wit.placemark.R
import org.wit.placemark.views.placemarklist.PlacemarkAdapter
import org.wit.placemark.views.placemarklist.PlacemarkListener
import org.wit.placemark.main.MainApp
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.utils.*
import kotlinx.android.synthetic.main.fragment_report.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class FavoriteFragment : Fragment(), AnkoLogger, PlacemarkListener {

    lateinit var app: MainApp
    lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_favorite, container, false)
        activity?.title = getString(R.string.action_report)

        root.recyclerView.setLayoutManager(LinearLayoutManager(context))


        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                FavoriteFragment().apply {
                    arguments = Bundle().apply { }
                }
    }


    override fun onPlacemarkClick(placemark: PlacemarkModel) {
        activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.homeFrame, EditFragment.newInstance(placemark))
                .addToBackStack(null)
                .commit()
    }

    override fun onResume() {
        super.onResume()
        loadFavorite()
    }


    private fun loadFavorite() {
        var placemarks = ArrayList(app.placemarks.findAll())
        var favoritePlacemarks = ArrayList(placemarks.filter {it.favorite == true})
        showPlacemarks(favoritePlacemarks)
    }

    fun showPlacemarks (favoritePlacemarks: ArrayList<PlacemarkModel>) {
        recyclerView.adapter = PlacemarkAdapter(favoritePlacemarks, this)
        recyclerView.adapter?.notifyDataSetChanged()
    }
}
