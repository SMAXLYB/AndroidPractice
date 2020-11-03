package com.example.androidpractice.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.androidpractice.R
import com.example.androidpractice.adapter.GalleryAdapter
import com.example.androidpractice.datasource.LoadingStatus
import com.example.androidpractice.model.GalleryViewModel
import kotlinx.android.synthetic.main.fragment_gallery.*

class GalleryFragment : Fragment() {
    private val viewModel by activityViewModels<GalleryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.swipeIndicator -> {
                swipeRefreshLayout.isRefreshing = true
                viewModel.resetQuery()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 每次切换界面都会被回调
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val galleryAdapter = GalleryAdapter(viewModel)
        recyclerView.apply {
            adapter = galleryAdapter
            // layoutManager = GridLayoutManager(requireContext(), 2)
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        viewModel.pagedListLiveData.observe(viewLifecycleOwner) {
            galleryAdapter.submitList(it)
        }

        // 下拉刷新
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.resetQuery()
        }

        viewModel.networkStatus.observe(viewLifecycleOwner) {
            // 将网络状态返回给adapter,adapter对footer界面做出改变
            galleryAdapter.updateNetworkStatus(it)
            swipeRefreshLayout.isRefreshing = it == LoadingStatus.INITIAL_LOADING
        }
    }
}