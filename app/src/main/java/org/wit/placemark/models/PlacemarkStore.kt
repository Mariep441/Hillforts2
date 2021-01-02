package org.wit.placemark.models

interface PlacemarkStore {
  fun findAll(): ArrayList<PlacemarkModel>
  fun create(placemark: PlacemarkModel)
  fun update(placemark: PlacemarkModel)
  fun delete(placemark: PlacemarkModel)
  fun findById(uid:String) : PlacemarkModel?
  fun clear()
}