package com.krotolamo.mobileappk
import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.facebook.AccessToken
import org.json.JSONObject
import com.facebook.GraphResponse
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import java.util.*
import android.support.v4.content.ContextCompat.startActivity
import android.view.View.VISIBLE
import android.widget.TextView
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.RelativeLayout
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.view.animation.Animation.AnimationListener
import android.widget.LinearLayout
import kotlin.math.log


class MainActivity : AppCompatActivity() {

    lateinit var loginButton : LoginButton
    lateinit var callbackManager: CallbackManager
    var token : String = ""
    var first_name : String = ""
    var last_name : String = ""
    var email : String = ""
    var id : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val session: SessionManager = SessionManager(this.applicationContext)
        if(session.isLoggedIn){
            val myIntent = Intent(this@MainActivity, DjBoard::class.java)
            this@MainActivity.startActivity(myIntent)
        }
        loginButton = findViewById(R.id.login_button)
        var slide: Animation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -4f)
        slide.duration = 1000
        slide.isFillEnabled = true
        slide.fillAfter = true
        slide.setAnimationListener(object : AnimationListener {

            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                loginButton.clearAnimation()
                val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                lp.topMargin += 1175
                loginButton.setLayoutParams(lp)
            }

        })
        loginButton.startAnimation(slide)
        callbackManager = CallbackManager.Factory.create()
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                ) { objeto, _ ->
                    email = objeto.getString("email")
                    var name = objeto.getString("name").split(" ")
                    id = objeto.getString("id")
                    first_name = name[0]
                    last_name = name[1]
                    token = AccessToken.getCurrentAccessToken().token.toString()
                    if(token != ""){
                        val path = "mixer/login_facebook/"
                        val params = JSONObject()
                        val headers = HashMap<String, String>()
                        val service = ServiceVolley()
                        Log.d("ID_USER",id)
                        params.put("id",id)
                        params.put("token",token)
                        params.put("email",email)
                        params.put("first_name",first_name)
                        params.put("last_name",last_name)
                        service.post(path, params, headers) { response ->
                            if(response?.get("code") == 200){
                                Log.d("RESPONSE",response.toString())
                                session.createLoginSession(response.getString("id"))
                                Toast.makeText(applicationContext,"Successful Login!",Toast.LENGTH_SHORT).show()
                                val myIntent = Intent(this@MainActivity, DjBoard::class.java)
                                this@MainActivity.startActivity(myIntent)
                            }else{
                                Toast.makeText(this@MainActivity, "Error loading profile", Toast.LENGTH_LONG).show()
                                LoginManager.getInstance().logOut()
                            }
                        }
                    }else{
                        Toast.makeText(this@MainActivity, "Error loading profile", Toast.LENGTH_LONG).show()
                        LoginManager.getInstance().logOut()
                    }
                }
                val parameters = Bundle()
                loginButton.setReadPermissions(Arrays.asList("email","public_profile"))
                parameters.putString("fields", "id,name,email")
                request.parameters = parameters
                request.executeAsync()

            }
            override fun onCancel() {
                Toast.makeText(applicationContext,"Facebook Login Canceled",Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(applicationContext,"Unsuccessful Login",Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}