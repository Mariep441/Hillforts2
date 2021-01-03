package org.wit.placemark.views


import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.nav_header_home.view.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.wit.placemark.R
import org.wit.placemark.fragments.AboutUsFragment
import org.wit.placemark.fragments.FavoriteFragment
import org.wit.placemark.fragments.ReportFragment
import org.wit.placemark.fragments.StatsFragment
import org.wit.placemark.main.MainApp
import org.wit.placemark.views.location.EditLocationView
import org.wit.placemark.views.login.LoginView
import org.wit.placemark.views.map.PlacemarkMapView
import org.wit.placemark.views.placemark.PlacemarkView
import org.wit.placemark.views.placemarklist.PlacemarkListView


class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var ft: FragmentTransaction
    lateinit var app: MainApp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        setSupportActionBar(toolbar)
        app = application as MainApp
        fab.setOnClickListener { coordinatorLayout ->
            Snackbar.make(coordinatorLayout, "Please visit the Atlas of Hillforts", Snackbar.LENGTH_LONG)
                    .setAction("GO") {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://hillforts.arch.ox.ac.uk/"))
                        startActivity(browserIntent)}
                    .setActionTextColor(Color.BLUE)
                    .show()
        }



        navView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.getHeaderView(0).nav_header_email.text = app.auth.currentUser?.email

        ft = supportFragmentManager.beginTransaction()

        val fragment = AboutUsFragment.newInstance()
        ft.replace(R.id.homeFrame, fragment)
        ft.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_stats ->
                navigateTo(StatsFragment.newInstance())
            R.id.nav_report ->
                navigateTo(ReportFragment.newInstance())
            R.id.nav_favourites ->
                navigateTo(FavoriteFragment.newInstance())
            R.id.nav_aboutus ->
                navigateTo(AboutUsFragment.newInstance())
            R.id.nav_sign_out ->
                signOut()

            else -> toast("You Selected Something Else")
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.item_add -> navigateTo( VIEW.PLACEMARK)
            R.id.item_list -> navigateTo( VIEW.LIST)
            R.id.item_map -> navigateTo( VIEW.MAPS)
        }

        return super.onOptionsItemSelected(item)
    }


    fun navigateTo(view: VIEW, code: Int = 0, key: String = "", value: Parcelable? = null) {
        var intent = Intent(this, PlacemarkListView::class.java)
        when (view) {
            VIEW.LOCATION -> intent = Intent(this, EditLocationView::class.java)
            VIEW.PLACEMARK -> intent = Intent(this, PlacemarkView::class.java)
            VIEW.MAPS -> intent = Intent(this, PlacemarkMapView::class.java)
            VIEW.LIST -> intent = Intent(this, PlacemarkListView::class.java)
            VIEW.LOGIN -> intent = Intent(this, LoginView::class.java)
            VIEW.HOME -> intent = Intent(this, Home::class.java)
        }
        if (key != "") {
            intent.putExtra(key, value)
        }
        startActivityForResult(intent, code)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
         else
            super.onBackPressed()
    }

    private fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.homeFrame, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun signOut()
    {
        app.auth.signOut()
        startActivity<LoginView>()
        finish()
    }
}
