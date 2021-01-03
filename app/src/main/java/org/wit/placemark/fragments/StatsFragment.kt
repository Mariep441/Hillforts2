package org.wit.placemark.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup




import org.jetbrains.anko.AnkoLogger

import org.wit.placemark.R


class StatsFragment : Fragment(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = getString(R.string.statsTitle)
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                StatsFragment().apply {
                    arguments = Bundle().apply { }
                }
    }
}
