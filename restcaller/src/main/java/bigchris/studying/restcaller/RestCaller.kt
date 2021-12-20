package bigchris.studying.restcaller

import bigchris.studying.restcaller.request.RestCallRequest

interface RestCaller {

    enum class ScopeType {Reminder, User}

    companion object {
        val ACCESS_TOKEN_URL = "https://www.reddit.com/api/v1/access_token"
        val USER_AGENT_REMINDER = "GermanRemindMeBot/1.0 by"
        val USER_AGENT_USER = "Android UserApiTest/1.0 by"
        val REMINDER_SCOPE_STRING = "scope=privatemessages%20submit"
        val USER_SCOPE_STRING = "scope="
        val KEY_HEADER_AUTHORIZATION = "Authorization"
        val KEY_HEADER_USER_AGENT = "User-Agent"
    }

    fun stopAuthTokenCoroutine()

    fun triggerJsonPostRequest(request: RestCallRequest)

    fun triggerJsonGetRequest(request: RestCallRequest)

    fun triggerStringPostRequest(request: RestCallRequest)

    fun triggerStringGetRequest(request: RestCallRequest)

    fun registerListener(listener: RestCallListener)

}