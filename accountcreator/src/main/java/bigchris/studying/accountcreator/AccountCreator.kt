package bigchris.studying.accountcreator

import android.content.Context

interface AccountCreator {

    fun createAccount(email: String, username: String, password: String, context: Context)

}