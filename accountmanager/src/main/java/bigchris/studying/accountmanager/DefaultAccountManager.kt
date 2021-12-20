package bigchris.studying.accountmanager

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import bigchris.studying.accountauthenticator.AccountAuthenticator
import bigchris.studying.accountauthenticator.AccountAuthenticatorFactory

internal class DefaultAccountManager(val context: Context) :
    bigchris.studying.accountmanager.AccountManager {
    private val TAG: String = "DEFAULTREDDITACCOUNTMANAGER"
    private val manager = AccountManager.get(context)
    private val authenticator: AccountAuthenticator by lazy {AccountAuthenticatorFactory.getAccountAuthenticator(context)}
    private val bundleInternalAccountTypeKey: String = "internalAccountType"
    private lateinit var managerListener: AccountManagerListener

    private fun logExistingAccounts() {
        manager.accounts.forEach {
            Log.e(TAG, it.name.plus(" ").plus(it.type).plus(" ").plus(manager.getPassword(it)))
        }
    }

    override fun addAccount(username: String, password: String, internalAccountType: String) {
        val account = Account(username, authenticator.getAccountType())
        var error = true
        if (!checkIfAccountExists(account)) {
            val userData = Bundle()
            userData.putString(bundleInternalAccountTypeKey, internalAccountType)
            if (manager.addAccountExplicitly(account, password, userData)) {
                managerListener.onAccountAdded()
                error = false
            }
        }
        if (error)
            managerListener.onAccountAddError()
    }

    override fun login(username: String, internalAccountType: String) {
        getAccountWithPassword(username, internalAccountType).also {
            if (it != null && it.first == username)
                managerListener.onLoginSuccess(username, it.second)
            else
                managerListener.onLoginError()
        }
    }

    override fun checkForMatchingAccounts(internalAccountType: String): List<Pair<String, String>>? {
        val result = mutableListOf<Pair<String, String>>()
        manager.getAccountsByType(authenticator.getAccountType()).asList().forEach {
            if (getFetchedInternalAccountType(it) == internalAccountType) {
                result.add(Pair(it.name, manager.getPassword(it)))
            }
        }
        if (result.isNotEmpty())
            return result
        return null
    }

    override fun removeAccount(username: String, internalAccountType: String) {
        manager.getAccountsByType(authenticator.getAccountType()).asList().forEach {
            if (it.name == username && getFetchedInternalAccountType(it) == internalAccountType) {
                if (manager.removeAccountExplicitly(it))
                    managerListener.onAccountRemoved()
                else
                    managerListener.onAccountRemoveError()
            }
        }
    }

    override fun setAccountManagerListener(listener: AccountManagerListener) {
        managerListener = listener
    }

    private fun checkIfAccountExists(account: Account) : Boolean {
        manager.getAccountsByType(authenticator.getAccountType()).forEach {
            if (it.equals(account)) {
                return true
            }
        }
        return false
    }

    private fun getFetchedInternalAccountType(account: Account) : String? =
        manager.getUserData(account, bundleInternalAccountTypeKey)

    private fun getAccountWithPassword(username: String, internalAccountType: String) : Pair<String, String>? {
        checkForMatchingAccounts(internalAccountType)?.forEach {
            if (it.first == username)
                return it
        }
        return null
    }

}