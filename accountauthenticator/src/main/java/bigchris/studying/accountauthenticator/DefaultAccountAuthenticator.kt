package bigchris.studying.accountauthenticator

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.content.Context
import android.os.Bundle
import android.os.IBinder
import bigchris.studying.accountauthenticator.util.Constants

internal class DefaultAccountAuthenticator(val context: Context) : AbstractAccountAuthenticator(context), AccountAuthenticator {

    override fun editProperties(
        response: AccountAuthenticatorResponse?,
        accountType: String?
    ): Bundle {
        throw UnsupportedOperationException()
    }

    override fun addAccount(
        response: AccountAuthenticatorResponse?,
        accountType: String?,
        authTokenType: String?,
        requiredFeatures: Array<out String>?,
        options: Bundle?
    ): Bundle {
        throw UnsupportedOperationException()
    }

    override fun confirmCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        options: Bundle?
    ): Bundle {
        throw UnsupportedOperationException()
    }

    override fun getAuthToken(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        throw UnsupportedOperationException()
    }

    override fun getAuthTokenLabel(authTokenType: String?): String {
        throw UnsupportedOperationException()
    }

    override fun updateCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        throw UnsupportedOperationException()
    }

    override fun hasFeatures(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        features: Array<out String>?
    ): Bundle {
        throw UnsupportedOperationException()
    }

    override fun getBinder(): IBinder = iBinder

    override fun getAccountType(): String = Constants.ACCOUNT_TYPE

}