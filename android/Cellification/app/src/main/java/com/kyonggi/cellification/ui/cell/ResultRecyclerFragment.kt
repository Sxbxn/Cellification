package com.kyonggi.cellification.ui.cell

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kyonggi.cellification.R
import com.kyonggi.cellification.databinding.FragmentResultRecyclerBinding
import com.kyonggi.cellification.utils.CustomAlertDialog
import com.kyonggi.cellification.utils.GlideApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultRecyclerFragment : Fragment() {

    private lateinit var binding: FragmentResultRecyclerBinding
    private lateinit var url: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResultRecyclerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null){
            val totalCell = requireArguments().getInt("totalCell")
            val liveCell = requireArguments().getInt("liveCell")
            val deadCell = requireArguments().getInt("deadCell")
            val viability = requireArguments().getDouble("viability")
            url = requireArguments().getString("url")!!
            initView(totalCell,liveCell,deadCell,viability)
        }
        binding.showImage.setOnClickListener{
            showImage()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView(total: Int, live: Int, dead: Int, Viability:Double) {
        with(binding) {
            totalCountText.text =
                "Total Count: $total"
            viability.text = String.format("%.2f",Viability) + "%"
            liveCell.text = live.toString()
            dieCell.text = dead.toString()
        }
    }
    private fun showImage(){
        val imgView = layoutInflater.inflate(R.layout.result_image,null)
        val dialog = CustomAlertDialog(requireContext())
        GlideApp.with(binding.root)
            .load(url)
            .placeholder(R.drawable.ic_baseline_settings_24)
            .error(R.drawable.ic_baseline_settings_24)
            .fallback(R.drawable.ic_baseline_settings_24)
            .override(1000,1000) // 추후조정
            .into(imgView.findViewById(R.id.result_image))

        dialog.initWithView("결과 이미지", imgView)
    }
}