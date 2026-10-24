package com.kglabs28.yapnplay.appengine.core.network

/**
 * Where the guest account is kept on the device. Android: private app
 * storage (survives restarts; encrypted storage is a later security item).
 */
interface CredentialStore {
    fun load(): BackendCredentials?
    fun save(credentials: BackendCredentials)
    fun clear()
    /** The account picked for sign-up, kept until the server confirms it (null: none waiting). */
    fun loadPending(): BackendCredentials?
    /** Keeps [credentials] as the account waiting for sign-up; null forgets it. */
    fun savePending(credentials: BackendCredentials?)
    /** A brand-new account that hasn't seen the welcome (name and avatar) yet. */
    fun welcomeNeeded(): Boolean
    fun setWelcomeNeeded(needed: Boolean)
}

/** Desktop / previews: the account lives only while the process runs. */
class InMemoryCredentialStore : CredentialStore {
    private var credentials: BackendCredentials? = null
    private var pending: BackendCredentials? = null
    override fun load(): BackendCredentials? = credentials
    override fun loadPending(): BackendCredentials? = pending
    override fun savePending(credentials: BackendCredentials?) {
        pending = credentials
    }
    private var welcome = false
    override fun welcomeNeeded(): Boolean = welcome
    override fun setWelcomeNeeded(needed: Boolean) {
        welcome = needed
    }

    override fun save(credentials: BackendCredentials) {
        this.credentials = credentials
    }
    override fun clear() {
        credentials = null
    }
}
