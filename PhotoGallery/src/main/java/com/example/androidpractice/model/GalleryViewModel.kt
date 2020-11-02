package com.example.androidpractice.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.androidpractice.net.OkHttpSingleton
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.math.ceil

enum class DataStatus {
    NO_MORE,
    HAS_MORE,
    NET_ERROR
}

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val _dataStatusLive = MutableLiveData<DataStatus>()
    val dataStatusLive: LiveData<DataStatus>
        get() = _dataStatusLive
    private val _photoListLive = MutableLiveData<List<PhotoItem>>()
    val photoListLive: LiveData<List<PhotoItem>>
        get() = _photoListLive

    // 加载数据的相关变量
    private val keyWords =
        arrayOf("cat", "dog", "car", "beauty", "phone", "computer", "flower", "animal")
    private val numPerPage = 50 // 每页加载50条数据
    private var currentPage = 1 // 当前是第几页
    private var totalPage = 1 // 总页数
    private var isLoading = false // 判断当前是否正在加载,减少网络压力
    private var isNewQuery = true // 判断本次是刷新还是加载更多,默认刷新
    private var currentKey = "cat" // 当前关键词
    var needToScrollTop = true // 是否要滑动到顶部

    init {
        resetQuery()
    }

    // 下拉刷新
    fun resetQuery() {
        //重置数据
        currentPage = 1
        totalPage = 1
        currentKey = keyWords.random()
        isNewQuery = true
        needToScrollTop = true
        fetchData()
    }

    // 抓取数据
    fun fetchData() {
        // 如果正在加载中,避免重复加载
        if (isLoading) {
            return
        }

        //如果已经到最后一页
        if (currentPage > totalPage) {
            _dataStatusLive.value = DataStatus.NO_MORE
            return
        }

        // 开始加载
        isLoading = true

        val request = Request.Builder()
            .url(getUrl())
            .build()
        OkHttpSingleton.getInstance(getApplication()).okHttpClient
            .newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("ViewModel", "onFailure: ${e.toString()}")
                    _dataStatusLive.postValue( DataStatus.NET_ERROR)
                    isLoading = false
                    isNewQuery = false
                }

                override fun onResponse(call: Call, response: Response) {
                    viewModelScope.launch(Dispatchers.Main) {
                        val string = response.body?.string()?.run {
                            val pixabay = Gson().fromJson(this, Pixabay::class.java)
                            with(pixabay) {
                                totalPage = ceil(totalHits.toDouble() / numPerPage).toInt()
                                // 判断当前是刷新还是加载,刷新直接覆盖,加载直接追加
                                if (isNewQuery) {
                                    _photoListLive.value = this.hits.toList()
                                    isNewQuery = false
                                } else {
                                    _photoListLive.value = arrayListOf(
                                        _photoListLive.value!!,
                                        this.hits.toList()
                                    ).flatten()
                                    currentPage++
                                }
                            }
                            // 加载结束
                            isLoading = false
                            _dataStatusLive.value = DataStatus.HAS_MORE
                        }
                    }
                }

            })
    }

    private fun getUrl() =
        "https://pixabay.com/api/?key=18929295-68999fa87a7c5e91bba898dc4&q=${currentKey}&per_page=${numPerPage}&page=${currentPage}"
}