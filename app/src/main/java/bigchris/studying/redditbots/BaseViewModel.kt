package bigchris.studying.redditbots

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bigchris.studying.redditbots.utils.Constants

class BaseViewModel : ViewModel() {
    private val TAG = "BASEVIEWMODEL"
    private val snackBarTextList = mutableListOf<String>()
    private val paused = MutableLiveData(true)
    val pausedLiveData: LiveData<Boolean> = paused
    private lateinit var preferences: SharedPreferences

    fun submitSnackBarText(text: String) {
        snackBarTextList.add(text)
    }

    fun isSnackBarTextListEmpty() = snackBarTextList.isEmpty()

    fun getSnackBarString() : String {
        if (snackBarTextList.isNotEmpty()) {
            val result = snackBarTextList[0]
            snackBarTextList.removeAt(0)
            return result
        }
        return ""
    }

    fun setOnPause(value: Boolean) {
        paused.value = value
    }

    fun setPreferences(preferences: SharedPreferences) {
        this.preferences = preferences
    }

    private fun writePreferences(key: String, value: Any) {
        preferences.let {
            when (value) {
                is Int -> it.edit().putInt(key, value).apply()
                is String -> it.edit().putString(key, value).apply()
                is Boolean -> it.edit().putBoolean(key, value).apply()
                is Float -> it.edit().putFloat(key, value).apply()
            }
        }
    }

    private fun getStringFromPreferences(key: String) : String
        = preferences.getString(key, "") ?: ""

    fun saveAppIdAndSecret(username: String, id: String, secret: String) {
        writePreferences(getAppIdKey(username), id)
        writePreferences(getAppSecretKey(username), secret)
    }

    fun getAppIdAndSecret(username: String) : Pair<String,String>? {
        val id = getStringFromPreferences(getAppIdKey(username))
        val secret = getStringFromPreferences(getAppSecretKey(username))
        if (id.isNotEmpty() && secret.isNotEmpty()) {
            return Pair(id, secret)
        }
        return null
    }

    private fun getAppIdKey(username: String) : String
        = Constants.APP_ID_KEY + "_" + username

    private fun getAppSecretKey(username: String) : String
        = Constants.APP_SECRET_KEY + "_" + username

}