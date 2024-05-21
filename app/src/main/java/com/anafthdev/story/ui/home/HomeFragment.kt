package com.anafthdev.story.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.anafthdev.story.R
import com.anafthdev.story.databinding.FragmentHomeBinding
import com.anafthdev.story.foundation.adapter.LoadingStateAdapter
import com.anafthdev.story.foundation.adapter.StoryRecyclerViewAdapter
import com.anafthdev.story.foundation.extension.toast
import com.anafthdev.story.foundation.extension.viewBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: Fragment(R.layout.fragment_home) {

    private lateinit var storyAdapter: StoryRecyclerViewAdapter

    private val viewModel: HomeViewModel by viewModels()
    private val binding: FragmentHomeBinding by viewBinding(FragmentHomeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stories.observe(viewLifecycleOwner) { stories ->
            storyAdapter.submitData(lifecycle, stories)
        }

        initView()
    }

    private fun initView() = with(binding) {
        storyAdapter = StoryRecyclerViewAdapter().apply {
            setOnItemClickListener { pos, story, extras ->
                val vh = rvStories.findViewHolderForAdapterPosition(pos) as? StoryRecyclerViewAdapter.StoryViewHolder

                vh?.let {
                    findNavController().navigate(
                        navigatorExtras = extras!!,
                        directions = HomeFragmentDirections.actionHomeFragmentToStoryDetailFragment(
                            Gson().toJson(story)
                        )
                    )

                    return@setOnItemClickListener
                }

                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToStoryDetailFragment(
                        Gson().toJson(story)
                    )
                )
            }

            addLoadStateListener {
                swipeRefreshLayout.isRefreshing = it.refresh == LoadState.Loading
                tvNoStories.isVisible = it.refresh is LoadState.Loading && itemCount == 0
            }
        }

        rvStories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { storyAdapter.retry() }
            )
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_settings -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
                    return@setOnMenuItemClickListener true
                }
            }

            false
        }

        fabPostStory.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewStoryFragment())
        }

        fabMap.setOnClickListener {
            viewModel.getStoriesWithLocation(
                onError = {
                    requireContext().toast(it.message!!)
                },
                onSuccess = { stories ->
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToMapsFragment(stories.toTypedArray())
                    )
                }
            )
        }

        swipeRefreshLayout.setOnRefreshListener {
            storyAdapter.refresh()
        }
    }
}