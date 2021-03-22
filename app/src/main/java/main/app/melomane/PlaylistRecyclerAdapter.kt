package main.app.melomane

import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import main.app.melomane.databinding.LayoutPlaylistItemBinding

class PlaylistRecyclerAdapter : RecyclerView.Adapter<PlaylistRecyclerAdapter.PlaylistViewHolder>() {
    private var items: List<Track> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val itemBinding = LayoutPlaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlistBean = items[position]
        holder.bind(playlistBean)
    }

    fun submitList(playlist: List<Track>){
        items = playlist
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class PlaylistViewHolder (private val itemBinding: LayoutPlaylistItemBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(track: Track){
            itemBinding.textArtist.text = track.artist
            itemBinding.textSong.text = track.name

//            val requestOptions = RequestOptions()
//                    .placeholder(R.drawable.ic_launcher_background)
//                    .error(R.drawable.ic_launcher_background)

//            Glide.with(itemView.context)
//                .applyDefaultRequestOptions(requestOptions)
//                .load(track.image)
//                .into(itemBinding.imgAlbum)
        }
    }
}