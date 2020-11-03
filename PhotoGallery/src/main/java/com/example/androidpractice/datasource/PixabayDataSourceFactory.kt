package com.example.androidpractice.datasource

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.androidpractice.model.PhotoItem

class PixabayDataSourceFactory(private val context: Context) :
    DataSource.Factory<Int, PhotoItem>() {
    private val _pixabayDataSource = MutableLiveData<PixabayDataSource>()
    val pixabayDataSource: LiveData<PixabayDataSource> = _pixabayDataSource

    override fun create(): DataSource<Int, PhotoItem> {
        return PixabayDataSource(context).also {
            _pixabayDataSource.postValue(it)
        }
    }
}