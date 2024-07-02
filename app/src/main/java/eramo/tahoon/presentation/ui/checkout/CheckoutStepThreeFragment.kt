package eramo.tahoon.presentation.ui.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.databinding.FragmentCheckoutStepThreeBinding
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.navOptionsAnimation
import eramo.tahoon.util.onBackPressed
import kotlinx.coroutines.delay

@AndroidEntryPoint
class CheckoutStepThreeFragment : Fragment(R.layout.fragment_checkout_step_three) {

    private lateinit var binding: FragmentCheckoutStepThreeBinding

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
        binding = FragmentCheckoutStepThreeBinding.bind(view)
        setupToolbar()

        lifecycleScope.launchWhenStarted {
            delay(2500L)
            findNavController().popBackStack(findNavController().graph.startDestinationId, false)
            findNavController().navigate(R.id.myOrdersAfterCheckoutFragment, null, navOptionsAnimation())
        }

        this@CheckoutStepThreeFragment.onBackPressed { }
    }

    private fun setupToolbar() {
        binding.apply {
//            FCheckoutInToolbar.toolbar.setNavigationIcon(R.drawable.ic_arrow_left_yellow)
//            FCheckoutInToolbar.toolbar.setNavigationOnClickListener {
//                findNavController().popBackStack()
//            }

            FCheckoutInToolbar.ivSearch.visibility = View.INVISIBLE
            FCheckoutInToolbar.ivShop.visibility = View.INVISIBLE
        }
    }
}