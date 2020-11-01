package com.example.androidpractice

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.RequestBuilder
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val _photoListLive = MutableLiveData<List<PhotoItem>>()
    private val keyWords =
        arrayOf("cat", "dog", "car", "beauty", "phone", "computer", "flower", "animal")

    val photoListLive: LiveData<List<PhotoItem>>
        get() = _photoListLive

    fun fetchData() {
        val request = Request.Builder()
            .url(getUrl())
            .build()
        OkHttpSingleton.getInstance(getApplication()).okHttpClient
            .newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("ViewModel", "onFailure: ${e.toString()}")
                }

                override fun onResponse(call: Call, response: Response) {
                    viewModelScope.launch(Dispatchers.Main) {
                        val string = response.body?.string()?.run {
                            _photoListLive.value =
                                Gson().fromJson(this, Pixabay::class.java).hits.toList()
                        }
                    }
                }

            })
    }

    private fun getUrl() =
        "https://pixabay.com/api/?key=18929295-68999fa87a7c5e91bba898dc4&q=${keyWords.random()}&per_page=100"
}