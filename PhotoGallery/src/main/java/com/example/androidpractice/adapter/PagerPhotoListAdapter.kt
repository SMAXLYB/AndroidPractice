package com.example.androidpractice.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.androidpractice.R
import com.example.androidpractice.model.PhotoItem
import io.supercharge.shimmerlayout.ShimmerLayout
import kotlinx.android.synthetic.main.pager_photo_view.view.*

class PagerPhotoListAdapter : ListAdapter<PhotoItem, PagerPhotoListAdapter.PagerPhotoViewHolder>(
    DIFF_CALLBACK
) {
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



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerPhotoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.pager_photo_view, parent, false)
        val holder = PagerPhotoViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: PagerPhotoViewHolder, position: Int) {
        holder.itemView.shimmerLayout.apply {
            setShimmerColor(0x55ffffff)
            setShimmerAngle(0)
            startShimmerAnimation()
        }
        Glide.with(holder.itemView)
            .load(getItem(position).fullUrl)
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
            .into(holder.itemView.pagerPhoto)
    }

    inner class PagerPhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}