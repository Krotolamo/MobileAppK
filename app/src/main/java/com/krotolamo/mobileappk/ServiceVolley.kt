package com.krotolamo.mobileappk

import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject


class ServiceVolley {
    val TAG = ServiceVolley::class.java.simpleName
    val basePath = "http://192.168.137.162:8000/"

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
}