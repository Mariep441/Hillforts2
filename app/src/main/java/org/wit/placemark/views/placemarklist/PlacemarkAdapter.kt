package org.wit.placemark.views.placemarklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_placemark.view.*
import kotlinx.android.synthetic.main.card_placemark.view.*
import kotlinx.android.synthetic.main.card_placemark.view.description
import org.wit.placemark.R
import org.wit.placemark.helpers.readImageFromPath
import org.wit.placemark.models.PlacemarkModel

interface PlacemarkListener {
  fun onPlacemarkClick(placemark: PlacemarkModel)
}

class PlacemarkAdapter constructor(
        private var placemarks: ArrayList<PlacemarkModel>,
        private val listener: PlacemarkListener
) : RecyclerView.Adapter<PlacemarkAdapter.MainHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
    return MainHolder(
            LayoutInflater.from(parent?.context).inflate(
                    R.layout.card_placemark,
                    parent,
                    false
            )
    )
  }

  fun removeAt(position: Int) {
    placemarks.removeAt(position)
    notifyItemRemoved(position)
  }




  override fun onBindViewHolder(holder: MainHolder, position: Int) {
    val placemark = placemarks[holder.adapterPosition]
    holder.bind(placemark, listener)
  }

  override fun getItemCount(): Int = placemarks.size

  class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(placemark: PlacemarkModel, listener: PlacemarkListener) {
      itemView.title.text = placemark.title
      itemView.description.text = placemark.description
      itemView.imageIcon.setImageBitmap(readImageFromPath(itemView.context, placemark.image))
      itemView.setOnClickListener { listener.onPlacemarkClick(placemark) }
    }
  }
}