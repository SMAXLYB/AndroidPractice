package com.example.mvvmdemo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mvvmdemo.R
import com.example.mvvmdemo.common.BaseConstant
import kotlinx.android.synthetic.main.fragment_welcome.*


class WelcomeFragment : Fragment() {

    @JvmField
    val test = "dfd";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_login.setOnClickListener {

            val bundle = Bundle().apply {
                putString(BaseConstant.ARGS_NAME,)
            }

            findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment,)
        }
    }
}