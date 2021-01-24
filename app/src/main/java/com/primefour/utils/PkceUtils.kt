package com.primefour.utils

import android.util.Base64.*
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom


object PkceUtils {

    @Throws(UnsupportedEncodingException::class)
    fun generateCodeVerifier(): String {
        val secureRandom = SecureRandom()
        val codeVerifier = ByteArray(32)
        secureRandom.nextBytes(codeVerifier)
        return encodeToString(codeVerifier, URL_SAFE + NO_PADDING)
    }

    @Throws(UnsupportedEncodingException::class, NoSuchAlgorithmException::class)
    fun generateCodeChallenge(codeVerifier: String): String {
        val bytes: ByteArray = codeVerifier.toByteArray(Charsets.US_ASCII)
        val messageDigest: MessageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(bytes, 0, bytes.size)
        val digest: ByteArray = messageDigest.digest()
        return encodeToString(digest, URL_SAFE with NO_PADDING)
    }

    fun getState(): String {
        return java.util.UUID.randomUUID().toString()
    }
}