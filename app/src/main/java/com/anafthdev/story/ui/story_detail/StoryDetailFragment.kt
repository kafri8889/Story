package com.anafthdev.story.ui.story_detail

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.anafthdev.story.R
import com.anafthdev.story.data.model.Story
import com.anafthdev.story.databinding.FragmentStoryDetailBinding
import com.anafthdev.story.foundation.extension.viewBinding
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.time.Instant

@AndroidEntryPoint
class StoryDetailFragment : Fragment(R.layout.fragment_story_detail) {


    private val args: StoryDetailFragmentArgs by navArgs()
    private val viewModel: StoryDetailViewModel by viewModels()
    private val binding: FragmentStoryDetailBinding by viewBinding(FragmentStoryDetailBinding::bind)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.story.isNotBlank()) {
            viewModel.setStory(Gson().fromJson(args.story, Story::class.java))
        }

        initView()

        viewModel.story.observe(viewLifecycleOwner) { story ->
            updateView(story)
        }
    }

    private fun initView() = with(binding) {
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun updateView(story: Story?) = with(binding) {
        ViewCompat.setTransitionName(ivStory, "story_image_${story?.id}")
        ViewCompat.setTransitionName(tvName, "story_username_${story?.id}")
        ViewCompat.setTransitionName(tvDescription, "story_description_${story?.id}")
        ViewCompat.setTransitionName(tvDate, "story_date_${story?.id}")

        Glide.with(requireContext())
            .load(story?.photoUrl)
            .into(ivStory)

        tvName.text = story?.name
        tvDescription.text = story?.description
        tvDate.text = DateFormat.getDateInstance(DateFormat.FULL).format(
            if (story != null) Instant.parse(story.createdAt).toEpochMilli()
            else 0
        )
    }
}