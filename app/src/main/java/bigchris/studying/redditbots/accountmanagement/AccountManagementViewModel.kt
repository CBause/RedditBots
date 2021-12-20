package bigchris.studying.redditbots.accountmanagement

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bigchris.studying.accountmanager.AccountManager
import bigchris.studying.accountmanager.AccountManagerFactory
import bigchris.studying.accountmanager.AccountManagerListener

class AccountManagementViewModel() : ViewModel(), AccountManagerListener  {
    private val TAG = "ACCOUNTMANAGEMENTVIEWMODEL"
    private lateinit var accountManager: AccountManager
    lateinit var listener: AccountManagementListener
    lateinit var internalAccountType: String
    private val loginSuccess: MutableLiveData<Boolean> = MutableLiveData(false)
    val loginSuccessLiveData: LiveData<Boolean> = loginSuccess
    private val loginError: MutableLiveData<Boolean> = MutableLiveData(false)
    val loginErrorLiveData: LiveData<Boolean> = loginError
    private val addAccountError: MutableLiveData<Boolean> = MutableLiveData(false)
    val addAccountErrorLiveData: LiveData<Boolean> = addAccountError
    private val addAccountSuccess: MutableLiveData<Boolean> = MutableLiveData(false)
    val addAccountSuccessLiveData: LiveData<Boolean> = addAccountSuccess
    private val removeAccountError: MutableLiveData<Boolean> = MutableLiveData(false)
    val removeAccountErrorLiveData: LiveData<Boolean> = removeAccountError
    private val removeAccountSuccess: MutableLiveData<Boolean> = MutableLiveData(false)
    val removeAccountSuccessLiveData: LiveData<Boolean> = removeAccountSuccess
    private val accountList: MutableLiveData<List<String>> = MutableLiveData(mutableListOf())


    override fun onLoginSuccess(username: String, password: String) {
        loginSuccess.value = true
        listener.onAccountManagementDone(username, password)
    }

    override fun onAccountAdded() {
        addAccountSuccess.value = true
    }

    override fun onAccountRemoved() {
        removeAccountSuccess.value = true
    }

    override fun onAccountAddError() {
        addAccountError.value = true
    }

    override fun onLoginError() {
        loginError.value = true
    }

    override fun onAccountRemoveError() {
        removeAccountError.value = true
    }

    fun createAccountManagerWithContext(context: Context) {
        accountManager = AccountManagerFactory.getAccountManager(context)
        accountManager.setAccountManagerListener(this)
    }

    fun onLogin(username: String, internalAccountType: String) {
        accountManager.login(username, internalAccountType)
    }

    fun onAddAccount(username: String, password: String, internalAccountType: String) {
        accountManager.addAccount(username, password, internalAccountType)
    }

    fun onRemoveAccount(username: String, internalAccountType: String) {
        accountManager.removeAccount(username, internalAccountType)
    }

    fun getAccounts() : List<String>? {
        accountManager.checkForMatchingAccounts(internalAccountType)?.let {
            val list = mutableListOf<String>()
            it.forEach {
                list.add(it.first)
            }
            return list
        }
        return null
    }

    fun resetAddAccountMessages() {
        addAccountError.value = false
        addAccountSuccess.value = false
    }

    fun resetRemoveAccountMessages() {
        removeAccountError.value = false
        removeAccountSuccess.value = false
    }

    fun resetLoginMessages() {
        loginError.value = false
        loginSuccess.value = false
    }

}