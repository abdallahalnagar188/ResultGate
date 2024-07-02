package eramo.resultgate.presentation.ui.drawer.myaccount.queries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.data.remote.dto.drawer.myaccount.MyQueriesResponse
import eramo.resultgate.databinding.FragmentQueryNewBinding
import eramo.resultgate.presentation.adapters.recycleview.vertical.QueryAdapter
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.viewmodel.drawer.myaccount.queries.QueryNewViewModel
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.navOptionsAnimation
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState
import javax.inject.Inject

@AndroidEntryPoint
class QueryNewFragment : Fragment(R.layout.fragment_query_new), QueryAdapter.OnItemClickListener {

    @Inject
    lateinit var queryAdapter: QueryAdapter
    private val viewModel by viewModels<QueryNewViewModel>()
    private lateinit var binding: FragmentQueryNewBinding

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
        binding = FragmentQueryNewBinding.bind(view)

        binding.apply {
            queryAdapter.setListener(this@QueryNewFragment)
            FQNewRv.adapter = queryAdapter
        }

        viewModel.getNewRequests()

        fetchGetRequestsState()
        handleLoadingCancellation()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getNewRequests()
    }

    private fun fetchGetRequestsState() {
        lifecycleScope.launchWhenCreated {
            viewModel.newRequestsDtoState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        queryAdapter.submitList(state.data)

                        if (state.data.isNullOrEmpty()) {
                            binding.lottieNoData.visibility = View.VISIBLE
                            binding.tvNoQueries.visibility = View.VISIBLE
                        } else {
                            binding.lottieNoData.visibility = View.GONE
                            binding.tvNoQueries.visibility = View.GONE
                        }
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        val message = state.message!!.asString(requireContext())
                        if (message.isNotBlank()) showToast(message)
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

    override fun onQueryClick(model: MyQueriesResponse.Data.MyQueryModel) {

        Navigation.findNavController(requireActivity(), R.id.bottom_navHost).navigate(
            R.id.queryDetailsFragment, QueryDetailsFragmentArgs(model.id.toString()).toBundle(),
            navOptionsAnimation()
        )

    }
}