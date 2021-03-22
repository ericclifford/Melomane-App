package main.app.melomane

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Menu
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.jsonDeserializer
import main.app.melomane.databinding.ActivityTimelineBinding
import org.json.JSONObject

class Timeline : AppCompatActivity() {

    private lateinit var binding: ActivityTimelineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimelineBinding.inflate(layoutInflater)

        var pm: PackageManager = this.packageManager
        val isSpotifyInstalled: Boolean
        isSpotifyInstalled = try {
            pm.getPackageInfo("com.spotify.music", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        if(isSpotifyInstalled){
            binding.btnSpotify2.setOnClickListener {
                openSpotify()
            }
        }
        else{
            binding.btnSpotify2.setOnClickListener {
                installSpotify()
            }
        }
        val view = binding.root
        setContentView(view)

        findViewById<View>(R.id.btn_playlist).setOnClickListener {
            processPlaylist()
        }

        binding.inputSearch.setOnFocusChangeListener { _, _ ->
            binding.inputSearch.hint = ""
        }

        binding.btnSearch.setOnClickListener {
            searchArtists()
        }
    }

    private fun searchArtists() {
        val input = binding.inputSearch.text.toString()
        val searchString = input.replace(" ", "%20")
        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val accessToken = intent.getStringExtra("access_token")
        val apiString = getString(R.string.spotify_api_artist_search, searchString)
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
            putExtra("artistName", "liked artists")
            putStringArrayListExtra("idList", finalIdList)
        }
        startActivity(intent)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }

    private fun installSpotify() {
        val appPackageName = "com.spotify.music"
        val referrer = "adjust_campaign=PACKAGE_NAME&adjust_tracker=ndjczk&utm_source=adjust_preinstall"

        try {
            val uri = Uri.parse("market://details")
                    .buildUpon()
                    .appendQueryParameter("id", appPackageName)
                    .appendQueryParameter("referrer", referrer)
                    .build()
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (ignored: ActivityNotFoundException) {
            val uri = Uri.parse("https://play.google.com/store/apps/details")
                    .buildUpon()
                    .appendQueryParameter("id", appPackageName)
                    .appendQueryParameter("referrer", referrer)
                    .build()
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    private fun openSpotify() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("spotify:album:0sNOF9WDwhWunNAHPD3Baj")
        intent.putExtra(Intent.EXTRA_REFERRER,
                Uri.parse("android-app://" + this.packageName))
        startActivity(intent)
    }
}