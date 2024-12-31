package com.example.remote_control.network

import android.util.Log
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.*
import java.io.IOException
import java.net.URISyntaxException
import java.util.Locale
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class NetworkService {

    private val serverUrl = "http://giv-sitcomdev.uni-muenster.de:5000"
    private val client = OkHttpClient()
    private var token: String = ""
    private var socket: Socket? = null

    init {
        connectSocket()
    }

    fun login(username: String, password: String, callback: (Boolean, String?) -> Unit) {
        val url = "$serverUrl/api/login"
        val json = Gson().toJson(mapOf("username" to username, "password" to password))
        val body = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Ensure callback runs on the main thread
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    callback(false, e.message)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let {
                        val json = Gson().fromJson(it, Map::class.java)
                        token = json["token"] as String
                        callback(true, null)
                    }
                } else {
                    callback(false, response.message)
                }
            }
        })
    }

    fun fetchOverlays(videoId: Int, callback: (List<Pair<Int, String>>?, String?) -> Unit) {
        val url = "http://giv-sitcomdev.uni-muenster.de:5000/api/videos/$videoId/overlays"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { jsonString ->
                        try {
                            val jsonArray = JSONArray(jsonString)
                            val overlays = mutableListOf<Pair<Int, String>>()
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                if (jsonObject.has("overlay_id") && jsonObject.has("category")) {
                                    val overlayId = jsonObject.getInt("overlay_id")
                                    val type = jsonObject.getString("category")
                                    overlays.add(overlayId to type)
                                }
                            }
                            callback(overlays, null)
                        } catch (e: Exception) {
                            callback(null, e.message)
                        }
                    }
                } else {
                    callback(null, "Error: ${response.message}")
                }
            }
        })
    }



    fun connectSocket() {
        try {
            socket = IO.socket(serverUrl)
            socket?.on(Socket.EVENT_CONNECT) {
                Log.d("Socket", "Connected")
                listenForState()
            }
            socket?.on(Socket.EVENT_DISCONNECT) {
                Log.d("Socket", "Disconnected")
            }
            socket?.on(Socket.EVENT_CONNECT_ERROR) { error ->
                Log.e("Socket", "Connection error: ${error[0]}")
            }
            socket?.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    private fun listenForState() {
        socket?.on("/get/state") { args ->
            if (args.isNotEmpty()) {
                Log.d("Socket", "State updated: ${args[0]}")
            }
        }
    }
    fun emitToggleOverlay(overlayId: Int, display: Boolean, type: String) {
        val data = JSONObject().apply {
            put("overlay_id", overlayId)
            put("display", display)
            put("type", type)
        }

        socket?.emit("/toggle/overlay", data)
        Log.d("Socket", "Emitted /toggle/overlay: $data")
    }



    fun postTemperature(temperature: Double) {
        // Round the temperature to 1 decimal place using the US locale
        val roundedTemperature = String.format(Locale.US, "%.1f", temperature).toDouble()

        val url = "http://giv-sitcomdev.uni-muenster.de:2000/api/temperature?value=$roundedTemperature"
        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create(null, ByteArray(0))) // Empty POST body
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("TemperaturePost", "Failed to post temperature: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("TemperaturePost", "API call unsuccessful: ${response.code}")
                } else {
                    Log.d("TemperaturePost", "Temperature posted successfully: $roundedTemperature")
                }
            }
        })
    }




    fun setScenario(scenarioId: Int, scenarioName: String) {
        val data = JSONObject().apply {
            put("scenario_id", scenarioId)
            put("scenario_name", scenarioName)
        }
        socket?.emit("/set/scenario", data)
    }

    fun setLocation(locationId: Int, locationType: String, locationName: String) {
        val data = JSONObject().apply {
            put("location_id", locationId)
            put("location_type", locationType)
            put("location_name", locationName)
        }
        socket?.emit("/set/location", data)
    }


}
