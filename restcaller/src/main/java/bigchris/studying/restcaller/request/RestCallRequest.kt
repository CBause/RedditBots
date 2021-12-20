package bigchris.studying.restcaller.request

interface  RestCallRequest {

    enum class Type {NEW_MESSAGES,MESSAGES,MARK_READ,COMMENT,MARK_UNREAD}

    companion object {
        val API_ENDPOINT_GET_MESSAGES = "/message/inbox"
        val API_ENDPOINT_GET_NEW_MESSAGES = "/message/unread"
        val API_ENDPOINT_MARK_MESSAGE_READ = "/api/read_message"
        val API_ENDPOINT_COMMENT = "/api/comment"
        val API_ENDPOINT_MARK_MESSAGE_UNREAD = "/api/unread_message"
        val OAUTH_REST_STUMP = "https://oauth.reddit.com"
        val KEY_MARK = "mark"
        val KEY_API_TYPE = "api_type"
        val VALUE_API_TYPE_JSON = "json"
        val KEY_THING_ID = "thing_id"
        val KEY_TEXT = "text"

        fun create(endPoint: String) : RestCallRequest
            = JSONRestCallRequest(endPoint)
    }

    fun addToBody(key: String, value: String)

}