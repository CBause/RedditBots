package bigchris.studying.restcaller

import android.util.Log
import bigchris.studying.restcaller.RestCaller

internal data class RestAuthorizationObject(val username: String,
                                   val password: String,
                                   val appId: String,
                                   val appSecret: String,
                                   val scopeType: RestCaller.ScopeType) {
    val TAG = "RESTAUTHORIZATIONOBJECT"
    lateinit var authToken: String
        private set

    fun log() {
        Log.d(TAG, "Username: $username, password: $password, appId: $appId, appSecret: $appSecret")
    }

    fun setAuthToken(token: String) {
        authToken = token
    }
}