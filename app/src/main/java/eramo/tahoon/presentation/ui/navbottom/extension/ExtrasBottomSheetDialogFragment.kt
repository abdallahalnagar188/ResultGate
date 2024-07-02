package eramo.tahoon.presentation.ui.navbottom.extension

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.databinding.FragmentExtrasBottomSheetDialogBinding
import eramo.tahoon.domain.model.CartProductModel
import eramo.tahoon.domain.model.products.orders.ProductExtrasModel
import eramo.tahoon.presentation.adapters.recycleview.vertical.RvProductExtrasAdapter
import eramo.tahoon.presentation.ui.dialog.LoadingDialog
import eramo.tahoon.presentation.viewmodel.dialog.ExtrasBottomSheetDialogViewModel
import eramo.tahoon.util.parseErrorResponse
import eramo.tahoon.util.showToast
import eramo.tahoon.util.state.UiState
import javax.inject.Inject

@AndroidEntryPoint
class ExtrasBottomSheetDialogFragment(
    private val productId: String,
    private val productQuantity: String,
    private val extrasList: List<CartProductModel.ProductList.Extra?>
) : BottomSheetDialogFragment() {

    private var binding: FragmentExtrasBottomSheetDialogBinding? = null

    private val viewModel by viewModels<ExtrasBottomSheetDialogViewModel>()

    @Inject
    lateinit var rvProductExtrasAdapter: RvProductExtrasAdapter

    private lateinit var listener: ExtrasBottomSheetDialogOnClickListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentExtrasBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val parent = view.parent as View
//        val behavior = BottomSheetBehavior.from(parent)
//        behavior.state = BottomSheetBehavior.STATE_EXPANDED
//
//        val layout = dialog?.findViewById<CoordinatorLayout>(R.id.root_layout)
//        layout?.minimumHeight = Resources.getSystem().displayMetrics.heightPixels
//
//        val constraintLayout = dialog?.findViewById<ConstraintLayout>(R.id.constraint_layout)
//        constraintLayout?.minimumHeight = Resources.getSystem().displayMetrics.heightPixels


        setupViews()
        listeners()

        viewModel.getProductExtras(productId)
        fetchProductExtras()
    }

    private fun listeners() {
//        selectDefaultCountryAndCity()
        binding?.ivClose?.setOnClickListener{
            dialog?.dismiss()
        }
        binding?.btnDoneSelection?.setOnClickListener {
            dialog?.dismiss()
            listener.onExtrasDoneSelectionClick(productId, productQuantity, rvProductExtrasAdapter.getNewSelectedItems())
        }
    }

    private fun setupViews() {
        val dialog = dialog as BottomSheetDialog
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

    }

    private fun fetchProductExtras() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getProductExtrasState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            LoadingDialog.showDialog()
                        }

                        is UiState.Success -> {
                            LoadingDialog.dismissDialog()
//                            setupExtrasRv(state.data ?: emptyList(),extrasList ?: emptyList<CartProductModel.ProductList.Extras>())

                            rvProductExtrasAdapter.submitSelectedExtrasList(extrasList)

                            setupExtrasRv(state.data ?: emptyList())

//                            val list = mutableListOf<ProductExtrasModel>()
//
//                            list.add(
//                                ProductExtrasModel(1, 1000, "name")
//                            )
//                            list.add(
//                                ProductExtrasModel(1, 1000, "name")
//                            )
//                            list.add(
//                                ProductExtrasModel(1, 1000, "name")
//                            )
//                            list.add(
//                                ProductExtrasModel(1, 1000, "name")
//                            )
//                            list.add(
//                                ProductExtrasModel(1, 1000, "name")
//                            )
//                            list.add(
//                                ProductExtrasModel(1, 1000, "name")
//                            )
//                            list.add(
//                                ProductExtrasModel(1, 1000, "name")
//                            )
//
//                            list.add(
//                                ProductExtrasModel(1, 1000, "name")
//                            )
//                            list.add(
//                                ProductExtrasModel(1, 1000, "name")
//                            )
//                            list.add(
//                                ProductExtrasModel(1, 1000, "name")
//                            )
//                            list.add(
//                                ProductExtrasModel(1, 1000, "name")
//                            )
//                            list.add(
//                                ProductExtrasModel(1, 1000, "name")
//                            )
//                            list.add(
//                                ProductExtrasModel(1, 1000, "name")
//                            )
//
//                            setupExtrasRv(list ?: emptyList())
                        }

                        is UiState.Error -> {
                            LoadingDialog.dismissDialog()
                            try {
                                showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                            } catch (e: Exception) {
                                showToast(state.message!!.asString(requireContext()))
                            }
                        }

                        else -> {}
                    }

                }
            }
        }

    }

    private fun setupExtrasRv(data: List<ProductExtrasModel>) {
        binding?.rv?.adapter = rvProductExtrasAdapter
        rvProductExtrasAdapter.submitList(data)
    }


    fun setListener(listener: ExtrasBottomSheetDialogOnClickListener) {
        this.listener = listener
    }

    interface ExtrasBottomSheetDialogOnClickListener {
        fun onExtrasDoneSelectionClick(productId: String, productQuantity: String, hashMap: HashMap<String, String>)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}