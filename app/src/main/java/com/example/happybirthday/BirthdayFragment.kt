package com.example.happybirthday

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.happybirthday.databinding.BirthdayScreenBinding
import java.util.*
import kotlin.random.Random

class BirthdayFragment  : Fragment() {
    companion object {
        private val IMAGE_RADIUS = 229/2
    }
    private lateinit var binding : BirthdayScreenBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BirthdayScreenBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val randomValues = Random.nextInt(0, 3)
            when(randomValues) {
                0 -> {
                    ivBackground.setImageResource(R.drawable.anniversary_assets)
                    ivBackground.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_background), android.graphics.PorterDuff.Mode.MULTIPLY)
                    ivImageSmile.setImageResource(R.drawable.ic_default_place_holder_green)
                    ivImageFrame.setImageResource(R.drawable.ic_frame_green)
                    ivCamera.setImageResource(R.drawable.ic_camera_icon_green)
                }
                1-> {
                    ivBackground.setImageResource(R.drawable.i_os_bg_elephant)
                    ivBackground.setColorFilter(ContextCompat.getColor(requireContext(), R.color.yellow_background), android.graphics.PorterDuff.Mode.MULTIPLY)
                    ivImageSmile.setImageResource(R.drawable.ic_default_place_holder_yellow)
                    ivImageFrame.setImageResource(R.drawable.ic_frame_yellow)
                    ivCamera.setImageResource(R.drawable.ic_camera_icon_yellow)
                }
                else -> {
                    ivBackground.setImageResource(R.drawable.i_os_bg_pelican_2)
                    ivBackground.setColorFilter(ContextCompat.getColor(requireContext(), R.color.blue_background), android.graphics.PorterDuff.Mode.MULTIPLY)
                    ivImageSmile.setImageResource(R.drawable.ic_default_place_holder_blue)
                    ivImageFrame.setImageResource(R.drawable.ic_frame_blue)
                    ivCamera.setImageResource(R.drawable.ic_camera_icon_blue)
                }

            }

            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            tvBirthday.text = resources.getString(R.string.today_name_is, viewModel.name.value)

            val month = DateFormat.format("MM", viewModel.birthday.value)
            val currentMonth = DateFormat.format("MM", Calendar.getInstance().time)
            val delta = (currentMonth.toString().toInt() - month.toString().toInt()) %12
            ivMonth.setImageResource(
            when (delta) {
                1 -> R.drawable.ic_age_number_1
                2 -> R.drawable.ic_age_number_2
                3 -> R.drawable.ic_age_number_3
                4 -> R.drawable.ic_age_number_4
                5 -> R.drawable.ic_age_number_5
                6 -> R.drawable.ic_age_number_6
                7 -> R.drawable.ic_age_number_7
                8 -> R.drawable.ic_age_number_8
                9 -> R.drawable.ic_age_number_9
                10 -> R.drawable.ic_age_number_10
                11 -> R.drawable.ic_age_number_11
                else -> R.drawable.ic_age_number_0
            })

            val layoutParams = (ivCamera.layoutParams as? ViewGroup.MarginLayoutParams)
            val length = getPositionByAngelX(dpToPx(IMAGE_RADIUS), 45)
            val height = getPositionByAngelX(dpToPx(IMAGE_RADIUS), 45)
            layoutParams?.setMargins(length, 0, 0, height)
            ivCamera.layoutParams = layoutParams

            bShare.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.today_name_is, viewModel.name.value) +
                            " " + resources.getString(R.string.month_old))
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }

            viewModel.pictureLiveData.observe(viewLifecycleOwner) {(bitmap, uri) ->
                if (viewModel.photoFile2 != null) {
                    loadPhoto(uri)
                }
            }
            ivCamera.setOnClickListener {
                viewModel.pickImageLiveData.postValue(BirthdayFragment::class.java.name)

            }

            if (viewModel.photoFile2 != null) {
                //!loadPhoto()
                val layoutParams = (ivImageSmile.layoutParams as? ViewGroup.MarginLayoutParams)
                layoutParams?.setMargins(dpToPx(4), dpToPx(4), 0, 0)
                ivImageSmile.layoutParams = layoutParams

            }
        }
    }

    private fun loadPhoto(uri : Uri) {
        Glide.with(binding.ivImageSmile).load(uri).transform(CenterCrop(), RoundedCorners(dpToPx(
            IMAGE_RADIUS
        ))) // depend on ivPhoto's background border radius
            .into(binding.ivImageSmile)

    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.getItemId()) {
//            android.R.id.home -> {
//                activity?.onBackPressed()
//                true
//            }
//            else -> false
//        }
//    }

}