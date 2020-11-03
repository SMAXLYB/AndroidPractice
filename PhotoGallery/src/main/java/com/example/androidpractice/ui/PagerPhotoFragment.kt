package com.example.androidpractice.ui

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.androidpractice.R
import com.example.androidpractice.adapter.PagerPhotoListAdapter
import com.example.androidpractice.adapter.PagerPhotoListAdapter.PagerPhotoViewHolder
import com.example.androidpractice.model.GalleryViewModel
import kotlinx.android.synthetic.main.fragment_pager_photo.*
import kotlinx.android.synthetic.main.pager_photo_view.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


const val REQUEST_WRITE_EXTERNAL_STORAGE: Int = 1

class PagerPhotoFragment : Fragment() {
    val viewModel by activityViewModels<GalleryViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pager_photo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 设置adapter和数据源
        val pagerPhotoListAdapter = PagerPhotoListAdapter().apply {
            viewPager2.adapter = this
        }
        viewModel.pagedListLiveData.observe(viewLifecycleOwner) {
            pagerPhotoListAdapter.submitList(it)
            // 设置当前页面
            viewPager2.setCurrentItem(arguments?.getInt("PHOTO_POSITION") ?: 0, false)
        }

        // 注册滑动监听
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                photoTag.text = getString(R.string.photo_tag, position + 1, viewModel.pagedListLiveData.value?.size)
            }
        })

        // 切换方向
        // viewPager2.orientation = ViewPager2.ORIENTATION_VERTICAL

        // 设置下载
        download.setOnClickListener {

            // 先授权
            // 如果已经授权
            if (Build.VERSION.SDK_INT < 29 && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_EXTERNAL_STORAGE
                )
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    savePhoto()
                }
            }
        }
    }

    private suspend fun savePhoto() {
        withContext(Dispatchers.IO) {
            // 找到viewHolder,直接存储viewHolder上的图片
            val holder =
                (viewPager2[0] as RecyclerView).findViewHolderForAdapterPosition(viewPager2.currentItem) as PagerPhotoViewHolder
            val bitmap = holder.itemView.pagerPhoto.drawable.toBitmap()

            // 获取URI路径
            val saveUri = requireContext().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues()
            ) ?: kotlin.run {
                MainScope().launch {
                    Toast.makeText(requireContext(), "保存失败", Toast.LENGTH_SHORT).show()
                }
                return@withContext
            }

            requireContext().contentResolver.openOutputStream(saveUri).use {
                // 如果保存成功
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) {
                    MainScope().launch {
                        Toast.makeText(requireContext(), "保存成功", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    MainScope().launch {
                        Toast.makeText(requireContext(), "保存失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_WRITE_EXTERNAL_STORAGE -> {
                // 如果允许了请求
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        savePhoto()
                    }
                } else {
                    // 如果拒绝了请求
                    Toast.makeText(requireContext(), "存储失败, 权限被拒绝!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}