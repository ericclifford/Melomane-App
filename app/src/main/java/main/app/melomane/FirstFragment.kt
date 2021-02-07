package main.app.melomane

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.google.android.material.snackbar.Snackbar
import main.app.melomane.auth.AuthToken
import main.app.melomane.auth.Register
import main.app.melomane.R
import main.app.utils.with
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationResponse


class FirstFragment : Fragment() {
    private val _authCodeRequestCode = 0x11

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.AuthorizeButton).setOnClickListener {
            authorizeSpotify()
        }

        view.findViewById<Button>(R.id.ViewProfile).setOnClickListener {
            getUserProfile()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == _authCodeRequestCode) {
            val response = AuthorizationClient.getResponse(resultCode, data)
            when (response.type) {
                AuthorizationResponse.Type.CODE -> {
                    exchangeAccessCodeForAuthToken(response.code)
                }
                AuthorizationResponse.Type.ERROR -> {
                    val snacks = Snackbar.make(requireActivity().findViewById(R.id.activity_main),
                        "Error: $response.error", Snackbar.LENGTH_SHORT)
                    snacks.show()
                }
                else -> {
                    val snacks = Snackbar.make(requireActivity().findViewById(R.id.activity_main),
                        "Unexpected response type: $response.type", Snackbar.LENGTH_SHORT)
                    snacks.show()
                }
            }
        }
    }

    private fun exchangeAccessCodeForAuthToken(accessCode: String) {
        val clientId = getString(R.string.melomane_client_id)
        val clientSecret = getString(R.string.melomane_client_secret)

        val requestHeader = "$clientId:$clientSecret"
        val base64EncodedHeader = encodeToString(requestHeader.toByteArray(),
                                          URL_SAFE with NO_PADDING with NO_WRAP)

        val requestParams = listOf("grant_type" to "authorization_code",
                                   "code" to accessCode,
                                   "redirect_uri" to getString(R.string.redirect_uri))

        getString(R.string.spotify_auth_token_uri)
            .httpPost(requestParams)
            .header("Authorization" to "Basic $base64EncodedHeader")
            .responseObject(AuthToken.Deserializer()) { _, response, result ->
                val error = result.component2()
                if (error == null) {
                    saveToken(result.component1()!!)
                } else {
                    val snacks = Snackbar.make(requireActivity().findViewById(R.id.activity_main),
                        "Something went wrong: $response.responseMessage", Snackbar.LENGTH_SHORT)
                    snacks.show()
                }
            }
    }

    private fun saveToken(token: AuthToken) {
        val sharedPrefs = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPrefs.edit()) {
            putString("accessToken", token.access_token)
            putString("refreshToken", token.refresh_token)
            apply()
        }
    }

    private fun authorizeSpotify() {
        val request = getAuthenticationRequest()
        val intent = AuthorizationClient.createLoginActivityIntent(activity, request)
        startActivityForResult(intent, _authCodeRequestCode)
    }

    private fun getUserProfile() {
        val prefs  = activity?.getPreferences(Context.MODE_PRIVATE) ///(getString("accessToken"), Context.MODE_PRIVATE))
        val accessToken = prefs?.getString("accessToken", null)
        getString(R.string.spotify_profile_uri)
            .httpGet()
            .header("Authorization" to "Bearer $accessToken")
            .response { _, response, _ ->
                val snacks = Snackbar.make(requireActivity().findViewById(R.id.activity_main),
                    "$response.responseMessage", Snackbar.LENGTH_SHORT)
                snacks.show()
            }
    }

    private fun getAuthenticationRequest(): AuthorizationRequest {
        val clientId = getString(R.string.melomane_client_id)
        return AuthorizationRequest.Builder(clientId, AuthorizationResponse.Type.CODE, stringifyRedirectUri())
            .setShowDialog(false)
            .setState(getState())
            .setScopes(arrayOf("playlist-modify-public"))
            .build()
    }

    private fun stringifyRedirectUri(): String {
        return Uri.parse(getString(R.string.redirect_uri)).toString()
    }

    private fun getState(): String {
        return java.util.UUID.randomUUID().toString()
    }
}
