package main.app.melomane

import android.os.Parcelable
import java.io.Serializable

data class Track(
        val id: String,
        val name: String,
        val artist: String,
        val image: String,
        val uri: String
) {
    public override fun toString(): String {
        val string = name + " - " + artist
        return(string)
    }
}