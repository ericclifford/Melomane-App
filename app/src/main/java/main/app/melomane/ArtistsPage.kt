package main.app.melomane

import android.content.Intent
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
                println(response)
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
}
