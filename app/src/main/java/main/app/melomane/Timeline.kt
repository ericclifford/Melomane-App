package main.app.melomane

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.github.kittinunf.fuel.httpGet

class Timeline : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)
        val name = intent.getStringExtra("name")
        findViewById<View>(R.id.btn_playlist).setOnClickListener {
            generatePlaylist()
        }
        val playlistLabel = findViewById<View>(R.id.user_playlists) as TextView
        playlistLabel.text = getString(R.string.user_playlists, name)

    }

    private fun generatePlaylist() {
        Toast.makeText(this, "Generating", Toast.LENGTH_LONG).show()

        val accessToken = intent.getStringExtra("access_token")
        // TODO: Move this access to the .NET API if we intend to use it from the website as well
        getString(R.string.spotify_api_user_tracks)
            .httpGet()
            .header("Authorization" to "Bearer $accessToken")
            .response { _, response, _ ->

                print(response)
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
}