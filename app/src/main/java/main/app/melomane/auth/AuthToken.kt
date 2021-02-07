package main.app.melomane.auth

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class AuthToken(
    val access_token: String,
    val refresh_token: String
) {
    class Deserializer : ResponseDeserializable<AuthToken> {
        override fun deserialize(content: String): AuthToken? = Gson().fromJson(content, AuthToken::class.java)
    }
}