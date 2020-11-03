package com.example.androidpractice.datasource

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.androidpractice.model.PhotoItem
import com.example.androidpractice.model.Pixabay
import com.example.androidpractice.net.OkHttpSingleton
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.math.ceil

enum class LoadingStatus {
    INITIAL_LOADING, //第一次加载
    NORMAL_LOADING, // 普通加载
    LOADED, // 本次加载完毕
    COMPLETED, // 全部加载完毕,没有数据
    ERROR // 加载出错
}

class PixabayDataSource(private val context: Context) : PageKeyedDataSource<Int, PhotoItem>() {
    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus> = _loadingStatus
    var retryFetchData: (() -> Unit)? = null

    private val queryKey =
        arrayOf("cat", "dog", "car", "beauty", "phone", "computer", "flower", "animal").random()

    companion object {
        private const val TAG = "PixabayDataSource"
        const val FIRST_PAGE = 1
        const val PAGE_SIZE = 50
    }

    // 初次加载
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, PhotoItem>
    ) {
        retryFetchData = null
        _loadingStatus.postValue(LoadingStatus.INITIAL_LOADING)
        val url =
            "https://pixabay.com/api/?key=18929295-68999fa87a7c5e91bba898dc4&q=${queryKey}&per_page=${PAGE_SIZE}&page=$FIRST_PAGE"
        OkHttpSingleton.getInstance(context.applicationContext)
            .doGet(url, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "onFailure: ${e.toString()}")
                    _loadingStatus.postValue(LoadingStatus.ERROR)
                    retryFetchData = { loadInitial(params, callback) }
                }

                override fun onResponse(call: Call, response: Response) {
                    val content = response.body?.string()
                    val pixabay = Gson().fromJson(content, Pixabay::class.java)
                    val photoItemList = pixabay.hits.toList()
                    callback.onResult(photoItemList, null, FIRST_PAGE + 1)
                    _loadingStatus.postValue(LoadingStatus.LOADED)
                }
            })
    }

    // 往前加载
    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
        TODO("Not yet implemented")
    }

    // 加载下一页
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PhotoItem>) {
        retryFetchData = null
        _loadingStatus.postValue(LoadingStatus.NORMAL_LOADING)
        val url =
            "https://pixabay.com/api/?key=18929295-68999fa87a7c5e91bba898dc4&q=${queryKey}&per_page=50&page=${params.key}"
        OkHttpSingleton.getInstance(context.applicationContext)
            .doGet(url, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d(TAG, "onFailure: ${e.toString()}")
                    _loadingStatus.postValue(LoadingStatus.ERROR)
                    retryFetchData = { loadAfter(params, callback) }
                }

                override fun onResponse(call: Call, response: Response) {
                    val content = response.body?.string()
                    content?.let {

                        val pixabay = Gson().fromJson(content, Pixabay::class.java)
                        val photoItemList = pixabay.hits.toList()

                        val totalPage = ceil(pixabay.totalHits.toDouble() / PAGE_SIZE).toInt()

                        val nextKey =
                            if (params.key > totalPage) {
                                _loadingStatus.postValue(LoadingStatus.COMPLETED)
                                null
                            } else {
                                _loadingStatus.postValue(LoadingStatus.LOADED)
                                params.key + 1
                            }
                        callback.onResult(photoItemList, nextKey)
                    }
                }
            })
    }

}