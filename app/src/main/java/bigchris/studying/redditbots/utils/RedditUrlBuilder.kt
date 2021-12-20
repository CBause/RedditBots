package bigchris.studying.redditbots.utils

import java.util.*

object RedditUrlBuilder {
    private var state: String = ""
    private var clientId: String = ""
    private var responseType: String = "code"
    private var redirectUri: String = ""
    private var duration: String = ""
    private var scopes: List<String>? = null

    enum class DURATION {
        TEMPORARY,
        PERMANENT,
    }

    fun buildUrl(
        clientId: String,
        responseType: String = "code",
        redirectUri: String,
        duration: DURATION = DURATION.TEMPORARY,
        scopes: List<String>?) : String? {

        this.clientId = clientId
        this.responseType = responseType
        this.duration = duration.name.lowercase()
        this.redirectUri = redirectUri
        this.state = UUID.randomUUID().toString()
        this.scopes = scopes
        return buildUrl()
    }

    fun buildUrl() : String? {
        if (clientId.isNotEmpty() && responseType.isNotEmpty() && duration.isNotEmpty() && redirectUri.isNotEmpty() && state.isNotEmpty()) {
            var scopeString = buildScopeString()
            var url = "https://www.reddit.com/api/v1/authorize?client_id=${clientId}&response_type=${responseType}&" +
                    "state=${state}&redirect_uri=${redirectUri}&duration=${duration}"
            if (scopeString.isNotEmpty())
                url.plus("&scope=${scopeString}")
            return url
        }
        return null
    }

    private fun buildScopeString() : String {
        var result = ""
        scopes?.forEachIndexed() {index, string ->
            result.plus(string)
            if (index < scopes!!.size - 1)
                result.plus("_")
        }
        return result
    }

    fun checkReturnedState(state: String) : Boolean = state.equals(this.state)
}