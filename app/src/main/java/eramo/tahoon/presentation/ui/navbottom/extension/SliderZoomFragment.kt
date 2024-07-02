package eramo.tahoon.presentation.ui.navbottom.extension

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.databinding.FragmentSliderZoomBinding
import eramo.tahoon.util.onBackPressed

@AndroidEntryPoint
class SliderZoomFragment : Fragment(R.layout.fragment_slider_zoom) {
    private lateinit var binding: FragmentSliderZoomBinding
    private val args by navArgs<SliderZoomFragmentArgs>()
    private val image get() = args.image


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSliderZoomBinding.bind(view)

        binding.ivImage.setImageBitmap(image)
        binding.ivBack.setOnClickListener { findNavController().popBackStack() }

        this@SliderZoomFragment.onBackPressed { findNavController().popBackStack() }
    }

}