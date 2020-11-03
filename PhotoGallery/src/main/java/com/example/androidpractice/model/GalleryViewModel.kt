package com.example.androidpractice.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.androidpractice.datasource.LoadingStatus
import com.example.androidpractice.datasource.PixabayDataSource
import com.example.androidpractice.datasource.PixabayDataSourceFactory

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var pagedListLiveData: LiveData<PagedList<PhotoItem>>
    private val factory = PixabayDataSourceFactory(application)

    // 通过观察liveData返回一个liveData,中介作用
    val networkStatus: LiveData<LoadingStatus> =
        Transformations.switchMap(factory.pixabayDataSource) { it.loadingStatus }

    init {
        PagedList.Config
            .Builder()
            .setEnablePlaceholders(false)
            .setPageSize(PixabayDataSource.PAGE_SIZE)
            .setPrefetchDistance(5)
            .build().also {
                pagedListLiveData =
                    LivePagedListBuilder(factory, it).build()
            }
    }

    // 下拉刷新
    fun resetQuery() {
        pagedListLiveData.value?.dataSource?.invalidate()
    }

    // 点击重试
    fun retryFetchData() {
        factory.pixabayDataSource.value?.retryFetchData?.invoke()
    }
}