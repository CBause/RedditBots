package bigchris.studying.restcaller

import android.content.Context

object RestCallerFactory {
    private val TAG = "RESTCALLERFACTORY"

    fun getRestCaller(context: Context,
                      username: String,
                      password: String,
                      appId: String,
                      appSecret: String,
                      scopeType: RestCaller.ScopeType) : RestCaller {
        val authObject = RestAuthorizationObject(username, password, appId, appSecret, scopeType)
        return DefaultRestCaller(context, authObject)
    }

}