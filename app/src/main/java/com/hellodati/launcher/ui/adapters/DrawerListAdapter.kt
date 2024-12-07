package com.hellodati.launcher.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.hellodati.launcher.R

class DrawerListAdapter(
    private val context: Context,
    private val listDataHeader: List<String>,
    private val listDataChild: Map<String, List<String>>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return listDataHeader.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        val groupKey = listDataHeader[groupPosition]
        return listDataChild[groupKey]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): Any {
        return listDataHeader[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        val groupKey = listDataHeader[groupPosition]
        return listDataChild[groupKey]?.get(childPosition) ?: ""
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertViewVar = convertView
        if (convertViewVar == null) {
            convertViewVar = LayoutInflater.from(context).inflate(R.layout.list_group, null)
        }
        val lblListHeader = convertViewVar!!.findViewById<TextView>(R.id.global_item_title)
        lblListHeader.text = getGroup(groupPosition) as String
        return convertViewVar
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertViewVar = convertView
        if (convertViewVar == null) {
            convertViewVar = LayoutInflater.from(context).inflate(R.layout.list_item, null)
        }
        val lblListItem = convertViewVar!!.findViewById<TextView>(R.id.title_item_child)
        lblListItem.text = getChild(groupPosition, childPosition) as String
        return convertViewVar
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}