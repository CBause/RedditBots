package bigchris.studying.redditbots.utils

object Constants {

    val NAV_CRAWLER: String = "crawler"
    val NAV_REMINDER: String = "reminder"
    val NAV_USER: String = "user"

    val SNACKBAR_DELAY: Long = 500

    val REMINDER_DATABASE_CALL_DELAY = 60000

    val ACCOUNT_SUBTYPE_REMINDER = "reminder"
    val ACCOUNT_SUBTYPE_USER = "user"

    val USERNAME_KEY = "username"
    val PASSWORD_KEY = "password"

    val APP_ID_KEY = "appIdKey"
    val APP_SECRET_KEY = "appSecretKey"

    val REMINDING_JOB_DELAY_IN_MS: Long = 300000
    val REMINDING_TOO_LATE_TRESHOLD = 3600

    val WHEN_TO_REMIND_KEY = "when_to_remind"

    val COMMENT_STUMP = "Hey, "
    val COMMENT_REMIND = ", ich sollte dich hieran erinnern!"

    val COMMENT_FALSE_USAGE_1 = ", ich habe deine Bitte leider nicht verstanden. " +
            "Bitte erwähne mich beispielsweise wie folgt: /u/"
    val COMMENT_FALSE_USAGE_2 = " 2d5h. Es sind auch nur Stunden oder Tage möglich."

    val COMMENT_SORRY_FORGOT_1 = ", sorry, ich hab vergessen dich zu " +
            "benachrichtigen. Du wolltest vor ca. "
    val COMMENT_SORRY_FORGOT_2 = " von mir benachrichtig werden."

    val COMMENT_HOURS = "Stunden"
    val COMMENT_DAYS = "Tagen"
}