package main.app.melomane.auth

import com.github.kittinunf.fuel.httpGet
import main.app.melomane.R
import main.app.utils.PkceUtils
import main.app.utils.Strings

object SpotifyAuth {

    fun AuthCodeFlowWithPkce(): String {
        val codeVerifier = PkceUtils.generateCodeVerifier()
        val codeChallenge = PkceUtils.generateCodeChallenge(codeVerifier)
        val state = PkceUtils.getState()

        // Scopes and explanations here: https://developer.spotify.com/documentation/general/guides/scopes/
        val scopes = arrayOf("playlist-modify-public",
                            "user-library-read",
                            "user-library-modify")
            .joinToString()

        val params = listOf("client_id" to Strings.get(R.string.melomane_client_id),
                            "response_type" to "code",
                            "redirect_uri" to Strings.get(R.string.redirect_uri),
                            "code_challenge_method" to "S256",
                            "code_challenge" to codeChallenge,
                            "state" to state,
                            "scope" to scopes)
        Strings.get(R.string.spotify_auth_uri).httpGet(params)
        return ""
    }
}