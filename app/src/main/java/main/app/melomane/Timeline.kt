package main.app.melomane

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.jsonDeserializer
import kotlinx.android.synthetic.main.activity_timeline.*
import org.json.JSONObject

class Timeline : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)
        val name = intent.getStringExtra("name")
        findViewById<View>(R.id.btn_playlist).setOnClickListener {
            processPlaylist()
        }

        input_search.setOnFocusChangeListener { v, hasFocus ->
            input_search.hint = ""
        }

        btn_search.setOnClickListener {
            searchArtists()
        }

    }

    private fun searchArtists() {
        val input = input_search.text.toString()
        val searchString = input.replace(" ", "%20")
        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val accessToken = intent.getStringExtra("access_token")
        val apiString = "https://api.spotify.com/v1/search?q=$searchString&type=artist"
        val intent = Intent(this, ArtistsPage::class.java).apply{
            putExtra("id", id)
            putExtra("name", name)
            putExtra("access_token", accessToken)
            putExtra("searchString", apiString)
        }
        startActivity(intent)
    }

    private fun processPlaylist() {
        val accessToken = intent.getStringExtra("access_token")
        // TODO: Move this access to the .NET API if we intend to use it from the website as well
        getString(R.string.spotify_api_user_tracks)
                .httpGet()
                .header("Authorization" to "Bearer $accessToken")
                .response { _, response, _ ->
                    //print(response)
                    val json = jsonDeserializer()
                    val results = json.deserialize(response).obj()
                    getFavorites(results)
                }
    }

    private fun getFavorites(results: JSONObject){
        val accessToken = intent.getStringExtra("access_token")
        val idList = mutableListOf<String>()
        val jsonArray = results.getJSONArray("items")

        for(i in 0 until jsonArray.length()){
            val item = jsonArray[i] as JSONObject
            val track = item["track"] as JSONObject
            val artists = track.getJSONArray("artists")

            for(j in 0 until artists.length()){
                val artist = artists[j] as JSONObject
                val name = artist.getString("name")
                val id = artist.getString("id")
                idList.add(id)
            }
        }
        val distinctIdList = idList.distinct() as MutableList<String>
        distinctIdList.shuffle()
        val finalIdList = distinctIdList as ArrayList<String>
        Thread.sleep(1000)
        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val intent = Intent(this, PlaylistPage::class.java).apply {
            putExtra("id", id)
            putExtra("name", name)
            putExtra("access_token", accessToken)
            putStringArrayListExtra("idList", finalIdList)
        }
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return super.onOptionsItemSelected(item)
    }
}