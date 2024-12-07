package com.hellodati.launcher.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.hellodati.launcher.R
import com.hellodati.launcher.model.Language

class LanguageAdapter(private val context: Context, private val langs: List<Language>): ArrayAdapter<Language>(context, android.R.layout.simple_spinner_item, ArrayList<Language>()) {
     var selectedLang: String? = null

    fun setSelected(langIso: String?) {
        selectedLang = langIso
    }

    fun getSelected(): Language {
        for (i in langs.indices) {
            if (langs[i].iso == selectedLang) {
                return langs[i]
            }
        }
        return langs[0]
    }

    override fun getCount(): Int {
        return langs.size
    }

    override fun getItem(position: Int): Language? {
        return if (position >= 0 && position < langs.size) langs[position] else null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItem = LayoutInflater.from(context).inflate(R.layout.lang_item, parent, false)
        val lang = langs[position]
        listItem.findViewById<TextView>(R.id.name).text = lang.name
        listItem.findViewById<TextView>(R.id.iso).text = lang.iso
        val imgChecked = listItem.findViewById<ImageView>(R.id.checked)
        if (lang.iso == getSelected().iso) {
            imgChecked.setImageResource(R.drawable.ic_valid_green)
        }
        return listItem
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItem = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        val lang = langs[position]
        listItem.findViewById<TextView>(android.R.id.text1).text = lang.name
        return listItem
    }
}