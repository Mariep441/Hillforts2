package org.wit.placemark.views.login

import android.os.Bundle
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast
import org.wit.placemark.R
import org.wit.placemark.main.MainApp
import org.wit.placemark.views.BaseView


class LoginView : BaseView() {

  lateinit var presenter: LoginPresenter
  lateinit var auth: FirebaseAuth
  lateinit var app: MainApp

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)
    app = application as MainApp
    init(toolbar, false)
    progressBar.visibility = View.GONE

    app.auth = FirebaseAuth.getInstance()
    app.database = FirebaseDatabase.getInstance().reference

    presenter = initPresenter(LoginPresenter(this)) as LoginPresenter

    signUp.setOnClickListener {
      val email = email.text.toString()
      val password = password.text.toString()
      if (email == "" || password == "") { toast("Please provide email + password")
      }
      else { presenter.doSignUp(email,password)
      }
    }

    logIn.setOnClickListener {
      val email = email.text.toString()
      val password = password.text.toString()
      if (email == "" || password == "") {
        toast("Please provide email + password")
      }
      else {
        presenter.doLogin(email,password)
      }
    }
  }

  override fun showProgress() {
    progressBar.visibility = View.VISIBLE
  }

  override fun hideProgress() {
    progressBar.visibility = View.GONE
  }

}
