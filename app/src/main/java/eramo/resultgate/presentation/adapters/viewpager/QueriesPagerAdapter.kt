package eramo.resultgate.presentation.adapters.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import eramo.resultgate.presentation.ui.drawer.myaccount.queries.QueryCancelledFragment
import eramo.resultgate.presentation.ui.drawer.myaccount.queries.QueryNewFragment
import eramo.resultgate.presentation.ui.drawer.myaccount.queries.QueryRepliedFragment

class QueriesPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> QueryRepliedFragment()
            2 -> QueryCancelledFragment()
            else -> QueryNewFragment()
        }
    }
}