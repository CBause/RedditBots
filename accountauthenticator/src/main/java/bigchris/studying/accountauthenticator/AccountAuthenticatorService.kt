package bigchris.studying.accountauthenticator

import android.app.Service
import android.content.Intent
import android.os.IBinder

internal class AccountAuthenticatorService : Service() {
    lateinit var accountAuthenticator: AccountAuthenticator

    override fun onCreate() {
        accountAuthenticator = AccountAuthenticatorFactory.getAccountAuthenticator(this)
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? = accountAuthenticator.getBinder()

}