package com.example.music_search

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestHeaders
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.example.music_search.databinding.ActivityMainBinding
import okhttp3.Headers

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var songList: MutableList<MutableMap<String, String>>
    private lateinit var rvSpot: RecyclerView
    private lateinit var adapter: SpotAdapter

    var songImageURL = ""
    var trackName = ""
    var trackURL =""
    var artist = ""
    val client = AsyncHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvSpot = binding.songItem
        songList = mutableListOf()
        var query = "drake"
        var id = "4lIDpSSMcrmN6XBQYjWfvv"

        getSpotDataURL(id)
        createAdapter()
    }

    private fun createAdapter(){
        adapter = SpotAdapter(songList)
        rvSpot.adapter = adapter
        rvSpot.layoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
    }

    private fun getSpotDataURL(id: String) {
//        val apiKey = getString(R.string.api_key)
//        var spotAPIURL = "https://developer.spotify.com/documentation/web-api/reference/search"
        var spotAPIURL = "https://api.spotify.com/v1/albums/$id"
        val accessToken = "BQCE9Jjb5mlsK92uPcPy_jkNcJDEB5zak7tHsGiPrKTlmADsMarb7aEuSTBlT2D_xAUK96OK7s8N8xb0Tu2x3pJL_h9JfBfC5-gZRcIhdPxeLZMzLf1J"
        val params = RequestParams()
//        params["id"] = "4lIDpSSMcrmN6XBQYjWfvv"
//        params["q"] = "drake"
//        params["type"] = "track"
//        params["market"] = "ES"
//        params["limit"] = "5"
//        params.put("page", 0)
        val requestHeaders = RequestHeaders()
        requestHeaders["Authorization"] = "Bearer " + accessToken
        val url = "https://accounts.spotify.com/api/token?grant_type=client_credentials"
        client[spotAPIURL, requestHeaders, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                var trackArray = json.jsonObject.getJSONObject("tracks").getJSONArray("items")
                var songImageURL = json.jsonObject.getJSONArray("images").getJSONObject(1).getString("url")
                var artist = json.jsonObject.getJSONArray("artists").getJSONObject(0).getString("name")
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

                    songList.add(mutableMapOf(
                        "imageURL" to songImageURL, "trackURL" to trackURL, "trackName" to trackName, "artist" to artist)
                    )
                    Log.d("Spotify API added", songList.last().toString())
                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("Spotify API Error", errorResponse)
            }
        }]
    }

}