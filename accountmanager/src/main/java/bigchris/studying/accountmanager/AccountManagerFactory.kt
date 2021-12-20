package bigchris.studying.accountmanager

import android.content.Context

class AccountManagerFactory {

    companion object {

        fun getAccountManager(context: Context) : AccountManager = DefaultAccountManager(context)

    }
}