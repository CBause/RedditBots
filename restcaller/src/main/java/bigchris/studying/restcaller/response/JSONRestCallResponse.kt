package bigchris.studying.restcaller.response

import android.util.Log
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import org.json.JSONObject
import java.net.ResponseCache

internal class JSONRestCallResponse private constructor() :
                    RestCallResponse.NewMessagesRestCallResponse,
                    RestCallResponse {

    private val TAG = "JSONRestCallResponse"
    private lateinit var responseObject: JSONObject

    companion object {
        fun create(responseObject: JSONObject) = JSONRestCallResponse().apply {
            this.responseObject = responseObject
        }

        fun create(responseString: String) = create(JSONObject(responseString))
    }

    override fun log() {
        Logger.addLogAdapter(AndroidLogAdapter())
        Logger.json(responseObject.toString())
    }

    override fun getStringValue(key: String): String
        = responseObject.getString(key)

    override fun getIntValue(key: String): Int
        = responseObject.getInt(key)

    override fun getReminderMessageData(): List<Map<String, String>> {
        val result = mutableListOf<Map<String, String>>()
        try {
            val messageArray = responseObject.getJSONObject(RestCallResponse.KEY_DATA)
                .getJSONArray(RestCallResponse.KEY_CHILDREN)
            for (index in 0 until messageArray.length()) {
                val currentMessage = messageArray.getJSONObject(index)
                val keyKind = RestCallResponse.KEY_KIND
                val typeKind = RestCallResponse.TYPE_KIND
                val keyUsernameMentioned = RestCallResponse.KEY_TYPE
                val typeUsernameMentioned = RestCallResponse.TYPE_VALUE
                if (currentMessage.getString(keyKind) == typeKind
                        && currentMessage.getJSONObject(RestCallResponse.KEY_DATA)
                        .getString(keyUsernameMentioned) == typeUsernameMentioned) {
                    result.add(getReminderMessageDataMap(messageArray.getJSONObject(index)))
                }
            }
        } catch (error: Error) {
            Log.e(TAG, error.toString())
        }
        return result
    }

    override fun nextMessagesAfter(): String? {
        try {
            val after = responseObject.getJSONObject(RestCallResponse.KEY_DATA)
                .getString(RestCallResponse.KEY_BEFORE)
            if (after != "null")
                return after
        } catch(error: Error) {
            Log.e(TAG, error.toString())
        }
        return null
    }

    private fun getReminderMessageDataMap(messageObject: JSONObject) : Map<String, String> {
        val result = mutableMapOf<String, String>()
        try {
            val messageData: JSONObject = messageObject.getJSONObject(RestCallResponse.KEY_DATA)
            result[RestCallResponse.KEY_CREATED_UTC] = messageData.getLong(RestCallResponse.KEY_CREATED_UTC).toString()
            result[RestCallResponse.KEY_KIND] = messageObject.getString(RestCallResponse.KEY_KIND)
            getReminderMessageDataMapPair(messageData,
                RestCallResponse.KEY_SUBREDDIT)?.let{result[it.first] = it.second}
            getReminderMessageDataMapPair(messageData,
                RestCallResponse.KEY_AUTHOR)?.let{result[it.first] = it.second}
            getReminderMessageDataMapPair(messageData,
                RestCallResponse.KEY_NAME)?.let{result[it.first] = it.second}
            getReminderMessageDataMapPair(messageData,
                RestCallResponse.KEY_PARENT_ID)?.let{result[it.first] = it.second}
            getReminderMessageDataMapPair(messageData,
                RestCallResponse.KEY_BODY)?.let{result[it.first] = it.second}
            getReminderMessageDataMapPair(messageData,
                RestCallResponse.KEY_AUTHOR_FULL)?.let{result[it.first] = it.second}
            getReminderMessageDataMapPair(messageData,
                RestCallResponse.KEY_TYPE)?.let{result[it.first] = it.second}
        } catch (error: Error) {
            Log.e(TAG, error.toString())
        }
        return result
    }

    private fun getReminderMessageDataMapPair(messageObject: JSONObject, key: String) :
            Pair<String, String>? {
        try {
            return Pair(key, messageObject.getString(key))
        } catch(error: Error) {
            Log.e(TAG, error.toString())
        }
        return null
    }
}