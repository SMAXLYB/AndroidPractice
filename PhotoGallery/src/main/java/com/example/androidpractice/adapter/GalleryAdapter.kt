package com.example.androidpractice.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.androidpractice.R
import com.example.androidpractice.datasource.LoadingStatus
import com.example.androidpractice.model.GalleryViewModel
import com.example.androidpractice.model.PhotoItem
import kotlinx.android.synthetic.main.gallery_cell.view.*
import kotlinx.android.synthetic.main.gallery_footer.view.*

// Gallery界面使用的adapter
class GalleryAdapter(private val galleryViewModel: GalleryViewModel) :
    PagedListAdapter<PhotoItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    private var loadingStatus: LoadingStatus? = null

    // 是否有footer,第一次加载不允许有footer
    private var hasFooter = false

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PhotoItem>() {
            override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
                return oldItem.photoId == newItem.photoId
            }

            override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    init {
        galleryViewModel.retryFetchData()
    }

    fun updateNetworkStatus(loadingStatus: LoadingStatus?) {
        this.loadingStatus = loadingStatus

        // 首次加载不显示footer
        if (loadingStatus == LoadingStatus.INITIAL_LOADING) {
            hideFooter()
        } else {
            showFooter()
        }
    }

    private fun showFooter() {
        // 如果原来有footer,改变状态
        if (hasFooter) {
            notifyItemChanged(itemCount - 1)
        } else {
            // 如果没有footer,加入
            hasFooter = true
            notifyItemInserted(itemCount - 1)
        }
    }

    private fun hideFooter() {
        // 如果原来有footer,移除
        if (hasFooter) {
            notifyItemRemoved(itemCount - 1)
        }
        hasFooter = false
    }

    // 每次加载新数据时都会调用,且数目叠加
    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasFooter) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasFooter && position == itemCount - 1) {
            R.layout.gallery_footer
        } else {
            R.layout.gallery_cell
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.gallery_cell -> {
                return PhotoViewHolder.newInstance(parent).also { holder ->
                    holder.itemView.setOnClickListener {
                        // 传多个图片给ViewPagerPhotoFragment
                        val bundle = bundleOf(
                            Pair<String, Int>("PHOTO_POSITION", holder.adapterPosition)
                        )
                        it.findNavController()
                            .navigate(R.id.action_galleryFragment_to_pagerPhotoFragment, bundle)
                    }
                }
            }
            else -> {
                return FooterViewHolder.newInstance(parent).also {
                    it.itemView.setOnClickListener {
                        galleryViewModel.retryFetchData()
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            R.layout.gallery_footer -> {
                (holder as FooterViewHolder).bindViewWithNetStatus(loadingStatus)
            }
            else -> {
                val photoItem = getItem(position) ?: return
                (holder as PhotoViewHolder).bindViewWithPhotoItem(photoItem)
            }
        }
    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        companion object {
            fun newInstance(parent: ViewGroup): PhotoViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.gallery_cell, parent, false)
                return PhotoViewHolder(view)
            }
        }

        fun bindViewWithPhotoItem(photoItem: PhotoItem) {
            with(itemView) {
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
            Glide.with(itemView)
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
                            itemView.shimmerLayout?.stopShimmerAnimation()
                        }
                    }
                })
                .into(itemView.imageView)

        }
    }

    class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        companion object {
            fun newInstance(parent: ViewGroup): FooterViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.gallery_footer, parent, false).also {
                        (it.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan =
                            true
                    }
                return FooterViewHolder(view)
            }
        }

        fun bindViewWithNetStatus(loadingStatus: LoadingStatus?) {
            with(itemView) {
                when (loadingStatus) {
                    LoadingStatus.ERROR -> {
                        textView.text = "网络错误,点击重试"
                        progressBar.visibility = View.GONE
                        isClickable = true
                    }
                    LoadingStatus.COMPLETED -> {
                        textView.text = "没有数据啦~"
                        progressBar.visibility = View.GONE
                        isClickable = false
                    }
                    else -> {
                        textView.text = "正在加载中"
                        progressBar.visibility = View.VISIBLE
                        isClickable = false
                    }
                }
            }
        }
    }
}