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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


import kotlinx.android.synthetic.main.fragment_report.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.placemark.R
import org.wit.placemark.main.MainApp
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.utils.*
import org.wit.placemark.views.placemarklist.PlacemarkAdapter
import org.wit.placemark.views.placemarklist.PlacemarkListener

class ReportFragment : Fragment(), AnkoLogger, PlacemarkListener {

    lateinit var app: MainApp
    lateinit var loader : AlertDialog
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
        root = inflater.inflate(R.layout.fragment_report, container, false)
        activity?.title = getString(R.string.action_report)

        root.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        setSwipeRefresh()

        val swipeDeleteHandler = object : SwipeToDeleteCallback(activity!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = root.recyclerView.adapter as PlacemarkAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                deletePlacemark((viewHolder.itemView.tag as PlacemarkModel).uid)
                deleteUserPlacemark(app.auth.currentUser!!.uid,
                        (viewHolder.itemView.tag as PlacemarkModel).uid)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(root.recyclerView)

        val swipeEditHandler = object : SwipeToEditCallback(activity!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onPlacemarkClick(viewHolder.itemView.tag as PlacemarkModel)
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(root.recyclerView)

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                ReportFragment().apply {
                    arguments = Bundle().apply { }
                }
    }

    fun setSwipeRefresh() {
        root.swiperefresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                root.swiperefresh.isRefreshing = true
                getAllPlacemarks(app.auth.currentUser!!.uid)
            }
        })
    }

    fun checkSwipeRefresh() {
        if (root.swiperefresh.isRefreshing) root.swiperefresh.isRefreshing = false
    }

    fun deleteUserPlacemark(userId: String, uid: String?) {
        app.database.child("user-donations").child(userId).child(uid!!)
                .addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.ref.removeValue()
                            }
                            override fun onCancelled(error: DatabaseError) {
                                info("Firebase Donation error : ${error.message}")
                            }
                        })
    }

    fun deletePlacemark(uid: String?) {
        app.database.child("donations").child(uid!!)
                .addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.ref.removeValue()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                info("Firebase Donation error : ${error.message}")
                            }
                        })
    }

    override fun onPlacemarkClick(placemark: PlacemarkModel) {
        activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.homeFrame, EditFragment.newInstance(placemark))
                .addToBackStack(null)
                .commit()
    }

    override fun onResume() {
        super.onResume()
        getAllPlacemarks(app.auth.currentUser!!.uid)
    }

    fun getAllPlacemarks(userId: String?) {
        loader = createLoader(activity!!)
        showLoader(loader, "Downloading Placemarks from Firebase")
        val placemarksList = ArrayList<PlacemarkModel>()
        app.database.child("user-placemarks").child(userId!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Placemark error : ${error.message}")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        hideLoader(loader)
                        val children = snapshot.children
                        children.forEach {
                            val placemark = it.
                            getValue<PlacemarkModel>(PlacemarkModel::class.java)

                            placemarksList.add(placemark!!)
                            root.recyclerView.adapter =
                                    PlacemarkAdapter(placemarksList, this@ReportFragment)
                            root.recyclerView.adapter?.notifyDataSetChanged()
                            checkSwipeRefresh()

                            app.database.child("user-donations").child(userId)
                                    .removeEventListener(this)
                        }
                    }
                })
    }
}

