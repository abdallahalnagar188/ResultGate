package eramo.resultgate.presentation.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.databinding.FragmentSortSearchResultDialogBinding
import eramo.resultgate.domain.model.SortSearchResultObject
import eramo.resultgate.presentation.ui.navbottom.extension.SearchFragmentArgs
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.navOptionsAnimation


@AndroidEntryPoint
class SortSearchResultDialogFragment : DialogFragment(R.layout.fragment_sort_search_result_dialog) {

    private lateinit var binding: FragmentSortSearchResultDialogBinding
    private val args by navArgs<SortSearchResultDialogFragmentArgs>()
    private val searchTerm get() = args.searchTerm
    private val categoryId get() = args.categoryId

    private var type: String? = ""
    private var value: String? = ""
    private var price_from: String? = "0.0"
    private var price_to: String? = "1000000.0"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
        LocalHelperUtil.loadLocal(requireActivity())
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSortSearchResultDialogBinding.bind(view)

        isCancelable = true

        setupRadioGroup()
        setupRangeSliderValues()

        binding.btnFilter.setOnClickListener {
//            findNavController().navigate(
//                R.id.shopFragment, ShopFragmentArgs(
//                    categoryId,
//                    FilterSubCategoryProductsObject(categoryId,type!!, value!!, price_from!!, price_to!!)
//                ).toBundle()
//            )

            findNavController().navigate(
                R.id.searchFragment, SearchFragmentArgs(
                    searchTerm,
                    null, SortSearchResultObject(searchTerm, type!!, value!!, price_from!!, price_to!!),categoryId
                ).toBundle(), navOptionsAnimation()
            )
            dismiss()
        }
    }

    private fun setupRadioGroup() {
        binding.radioGroup.setOnCheckedChangeListener { _, optionId ->
            when (optionId) {
                R.id.rbLowPrice -> {
                    type = "price"
                    value = "asc"
                }

                R.id.rbHighPrice -> {
                    type = "price"
                    value = "desc"
                }

                R.id.rbLatest -> {
                    type = "date"
                    value = "desc"
//                    value = "asc"

                }

                R.id.rbOldest -> {
                    type = "date"
                    value = "asc"
//                    value = "desc"

                }

                else -> {
                }
            }
        }
    }

    private fun setupRangeSliderValues() {
//        binding.slider.setValues(0f, 10000f)
//        binding.slider.valueTo = 10000f

        binding.slider.setValues(0f, 1000000f)
        binding.slider.valueTo = 1000000f

        // Listener
        binding.slider.addOnChangeListener { slider, value, fromUser ->
            val values = binding.slider.values

            price_from = values[0].toString()
            price_to = values[1].toString()
        }
    }

}