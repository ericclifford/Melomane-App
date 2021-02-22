package main.app.melomane

import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.layout_playlist_item.view.*

class PlaylistRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Track> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PlaylistViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.layout_playlist_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PlaylistViewHolder ->{
                holder.bind(items.get(position))
            }
        }
    }

    fun submitList(playlist: List<Track>){
        items = playlist
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class PlaylistViewHolder constructor(
            itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val name = itemView.text_song
        val artist = itemView.text_artist
        val image = itemView.img_album

        fun bind(track: Track){
            name.setText(track.name)
            artist.setText(track.artist)

            val requestOptions = RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(track.image)
                .into(image)
        }
    }
}