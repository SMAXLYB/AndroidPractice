package com.example.animationdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animationdemo.databinding.ActivityMainBinding
import com.example.animationdemo.databinding.LayoutItemBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.recyclerView.adapter = MyAdapter(mockData())
    }

    private fun mockData(): Map<Int, String> {
        val map = HashMap<Int, String>()

        for (i in 0.until(100)) {
            map[i] = "这是第${i}条数据"
        }

        return map
    }

    private class MyAdapter(val map: Map<Int, String>) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        class MyViewHolder(val binding: LayoutItemBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bindView(order: Int, hint: String) {
                binding.setOrder(order)
                binding.setHint(hint)
                binding.root.animation =
                    AnimationUtils.loadAnimation(binding.root.context, R.anim.animation)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val binding = DataBindingUtil.inflate<LayoutItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.layout_item,
                parent,
                false
            )

            return MyViewHolder(binding)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindView(
                position, map[position] ?: ""
            )
        }

        override fun getItemCount(): Int {
            return map.size
        }
    }
}