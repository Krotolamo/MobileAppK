package com.krotolamo.mobileappk

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import org.json.JSONObject

class DjBoard : AppCompatActivity() {

    val service = ServiceVolley()
    val path = "mixer/play_song/"

    fun playSong(v: View){

        var button = v.tag
        val params = JSONObject()
        val headers = HashMap<String, String>()

        params.put("user", 1)
        params.put("button", button)

        service.post(path, params, headers) { response ->

            if(response?.get("code") == 200){
                Log.d("HOLA","HOLA")
            }else{
                Log.d("HOLAA","HOLAA")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dj_board)

    }
}
