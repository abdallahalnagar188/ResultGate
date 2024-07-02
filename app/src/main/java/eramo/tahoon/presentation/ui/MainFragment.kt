package eramo.tahoon.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import eramo.tahoon.R
import eramo.tahoon.data.remote.dto.general.Member
import eramo.tahoon.databinding.FragmentMainBinding
import eramo.tahoon.presentation.viewmodel.SharedViewModel
import eramo.tahoon.util.Constants
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.UserUtil
import eramo.tahoon.util.hideSoftKeyboard
import eramo.tahoon.util.isFragmentExist
import eramo.tahoon.util.navOptionsAnimation
import eramo.tahoon.util.uncheckBottomNavSelection
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var binding: FragmentMainBinding
    private lateinit var mainNavController: NavController
    private lateinit var bottomNavController: NavController
    private val viewModelShared: SharedViewModel by activityViewModels()

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
        binding = FragmentMainBinding.bind(view)

        mainNavController = Navigation.findNavController(requireActivity(), R.id.main_navHost)
        bottomNavController = Navigation.findNavController(requireActivity(), R.id.bottom_navHost)

        binding.apply {
            mainBn.background = null
            mainBn.menu.getItem(2).isEnabled = false
            mainBn.setupWithNavController(bottomNavController)

            viewModelShared.openDrawer.observe(viewLifecycleOwner) { isOpen ->
                if (isOpen) {
                    requireActivity().hideSoftKeyboard()
                    mainDrawerLayout.openDrawer(GravityCompat.START)
                    viewModelShared.openDrawer.value = false
                }
            }

            viewModelShared.profileData.observe(viewLifecycleOwner) { member ->
                if (member != null) {
                    setupProfile(member)
                } else {
                    binding.inDrawer.apply {
                        navHeaderTvName.text = getString(R.string.guest_mode)
                        Glide.with(requireContext())
                            .load(R.drawable.pic_user_placeholder)
                            .into(navHeaderIvProfile)
                    }
                }
            }

        }
        setupCartCount()
        setupNotificationsCount()
        setupDrawer()
        setupNavBottomSelection()
        setupNavBottomVisibility()
    }

    private fun setupProfile(memberModel: Member) {
        binding.inDrawer.apply {
//            UserUtil.saveUserProfile(EramoApi.IMAGE_URL_PROFILE + memberModel.mImage!!)
            UserUtil.saveUserProfile(memberModel.imageUrl!!)
            navHeaderTvName.text = memberModel.name
            Glide.with(requireContext())
//                .load(EramoApi.IMAGE_URL_PROFILE + memberModel.mImage)
                .load(memberModel.imageUrl)
                .into(navHeaderIvProfile)
        }
    }

    private fun setupCartCount() {
        viewModelShared.cartCount.observe(viewLifecycleOwner) { count ->

            binding.mainBn.getOrCreateBadge(R.id.cartFragment).apply {
                number = count ?: 0
                verticalOffset = 8
                horizontalOffset = 8
                backgroundColor = ContextCompat.getColor(requireContext(), R.color.eramo_color)
            }
        }
    }

    private fun setupNotificationsCount() {
        viewModelShared.notificationsCount.observe(viewLifecycleOwner) { count ->

            binding.mainBn.getOrCreateBadge(R.id.notificationFragment).apply {
                number = count ?: 0
                verticalOffset = 8
                horizontalOffset = 8
                backgroundColor = ContextCompat.getColor(requireContext(), R.color.eramo_color)
            }
        }
    }

    fun englishNumberToArabicNumber(number: Int): String {
        val arabicNumber = mutableListOf<String>()
        for (element in number.toString()) {
            when (element) {
                '1' -> arabicNumber.add("١")
                '2' -> arabicNumber.add("٢")
                '3' -> arabicNumber.add("٣")
                '4' -> arabicNumber.add("٤")
                '5' -> arabicNumber.add("٥")
                '6' -> arabicNumber.add("٦")
                '7' -> arabicNumber.add("٧")
                '8' -> arabicNumber.add("٨")
                '9' -> arabicNumber.add("٩")
                else -> arabicNumber.add("٠")
            }
        }
        return arabicNumber.toString()
            .replace("[", "")
            .replace("]", "")
            .replace(",", "")
            .replace(" ", "")


    }

    private fun setupDrawer() {
        binding.inDrawer.apply {
            Constants.setupLangChooser(
                requireActivity(),
                layoutLangIvFlag,
                layoutLangCvHeader,
                layoutLangCvBody,
                layoutLangArrow,
                layoutLangIvCheckEn,
                layoutLangIvCheckAr,
                layoutLangLinChoiceEn,
                layoutLangLinChoiceAr
            )

            navHeaderTvHome.setOnClickListener {
                bottomNavController.navigate(R.id.homeFragment)

                binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
            }

            navHeaderTvMyAccount.setOnClickListener {
                if (UserUtil.isUserLogin()) bottomNavController.navigate(R.id.myAccountFragment)
                else bottomNavController.navigate(R.id.loginDialog)

                binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
            }

            navHeaderTvAllCategories.setOnClickListener {
                bottomNavController.navigate(R.id.allCategoryFragment)

                binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
            }

            navHeaderTvMyFavourite.setOnClickListener {
                bottomNavController.navigate(R.id.favouriteFragment)
//                if (UserUtil.isUserLogin()) bottomNavController.navigate(R.id.favouriteFragment)
//                else bottomNavController.navigate(R.id.loginDialog)

                binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
            }

            navHeaderTvMyOrders.setOnClickListener {
                if (UserUtil.isUserLogin()) bottomNavController.navigate(R.id.myOrdersFragment)
                else bottomNavController.navigate(R.id.loginDialog)

                binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
            }

            navHeaderTvMyQueries.setOnClickListener {
                if (UserUtil.isUserLogin()) bottomNavController.navigate(R.id.myQueriesFragment)
                else bottomNavController.navigate(R.id.loginDialog)

//                bottomNavController.navigate(R.id.myQueriesFragment)

                binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
            }

            navHeaderTvAbout.setOnClickListener {
                bottomNavController.navigate(R.id.aboutUsFragment)

                binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
            }

            navHeaderTvContact.setOnClickListener {
                if (UserUtil.isUserLogin()) bottomNavController.navigate(R.id.contactUsFragment)
                else bottomNavController.navigate(R.id.loginDialog)

                binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
            }

            navHeaderTvFaq.setOnClickListener {
                if (UserUtil.isUserLogin()) bottomNavController.navigate(R.id.questionDetailsFragment)
                else bottomNavController.navigate(R.id.loginDialog)

                binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
            }

            navHeaderTvTerms.setOnClickListener {
                mainNavController.navigate(R.id.policyFragment)

                binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
            }

            navHeaderTvLogout.text = if (UserUtil.isUserLogin()) getString(R.string.logout)
            else getString(R.string.log_in_register)

            navHeaderTvLogout.setOnClickListener {
                if (UserUtil.isUserLogin()) {
                    UserUtil.clearUserInfo()
                    UserUtil.isRememberUser()
                    mainNavController.navigate(
                        R.id.loginFragment, null,
                        NavOptions.Builder().setPopUpTo(R.id.mainFragment, true).build()
                    )

                    viewModelShared.profileData.value = null

                } else {
                    mainNavController.navigate(R.id.loginFragment)
                }

                binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
            }


            navHeaderLinEramo.setOnClickListener {
                bottomNavController.navigate(
                    R.id.ERAMODialog, null,
                    navOptionsAnimation()
                )

                binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
            }

            navHeaderIvBack.setOnClickListener {
                binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
            }
        }
    }

    private fun setupNavBottomSelection() {
        binding.apply {
            mainFabHome.setOnClickListener { setCurrentDestination(R.id.homeFragment) }
            mainBn.setOnItemSelectedListener { item ->
                when (item.itemId) {
//                    R.id.shopFragment -> setCurrentDestination(R.id.shopFragment)
                    R.id.allCategoryFragment -> setCurrentDestination(R.id.allCategoryFragment)
                    R.id.allStoresFragment-> setCurrentDestination(R.id.allStoresFragment)

                    R.id.notificationFragment -> setCurrentDestination(R.id.notificationFragment)
                    R.id.cartFragment -> setCurrentDestination(R.id.cartFragment)
                }
                true
            }

            uncheckBottomNavSelectionOnAllStoresDestination()
        }
    }

    private fun setCurrentDestination(destinationId: Int) {
        if (bottomNavController.isFragmentExist(destinationId))
            bottomNavController.popBackStack(destinationId, false)
        else bottomNavController.navigate(destinationId)
    }

    private fun uncheckBottomNavSelectionOnAllStoresDestination() {
        bottomNavController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.allStoresFragment -> binding.mainBn.uncheckBottomNavSelection()
            }
        }
    }

    private fun setupNavBottomVisibility() {
        bottomNavController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {

                R.id.loginDialog,
                R.id.sliderZoomFragment,
                R.id.orderDetailsFragment,
                R.id.myOrdersFragment,
                R.id.myOrdersAfterCheckoutFragment,
                R.id.changePasswordFragment,
                R.id.editPersonalDetailsFragment,
                R.id.myAddressesFragment,
                R.id.myAccountFragment,
                R.id.myQueriesFragment,
                R.id.queryDetailsFragment,
                R.id.questionDetailsFragment,
                R.id.contactUsFragment,
                R.id.aboutUsFragment,
                R.id.promoCodeDialog,
                R.id.cancelDialog,
                R.id.suspendAccountDialog,
                R.id.checkoutStepTwoFragment,
                R.id.checkoutStepThreeFragment,
                R.id.checkoutStepOneFragment -> {
                    binding.apply {
                        mainBottomAppBar.visibility = View.GONE
                        mainFabHome.visibility = View.GONE
                    }
                }

                else -> {
                    lifecycleScope.launchWhenResumed {
                        delay(Constants.ANIMATION_DELAY)
                        binding.apply {
                            mainBottomAppBar.visibility = View.VISIBLE
                            mainFabHome.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }
}