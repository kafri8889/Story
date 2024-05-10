package com.anafthdev.story.ui.story_detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.anafthdev.story.data.model.Story
import com.anafthdev.story.databinding.FragmentStoryDetailBinding
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.time.Instant

@AndroidEntryPoint
class StoryDetailFragment : Fragment() {

    private lateinit var binding: FragmentStoryDetailBinding
    private lateinit var story: Story

    private val args: StoryDetailFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        story = Gson().fromJson(args.story, Story::class.java)

        binding = FragmentStoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() = with(binding) {
        ViewCompat.setTransitionName(ivStory, "story_image_${story.id}")
        ViewCompat.setTransitionName(tvName, "story_username_${story.id}")
        ViewCompat.setTransitionName(tvDescription, "story_description_${story.id}")
        ViewCompat.setTransitionName(tvDate, "story_date_${story.id}")

        Glide.with(requireContext())
            .load(story.photoUrl)
            .into(ivStory)

        tvName.text = story.name
        tvDescription.text = story.description
        tvDate.text = DateFormat.getDateInstance(DateFormat.FULL).format(
            Instant.parse(story.createdAt).toEpochMilli()
        )
    }
}