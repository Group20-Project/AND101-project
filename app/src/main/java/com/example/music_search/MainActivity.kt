package com.example.music_search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestHeaders
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.example.music_search.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.IOException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var songList: MutableList<MutableMap<String, String>>
    private lateinit var songSubList: MutableList<MutableMap<String, String>>
    private lateinit var rvSpot: RecyclerView
    private lateinit var adapter: SpotAdapter

    var songImageURL = ""
    var trackName = ""
    var trackURL =""
    var artist = ""
    val client = AsyncHttpClient()
    var accessToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvSpot = binding.songItem
        songList = mutableListOf()
        songSubList = mutableListOf()
        var query = "drake"
        var id = "4lIDpSSMcrmN6XBQYjWfvv"

        // search input
        var queryET = binding.queryEt as EditText
        queryET.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                id = queryET.text.toString()
                // parse ID from URL
                id = id.substringAfter("https://open.spotify.com/album/")
                id = id.substringBefore("?")
                Log.d("Spotify Query ID", id)
                queryET.text.clear()
                songList.clear()
                lifecycleScope.launch{
                    getSpotDataURL(id)
                    createAdapter()
                }
                this.currentFocus?.let { hideSoftKeyboard(it) }
                return@OnEditorActionListener true
            }
            false
        })

        Log.d("Spotify Query ID", id)

        lifecycleScope.launch (Dispatchers.IO) {
            syncGenerateToken()
            getSpotDataURL(id)
        }

    }

    private fun createAdapter(){
        adapter = SpotAdapter(songList)
        rvSpot.adapter = adapter
//        rvSpot.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        rvSpot.layoutManager = LinearLayoutManager(this@MainActivity)
    }

    private suspend fun getSpotDataURL(id: String) {
//        val apiKey = getString(R.string.api_key)
//        var spotAPIURL = "https://developer.spotify.com/documentation/web-api/reference/search"
        var spotAPIURL = "https://api.spotify.com/v1/albums/$id"
        Log.d("Spotify Token getSpot", accessToken)
        val params = RequestParams()
//        params["id"] = "4lIDpSSMcrmN6XBQYjWfvv"
//        params["q"] = "drake"
//        params["type"] = "track"
//        params["market"] = "ES"
//        params["limit"] = "5"
//        params.put("page", 0)
        val requestHeaders = RequestHeaders()

        requestHeaders["Authorization"] = "Bearer " + accessToken
        client[spotAPIURL, requestHeaders, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                var trackArray = json.jsonObject.getJSONObject("tracks").getJSONArray("items")
                var songImageURL = json.jsonObject.getJSONArray("images").getJSONObject(0).getString("url")
                var artist = json.jsonObject.getJSONArray("artists").getJSONObject(0).getString("name")
                var albumURL = json.jsonObject.getJSONObject("external_urls").getString("spotify")
                var artistURL = ""
                Log.d("Spotify API albumCover", songImageURL)
                Log.d("Spotify API artist", artist)
//                Log.d("Spotify API track array", trackArray.toString())
//                Log.d("Spotify API ", json.jsonObject.getJSONObject("tracks").
//                getJSONArray("items").getJSONObject(1).getString("name"))

                for (i in 0 until trackArray.length()) {
                    trackName = trackArray.getJSONObject(i).getString("name")
                    Log.d("Spotify API trackName", trackName)

//                     $.tracks.items[i].album.external_urls.spotify
                    trackURL = trackArray.getJSONObject(i).getJSONObject("external_urls").
                    getString("spotify")
                    Log.d("Spotify API trackURL", trackURL)

                    // tracks.items[0].artists[0].external_urls.spotify
                    artistURL = trackArray.getJSONObject(i).getJSONArray("artists").getJSONObject(0).
                    getJSONObject("external_urls").getString("spotify")
                    Log.d("Spotify API artistURL", artistURL)

                    songList.add(mutableMapOf(
                        "imageURL" to songImageURL, "trackURL" to trackURL, "trackName" to trackName,
                        "artist" to artist, "artistURL" to artistURL, "albumURL" to albumURL)
                    )
                    Log.d("Spotify API added", songList.last().toString())
                }

                createAdapter()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                lifecycleScope.launch (Dispatchers.IO) {
                    syncGenerateToken()
                }
                Log.d("Spotify API Error", errorResponse)
            }
        }]
    }

    suspend fun generateToken(): String {
        var spotTokenURL = "https://accounts.spotify.com/api/token"

        val params = RequestParams()
        val requestHeaders = RequestHeaders()
        requestHeaders["Content-Type"] = "application/x-www-form-urlencoded"
        // enter your credentials
        val base64credentials = "N2M2YzdiODMyOWQ5NDVkNGE3NzBmYmYxYTg1NDA4MjI6MDgyOTVhYmM0ZGE2NGI0MWFlYmQxYTcyODFiM2U5ZmQ="
        requestHeaders["Authorization"] = "Basic $base64credentials"

        val requestBody: RequestBody = FormBody.Builder().addEncoded("grant_type", "client_credentials").build()
        client.post(spotTokenURL, requestHeaders, params, requestBody, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {

                accessToken =  json.jsonObject.getString("access_token")

                Log.d("Spotify Token success", accessToken)

            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("Spotify Token Error", errorResponse)
            }
        })
        return accessToken
    }

    suspend fun syncGenerateToken(): String {
        var spotTokenURL = "https://accounts.spotify.com/api/token"
        // enter your credentials
        val base64credentials = "N2M2YzdiODMyOWQ5NDVkNGE3NzBmYmYxYTg1NDA4MjI6MDgyOTVhYmM0ZGE2NGI0MWFlYmQxYTcyODFiM2U5ZmQ="

        val okClient = OkHttpClient()
        val requestBody: RequestBody = FormBody.Builder().addEncoded("grant_type", "client_credentials").build()

        val request = Request.Builder()
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Authorization", "Basic $base64credentials")
            .method("POST", requestBody)
            .url(spotTokenURL)
            .build()

        // retrieve access token
        okClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val responseData = response.body!!.string()
            val json = JSONObject(responseData)
            accessToken = json.getString("access_token")
            Log.d("Spotify Sync Token", accessToken)
        }
        return accessToken
    }

    fun hideSoftKeyboard(view: View) {
//        val view: View? = this.currentFocus
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
