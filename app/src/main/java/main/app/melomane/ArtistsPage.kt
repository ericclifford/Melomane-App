package main.app.melomane

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.isClientError
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.jsonDeserializer
import com.spotify.sdk.android.auth.AuthorizationResponse
import main.app.melomane.databinding.ActivityArtistsBinding
import org.json.JSONObject

class ArtistsPage : AppCompatActivity() {
    private val artists = ArrayList<Artist>()

    private lateinit var binding: ActivityArtistsBinding
    private lateinit var artistsAdapter: ArtistRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtistsBinding.inflate((layoutInflater))

        var pm: PackageManager = this.packageManager
        val isSpotifyInstalled: Boolean
        isSpotifyInstalled = try {
            pm.getPackageInfo("com.spotify.music", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        if(isSpotifyInstalled){
            binding.btnSpotify3.setOnClickListener {
                openSpotify()
            }
        }
        else{
            binding.btnSpotify3.setOnClickListener {
                installSpotify()
            }
        }

        val view = binding.root
        setContentView(view)

        initRecyclerView()

        searchArtists()
        Thread.sleep(2000)

        initData()
     }

    private fun initRecyclerView() {
        binding.recyclerViewArtist.apply {
            layoutManager = LinearLayoutManager(this@ArtistsPage)
            artistsAdapter = ArtistRecyclerAdapter(intent)
            adapter = artistsAdapter
        }
    }

    private fun initData() {
        artistsAdapter.submitList(artists)
    }

    private fun searchArtists() {
        val searchString = intent.getStringExtra("searchString")
        val accessToken = intent.getStringExtra("access_token")

        searchString?.httpGet()?.header("Authorization" to "Bearer $accessToken")
            ?.response { _, response, _ ->
                if(response.statusCode == 400) {
                    binding.txtNoArtists.visibility = View.VISIBLE
                    binding.recyclerViewArtist.visibility = View.INVISIBLE
                }
                println("THIS SHOULD PRINT ONCE")
                val json = jsonDeserializer()
                val results = json.deserialize(response).obj().getJSONObject("artists")
                val artistArray = results.getJSONArray("items")

                if(artistArray.length() == 0){
                    binding.txtNoArtists.visibility = View.VISIBLE
                    binding.recyclerViewArtist.visibility = View.INVISIBLE
                }

                for (i in 0 until artistArray.length()) {
                    val item = artistArray.getJSONObject(i)

                    val id = item.getString("id")
                    val name = item.getString("name")
                    val followers = item.getJSONObject("followers").getInt("total")

                    val imgArray = item.getJSONArray("images")
                    val imgItem = imgArray[0] as JSONObject
                    val img = imgItem.getString("url")

                    artists.add(Artist(id, name, img, followers))
                }
            }
    }

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
