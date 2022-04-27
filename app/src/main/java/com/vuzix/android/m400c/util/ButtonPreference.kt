package com.vuzix.android.m400c.util

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.vuzix.android.m400c.R


class ButtonPreference(context: Context,  attrs: AttributeSet):
    Preference(context, attrs) {

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        val button: Button? = holder?.itemView?.findViewById<Button>(R.id.restore_defaults)
        button?.setOnClickListener {
            onPreferenceClickListener.onPreferenceClick(this)
        }
    }

}