package com.example.androidpractice.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.androidpractice.adapter.GalleryAdapter
import com.example.androidpractice.R
import com.example.androidpractice.model.DataStatus
import com.example.androidpractice.model.GalleryViewModel
import kotlinx.android.synthetic.main.fragment_gallery.*

class GalleryFragment : Fragment() {
    private lateinit var viewModel: GalleryViewModel

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(GalleryViewModel::class.java)

        val galleryAdapter = GalleryAdapter(viewModel)
        recyclerView.apply {
            adapter = galleryAdapter
            // layoutManager = GridLayoutManager(requireContext(), 2)
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        viewModel.photoListLive.observe(viewLifecycleOwner) {
            // 如果是一次刷新操作,需要手动滑动到顶部
            if (viewModel.needToScrollTop) {
                recyclerView.scrollToPosition(0)
                viewModel.needToScrollTop = false
            }
            swipeRefreshLayout.isRefreshing = false
            galleryAdapter.submitList(it)
        }
        viewModel.dataStatusLive.observe(viewLifecycleOwner) {
            galleryAdapter.dataStatus = it
            // 如果footer的状态发生改变,手动通知视图
            galleryAdapter.notifyItemChanged(galleryAdapter.itemCount - 1)
            // 如果网络错误,无需加载
            if (swipeRefreshLayout.isRefreshing && it == DataStatus.NET_ERROR) {
                swipeRefreshLayout.isRefreshing = false
            }
        }

        // 下拉刷新
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.resetQuery()
        }

        // 对recyclerView进行滚动监听,进行分页加载
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            // 对滑动状态进行监听
            // 0 代表静止
            // 1 代表滑动
            // 2 代表快速滑动
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            // 向下拉 dy<0
            // 向上拉 dy>0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // 如果下拉,不做处理
                if (dy < 0) {
                    return
                }

                // 找出recyclerView最后可见的view的位置
                val layoutManager =
                    recyclerView.layoutManager as StaggeredGridLayoutManager
                //最后可见view的位置
                val intArray = IntArray(2)
                layoutManager.findLastVisibleItemPositions(intArray)
                // 如果已经是最后一个footer
                if (intArray[0] == galleryAdapter.itemCount - 1) {
                    // 开始加载下一页
                    viewModel.fetchData()
                }
            }
        })
    }
}