package com.vuzix.android.m400c.audio.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.audio.domain.AudioFileInfo
import com.vuzix.android.m400c.common.presentation.ItemClickListener
import com.vuzix.android.m400c.databinding.ListFilesBinding
import timber.log.Timber

class AudioFileAdapter(
    private var fileList: List<AudioFileInfo>,
    private val clickListener: ItemClickListener
) : Adapter<AudioFileViewHolder>() {
    lateinit var binding: ListFilesBinding
    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioFileViewHolder {
        binding = ListFilesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AudioFileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AudioFileViewHolder, position: Int) {
        fileList[position].let { info ->
            Timber.d(info.toString())
            holder.binding.tvFilename.text = info.name
            if (info.selected) {
                holder.binding.clFilename.setBackgroundResource(R.color.vuzix_red)
            } else {
                holder.binding.clFilename.setBackgroundColor(Color.TRANSPARENT)
            }
            holder.binding.clFilename.setOnClickListener {
                if (selectedPosition != position) {
                    val newList = fileList.toMutableList()
                    newList[selectedPosition] = fileList[selectedPosition].copy(selected = false)
                    newList[position] = fileList[position].copy(selected = true)
                    selectedPosition = position
                    clickListener.onItemClicked(info.name)
                    updateList(newList)
                }
            }
        }
    }

    override fun getItemCount(): Int = fileList.size

    fun updateList(list: List<AudioFileInfo>) {
        val diffCallback = AudioFileListDiffCallback(fileList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        fileList = list
        diffResult.dispatchUpdatesTo(this)
    }
}