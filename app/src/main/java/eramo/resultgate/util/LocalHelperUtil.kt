package eramo.resultgate.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.Locale

object LocalHelperUtil {
    private lateinit var sharedPreferences: SharedPreferences
    private const val LANGUAGE = "language"

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("local", Context.MODE_PRIVATE)
    }

    fun setLocal(activity: Activity, language: String) {
        val configuration = Configuration()
        val locale = Locale(language)
        Locale.setDefault(locale)
        configuration.locale = locale
        configuration.setLayoutDirection(locale)
        activity.resources.updateConfiguration(configuration, activity.resources.displayMetrics)
        sharedPreferences.edit().putString(LANGUAGE, language).apply()
    }

    fun loadLocal(activity: Activity) =
        setLocal(activity, sharedPreferences.getString(LANGUAGE, "ar")!!)

    fun getLang() = sharedPreferences.getString(LANGUAGE, "en")

    fun isEnglish() = sharedPreferences.getString(LANGUAGE, "en").equals("en")
}