package eramo.tahoon.presentation.ui.auth

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.databinding.FragmentOnBoardingBinding
import eramo.tahoon.presentation.adapters.viewpager.OnBoardingAdapter
import eramo.tahoon.presentation.ui.dialog.LoadingDialog
import eramo.tahoon.presentation.viewmodel.auth.OnBoardingViewModel
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.UserUtil
import eramo.tahoon.util.showToast
import eramo.tahoon.util.state.UiState
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingFragment : Fragment(R.layout.fragment_on_boarding) {

    @Inject
    lateinit var slideAdapter: OnBoardingAdapter
    private lateinit var binding: FragmentOnBoardingBinding
    private val viewModel by viewModels<OnBoardingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LocalHelperUtil.loadLocal(requireActivity())
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOnBoardingBinding.bind(view)
//        addDots(0)
//        setupSlider()

        binding.apply {
            onBoardingBtnNext.setOnClickListener {
                if (binding.slider.currentItem < 2)
                    binding.slider.currentItem = binding.slider.currentItem + 1
                else navigateToMain()
            }

            onBoardingTvSkip.setOnClickListener { navigateToMain() }
        }
        fetchLatestDealsState()
        handleLoadingCancellation()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getOnBoarding()
    }

    private fun fetchLatestDealsState() {
        lifecycleScope.launchWhenStarted {
            viewModel.onBoardingStateStateDto.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        slideAdapter.setScreens(state.data ?: emptyList())

                        addDots(0, state.data?.size!!)
                        setupSlider(state.data.size)
                    }
                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
                    }
                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun handleLoadingCancellation() {
        LoadingDialog.cancelCurrentRequest.observe(viewLifecycleOwner) { isCancel ->
            if (isCancel) {
                viewModel.cancelRequest()
                LoadingDialog.dismissDialog()
                LoadingDialog.cancelCurrentRequest.value = false
            }
        }
    }

    private fun navigateToMain() {
        UserUtil.saveFirstTime()
        findNavController().navigate(
            R.id.mainFragment, null,
            NavOptions.Builder().setPopUpTo(R.id.onBoardingFragment, true).build()
        )
    }

    private fun setupSlider(numOfDots: Int) {
        binding.slider.apply {
            adapter = slideAdapter
            addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    addDots(position,numOfDots)
                    if (position == 2) {
                        binding.onBoardingBtnNext.text = getString(R.string.txt_get_start)
                        binding.onBoardingTvSkip.visibility = View.INVISIBLE
                    } else {
                        binding.onBoardingBtnNext.text = getString(R.string.next)
                        binding.onBoardingTvSkip.visibility = View.VISIBLE
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }
    }

    fun addDots(position: Int,numOfDots: Int) {
        lifecycleScope.launchWhenStarted {
            val dots = arrayOfNulls<TextView>(numOfDots)
            binding.dots.removeAllViews()
            for (i in dots.indices) {
                dots[i] = TextView(requireContext())
                dots[i]!!.text = Html.fromHtml("&#8226;")
                dots[i]!!.setTextColor(getColor(requireContext(), R.color.gray_low))
                dots[i]!!.textSize = 40f
                binding.dots.addView(dots[i])
            }
            dots[position]!!.setTextColor(resources.getColor(R.color.eramo_color))
        }
    }
}
