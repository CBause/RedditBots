package bigchris.studying.accountauthenticator

import android.content.Context

object AccountAuthenticatorFactory {

    fun getAccountAuthenticator(context: Context) : AccountAuthenticator = DefaultAccountAuthenticator(context)

}