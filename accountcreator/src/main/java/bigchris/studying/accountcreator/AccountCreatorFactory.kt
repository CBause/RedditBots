package bigchris.studying.accountcreator

object AccountCreatorFactory {
    private var accountCreator: AccountCreator? = null

    fun getAccountCreator() : AccountCreator {
        if (accountCreator == null)
            accountCreator = DefaultAccountCreator()
        return accountCreator!!
    }
}