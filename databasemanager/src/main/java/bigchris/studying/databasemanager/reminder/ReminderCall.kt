package bigchris.studying.databasemanager.reminder

data class ReminderCall(val id: Int,
                        val author_full: String,
                        val name: String,
                        val parentId: String,
                        val subreddit: String,
                        val creationTime: String,
                        val remindTime: String,
                        val author: String) {

}