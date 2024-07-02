package eramo.tahoon.presentation.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.slider.LabelFormatter
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.databinding.FragmentFilterSubCategoryProductsDialogBinding
import eramo.tahoon.domain.model.FilterSubCategoryProductsObject
import eramo.tahoon.presentation.ui.navbottom.ShopFragmentArgs
import eramo.tahoon.presentation.viewmodel.SharedViewModel
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.UserUtil


@AndroidEntryPoint
class FilterSubCategoryProductsDialog : DialogFragment(R.layout.fragment_filter_sub_category_products_dialog) {

    private lateinit var binding: FragmentFilterSubCategoryProductsDialogBinding
    private val args by navArgs<FilterSubCategoryProductsDialogArgs>()
    private val categoryId get() = args.subCategoryId
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val brandId = UserUtil.getBrandId()
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
        binding = FragmentFilterSubCategoryProductsDialogBinding.bind(view)

        isCancelable = true

        setupRadioGroup()
        setupRangeSliderValues()

        setupSliderRange("1000000.0", "0")
//        if (MySingleton.filterSubCategoryProductsMaxPrice != "0.0") {
//            setupSliderRange(removePriceComma(MySingleton.filterSubCategoryProductsMaxPrice), "0")
//        } else {
//            setupSliderRange("1000000.0", "0")
//        }


        binding.btnFilter.setOnClickListener {
            findNavController().navigate(
                R.id.shopFragment, ShopFragmentArgs(
                    categoryId,
                    FilterSubCategoryProductsObject(categoryId,type!!, value!!, price_from!!, price_to!!)
                ).toBundle()
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
        binding.slider.setValues(0f, 10000f)
        binding.slider.valueTo = 10000f

        // Listener
        binding.slider.addOnChangeListener { slider, value, fromUser ->
            val values = binding.slider.values

            price_from = values[0].toString()
            price_to = values[1].toString()
        }
    }

    private fun setupSliderRange(maxPrice: String, minPrice: String) {
        if (maxPrice.isNotEmpty() && minPrice.isNotEmpty()) {
            binding.slider.apply {
                valueFrom = if (minPrice.toFloat() >= maxPrice.toFloat()) 0.0f
                else minPrice.toFloat()
                valueTo = maxPrice.toFloat()
                setValues(valueFrom, valueTo)
                labelBehavior = LabelFormatter.LABEL_VISIBLE
            }
        }
    }

}