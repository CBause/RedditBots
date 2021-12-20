package bigchris.studying.redditbots.accountmanagement

interface AccountManagementListener {

    fun onAccountManagementDone(username: String, password: String)

}