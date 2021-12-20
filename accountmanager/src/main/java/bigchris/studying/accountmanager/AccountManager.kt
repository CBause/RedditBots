package bigchris.studying.accountmanager

interface AccountManager {

    fun addAccount(username: String, password: String, internalAccountType: String)

    fun checkForMatchingAccounts(internalAccountType: String) : List<Pair<String, String>>?

    fun login(username: String, internalAccountType: String)

    fun removeAccount(username: String, internalAccountType: String)

    fun setAccountManagerListener(listener: AccountManagerListener)

}