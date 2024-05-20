package com.anafthdev.story.foundation.common.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.anafthdev.story.data.model.Story
import com.anafthdev.story.data.repository.StoryRepository
import com.anafthdev.story.foundation.adapter.StoryRecyclerViewAdapter
import com.anafthdev.story.foundation.common.data.LocalStoryDataProvider
import com.anafthdev.story.foundation.common.extension.InstantExecutorExtension
import com.anafthdev.story.foundation.common.extension.getOrAwaitValue
import com.anafthdev.story.ui.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
@ExtendWith(InstantExecutorExtension::class)
class HomeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `get story should not return empty data`() = runTest {
        val stories = LocalStoryDataProvider.values
        val data: PagingData<Story> = StoryPagingSource.snapshot(stories)
        val expectedStory = MutableLiveData(data)

        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStory)

        val homeViewModel = HomeViewModel(storyRepository)
        val actualStories: PagingData<Story> = homeViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryRecyclerViewAdapter.STORY_COMPARATOR,
            updateCallback = noOperationListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStories)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(stories.size, differ.snapshot().size)
        Assert.assertEquals(stories[0], differ.snapshot()[0])
    }

    @Test
    fun `get empty story should not return any data` () = runTest {
        val data: PagingData<Story> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData(data)

        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStory)

        val homeViewModel = HomeViewModel(storyRepository)
        val actualStory: PagingData<Story> = homeViewModel.stories.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryRecyclerViewAdapter.STORY_COMPARATOR,
            updateCallback = noOperationListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStory)
        Assert.assertEquals(0, differ.snapshot().size)
    }

}

val noOperationListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

class StoryPagingSource : PagingSource<Int, LiveData<List<Story>>>() {
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}
