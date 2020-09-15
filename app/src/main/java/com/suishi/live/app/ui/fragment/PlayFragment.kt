package com.suishi.live.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.suishi.live.app.R

/**
 * 播放
 */
class PlayFragment : Fragment() {
    private var mParam1: String? = null
    private var mParam2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_play, container, false)
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private var fragment: PlayFragment? = null

        /**
         */
        fun newInstance(): PlayFragment? {
            if (fragment == null) {
                fragment = PlayFragment()
                val args = Bundle()
                fragment!!.arguments = args
            }
            return fragment
        }
    }
}