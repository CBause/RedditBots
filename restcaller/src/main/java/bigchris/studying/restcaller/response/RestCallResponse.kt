package bigchris.studying.restcaller.response

interface RestCallResponse {

    companion object {
        val KEY_TOKEN = "access_token"
        val KEY_EXPIRES_IN = "expires_in"
        val KEY_DATA = "data"
        val KEY_CHILDREN = "children"
        val KEY_AFTER = "after"
        val KEY_BEFORE = "before"
        val VALUE_BEFORE = "null"
        val KEY_TYPE = "type"
        val TYPE_VALUE = "username_mention"
        val KEY_BODY = "body"
        val KEY_KIND = "kind"
        val TYPE_KIND = "t1"
        val KEY_PARENT_ID = "parent_id"
        val KEY_NEW = "new"
        val KEY_NAME = "name"
        val KEY_CREATED_UTC = "created_utc"
        val KEY_SUBREDDIT = "subreddit"
        val KEY_AUTHOR_FULL = "author_fullname"
        val KEY_AUTHOR = "author"
        val KEY_ID = "id"
    }

    interface NewMessagesRestCallResponse : RestCallResponse {

        fun getReminderMessageData() : List<Map<String, String>>

        fun nextMessagesAfter() : String?

    }

    fun log()

    fun getStringValue(key: String): String

    fun getIntValue(key: String): Int

}