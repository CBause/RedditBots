package bigchris.studying.accountmanager

interface AccountManagerListener {

    fun onLoginSuccess(username: String, password: String)

    fun onAccountAdded()

    fun onAccountRemoved()

    fun onAccountAddError()

    fun onLoginError()

    fun onAccountRemoveError()

}