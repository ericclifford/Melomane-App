package main.app.melomane

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.jsonDeserializer
import main.app.melomane.databinding.ActivityPlaylistBinding
import org.json.JSONArray
import org.json.JSONObject

class PlaylistPage : AppCompatActivity() {
    private val artistIdList = mutableListOf<String>()
    private val trackList = ArrayList<Track>()

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var playlistAdapter: PlaylistRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initRecyclerView()

        val idList = intent.getStringArrayListExtra("idList")

        if (idList != null) {
            scoreRelatedArtists(idList)
        }
        else {
            val artistId = intent.getStringExtra("artistId")
            if (artistId != null) {
                val newIdList = ArrayList<String>()
                newIdList.add(artistId)
                scoreRelatedArtists(newIdList)
            }
        }

        Thread.sleep(2000)
        initData()
        binding.btnExport.setOnClickListener {
            createPlaylist()
        }
    }

    private fun initRecyclerView(){
        binding.recView.apply {
            layoutManager = LinearLayoutManager(this@PlaylistPage)
            playlistAdapter = PlaylistRecyclerAdapter()
            adapter = playlistAdapter
        }
    }

    private fun initData(){
        playlistAdapter.submitList(trackList)
        println("Finishing")
    }

    private fun scoreRelatedArtists(idList: ArrayList<String>) {
        val accessToken = intent.getStringExtra("access_token")
        for(id in idList) {

            // TODO: This should probably be a SortedList, but lord is it a weird interface.
            val scoredArtists = HashMap<Double, String>()

            for (i in 0 until 5) {
                val key = scoredArtists.keys.firstOrNull()
                val initialArtistId: String? = if (key == null) id else scoredArtists[key]
                getString(R.string.spotify_api_related_artists, initialArtistId)
                        .httpGet()
                        .header("Authorization" to "Bearer $accessToken")
                        .response { _, response, _ ->
                            val artists = jsonDeserializer().deserialize(response).obj()
                                    .getJSONArray("artists")

                            for (j in 0 until artists.length()) {
                                val artist = artists[j] as JSONObject
                                val followers = artist.getJSONObject("followers").getInt("total")
                                val popularity = artist.getDouble("popularity")
                                val score = followers / popularity
                                if (!scoredArtists.containsKey(score)) {
                                    scoredArtists[score] = artist.getString("id")
                                }
                            }
                        }
            }
            Thread.sleep(300)
            val sortedStuff = scoredArtists.toSortedMap()
            for (thing in sortedStuff) {
                println(thing.value)
            }
            val values = scoredArtists.toSortedMap().values.take(10).toMutableList()
            for(artistId in values){
                artistIdList.add(artistId)
            }
        }
        Thread.sleep(500)
        val distinctIdList = artistIdList.distinct() as MutableList<String>
        distinctIdList.shuffle()
        getTracks(distinctIdList)
    }

    private fun getTracks(idList: MutableList<String>){
        val accessToken = intent.getStringExtra("access_token")
        if(idList.isNotEmpty()){
            for(id in idList){
                val searchString = getString(R.string.spotify_api_top_tracks, id)
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
                    getString(R.string.toast_need_saved_tracks), Toast.LENGTH_LONG).show()
        }
        Thread.sleep(500)
        trackList.shuffle()
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
            for (j in 0 until artistArray.length()){
                val artist = artistArray[j] as JSONObject
                val artistName = artist.getString("name")

                artistNames += if(j == 0){
                    artistName
                } else{
                    ", $artistName"
                }
            }
            val newTrack = Track(trackId, name, artistNames, imageUrl, trackUri)
            tracks.add(newTrack)
        }
        tracks.shuffle()
        for (i in 0 until 3){
            trackList.add(tracks[i])
        }
    }

    private fun createPlaylist() {
        println("Exporting")
        val accessToken = intent.getStringExtra("access_token")
        val artistName = intent.getStringExtra("artistName")
        val body = JSONObject()
        body.put("name", "Related to $artistName")
        body.put("description", getString(R.string.description_generated_playlist))
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
            val track = trackList[i]
            val uri = track.uri
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

                    binding.btnExport.text = getString(R.string.playlist_exported)
                    binding.btnExport.setOnClickListener {
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
