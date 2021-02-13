package main.app.melomane

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.jsonDeserializer
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.android.material.snackbar.Snackbar
import main.app.melomane.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers.Main

class Timeline : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)
        val name = getIntent().getStringExtra("name")
        findViewById<View>(R.id.btn_playlist).setOnClickListener(){
            generatePlaylist()
        }
        val playlistLabel = findViewById<View>(R.id.user_playlists) as TextView
        playlistLabel.setText(name + "'s Recent Playlists")

    }

    private fun generatePlaylist() {
        Toast.makeText(this, "Generating", Toast.LENGTH_LONG).show()

        val accessToken = getIntent().getStringExtra("access_token")
        "https://api.spotify.com/v1/me/tracks"
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