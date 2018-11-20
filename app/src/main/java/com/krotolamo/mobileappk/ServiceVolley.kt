package com.krotolamo.mobileappk

import android.util.Log
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject
import android.R.attr.data
import com.android.volley.*
import com.android.volley.Request.Method.POST
import com.krotolamo.mobileappk.BackendVolley.Companion.instance


class ServiceVolley {
    val TAG = ServiceVolley::class.java.simpleName
    var basePath = "http://10.25.71.234:8000/"

    fun post(path: String, params: JSONObject, headers:HashMap<String, String>, completionHandler: (response: JSONObject?) -> Unit) {
        val jsonObjReq = object : JsonObjectRequest(Method.POST, basePath + path, params,
                Response.Listener<JSONObject> { response ->
                    Log.d(TAG, "/post request OK! Response: $response")
                    val key = "code"
                    val value = 200
                    response.put(key, value)
                    completionHandler(response)
                },
                Response.ErrorListener { error ->
                    Log.d(TAG, "/post request fail! Error: ${error.message}")
                    completionHandler(null)
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                headers.put("Content-Type", "application/json")
                Log.i("headers",headers.toString())
                return headers
            }
        }

        BackendVolley.instance?.addToRequestQueue(jsonObjReq, TAG)
    }

    fun setIP(s: String){
        basePath = s
    }

    fun getIP(): String {
        return basePath
    }
}