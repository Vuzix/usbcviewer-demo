package com.vuzix.android.m400c.audio.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.vuzix.android.m400c.databinding.ListFilesBinding

class AudioFileAdapter (private var fileList: List<String>): Adapter<AudioFileViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioFileViewHolder {
        val binding = ListFilesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AudioFileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AudioFileViewHolder, position: Int) {
        holder.bind(fileList[position])
    }

    override fun getItemCount(): Int = fileList.size

    fun updateList(list: List<String>) {
        val diffCallback = AudioFileListDiffCallback(fileList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        fileList = list
        diffResult.dispatchUpdatesTo(this)
    }
}