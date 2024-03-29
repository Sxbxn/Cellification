package com.kyonggi.cellification.ui.cell

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.kyonggi.cellification.data.model.cell.ResponseCell
import com.kyonggi.cellification.databinding.FragmentAnalysisBinding
import com.kyonggi.cellification.ui.di.App
import com.kyonggi.cellification.ui.viewmodel.CellViewModel
import com.kyonggi.cellification.ui.viewmodel.UserViewModel
import com.kyonggi.cellification.utils.*
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class AnalysisFragment : Fragment() {
    private val cellViewModel: CellViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var binding: FragmentAnalysisBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var sendFile: File
    private val REQ_STORAGE_PERMISSION = 1
    private lateinit var loading: LoadingDialog
    private lateinit var analysisData: ResponseCell
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                ActivityCompat.requestPermissions(
                    mainActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQ_STORAGE_PERMISSION
                )
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAnalysisBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = LoadingDialog(requireContext())
        loading.cancelButton().setOnClickListener {
            loading.setInvisible()
        }
        loading.retryButton().setOnClickListener {
            loading.setInvisible()
        }
        setOnImageButtonClickListener()
        setOnAnalysisButtonClickListener()
    }

    private fun setOnImageButtonClickListener() {
        val getGalleryImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri = result.data?.data // 선택한 이미지의 주소(상대경로)
                    val absoluteUri = getFullPathFromUri(requireContext(), uri!!)

                    GlideApp.with(requireContext())
                        .load(uri)
                        .transform(CenterCrop(), RoundedCorners(15))
                        .into(binding.imageButtonImageSelect)

                    sendFile = File(absoluteUri!!)
                }
            }

        binding.imageButtonImageSelect.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    mainActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                getGalleryImageLauncher.launch(intent)
            }
        }
    }

    private fun setOnAnalysisButtonClickListener() {
        binding.analysis.setOnClickListener {
            if (!::sendFile.isInitialized) {
                Toast.makeText(requireContext(), "이미지를 불러와 주십시오", Toast.LENGTH_SHORT).show()
            } else {
                var fileName = sendFile.name
                val requestFile = sendFile.asRequestBody("image/*".toMediaTypeOrNull())

                val token = "Bearer " + App.prefs.token.toString()
                val body = MultipartBody.Part.createFormData("file", fileName, requestFile)
                val userId = App.prefs.userId.toString()
                //실제 쓸거
                if (getConnectivityStatus(requireContext())) {
                    analysisCell(token, body, userId)
                }
            }
        }
    }

    private fun analysisCell(token: String, body: MultipartBody.Part, userId: String) {
        cellViewModel.makeCell(token, body, userId)
        cellViewModel.state.observe(viewLifecycleOwner, Observer {
            when (it) {
                is APIResponse.Success -> {
                    Toast.makeText(requireActivity(), "생성 성공", Toast.LENGTH_SHORT).show()
                    analysisData =
                        it.data!! // 이 데이터를 AnalysisDoneFragment 에 전달   --- 이미지 정보는 현재 프래그먼트에서 사용
                    mainActivity.changeFragment(AnalysisDoneFragment(), analysisData)
                    loading.dismiss()
                }
                is APIResponse.Error -> {
                    //분석 실패
                    loading.setError()
                    loading.setInvisible()
                }
                is APIResponse.Loading -> {
                    loading.setVisible()
                }
            }
        })
    }
}