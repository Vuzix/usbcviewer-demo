package com.vuzix.android.m400c.audio.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.vuzix.android.m400c.audio.domain.AudioFileInfo

class AudioFileListDiffCallback(private val oldList: List<AudioFileInfo>, private val newList: List<AudioFileInfo>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}