package bigchris.studying.restcaller

import android.content.Context
import android.util.Base64
import android.util.Log
import bigchris.studying.restcaller.request.JSONRestCallRequest
import bigchris.studying.restcaller.request.RestCallRequest
import bigchris.studying.restcaller.response.JSONRestCallResponse
import bigchris.studying.restcaller.response.RestCallResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.*
import org.json.JSONObject

internal class DefaultRestCaller(private val context: Context,
                                 private val authorizationObject: RestAuthorizationObject) : RestCaller {
    private val TAG = "DEFAULTRESTCALLER"
    private val requestQueue = Volley.newRequestQueue(context)
    private lateinit var authTokenRenewalJob: Job
    private lateinit var restCallListener: RestCallListener
    private var isAuthorized = false

    init {
        getAuthToken()
    }

    class CustomJSONRequest(method: Int,
                            url: String,
                            body: JSONObject?,
                            private val additionalHeaders: Map<String, String>?,
                            responseListener: Response.Listener<JSONObject>,
                            responseErrorListener: Response.ErrorListener?)
        : JsonObjectRequest(method, url, body, responseListener, responseErrorListener) {

        override fun getHeaders(): MutableMap<String, String> {
            val headers = mutableMapOf<String, String>()
            additionalHeaders?.forEach {
                headers[it.key] = it.value
            }
            super.getHeaders().forEach {
                headers[it.key] = it.value
            }
            return headers
        }
    }

    class CustomStringRequest(method: Int,
                              url: String,
                              private val body: String?,
                              private val additionalHeaders: Map<String, String>?,
                              responseListener: Response.Listener<String>,
                              responseErrorListener: Response.ErrorListener)
        : StringRequest(method, url, responseListener, responseErrorListener) {

        override fun getHeaders(): MutableMap<String, String> {
            val headers = mutableMapOf<String, String>()
            additionalHeaders?.forEach {
                headers[it.key] = it.value
            }
            super.getHeaders().forEach {
                headers[it.key] = it.value
            }
            return headers
        }

        override fun getBody(): ByteArray {
            if (body != null)
                return body.toByteArray()
            else
                return super.getBody()
        }
    }

    override fun registerListener(listener: RestCallListener) {
        restCallListener = listener
    }

    override fun stopAuthTokenCoroutine() {
        if (this::authTokenRenewalJob.isInitialized)
            authTokenRenewalJob.cancel()
    }

    override fun triggerJsonGetRequest(request: RestCallRequest) {
        (request as JSONRestCallRequest)
        val restRequest = CustomJSONRequest(Request.Method.GET,
                request.endPoint,
                request.body,
                getAuthorizedHeaders(), {
                    informListener(JSONRestCallResponse.create(it), request.type)
                }, getErrorHandler())
        requestQueue.add(restRequest)
    }

    override fun triggerJsonPostRequest(request: RestCallRequest) {
        (request as JSONRestCallRequest)
        val restRequest = CustomJSONRequest(
            Request.Method.POST,
            request.endPoint,
            request.body,
            getAuthorizedHeaders(),
            {Log.d(TAG, it.toString())},
            getErrorHandler())
        requestQueue.add(restRequest)
    }

    override fun triggerStringGetRequest(request: RestCallRequest) {
        (request as JSONRestCallRequest)

        val restRequest = CustomStringRequest(Request.Method.GET,
            request.endPoint,
            getStringBody(request.body),
            getAuthorizedHeaders(), {
                informListener(JSONRestCallResponse.create(it), request.type)
            }, getErrorHandler())
        requestQueue.add(restRequest)
    }

    override fun triggerStringPostRequest(request: RestCallRequest) {
        (request as JSONRestCallRequest)
        val restRequest = CustomStringRequest(
            Request.Method.POST,
            request.endPoint,
            getStringBody(request.body),
            getAuthorizedHeaders(),
            {Log.d(TAG, it.toString())},
            getErrorHandler())
        requestQueue.add(restRequest)
    }

    private fun getStringBody(body: JSONObject?) : String? {
        if (body == null)
            return null
        var result = ""
        body.keys().forEach {
            result = result.plus("&$it=${body[it]}")
        }
        return result
    }

    private fun getAuthToken() {
        val request = CustomStringRequest(Request.Method.POST,
                RestCaller.ACCESS_TOKEN_URL,
                getAuthStringBody(),
                getAuthHeaders(),
                getAuthResponseHandler(),
                getErrorHandler())
        requestQueue.add(request)
    }

    private fun informListener(response: JSONRestCallResponse, type: RestCallRequest.Type) {
        if (this::restCallListener.isInitialized) {
            when (type) {
                RestCallRequest.Type.NEW_MESSAGES -> restCallListener.onGetNewMessages(response)
                else -> restCallListener.onGetNewMessages(response)
            }
        }
    }

    private fun getAuthStringBody(): String = "grant_type=password&username=${authorizationObject.username}" +
            "&password=${authorizationObject.password}&${getScopeString()}"


    private fun getAuthString(): String {
        val auth = Base64.encodeToString(authorizationObject.appId.plus(":")
                .plus(authorizationObject.appSecret).toByteArray(), Base64.NO_WRAP)
        return "Basic ".plus(auth)
    }


    private fun getBasicHeaders(): MutableMap<String, String> {
        val result = mutableMapOf<String, String>()
        result[RestCaller.KEY_HEADER_USER_AGENT] = getUserAgentString().plus(" ")
                .plus(authorizationObject.username)
        return result
    }

    private fun getAuthorizedHeaders() : Map<String, String> = getBasicHeaders().apply {
        this[RestCaller.KEY_HEADER_AUTHORIZATION] = "bearer ${authorizationObject.authToken}"
    }

    private fun getAuthHeaders(): Map<String, String> = getBasicHeaders().apply {
        this[RestCaller.KEY_HEADER_AUTHORIZATION] = getAuthString()
    }

    private fun getAuthResponseHandler(): (response: String) -> Unit = {
        val authResponse: RestCallResponse = JSONRestCallResponse.create(it)
        authorizationObject.setAuthToken(authResponse.getStringValue(RestCallResponse.KEY_TOKEN))
        authTokenRenewalJob = startAuthTokenTimerFunction(getRenewalDelayInMilliseconds(
                authResponse.getIntValue(RestCallResponse.KEY_EXPIRES_IN)))
        isAuthorized = true
        if (this::restCallListener.isInitialized)
            restCallListener.onIsAuthorized()
        Log.d(TAG, "New access token: $it")
    }

    private fun getErrorHandler(): (errorResponse: VolleyError) -> Unit = {
        if (it.message != null) {
            Log.e(TAG, it.message!!)
        } else {
            Log.e(TAG, it.toString())
        }
        it.stackTrace.forEach {
            Log.e(TAG, it.toString())
        }
    }

    private fun getScopeString(): String = when (authorizationObject.scopeType) {
        RestCaller.ScopeType.Reminder -> RestCaller.REMINDER_SCOPE_STRING
        RestCaller.ScopeType.User -> RestCaller.USER_SCOPE_STRING
    }

    private fun getRenewalDelayInMilliseconds(renewalTimeInSeconds: Int) :
            Long = ((renewalTimeInSeconds - 300) * 1000).toLong()


    private fun getUserAgentString(): String {
        val userAgent = when (authorizationObject.scopeType) {
            RestCaller.ScopeType.Reminder -> RestCaller.USER_AGENT_REMINDER
            RestCaller.ScopeType.User -> RestCaller.USER_AGENT_USER
        }
        return userAgent.plus(authorizationObject.username)
    }

    private fun startAuthTokenTimerFunction(renewalDelay: Long) = CoroutineScope(Dispatchers.IO).launch {
        delay(renewalDelay)
        getAuthToken()
    }
}