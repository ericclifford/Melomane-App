package main.app.melomane

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import main.app.melomane.databinding.LayoutArtistItemBinding

class ArtistRecyclerAdapter(val intent: Intent) : RecyclerView.Adapter<ArtistRecyclerAdapter.ArtistViewHolder>() {

    private var items: List<Artist> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val itemBinding =
            LayoutArtistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtistViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val artist = items[position]
        holder.bind(artist)
    }

    fun submitList(artists: List<Artist>) {
        items = artists
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ArtistViewHolder(private val itemBinding: LayoutArtistItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(artist: Artist) {
            itemBinding.textArtist.text = artist.name
            itemBinding.textListeners.text = artist.followers.toString()
            itemBinding.createPlaylist.setOnClickListener {
                getPlaylist(this, artist)
            }
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(artist.image)
                .into(itemBinding.imgArtist)
        }

        private fun getPlaylist(view: RecyclerView.ViewHolder, artist: Artist) {
            val accessToken = intent.getStringExtra("access_token")
            val id = intent.getStringExtra("id")
            val name = intent.getStringExtra("name")
            val intent = Intent(view.itemView.context, PlaylistPage::class.java).apply {
                putExtra("id", id)
                putExtra("name", name)
                putExtra("access_token", accessToken)
                putExtra("artistId", artist.id)
            }
            view.itemView.context.startActivity(intent)
        }
    }
}
