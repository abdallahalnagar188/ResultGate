package eramo.resultgate.presentation.ui.teams

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.databinding.FragmentMainBinding
import eramo.resultgate.databinding.FragmentTeamsBinding
import eramo.resultgate.util.LocalHelperUtil

@AndroidEntryPoint
class TeamsFragment : Fragment() {
    private lateinit var binding: FragmentTeamsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        LocalHelperUtil.loadLocal(requireActivity())
        binding = FragmentTeamsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindTabs()
        tabsClickListeners()
    }
    private fun bindTabs() {
        val allTab = binding.tabLayout.newTab()
        allTab.text = getText(R.string.all_teamss)
        allTab.id = 0
        binding.tabLayout.addTab(allTab)

        val myTab = binding.tabLayout.newTab()
        myTab.text = getText(R.string.my_teams)
        myTab.id = 1
        binding.tabLayout.addTab(myTab)

        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
//            p.setMargins(0, 0, 50, 0)
            p.marginEnd = 25
            tab.requestLayout()
        }
        binding.tabLayout.getTabAt(0)?.select()

    }
    private fun tabsClickListeners() {
        binding.tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val tabIndex = tab?.position
                    if (tabIndex != null) {
                        binding.tabLayout.setScrollPosition(tabIndex, 0f, true)
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }
            }
        )
    }


}