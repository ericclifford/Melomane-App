package main.app.melomane

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.jsonDeserializer
import kotlinx.android.synthetic.main.activity_playlist.*
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONStringer
import kotlin.reflect.typeOf

class PlaylistPage() : AppCompatActivity() {
    private val artistIdList = mutableListOf<String>()
    private val trackList = ArrayList<Track>()
    private lateinit var playlistAdapter: PlaylistRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)
        initRecyclerView()
        val idList = intent.getStringArrayListExtra("idList") as ArrayList<String>
        getRelated(idList)
        Thread.sleep(2000)
        initData()
        btn_export.setOnClickListener {
            createPlaylist()
        }
    }

    private fun initRecyclerView(){
        rec_view.apply {
            layoutManager = LinearLayoutManager(this@PlaylistPage)
            playlistAdapter = PlaylistRecyclerAdapter()
            adapter = playlistAdapter
        }
    }

    private fun initData(){
        playlistAdapter.submitList(trackList)
        println("Finishing")
    }

    private fun getRelated(idList : ArrayList<String>){
        val accessToken = intent.getStringExtra("access_token")
        for(id in idList){
            val searchString = "https://api.spotify.com/v1/artists/$id/related-artists"
            searchString.httpGet()
                    .header("Authorization" to "Bearer $accessToken")
                    .response { _, response, _ ->
                        //print(response)
                        val json = jsonDeserializer()
                        val results = json.deserialize(response).obj()
                        processRelated(results)
                    }
        }
        Thread.sleep(500)
        val distinctIdList = artistIdList.distinct() as MutableList<String>
        distinctIdList.shuffle()
        getTracks(distinctIdList)

    }

    private fun processRelated(results: JSONObject){

        val jsonArray = results.getJSONArray("artists")

        for(i in 0 until jsonArray.length()){
            val artist = jsonArray[i] as JSONObject
            val artistId = artist.getString("id")
            artistIdList.add(artistId)
        }
    }

    private fun getTracks(idList: MutableList<String>){
        val accessToken = intent.getStringExtra("access_token")
        if(idList.isNotEmpty()){
            val ids = idList.subList(0,10)
            for(id in ids){
                val searchString = "https://api.spotify.com/v1/artists/$id/top-tracks?market=US"
                searchString.httpGet()
                        .header("Authorization" to "Bearer $accessToken")
                        .response { _, response, _ ->
                            //print(response)
                            val json = jsonDeserializer()
                            val results = json.deserialize(response).obj()
                            processTracks(results)

                        }
            }
        }
        else {
            Toast.makeText(this,
                    "Must have saved tracks in Spotify account", Toast.LENGTH_LONG).show()
        }
        Thread.sleep(500)
        trackList.shuffle()
        for(track in trackList){
            println(track.toString())
        }
    }
    private fun processTracks(results: JSONObject){

        val jsonArray = results.getJSONArray("tracks")
        val tracks = mutableListOf<Track>()
        for(i in 0 until jsonArray.length()){
            val track = jsonArray[i] as JSONObject
            val album = track.getJSONObject("album")
            val images = album.getJSONArray("images")
            val image = images[0] as JSONObject
            val imageUrl = image.getString("url")
            val name = track.getString("name")
            val trackId = track.getString("id")
            val trackUri = track.getString("uri")
            var artistNames = ""
            val artistArray = track.getJSONArray("artists")
            for (i in 0 until artistArray.length()){
                val artist = artistArray[i] as JSONObject
                val artistName = artist.getString("name")

                artistNames += if(i == 0){
                    artistName
                } else{
                    ", $artistName"
                }
            }
            val newTrack = Track(trackId, name, artistNames, imageUrl, trackUri)
            tracks.add(newTrack)
        }
        for (i in 0 until 3){
            trackList.add(tracks[i])
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return super.onOptionsItemSelected(item)
    }

    private fun createPlaylist() {
        println("Exporting")
        val accessToken = intent.getStringExtra("access_token")
        val body = JSONObject()
        body.put("name", "Melomane Playlist")
        body.put("description", "Playlist generated by your Melomane app.")
        body.put("public", true)
        println("JSON Made")
        println(body.toString())
        getString(R.string.spotify_api_user_playlists)
        .httpPost()
                .header("Authorization" to "Bearer $accessToken")
                .jsonBody(body.toString())
                .response { _, response, _ ->
                    println("Response")
                    println(response)
                    val json = jsonDeserializer()
                    val results = json.deserialize(response).obj()
                    val pid = results.getString("id")
                    exportPlaylist(pid)
                }
    }

    private fun exportPlaylist(pid: String) {
        val accessToken = intent.getStringExtra("access_token")
        val trackArray = JSONArray()
        for(i in 0 until trackList.size){
            val track = trackList[i] as Track
            val uri = track.uri as String
            trackArray.put(i, uri)
        }
        val body = JSONObject()
        body.put("uris", trackArray)
        val addString = "https://api.spotify.com/v1/playlists/$pid/tracks"
        addString.httpPost()
                .header("Authorization" to "Bearer $accessToken")
                .jsonBody(body.toString())
                .response { _, response, _ ->
                    println("Response")
                    println(response)

                    btn_export.text = "Playlist Exported!! Go to Home Screen"
                    btn_export.setOnClickListener {
                        goHome()
                    }
                }
    }

    private fun goHome() {
        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val accessToken = intent.getStringExtra("access_token")
        val intent = Intent(this, Timeline::class.java).apply{
            putExtra("id", id)
            putExtra("name", name)
            putExtra("access_token", accessToken)
        }
        startActivity(intent)
    }
}
