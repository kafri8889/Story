package com.anafthdev.story.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anafthdev.story.R
import com.anafthdev.story.databinding.FragmentHomeBinding
import com.anafthdev.story.foundation.adapter.StoryRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var storyAdapter: StoryRecyclerViewAdapter

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.refreshStories()

        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefreshLayout.isRefreshing = isRefreshing
        }

        viewModel.stories.observe(viewLifecycleOwner) { stories ->
            storyAdapter.submitList(stories)
        }

        initView()
    }

    private fun initView() = with(binding) {
        storyAdapter = StoryRecyclerViewAdapter().apply {
            setOnItemClickListener { story ->

            }
        }

        rvStories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = storyAdapter
        }

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_logout -> {
                    viewModel.logout()
                    findNavController().navigate(R.id.action_homeFragment_to_onboarding_fragment)
                    return@setOnMenuItemClickListener true
                }
            }

            false
        }

        fabPostStory.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_newStoryFragment)
        }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshStories()
        }
    }
}