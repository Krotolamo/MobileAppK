package com.krotolamo.mobileappk

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import org.json.JSONObject
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_dj_board.*
import org.json.JSONException
import android.R.string.cancel
import android.app.PendingIntent.getActivity
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import android.provider.OpenableColumns
import android.R.attr.path
import java.io.*
import android.Manifest.permission
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.content.DialogInterface
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.text.InputType
import com.facebook.login.LoginManager


class DjBoard : AppCompatActivity() {

    val service = ServiceVolley()
    var update : String = "0"
    lateinit var textSong : TextView
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1
    lateinit var session: SessionManager

    fun playSong(v: View){

        var button = v.tag
        val path = "mixer/play_song/"
        val params = JSONObject()
        val headers = HashMap<String, String>()

        v.setOnLongClickListener(View.OnLongClickListener {
            this.update = v.tag.toString()
            requestRead()
        })

        params.put("user", session.userDetails)
        params.put("button", button)

        service.post(path, params, headers) { response ->

            if(response?.get("code") == 200){
                var path_cancion = response.getString("song").replace("\\","")
                var cancion = path_cancion.split("/")
                var n_cancion = cancion[cancion.size - 1]
                textView.setText("Playing song: " + n_cancion)
            }else{
                Toast.makeText(this, "Error playing song", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun stopSong(v: View){

        var button = v.tag
        val path = "mixer/stop_song/"
        textSong = findViewById(R.id.textView)
        val params = JSONObject()
        val headers = HashMap<String, String>()

        params.put("user", session.userDetails)
        params.put("button", button)

        service.post(path, params, headers) { response ->

            if(response?.get("code") == 200){

                textView.setText("Paused")
            }else{
                Toast.makeText(this, "Error pausing song", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getFileName(uri: Uri?): String {
        var result: String? = null
        if (uri?.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri?.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    fun updateSong(uri : Uri?){
        val path = "mixer/update_song_button/"
        val multipartRequest = object : VolleyMultipartRequest(com.android.volley.Request.Method.POST,service.getIP()+path, object : Response.Listener<NetworkResponse> {

            override fun onResponse(response: NetworkResponse) {
                Toast.makeText(baseContext,"Song changed", Toast.LENGTH_SHORT).show()
            }

        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                Toast.makeText(baseContext,"Error uploading song", Toast.LENGTH_SHORT).show()
            }
        }) {
            //pass String parameters here
            override fun getParams(): Map<String, String> {
                val params = HashMap<String,String>()
                params["user"] = session.userDetails
                params["button"] = update
                return params
            }

            //pass header
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                return headers
            }

            //pass file here (*/* - means you can pass any kind of file)
            override fun getByteData(): Map<String, VolleyMultipartRequest.DataPart>? {
                val params = HashMap<String, VolleyMultipartRequest.DataPart>()
                var nombre = getFileName(uri)
                val bytes = ByteArray(10000000)
                try {
                    val buf = BufferedInputStream(contentResolver.openInputStream(uri))
                    buf.read(bytes, 0, bytes.size)
                    buf.close()
                } catch (e: FileNotFoundException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
                params["song"] = DataPart(nombre, bytes, "*/*")
                return params
            }
        }
        VolleySingleton.getInstance(baseContext).addToRequestQueue(multipartRequest)
    }

    fun requestRead(): Boolean {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
        } else {
            selectSong()
        }
        return true
    }


    fun selectSong(){
        val ringIntent = Intent()
        ringIntent.type = "*/*"
        ringIntent.action = Intent.ACTION_GET_CONTENT
        ringIntent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(ringIntent, "Select Song"), 111)
    }

    fun logout(v:View){
        LoginManager.getInstance().logOut()
        var button = v.tag
        val path = "mixer/logout/"
        val params = JSONObject()
        val headers = HashMap<String, String>()

        params.put("user", session.userDetails)

        service.post(path, params, headers) { response ->

            if(response?.get("code") == 200){
                LoginManager.getInstance().logOut()
                session.logoutUser()
                val myIntent = Intent(this@DjBoard, MainActivity::class.java)
                this@DjBoard.startActivity(myIntent)
                Toast.makeText(this, "Successful logout", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "Error logout", Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            updateSong(selectedFile)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectSong()
            } else {
                // Permission Denied
                Toast.makeText(this@DjBoard, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dj_board)
        session = SessionManager(this.applicationContext)
        if(session.isLoggedIn == false){
            val myIntent = Intent(this@DjBoard, MainActivity::class.java)
            this@DjBoard.startActivity(myIntent)
        }
        textSong = this.findViewById(R.id.textView) as TextView
        textSong.isSelected = true

    }
}
