package com.krotolamo.mobileappk

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import es.dmoral.toasty.Toasty
import org.json.JSONObject


class Login : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var button_login_login = findViewById<Button>(R.id.boton_login)
        var editText_login_username = findViewById<EditText>(R.id.email)
        var editText_login_password = findViewById<EditText>(R.id.password)

        val service = ServiceVolley()
        val session: SessionManager = SessionManager(applicationContext)

        val path = "rest-auth/login/"
        val params = JSONObject()
        val headers = HashMap<String, String>()

        button_login_login.setOnClickListener {

            var username = editText_login_username.getText().split("@")

            params.put("username", username[0])
            params.put("password", editText_login_password.getText())

            service.post(path, params, headers) { response ->

                if(response?.get("code") == 200){
                    session.createLoginSession(response.get("key").toString())
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }else{
                    Toasty.error(this,"Incorrect Credentials",Toast.LENGTH_SHORT,true).show()
                }
            }
        }

    }
}
