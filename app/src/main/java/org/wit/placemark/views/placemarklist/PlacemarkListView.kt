package org.wit.placemark.views.placemarklist

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_placemark_list.*
import org.wit.placemark.R
import org.wit.placemark.main.MainApp
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.BaseView


class PlacemarkListView :  BaseView(),  PlacemarkListener {

  lateinit var presenter: PlacemarkListPresenter
  lateinit var app: MainApp

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_placemark_list)
    setSupportActionBar(toolbar)
    super.init(toolbar, false)
    handleIntent(intent)
    app = application as MainApp


    presenter = initPresenter(PlacemarkListPresenter(this)) as PlacemarkListPresenter

    val layoutManager = LinearLayoutManager(this)
    recyclerView.layoutManager = layoutManager
    presenter.loadPlacemarks()

  }

  override fun showPlacemarks(placemarks: ArrayList<PlacemarkModel>) {
    recyclerView.adapter = PlacemarkAdapter(placemarks, this)
    recyclerView.adapter?.notifyDataSetChanged()
  }


  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.options_menu, menu)

    val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
    (menu.findItem(R.id.action_search).actionView as SearchView).apply {
      setSearchableInfo(searchManager.getSearchableInfo(componentName))
      setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
    }

    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item?.itemId) {
      R.id.item_add -> presenter.doAddPlacemark()
      R.id.item_map -> presenter.doShowPlacemarksMap()
      R.id.item_logout ->presenter.doLogout()
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onPlacemarkClick(placemark: PlacemarkModel) {
    presenter.doEditPlacemark(placemark)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    presenter.loadPlacemarks()
    super.onActivityResult(requestCode, resultCode, data)
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    handleIntent(intent)
  }


  private fun handleIntent(intent: Intent) {

    if (Intent.ACTION_SEARCH == intent.action) {
      val query = intent.getStringExtra(SearchManager.QUERY)
      if (query != null) {
        var placemarks = ArrayList(app.placemarks.findAll())
        var searchedPlacemarks = ArrayList(placemarks.filter { it.title.contains(query)})
        showPlacemarks(searchedPlacemarks)
      } else {
        presenter.loadPlacemarks()
      }
    }
  }

}