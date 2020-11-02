package com.example.androidpractice.adapter

import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.androidpractice.R
import com.example.androidpractice.model.DataStatus
import com.example.androidpractice.model.GalleryViewModel
import com.example.androidpractice.model.PhotoItem
import kotlinx.android.synthetic.main.gallery_cell.view.*
import kotlinx.android.synthetic.main.gallery_footer.view.*
import java.util.ArrayList

class GalleryAdapter(private val galleryViewModel: GalleryViewModel) :
    ListAdapter<PhotoItem, GalleryAdapter.MyViewHolder>(DIFF_CALLBACK) {
    var dataStatus = DataStatus.HAS_MORE

    companion object {
        private const val NORMAL_VIEW_TYPE = 0
        private const val FOOTER_VIEW_TYPE = 1

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PhotoItem>() {
            override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
                return oldItem.photoId == newItem.photoId
            }

            override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            FOOTER_VIEW_TYPE
        } else {
            NORMAL_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder: MyViewHolder
        if (viewType == NORMAL_VIEW_TYPE) {
            // 如果是正常视图
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.gallery_cell, parent, false)
            holder = MyViewHolder(view)
            holder.itemView.setOnClickListener {

                // 传多个图片给ViewPagerPhotoFragment
                val bundle = bundleOf(
                    Pair<String, ArrayList<Parcelable>>("PHOTO_LIST", ArrayList(currentList)),
                    Pair<String, Int>("PHOTO_POSITION", holder.adapterPosition)
                )
                it.findNavController()
                    .navigate(R.id.action_galleryFragment_to_pagerPhotoFragment, bundle)
            }
        } else {
            // 如果是footer
            holder = MyViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.gallery_footer, parent, false)
                    .also {
                        // 不分裂
                        (it.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan =
                            true
                        it.setOnClickListener { itemView ->
                            itemView.progressBar.visibility = View.VISIBLE
                            itemView.textView.text = "正在加载中"
                            galleryViewModel.fetchData()
                        }
                    }
            )
        }

        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if (position == itemCount - 1) {
            // 如果是footer,判断是否还有数据
            with(holder.itemView) {
                when (dataStatus) {
                    DataStatus.HAS_MORE -> {
                        progressBar.visibility = View.VISIBLE
                        textView.text = "正在加载中"
                        isClickable = false
                    }
                    DataStatus.NO_MORE -> {
                        progressBar.visibility = View.GONE
                        textView.text = "没有数据啦~"
                        isClickable = false
                    }
                    DataStatus.NET_ERROR -> {
                        progressBar.visibility = View.GONE
                        textView.text = "网络错误,点击重试"
                        isClickable = true
                    }
                }
            }
            return
        } else {
            // 如果是正常view
            val photoItem = getItem(position)

            with(holder.itemView) {
                // 背景动态闪动
                shimmerLayout.apply {
                    setShimmerColor(0x55ffffff)
                    setShimmerAngle(0)
                    startShimmerAnimation()
                }

                // 设置文字
                textViewUser.text = photoItem.photoUser
                textViewLikes.text = photoItem.photoLikes.toString()
                textViewFavorites.text = photoItem.photoFavorites.toString()

                // 设置图片高度
                imageView.layoutParams.height = photoItem.photoHeight
            }

            // 加载图片
            Glide.with(holder.itemView)
                .load(photoItem.previewUrl)
                .placeholder(R.drawable.ic_photo_gray)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false.also {
                            holder.itemView.shimmerLayout?.stopShimmerAnimation()
                        }
                    }
                })
                .into(holder.itemView.imageView)
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}