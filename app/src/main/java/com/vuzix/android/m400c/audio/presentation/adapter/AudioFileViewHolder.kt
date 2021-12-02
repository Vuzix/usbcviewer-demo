package com.vuzix.android.m400c.audio.presentation.adapter

import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.vuzix.android.m400c.R
import com.vuzix.android.m400c.databinding.ListFilesBinding

class AudioFileViewHolder(private val binding: ListFilesBinding) : ViewHolder(binding.root) {

    fun bind(name: String) {
        binding.tvFilename.text = name
        binding.setClickListener {
            val bundle = bundleOf("filename" to name)
            it.findNavController().navigate(R.id.action_audioFragment_to_playSavedAudioFragment, bundle)
        }
    }
}