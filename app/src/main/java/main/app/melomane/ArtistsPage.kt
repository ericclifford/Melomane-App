package main.app.melomane

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.jsonDeserializer
import kotlinx.android.synthetic.main.activity_artists.*
import org.json.JSONObject

class ArtistsPage : AppCompatActivity(){
    private lateinit var artist: Artist
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artists)
        searchArtists()
        Thread.sleep(2000)
        fillContent()
        btn_artistPlaylist.setOnClickListener {
            getPlaylist()
        }
     }

    private fun getPlaylist() {
        if(artist != null){
            val idList = ArrayList<String>()
            val accessToken = intent.getStringExtra("access_token")
            idList.add(artist.id)
            val id = intent.getStringExtra("id")
            val name = intent.getStringExtra("name")
            val intent = Intent(this, PlaylistPage::class.java).apply {
                putExtra("id", id)
                putExtra("name", name)
                putExtra("access_token", accessToken)
                putStringArrayListExtra("idList", idList)
            }
            startActivity(intent)
        }
    }

    private fun fillContent() {
        if(artist != null){
            lbl_artistName.text = artist.name
            val requestOptions = RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)

            Glide.with(this)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(artist.image)
                    .into(img_artist)
        }
        else{
            lbl_artistName.text = "Artist Not Found"
            img_artist.isVisible = false
            btn_artistPlaylist.isVisible = false
        }
    }


    private fun searchArtists() {
        val searchString = intent.getStringExtra("searchString")
        val accessToken = intent.getStringExtra("access_token")
        if (searchString != null) {
            searchString.httpGet()
                    .header("Authorization" to "Bearer $accessToken")
                    .response { _, response, _ ->
                        println("Response")
                        println(response)
                        val json = jsonDeserializer()
                        val results = json.deserialize(response).obj()
                        val artists = results.getJSONObject("artists")
                        val artistArray = artists.getJSONArray("items")
                        val item = artistArray[0] as JSONObject
                        val id = item.getString("id")
                        val imgArray = item.getJSONArray("images")
                        val imgItem = imgArray[0] as JSONObject
                        val img = imgItem.getString("url")
                        val name = item.getString("name")
                        artist = Artist(id, name, img)
                    }
        }
    }

}
