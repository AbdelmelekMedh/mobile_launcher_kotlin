package com.hellodati.launcher.ui.helper

import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import java.util.Locale

object LocalHelper {
    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
    fun onAttach(context: Context): Context {
        val lang = getPersistedData(context, Locale.getDefault().language)
        return setLocale(context, lang)
    }

    fun onAttach(context: Context, defaultLanguage: String): Context {
        val lang = getPersistedData(context, defaultLanguage)
        return setLocale(context, lang)
    }

    fun getLanguage(context: Context): String? {
       // return getPersistedData(context, Locale.getDefault().language)
        val sharedPref = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val defaultLanguage = sharedPref.getString("language", Locale.getDefault().toString())
        return defaultLanguage
    }

    fun setLocale(context: Context, language: String?): Context {
        persist(context, language)
        return updateResources(context, language)
    }

    private fun getPersistedData(context: Context, defaultLanguage: String): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage)
    }

    private fun persist(context: Context, language: String?) {
     /*   val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString(SELECTED_LANGUAGE, language)
        editor.apply()*/
        val sharedPref = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("language", language.toString())
        Log.e("language --- saved",language.toString())
        editor.apply()
    }

    private fun updateResources(context: Context, language: String?): Context {
       /* val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val defaultLanguage = sharedPref.getString("language", Locale.getDefault().toString())*/
        Log.e("language --- updateRes",language.toString())
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }

    private fun updateResourcesLegacy(context: Context, language: String?): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale)
        }
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }
}