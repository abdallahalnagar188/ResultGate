package eramo.tahoon.presentation.ui.auth

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.util.UserUtil
import kotlinx.coroutines.delay

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val videoView: VideoView = view.findViewById(R.id.videoView)
//        val video = Uri.parse("android.resource://${activity?.packageName}/${R.raw.intro}")
//        videoView.setVideoURI(video)

        lifecycleScope.launchWhenResumed {
//            videoView.start()
            if(!UserUtil.hasDeepLink()) delay(2000L)
            if(!UserUtil.hasDeepLink()) delay(1L)

            val shouldNavigateToMain = UserUtil.isRememberUser() || !UserUtil.isFirstTime()
            if (shouldNavigateToMain) {
                findNavController().navigate(
                    R.id.mainFragment, null,
                    NavOptions.Builder().setPopUpTo(R.id.splashFragment, true).build()
                )
            } else findNavController().navigate(
                R.id.onBoardingFragment, null,
                NavOptions.Builder().setPopUpTo(R.id.splashFragment, true).build()
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        UserUtil.setHasDeepLink(false)
    }
}