package eramo.tahoon.presentation.ui.navbottom.extension

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.data.remote.dto.home.SubCategoryModel
import eramo.tahoon.databinding.FragmentSubCategoriesBinding
import eramo.tahoon.presentation.adapters.recycleview.vertical.SubCategoriesAdapter
import eramo.tahoon.presentation.ui.navbottom.ShopFragmentArgs
import eramo.tahoon.presentation.viewmodel.SharedViewModel
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.UserUtil
import eramo.tahoon.util.navOptionsAnimation
import eramo.tahoon.util.onBackPressed
import javax.inject.Inject

@AndroidEntryPoint
class SubCategoriesFragment : Fragment(R.layout.fragment_sub_categories),SubCategoriesAdapter.OnItemClickListener {

    private lateinit var binding: FragmentSubCategoriesBinding
    private val viewModelShared: SharedViewModel by activityViewModels()
    private val args by navArgs<SubCategoriesFragmentArgs>()
    val currentList get() = args.subCategoriesList.toMutableList()

    @Inject
    lateinit var subCategoriesAdapter: SubCategoriesAdapter

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
        binding = FragmentSubCategoriesBinding.bind(view)
        setupToolbar()

        subCategoriesAdapter.setListener(this)
        binding.apply {
            rvSubCategories.adapter = subCategoriesAdapter
            subCategoriesAdapter.submitList(currentList)

            setupNoDataAnimation()

            FAllCategoryEtSearch.addTextChangedListener { text ->
                if (text.toString().isEmpty()) {
                    subCategoriesAdapter.submitList(null)
                    subCategoriesAdapter.submitList(currentList)
                } else {
                    val list = currentList.filter {
                        it?.title?.lowercase()?.contains(text.toString().lowercase()) == true
                    }
                    subCategoriesAdapter.submitList(null)
                    subCategoriesAdapter.submitList(list)

                    setupNoDataAnimation()
                }
            }
        }

        this@SubCategoriesFragment.onBackPressed { findNavController().popBackStack() }
    }

    private fun setupNoDataAnimation(){
        if (currentList.isNullOrEmpty()){
            binding.rvSubCategories.visibility = View.INVISIBLE
            binding.lottieNoData.visibility = View.VISIBLE
        }else{
            binding.rvSubCategories.visibility = View.VISIBLE
            binding.lottieNoData.visibility = View.INVISIBLE
        }
    }

    private fun setupToolbar() {
        binding.apply {
            inTbLayout.toolbarIvMenu.setOnClickListener {
                viewModelShared.openDrawer.value = true
            }

            inTbLayout.toolbarInNotification.root.setOnClickListener {
                if (UserUtil.isUserLogin())
                    findNavController().navigate(
                        R.id.notificationFragment,
                        null,
                        navOptionsAnimation()
                    )
                else findNavController().navigate(R.id.loginDialog)
            }

            inTbLayout.toolbarIvProfile.setOnClickListener {
                if (UserUtil.isUserLogin())
                    findNavController().navigate(
                        R.id.myAccountFragment,
                        null,
                        navOptionsAnimation()
                    )
                else findNavController().navigate(R.id.loginDialog)
            }

            if (UserUtil.isUserLogin()) {
                Glide.with(requireContext())
                    .load(UserUtil.getUserProfileImageUrl())
                    .into(inTbLayout.toolbarIvProfile)
            }
        }
    }

    override fun onSubCategoryClick(model: SubCategoryModel) {
        findNavController().navigate(
            R.id.shopFragment,ShopFragmentArgs(model.id.toString()).toBundle(),
            navOptionsAnimation()
        )
    }
}