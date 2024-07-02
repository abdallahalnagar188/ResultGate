package eramo.tahoon.presentation.ui.drawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.databinding.FragmentAboutUsBinding
import eramo.tahoon.presentation.ui.dialog.LoadingDialog
import eramo.tahoon.presentation.viewmodel.drawer.AboutUsViewModel
import eramo.tahoon.util.Constants
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.getYoutubeUrlId
import eramo.tahoon.util.htmlFormatToString
import eramo.tahoon.util.onBackPressed
import eramo.tahoon.util.showToast
import eramo.tahoon.util.state.UiState
import kotlinx.coroutines.delay

@AndroidEntryPoint
class AboutUsFragment : Fragment(R.layout.fragment_about_us) {

    private val viewModel by viewModels<AboutUsViewModel>()
    private lateinit var binding: FragmentAboutUsBinding
    private var videoId: String = "ZFzgCwmKFAc"

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
        binding = FragmentAboutUsBinding.bind(view)
        setupToolbar()

        fetchGetAppInfoState()
        handleLoadingCancellation()

        this@AboutUsFragment.onBackPressed { findNavController().popBackStack() }
    }

    private fun setupToolbar() {
        binding.apply {
            FAboutUsToolbar.setNavigationIcon(R.drawable.ic_arrow_left_yellow)
            FAboutUsToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun fetchGetAppInfoState() {
        lifecycleScope.launchWhenCreated {
            delay(Constants.ANIMATION_DELAY)
            viewModel.getAppInfoState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        val youtubeUrl = state.data?.get(0)?.videoLink
                        val aboutUs = htmlFormatToString(
                            state.data?.get(0)?.aboutUs.toString()
                        )

                        binding.tvAboutUs.text = aboutUs

                        youtubeUrl?.let {
                            videoId = getYoutubeUrlId(it).toString()
                            setupVideo()
                        }
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

    private fun setupVideo() {
        binding.apply {
            lifecycle.addObserver(FAboutUsYoutubeView)
            FAboutUsYoutubeView.addYouTubePlayerListener(object :
                AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    super.onReady(youTubePlayer)
                    onVideoId(youTubePlayer, videoId)
                    youTubePlayer.loadVideo(videoId, 0f)
                    youTubePlayer.play()
                }
            })
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
}