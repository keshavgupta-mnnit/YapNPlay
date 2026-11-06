package com.kglabs28.yapnplay.appengine.core.auth

/** Google sign-in on the web: off to Google's page, back with an ID token. */
interface GoogleSignInLauncher {
    /** Sends the player to Google's sign-in page; they come back to the app with a token. */
    fun start()

    /** The ID token Google came back with on this page load, once (null: none). */
    fun takeReturnedIdToken(): String?

    /** The first name in that token, to suggest on the welcome (null: none). */
    fun returnedFirstName(): String?
}

/** The app and desktop: Play Games instead, never Google's page. */
class NoGoogleSignIn : GoogleSignInLauncher {
    override fun start() = Unit
    override fun takeReturnedIdToken(): String? = null
    override fun returnedFirstName(): String? = null
}
