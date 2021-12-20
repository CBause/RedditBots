package bigchris.studying.restcaller.request

import org.json.JSONObject

internal class JSONRestCallRequest(requestEndPoint: String) : RestCallRequest {
    val type: RestCallRequest.Type = getRequestType(requestEndPoint)
    val endPoint = getCompleteOAuthEndpoint(requestEndPoint)

    var body: JSONObject? = null
        private set

    override fun addToBody(key: String, value: String) {
        if (body == null)
            body = JSONObject()
        body!!.put(key, value)
    }

    private fun getCompleteOAuthEndpoint(endPoint: String) : String
        = RestCallRequest.OAUTH_REST_STUMP.plus(endPoint)

    private fun getRequestType(endPoint: String) : RestCallRequest.Type
        = when (endPoint) {
            RestCallRequest.API_ENDPOINT_GET_NEW_MESSAGES -> RestCallRequest.Type.NEW_MESSAGES
            RestCallRequest.API_ENDPOINT_COMMENT -> RestCallRequest.Type.COMMENT
            RestCallRequest.API_ENDPOINT_MARK_MESSAGE_READ -> RestCallRequest.Type.MARK_READ
            RestCallRequest.API_ENDPOINT_MARK_MESSAGE_UNREAD -> RestCallRequest.Type.MARK_UNREAD
            else -> RestCallRequest.Type.MESSAGES
        }

}