package eramo.resultgate.presentation.ui.drawer.myaccount.queries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.databinding.FragmentMyQueriesBinding
import eramo.resultgate.presentation.adapters.viewpager.QueriesPagerAdapter
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.navOptionsAnimation
import eramo.resultgate.util.onBackPressed

@AndroidEntryPoint
class MyQueriesFragment : Fragment(R.layout.fragment_my_queries) {

    private lateinit var binding: FragmentMyQueriesBinding

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
        binding = FragmentMyQueriesBinding.bind(view)
        setupToolbar()

        val queriesPagerAdapter =
            QueriesPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        binding.apply {
            FQueryViewPager.adapter = queriesPagerAdapter
            TabLayoutMediator(FQueryTabLayout, FQueryViewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.new_queries)
                    1 -> tab.text = getString(R.string.replied_queries)
                    2 -> tab.text = getString(R.string.cancelled_queries)
                }
            }.attach()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.questionDetailsFragment, null, navOptionsAnimation())
        }

        this@MyQueriesFragment.onBackPressed {
//            findNavController().popBackStack()
            findNavController().navigate(R.id.homeFragment)
        }
    }

    private fun setupToolbar() {
        binding.apply {
            FQueryToolbar.setNavigationIcon(R.drawable.ic_arrow_left_yellow)
            FQueryToolbar.setNavigationOnClickListener {
//                findNavController().popBackStack()
                findNavController().navigate(R.id.homeFragment)
            }
        }
    }
}