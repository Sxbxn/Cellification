package com.kyonggi.cellification.ui.cell

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.kyonggi.cellification.R
import com.kyonggi.cellification.TestActivity
import com.kyonggi.cellification.databinding.FragmentAnalysisBinding
import com.kyonggi.cellification.databinding.FragmentAnalysisDoneBinding

class AnalysisDoneFragment: Fragment() {
    private lateinit var binding: FragmentAnalysisDoneBinding
    private lateinit var mainActivity: TestActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnalysisDoneBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as TestActivity
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnReselectOnClickListener()
        setOnResultButtonClickListener()
    }

    private fun setOnReselectOnClickListener() {
        binding.reselect.setOnClickListener {
            mainActivity.changeFragment(R.id.reselect)
        }
    }
    private fun setOnResultButtonClickListener() {
        binding.result.setOnClickListener {
            mainActivity.changeFragment(R.id.result)
        }
    }
}