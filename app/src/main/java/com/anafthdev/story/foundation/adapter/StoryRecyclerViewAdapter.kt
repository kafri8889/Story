package com.anafthdev.story.foundation.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anafthdev.story.data.model.Story
import com.anafthdev.story.databinding.StoryItemBinding
import com.anafthdev.story.foundation.common.OnItemClickListener
import com.bumptech.glide.Glide
import java.text.DateFormat
import java.time.Instant

class StoryRecyclerViewAdapter: ListAdapter<Story, StoryRecyclerViewAdapter.StoryViewHolder>(STORY_COMPARATOR) {

    private var onItemClickListener: OnItemClickListener<Story>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {

        holder.bind(currentList[position])
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<Story>) {
        this.onItemClickListener = onItemClickListener
    }

    inner class StoryViewHolder(
        private val binding: StoryItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) = with(binding) {
            tvName.text = story.name
            tvDescription.text = story.description
            tvDate.text = DateFormat.getDateInstance(DateFormat.FULL).format(
                Instant.parse(story.createdAt).toEpochMilli()
            )

            root.setOnClickListener {
                onItemClickListener?.onItemClick(story)
            }

            Glide.with(root.context)
                .load(story.photoUrl)
                .placeholder(ColorDrawable(Color.GRAY))
                .centerCrop()
                .into(ivStory)
        }
    }

    companion object {
        private val STORY_COMPARATOR = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean =
                oldItem == newItem
        }
    }

}