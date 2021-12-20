package bigchris.studying.accountauthenticator

import android.os.IBinder

interface AccountAuthenticator {

    fun getAccountType() : String

    fun getBinder() : IBinder

}