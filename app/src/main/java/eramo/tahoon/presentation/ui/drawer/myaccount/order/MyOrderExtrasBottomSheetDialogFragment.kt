package eramo.tahoon.presentation.ui.drawer.myaccount.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.data.remote.dto.drawer.myaccount.OrderDetailsResponse
import eramo.tahoon.databinding.FragmentMyOrderExtrasBottomSheetDialogBinding
import eramo.tahoon.presentation.adapters.recycleview.vertical.RvMyOrderExtrasAdapter
import javax.inject.Inject

@AndroidEntryPoint
class MyOrderExtrasBottomSheetDialogFragment(
    private val extrasList: List<OrderDetailsResponse.Data.Product.Extra?>
) : BottomSheetDialogFragment() {

    private var binding: FragmentMyOrderExtrasBottomSheetDialogBinding? = null

    @Inject
    lateinit var rvMyOrderExtrasAdapter: RvMyOrderExtrasAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMyOrderExtrasBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        listeners()
    }

    private fun listeners() {
        binding?.ivClose?.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun setupViews() {
        val dialog = dialog as BottomSheetDialog
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        setupExtrasRv()
    }

    private fun setupExtrasRv() {
        binding?.rv?.adapter = rvMyOrderExtrasAdapter
        rvMyOrderExtrasAdapter.submitList(extrasList)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}