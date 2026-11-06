package com.kglabs28.yapnplay.appengine.core.auth

import com.kglabs28.yapnplay.appengine.core.common.BrowserStorage
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Google sign-in by a full-page redirect (no popups, which iPhone Safari
 * blocks): the page goes to Google and comes back to the same address
 * with `#id_token=...`. The token is taken from the address at once and
 * the address cleaned, so it never stays in the bar or the history. A
 * random nonce (kept for this tab only) proves the token answers our own
 * request. The web OAuth client id is the page's
 * `<meta name="yapnplay-google-client-id">`.
 */
@OptIn(ExperimentalUuidApi::class)
class GoogleRedirectSignIn : GoogleSignInLauncher {
    /** A token Google came back with on this page load, and its claims. */
    private class Returned(val idToken: String, val claims: JsonObject)

    private val returned: Returned? = readReturned()
    private var unused: String? = returned?.idToken

    override fun start() {
        val clientId = document.querySelector(CLIENT_ID_META)?.getAttribute(CONTENT).orEmpty()
        val nonce = Uuid.random().toHexString()
        BrowserStorage.session.set(NONCE_KEY, nonce)
        val params = listOf(
            "client_id" to clientId,
            "redirect_uri" to pageAddress(),
            "response_type" to "id_token",
            "scope" to "openid profile",
            "nonce" to nonce,
            "prompt" to "select_account",
        ).joinToString("&") { (k, v) -> k + "=" + encode(v) }
        window.location.href = AUTH_URL + params
    }

    override fun takeReturnedIdToken(): String? = unused.also { unused = null }

    override fun returnedFirstName(): String? = returned?.claims?.get(GIVEN_NAME)?.jsonPrimitive?.contentOrNull

    /** The token in this page's address (from Google), if it answers our request; the address is cleaned either way. */
    private fun readReturned(): Returned? {
        val hash = window.location.hash.removePrefix("#")
        if (hash.isEmpty()) return null
        val fields = hash.split("&").associate { it.substringBefore("=") to decode(it.substringAfter("=", "")) }
        val token = fields[ID_TOKEN] ?: return null
        window.history.replaceState(null, "", pageAddress())
        val claims = claimsOf(token) ?: return null
        val expected = BrowserStorage.session.get(NONCE_KEY)
        BrowserStorage.session.set(NONCE_KEY, null)
        if (expected == null || claims[NONCE]?.jsonPrimitive?.contentOrNull != expected) return null
        return Returned(token, claims)
    }

    /** The token's middle part (its claims). The server checks the signature; this only reads the nonce and name. */
    @OptIn(ExperimentalEncodingApi::class)
    private fun claimsOf(token: String): JsonObject? = runCatching {
        val payload = token.split(".")[1]
        val bytes = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL).decode(payload)
        Json.parseToJsonElement(bytes.decodeToString()) as JsonObject
    }.getOrNull()

    private fun pageAddress(): String = window.location.origin + window.location.pathname

    private companion object {
        const val AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth?"
        const val CLIENT_ID_META = "meta[name=yapnplay-google-client-id]"
        const val CONTENT = "content"
        const val NONCE_KEY = "yapnplay.nonce"
        const val ID_TOKEN = "id_token"
        const val NONCE = "nonce"
        const val GIVEN_NAME = "given_name"
    }
}

private fun encode(value: String): String = js("encodeURIComponent(value)")

private fun decode(value: String): String = js("decodeURIComponent(value)")
