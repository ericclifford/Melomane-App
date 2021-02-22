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
import org.json.JSONObject

class Timeline : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)
        val name = intent.getStringExtra("name")
        findViewById<View>(R.id.btn_playlist).setOnClickListener {
            processPlaylist()
        }
        val playlistLabel = findViewById<View>(R.id.user_playlists) as TextView
        playlistLabel.text = getString(R.string.user_playlists, name)

    }

    private fun processPlaylist() {
        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val accessToken = intent.getStringExtra("access_token")
        val intent = Intent(this, PlaylistPage::class.java).apply {
            putExtra("id", id)
            putExtra("name", name)
            putExtra("access_token", accessToken)
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