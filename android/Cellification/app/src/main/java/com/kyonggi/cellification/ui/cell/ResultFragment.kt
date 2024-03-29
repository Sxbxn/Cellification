package com.kyonggi.cellification.ui.cell

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.kyonggi.cellification.data.model.cell.Cell
import com.kyonggi.cellification.databinding.FragmentResultBinding
import com.kyonggi.cellification.ui.di.App
import com.kyonggi.cellification.ui.viewmodel.CellViewModel
import com.kyonggi.cellification.utils.CustomAlertDialog

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : Fragment() {
    private val cellViewModel: CellViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var cell: Cell
    private lateinit var binding: FragmentResultBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResultBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null){
            val totalCell = requireArguments().getInt("totalCell")
            val liveCell = requireArguments().getInt("liveCell")
            val deadCell = requireArguments().getInt("deadCell")
            val viability = requireArguments().getDouble("viability")
            val url = requireArguments().getString("url")
            val userId = App.prefs.userId.toString()
            cell = Cell(0,liveCell,deadCell,url!!,viability,userId)
            initView(totalCell,liveCell,deadCell,viability)
        }
        setOnButtonClickListener()
    }

    private fun setOnButtonClickListener() {
        binding.button.setOnClickListener {
            //local db에 저장
            val dialog = CustomAlertDialog(requireContext())
            dialog.init("로컬 DB에 저장하시겠습니까?")
            dialog.getPositive().text ="저장"
            dialog.getPositive().setOnClickListener {
                cellViewModel.insertCell(cell)
                cellViewModel.stateListLocal.observe(viewLifecycleOwner, Observer{
                    Toast.makeText(requireContext(),"저장 성공", Toast.LENGTH_SHORT).show()
                })
                handler.postDelayed({
                    dialog.exit()
                }, 400)
            }
            dialog.getNegative().setOnClickListener {
                handler.postDelayed({
                    dialog.exit()
                }, 400)
            }
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
}