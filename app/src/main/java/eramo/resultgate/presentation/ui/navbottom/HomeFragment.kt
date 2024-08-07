package eramo.resultgate.presentation.ui.navbottom

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import eramo.resultgate.R
import eramo.resultgate.data.local.entity.MyFavouriteEntity
import eramo.resultgate.data.remote.dto.home.HomeBestCategoriesResponse
import eramo.resultgate.data.remote.dto.home.HomeBootomSectionsResponse
import eramo.resultgate.data.remote.dto.home.HomePageSliderResponse
import eramo.resultgate.databinding.FragmentHomeBinding
import eramo.resultgate.domain.model.OffersModel
import eramo.resultgate.domain.model.home.HomeBrandsModel
import eramo.resultgate.domain.model.products.MyProductModel
import eramo.resultgate.presentation.adapters.BannerSliderAdapter
import eramo.resultgate.presentation.adapters.OffersSliderAdapter
import eramo.resultgate.presentation.adapters.recycleview.horizontal.FeaturedAdapter
import eramo.resultgate.presentation.adapters.recycleview.horizontal.HomeBrandsAdapter
import eramo.resultgate.presentation.adapters.recycleview.horizontal.PagerSliderAdapter
import eramo.resultgate.presentation.adapters.recycleview.horizontal.ProductAdapter
import eramo.resultgate.presentation.adapters.recycleview.vertical.DealAdapter
import eramo.resultgate.presentation.adapters.recycleview.vertical.MostSaleAdapter
import eramo.resultgate.presentation.adapters.recycleview.vertical.MostViewedAdapter
import eramo.resultgate.presentation.adapters.recycleview.vertical.RvHomeRecentCategoriesAdapter
import eramo.resultgate.presentation.adapters.recycleview.vertical.RvSecFourAdapter
import eramo.resultgate.presentation.adapters.recycleview.vertical.RvSecOneAdapter
import eramo.resultgate.presentation.adapters.recycleview.vertical.RvSecThreeAdapter
import eramo.resultgate.presentation.adapters.recycleview.vertical.RvSecTwoAdapter
import eramo.resultgate.presentation.ui.dialog.LoadingDialog
import eramo.resultgate.presentation.ui.drawer.myaccount.order.OrderDetailsFragmentArgs
import eramo.resultgate.presentation.ui.navbottom.extension.AllCategoryFragmentArgs
import eramo.resultgate.presentation.ui.navbottom.extension.NotificationInfoFragmentArgs
import eramo.resultgate.presentation.ui.navbottom.extension.ProductDetailsFragmentArgs
import eramo.resultgate.presentation.ui.navbottom.extension.SearchFragmentArgs
import eramo.resultgate.presentation.ui.navbottom.extension.SectionsProductsFragmentArgs
import eramo.resultgate.presentation.ui.navbottom.extension.SubCategoriesFragmentArgs
import eramo.resultgate.presentation.viewmodel.SharedViewModel
import eramo.resultgate.presentation.viewmodel.navbottom.HomeViewModel
import eramo.resultgate.util.Constants
import eramo.resultgate.util.Constants.TAG
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.MySingleton
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.navOptionsAnimation
import eramo.resultgate.util.notification.FirebaseMessageReceiver
import eramo.resultgate.util.notification.FirebaseMessageReceiver.Companion.sharedPref
import eramo.resultgate.util.parseErrorResponse
import eramo.resultgate.util.showToast
import eramo.resultgate.util.state.UiState
import me.leolin.shortcutbadger.ShortcutBadger
import org.json.JSONObject
import javax.inject.Inject
import kotlin.math.abs


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home),
    ProductAdapter.OnItemClickListener,
    DealAdapter.OnItemClickListener,
    BannerSliderAdapter.OnItemClickListener,
    FeaturedAdapter.OnItemClickListener,
    RvHomeRecentCategoriesAdapter.OnItemClickListener,
    HomeBrandsAdapter.OnItemClickListener,
    PagerSliderAdapter.OnItemClickListener,
    MostSaleAdapter.OnItemClickListener,
    MostViewedAdapter.OnItemClickListener,
    RvSecOneAdapter.OnItemClickListener,
    RvSecTwoAdapter.OnItemClickListener,
    RvSecThreeAdapter.OnItemClickListener,
    RvSecFourAdapter.OnItemClickListener {

    @Inject
    lateinit var productAdapter: ProductAdapter

    @Inject
    lateinit var dealAdapter: DealAdapter

    @Inject
    lateinit var bannerSliderAdapter: BannerSliderAdapter

    @Inject
    lateinit var offersSliderAdapter: OffersSliderAdapter

//    @Inject
//    lateinit var categoryAdapter: CategoryAdapter

    @Inject
    lateinit var brandsAdapter: HomeBrandsAdapter

    @Inject
    lateinit var featuredAdapter: FeaturedAdapter

    @Inject
    lateinit var rvHomeRecentCategoriesAdapter: RvHomeRecentCategoriesAdapter

    @Inject
    lateinit var mostViewedAdapter: MostViewedAdapter

    @Inject
    lateinit var rvSecOneAdapter: RvSecOneAdapter

    @Inject
    lateinit var rvSecTwoAdapter: RvSecTwoAdapter

    @Inject
    lateinit var rvSecThreeAdapter: RvSecThreeAdapter

    @Inject
    lateinit var rvSecFourAdapter: RvSecFourAdapter

    @Inject
    lateinit var mostSaleAdapter: MostSaleAdapter

    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<HomeViewModel>()
    private val viewModelShared: SharedViewModel by activityViewModels()
    private var isFavPressedProduct = false
    private var isFavPressedDeal = false
    private var isFavPressedFeatured = false

    // counter down for deals
    private var timer: CountDownTimer? = null

    // bottom slider for offers
    private val sliderHandler: Handler = Handler()
    private lateinit var sliderRun: Runnable

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it[Manifest.permission.POST_NOTIFICATIONS] == false) {
                showToast(getString(R.string.notifications_is_denied))
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LocalHelperUtil.loadLocal(requireActivity())
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        setupToolbar()
        setupFeaturedRvWithDots()
        setupOffersSlider()

        requestNotificationPermission()

        dealAdapter.setListener(this)
        productAdapter.setListener(this)
        bannerSliderAdapter.setListener(this)
        brandsAdapter.setListener(this)
        featuredAdapter.setListener(this)
        rvHomeRecentCategoriesAdapter.setListener(this)
        mostViewedAdapter.setListener(this)
        mostSaleAdapter.setListener(this)

        rvSecOneAdapter.setListener(this)
        rvSecTwoAdapter.setListener(this)
        rvSecThreeAdapter.setListener(this)
        rvSecFourAdapter.setListener(this)

        binding.root.setOnRefreshListener {
            callHomeApis()

            if (UserUtil.isUserLogin()) {
                viewModel.getProfile()
                viewModel.getNotificationCount()
            }
        }

        binding.apply {
            FHomeIvFilter.requestFocus()
            FHomeTvCategory.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            FHomeTvFeatured.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            FHomeTvLatestDeals.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            FHomeTvLatestProducts.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            FHomeTvSpecialOffers.paintFlags = Paint.UNDERLINE_TEXT_FLAG

            FHomeTvMostViewed.paintFlags = Paint.UNDERLINE_TEXT_FLAG

            FHomeTvRecentCategories.paintFlags = Paint.UNDERLINE_TEXT_FLAG

            FHomeTvSecOne.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            FHomeTvSecTwo.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            FHomeTvSecThree.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            FHomeTvSecFour.paintFlags = Paint.UNDERLINE_TEXT_FLAG

            FHomeTvMostSale.paintFlags = Paint.UNDERLINE_TEXT_FLAG

            FHomeRvDeals.adapter = dealAdapter
            FHomeRvProducts.adapter = productAdapter
            FHomeRvBrands.adapter = brandsAdapter
            FHomeRvFeatured.adapter = featuredAdapter
            FHomeRvMostViewed.adapter = mostViewedAdapter

            rvRecentCategories.adapter = rvHomeRecentCategoriesAdapter

            FHomeRvSecOne.adapter = rvSecOneAdapter
            FHomeRvSecTwo.adapter = rvSecTwoAdapter
            FHomeRvSecThree.adapter = rvSecThreeAdapter
            FHomeRvSecFour.adapter = rvSecFourAdapter

            FHomeRvMostSale.adapter = mostSaleAdapter

//            val filterCitiesDialogFragment = FilterCitiesDialogFragment()
            inTbLayout.inCityFilter.root.setOnClickListener {
//                filterCitiesDialogFragment.setListener(this@HomeFragment)
//                filterCitiesDialogFragment.show(
//                    activity?.supportFragmentManager!!,
//                    "FilterCitiesDialogFragment"
//                )
                findNavController().navigate(R.id.filterCitiesDialogFragment2)
            }

            FHomeBtnCheckAll.setOnClickListener {
//                findNavController().navigate(R.id.shopFragment)
                findNavController().navigate(
                    R.id.sectionsProductsFragment,
                    SectionsProductsFragmentArgs(getString(R.string.latest_products)).toBundle(),
                    navOptionsAnimation()
                )
            }

            FHomeBtnBrowseAll.setOnClickListener {
//                findNavController().navigate(R.id.allCategoryFragment, null, navOptionsAnimation())
                findNavController().navigate(R.id.allStoresFragment, null, navOptionsAnimation())
            }

            /////////
            FHomeTvLatestDeals.setOnClickListener {
                findNavController().navigate(
                    R.id.sectionsProductsFragment,
                    SectionsProductsFragmentArgs(getString(R.string.latest_deals)).toBundle(),
                    navOptionsAnimation()
                )
            }

            FHomeTvLatestProducts.setOnClickListener {
                findNavController().navigate(
                    R.id.sectionsProductsFragment,
                    SectionsProductsFragmentArgs(getString(R.string.latest_products)).toBundle(),
                    navOptionsAnimation()
                )
            }

            FHomeTvMostViewed.setOnClickListener {
                findNavController().navigate(
                    R.id.sectionsProductsFragment,
                    SectionsProductsFragmentArgs(getString(R.string.most_viewed_products)).toBundle(),
                    navOptionsAnimation()
                )
            }


            FHomeTvSecOne.setOnClickListener {
                findNavController().navigate(
                    R.id.sectionsProductsFragment,
                    SectionsProductsFragmentArgs("first section").toBundle(),
                    navOptionsAnimation()
                )
            }

            FHomeTvSecTwo.setOnClickListener {
                findNavController().navigate(
                    R.id.sectionsProductsFragment,
                    SectionsProductsFragmentArgs("second section").toBundle(),
                    navOptionsAnimation()
                )
            }

            FHomeTvSecThree.setOnClickListener {
                findNavController().navigate(
                    R.id.sectionsProductsFragment,
                    SectionsProductsFragmentArgs("third section").toBundle(),
                    navOptionsAnimation()
                )
            }

            FHomeTvSecFour.setOnClickListener {
                findNavController().navigate(
                    R.id.sectionsProductsFragment,
                    SectionsProductsFragmentArgs("fourth section").toBundle(),
                    navOptionsAnimation()
                )
            }

            FHomeTvMostSale.setOnClickListener {
                findNavController().navigate(
                    R.id.sectionsProductsFragment,
                    SectionsProductsFragmentArgs(getString(R.string.most_sale_products)).toBundle(),
                    navOptionsAnimation()
                )
            }
            //////////

            FHomeIvFilter.setOnClickListener {
                findNavController().navigate(R.id.filterFragment, null, navOptionsAnimation())
            }

            FHomeEtSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = FHomeEtSearch.text.toString().trim()
                    if (query.isEmpty()) showToast(getString(R.string.enter_a_query))
                    else findNavController().navigate(
                        R.id.searchFragment,
                        SearchFragmentArgs(searchModel = null, searchTitle = query).toBundle(),
                        navOptionsAnimation()
                    )
                }
                true
            }

            ivSearch.setOnClickListener {
                val query = FHomeEtSearch.text.toString().trim()
                if (query.isEmpty()) {
                    showToast(getString(R.string.enter_a_query))
                } else {
                    findNavController().navigate(
                        R.id.searchFragment,
                        SearchFragmentArgs(searchModel = null, searchTitle = query).toBundle(),
                        navOptionsAnimation()
                    )
                }
            }
        }

        // Request
        callHomeApis()

        // Fetch
        fetchCounterValueState()

        fetchLatestDealsState()
        fetchHomeCounterState()
        fetchLatestProductsState()
        fetchMostViewedState()
        fetchBestCategoriesState()
        fetchBottomSectionsState()
        fetchMostSaleState()
        fetchFeaturedProductsState()
        fetchBrandsState()
        fetchFilterByCityEvent()
//        fetchCategoriesState()
        fetchAddFavouriteState()
        fetchRemoveFavouriteState()
        fetchProfileState()
        fetchFirebaseTokenState()
        fetchHomeTopSliderState()
        fetchHomeOffersState()
        fetchCartCountState()
        fetchNotificationCount()

        fetchAddRemoveItemWishlistAndRefreshState()

        fetchAddItemsListToWishlistState()

        fetchGetFavouriteListDBState()
        fetchAdRemoveItemWishlistDBState()
        handleLoadingCancellation()
        handleBackPressed()

        handelFCMInBackground()
        Log.e("token", UserUtil.getUserToken())
    }

    private fun handelFCMInBackground() {
        val bundle: Bundle? = activity?.intent?.extras
        if (bundle != null) {
            val keys = bundle.keySet()
            val it: Iterator<String> = keys.iterator()
//            Log.e("intent", "Dumping Intent start")
            while (it.hasNext()) {
                val key = it.next()
////                Log.e("intent", "[" + key + "=" + bundle[key] + "]")
            }
//            Log.e("intent", "Dumping Intent end")
        }
        bundle?.let {

            val orderNumber = activity?.intent?.getStringExtra("order_number")
            val notificationIdd = activity?.intent?.getStringExtra("notification_idd")

            if (orderNumber != "" && orderNumber != null) {

                findNavController().navigate(
                    R.id.orderDetailsFragment, OrderDetailsFragmentArgs(orderId = orderNumber).toBundle(),
                    navOptionsAnimation()
                )

                activity?.intent = null
            } else if (notificationIdd != "" && notificationIdd != null) {

                findNavController().navigate(
                    R.id.notificationInfoFragment, NotificationInfoFragmentArgs(notificationIdd).toBundle(),
                    navOptionsAnimation()
                )

                activity?.intent = null
            }

        }
    }

    override fun onStart() {
        super.onStart()

        if (UserUtil.isUserLogin()) {
            switchOfflineCart()
        }
        binding.FHomeEtSearch.setText("")
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRun, 2000)

        binding.inTbLayout.inCityFilter.root.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
        timer = null
        sliderHandler.removeCallbacks(sliderRun)

        binding.inTbLayout.inCityFilter.root.visibility = View.INVISIBLE
    }

    private fun setupFilterCity() {
        if (UserUtil.getCityFiltrationId() != "-1") {
            if (LocalHelperUtil.isEnglish()) {
                binding.inTbLayout.inCityFilter.tvCity.text = UserUtil.getCityFiltrationTitleEn()
            } else {
                binding.inTbLayout.inCityFilter.tvCity.text = UserUtil.getCityFiltrationTitleAr()
            }
        } else {
            binding.inTbLayout.inCityFilter.tvCity.text = getString(R.string.select_region)
        }
    }

    private fun setupToolbar() {
        binding.apply {
            setupFilterCity()

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

    private fun callHomeApis() {
        viewModel.getHomeTopSlider()
////        viewModel.getHomeCategories()
        viewModel.getHomeBrands()
        viewModel.getFeaturedProducts()
        viewModel.latestDeals()
        viewModel.getLatestProducts()
        viewModel.getHomeOffers()
        viewModel.getCartCount()

        viewModel.mostViewed()

        viewModel.bestCategories()

        viewModel.bottomSections()

        viewModel.mostSale()


        if (UserUtil.isUserLogin()) {
            viewModel.getProfile()
            viewModel.getNotificationCount()
        }

        viewModel.getFavouriteListDB()

        updateFcmToken()
    }

    private fun setupFeaturedRvWithDots() {
        binding.apply {
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(FHomeRvFeatured)
            FHomeRvFeatured.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val centerView = snapHelper.findSnapView(FHomeRvFeatured.layoutManager)
                        centerView?.let {
                            val pos: Int = FHomeRvFeatured.layoutManager?.getPosition(it) ?: 0
                            addFeaturedDots(pos, featuredAdapter.currentList.size)
                        }
                    }
                }
            })
        }
    }

    fun addFeaturedDots(position: Int, size: Int) {
        if (size == 0) return
        lifecycleScope.launchWhenStarted {
            val dots = arrayOfNulls<TextView>(size)
            binding.dots.removeAllViews()
            for (i in dots.indices) {
                dots[i] = TextView(requireContext())
                dots[i]!!.text = Html.fromHtml("&#8226;")
                dots[i]!!.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_low))
                dots[i]!!.textSize = 34f
                binding.dots.addView(dots[i])
            }
            dots[position]!!.setTextColor(resources.getColor(R.color.eramo_color))
        }
    }

    private fun setupOffersSlider() {
        binding.apply {
            vpSliderImages.clipToPadding = false
            vpSliderImages.clipChildren = false
            vpSliderImages.offscreenPageLimit = 3
            vpSliderImages.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

            val comPageTrans = CompositePageTransformer()
            comPageTrans.addTransformer(MarginPageTransformer(40))
            comPageTrans.addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = 0.85f + r * 0.15f
            }
            vpSliderImages.setPageTransformer(comPageTrans)
            sliderRun = Runnable {
                vpSliderImages.currentItem = vpSliderImages.currentItem + 1
            }
            vpSliderImages.registerOnPageChangeCallback(
                object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        sliderHandler.removeCallbacks(sliderRun)
                        sliderHandler.postDelayed(sliderRun, 2000)
                    }
                }
            )
        }
    }

    private fun fetchHomeOffersState() {
        lifecycleScope.launchWhenStarted {
            viewModel.homeOffersState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        offersSliderAdapter.renewItems(state.data!!)
                        binding.apply {
                            val sliderAdapter =
                                PagerSliderAdapter(vpSliderImages, state.data.toMutableList())
                            sliderAdapter.setListener(this@HomeFragment)
                            vpSliderImages.adapter = sliderAdapter
                        }
                    }

                    is UiState.Error -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        if (!binding.root.isRefreshing) LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchLatestDealsState() {
        lifecycleScope.launchWhenStarted {
            viewModel.latestDealsState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        dealAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        if (!binding.root.isRefreshing) LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchHomeCounterState() {
        lifecycleScope.launchWhenStarted {
            viewModel.homeCounterState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()

//                        setupCounterDown(
//                            periodToMs(
//                                state.data?.data?.days!!,
//                                state.data?.data?.hours!!,
//                                state.data?.data?.minutes!!,
//                                state.data?.data?.seconds!!
//                            )
//                        )
                    }

                    is UiState.Error -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
                    }

                    is UiState.Loading -> {
                        if (!binding.root.isRefreshing) LoadingDialog.showDialog()
                    }

                    is UiState.Empty -> {
                        binding.apply {
                            view2.visibility = View.GONE
                            FHomeTvDays.visibility = View.GONE
                            textView20.visibility = View.GONE

                            FHomeTvHours.visibility = View.GONE
                            textView21.visibility = View.GONE

                            FHomeTvMinutes.visibility = View.GONE
                            textView22.visibility = View.GONE

                            FHomeTvSeconds.visibility = View.GONE
                            textView23.visibility = View.GONE

                            textView24.visibility = View.GONE
                            textView25.visibility = View.GONE
                            textView26.visibility = View.GONE
                        }

                    }
                }
            }
        }
    }

    private fun fetchCounterValueState() {
        lifecycleScope.launchWhenStarted {
            viewModel.homeCounterValueState.collect { data ->
                binding.apply {
                    FHomeTvDays.text = "%02d".format(data.days)
                    FHomeTvHours.text = "%02d".format(data.hours % 24)
                    FHomeTvMinutes.text = "%02d".format(data.minutes % 60)
                    FHomeTvSeconds.text = "%02d".format(data.seconds % 60)

                    if (data.days.toInt() == 0 && data.hours.toInt() == 0 && data.minutes.toInt() == 0 && data.seconds.toInt() == 0) {
                        binding.apply {
                            view2.visibility = View.GONE
                            FHomeTvDays.visibility = View.GONE
                            textView20.visibility = View.GONE

                            FHomeTvHours.visibility = View.GONE
                            textView21.visibility = View.GONE

                            FHomeTvMinutes.visibility = View.GONE
                            textView22.visibility = View.GONE

                            FHomeTvSeconds.visibility = View.GONE
                            textView23.visibility = View.GONE

                            textView24.visibility = View.GONE
                            textView25.visibility = View.GONE
                            textView26.visibility = View.GONE
                        }
                    } else {
                        binding.apply {
                            view2.visibility = View.VISIBLE
                            FHomeTvDays.visibility = View.VISIBLE
                            textView20.visibility = View.VISIBLE

                            FHomeTvHours.visibility = View.VISIBLE
                            textView21.visibility = View.VISIBLE

                            FHomeTvMinutes.visibility = View.VISIBLE
                            textView22.visibility = View.VISIBLE

                            FHomeTvSeconds.visibility = View.VISIBLE
                            textView23.visibility = View.VISIBLE

                            textView24.visibility = View.VISIBLE
                            textView25.visibility = View.VISIBLE
                            textView26.visibility = View.VISIBLE
                        }

                    }
                }
            }
        }
    }

    private fun fetchLatestProductsState() {
        lifecycleScope.launchWhenStarted {
            viewModel.latestProductsState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        productAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        if (!binding.root.isRefreshing) LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchMostViewedState() {
        lifecycleScope.launchWhenStarted {
            viewModel.mostViewedState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        mostViewedAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        if (!binding.root.isRefreshing) LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchBestCategoriesState() {
        lifecycleScope.launchWhenStarted {
            viewModel.bestCategoriesState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        rvHomeRecentCategoriesAdapter.submitList(state.data?.data)
                    }

                    is UiState.Error -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        if (!binding.root.isRefreshing) LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchBottomSectionsState() {
        lifecycleScope.launchWhenStarted {
            viewModel.bottomSectionsState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()

                        // Visibility
                        binding.apply {
                            FHomeTvSecOne.visibility = View.GONE
                            FHomeRvSecOne.visibility = View.GONE

                            FHomeTvSecTwo.visibility = View.GONE
                            FHomeRvSecTwo.visibility = View.GONE

                            FHomeTvSecThree.visibility = View.GONE
                            FHomeRvSecThree.visibility = View.GONE

                            FHomeTvSecFour.visibility = View.GONE
                            FHomeRvSecFour.visibility = View.GONE
                        }

                        parseBottomSectionsCases(state.data?.data)

                    }

                    is UiState.Error -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        if (!binding.root.isRefreshing) LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun parseBottomSectionsCases(dataList: List<HomeBootomSectionsResponse.Data?>?) {
        binding.apply {
            when (dataList?.size) {
                1 -> {
                    FHomeTvSecOne.text = dataList[0]?.title
                    rvSecOneAdapter.submitList(dataList[0]?.products?.map { it?.toMyProductModel() })

                    // Visibility
                    FHomeTvSecOne.visibility = View.VISIBLE
                    FHomeRvSecOne.visibility = View.VISIBLE

                    FHomeTvSecTwo.visibility = View.GONE
                    FHomeRvSecTwo.visibility = View.GONE

                    FHomeTvSecThree.visibility = View.GONE
                    FHomeRvSecThree.visibility = View.GONE

                    FHomeTvSecFour.visibility = View.GONE
                    FHomeRvSecFour.visibility = View.GONE
                }

                2 -> {
                    FHomeTvSecOne.text = dataList[0]?.title
                    rvSecOneAdapter.submitList(dataList[0]?.products?.map { it?.toMyProductModel() })

                    FHomeTvSecTwo.text = dataList[1]?.title
                    rvSecTwoAdapter.submitList(dataList[1]?.products?.map { it?.toMyProductModel() })

                    // Visibility
                    FHomeTvSecOne.visibility = View.VISIBLE
                    FHomeRvSecOne.visibility = View.VISIBLE

                    FHomeTvSecTwo.visibility = View.VISIBLE
                    FHomeRvSecTwo.visibility = View.VISIBLE

                    FHomeTvSecThree.visibility = View.GONE
                    FHomeRvSecThree.visibility = View.GONE

                    FHomeTvSecFour.visibility = View.GONE
                    FHomeRvSecFour.visibility = View.GONE
                }

                3 -> {
                    FHomeTvSecOne.text = dataList[0]?.title
                    rvSecOneAdapter.submitList(dataList[0]?.products?.map { it?.toMyProductModel() })

                    FHomeTvSecTwo.text = dataList[1]?.title
                    rvSecTwoAdapter.submitList(dataList[1]?.products?.map { it?.toMyProductModel() })

                    FHomeTvSecThree.text = dataList[2]?.title
                    rvSecThreeAdapter.submitList(dataList[2]?.products?.map { it?.toMyProductModel() })

                    // Visibility
                    FHomeTvSecOne.visibility = View.VISIBLE
                    FHomeRvSecOne.visibility = View.VISIBLE

                    FHomeTvSecTwo.visibility = View.VISIBLE
                    FHomeRvSecTwo.visibility = View.VISIBLE

                    FHomeTvSecThree.visibility = View.VISIBLE
                    FHomeRvSecThree.visibility = View.VISIBLE

                    FHomeTvSecFour.visibility = View.GONE
                    FHomeRvSecFour.visibility = View.GONE

                }

                4 -> {
                    FHomeTvSecOne.text = dataList[0]?.title
                    rvSecOneAdapter.submitList(dataList[0]?.products?.map { it?.toMyProductModel() })

                    FHomeTvSecTwo.text = dataList[1]?.title
                    rvSecTwoAdapter.submitList(dataList[1]?.products?.map { it?.toMyProductModel() })

                    FHomeTvSecThree.text = dataList[2]?.title
                    rvSecThreeAdapter.submitList(dataList[2]?.products?.map { it?.toMyProductModel() })

                    FHomeTvSecFour.text = dataList[3]?.title
                    rvSecFourAdapter.submitList(dataList[3]?.products?.map { it?.toMyProductModel() })

                    // Visibility
                    FHomeTvSecOne.visibility = View.VISIBLE
                    FHomeRvSecOne.visibility = View.VISIBLE

                    FHomeTvSecTwo.visibility = View.VISIBLE
                    FHomeRvSecTwo.visibility = View.VISIBLE

                    FHomeTvSecThree.visibility = View.VISIBLE
                    FHomeRvSecThree.visibility = View.VISIBLE

                    FHomeTvSecFour.visibility = View.VISIBLE
                    FHomeRvSecFour.visibility = View.VISIBLE

                }

                else -> {

                    // Visibility
                    FHomeTvSecOne.visibility = View.GONE
                    FHomeRvSecOne.visibility = View.GONE

                    FHomeTvSecTwo.visibility = View.GONE
                    FHomeRvSecTwo.visibility = View.GONE

                    FHomeTvSecThree.visibility = View.GONE
                    FHomeRvSecThree.visibility = View.GONE

                    FHomeTvSecFour.visibility = View.GONE
                    FHomeRvSecFour.visibility = View.GONE
                }
            }
        }
    }

    private fun fetchMostSaleState() {
        lifecycleScope.launchWhenStarted {
            viewModel.mostSaleState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        mostSaleAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        if (!binding.root.isRefreshing)
                            LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchFeaturedProductsState() {
        lifecycleScope.launchWhenStarted {
            viewModel.featuredProductsState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        featuredAdapter.submitList(null)
                        featuredAdapter.submitList(state.data)
                        addFeaturedDots(0, state.data?.size ?: 0)
                        binding.FHomeRvFeatured.scrollToPosition(0)

                        if (state.data.isNullOrEmpty()) {
                            binding.FHomeRvFeatured.visibility = View.GONE
                            binding.FHomeTvFeatured.visibility = View.GONE
                        } else {
                            binding.FHomeRvFeatured.visibility = View.VISIBLE
                            binding.FHomeTvFeatured.visibility = View.VISIBLE
                        }
                    }

                    is UiState.Error -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        if (!binding.root.isRefreshing) LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchCategoriesState() {
        lifecycleScope.launchWhenStarted {
            viewModel.categoriesState.collect { state ->
                when (state) {
                    is UiState.Success -> {
//                        binding.root.isRefreshing = false
//                        LoadingDialog.dismissDialog()
//                        categoryAdapter.submitList(state.data?.data?.reversed())
//
//                        binding.FHomeBtnBrowseAll.setOnClickListener {
//                            findNavController().navigate(
//                                R.id.allCategoryFragment,
//                                AllCategoryFragmentArgs(state.data).toBundle(),
//                                navOptionsAnimation()
//                            )
//                        }
                    }

                    is UiState.Error -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        if (!binding.root.isRefreshing) LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchBrandsState() {
        lifecycleScope.launchWhenStarted {
            viewModel.brandsState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        brandsAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        if (!binding.root.isRefreshing) LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchProfileState() {
        lifecycleScope.launchWhenStarted {
            viewModel.getProfileState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        if (UserUtil.isUserLogin()) {
                            viewModelShared.profileData.value = state.data
                            Glide.with(requireContext())
                                .load(state.data?.imageUrl)
                                .into(binding.inTbLayout.toolbarIvProfile)
                        }
                    }

                    is UiState.Error -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
                    }

                    is UiState.Loading -> {
                        if (!binding.root.isRefreshing) LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchFirebaseTokenState() {
        lifecycleScope.launchWhenStarted {
            viewModel.firebaseTokenState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        Log.d(TAG, "firebaseToken sent: ${FirebaseMessageReceiver.token}")
                    }

                    is UiState.Error -> {
                        Log.d(TAG, "firebaseToken sent failed")
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchRemoveFavouriteState() {
        lifecycleScope.launchWhenStarted {
            viewModel.removeFavouriteState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        showToast(state.data?.message ?: getString(R.string.success))
                        if (isFavPressedProduct) {
                            isFavPressedProduct = false
                            productAdapter.updateFav(false)
                            return@collect
                        }
                        if (isFavPressedDeal) {
                            isFavPressedDeal = false
                            dealAdapter.updateFav(false)
                            return@collect
                        }
                        if (isFavPressedFeatured) {
                            isFavPressedFeatured = false
                            featuredAdapter.updateFav(false)
                            return@collect
                        }
                    }

                    is UiState.Error -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
                    }

                    is UiState.Loading -> {
                        if (!binding.root.isRefreshing) LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchAddFavouriteState() {
        lifecycleScope.launchWhenStarted {
            viewModel.addFavouriteState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        showToast(state.data?.message ?: getString(R.string.success))
                        if (isFavPressedProduct) {
                            isFavPressedProduct = false
                            productAdapter.updateFav(true)
                            return@collect
                        }
                        if (isFavPressedDeal) {
                            isFavPressedDeal = false
                            dealAdapter.updateFav(true)
                            return@collect
                        }
                        if (isFavPressedFeatured) {
                            isFavPressedFeatured = false
                            featuredAdapter.updateFav(true)
                            return@collect
                        }
                    }

                    is UiState.Error -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
                    }

                    is UiState.Loading -> {
                        if (!binding.root.isRefreshing) LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchAddRemoveItemWishlistAndRefreshState() {
        lifecycleScope.launchWhenStarted {
            viewModel.addRemoveItemWishlistState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                    }

                    is UiState.Error -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
                    }

                    is UiState.Loading -> {
                        if (!binding.root.isRefreshing) LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchHomeTopSliderState() {
        lifecycleScope.launchWhenStarted {
            viewModel.homeTopSliderState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        bannerSliderAdapter.renewItems(state.data?.data!!)
                        binding.FHomeSliderTop.setSliderAdapter(bannerSliderAdapter)
                        binding.FHomeSliderTop.startAutoCycle()
                    }

                    is UiState.Error -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        try {
                            showToast(parseErrorResponse(state.message!!.asString(requireContext())))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        if (!binding.root.isRefreshing) LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchCartCountState() {
        lifecycleScope.launchWhenStarted {
            viewModel.cartCountState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        viewModelShared.cartCount.value = state.data?.count
                    }

                    is UiState.Error -> {
                        binding.root.isRefreshing = false
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
//                        showToast("cart count error")
                    }

                    is UiState.Loading -> {
                        if (!binding.root.isRefreshing) LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchNotificationCount() {
        lifecycleScope.launchWhenStarted {
            viewModel.notificationCount.collect { state ->
                viewModelShared.notificationsCount.value = state

                saveNotificationsCountInSharedPreferences(state)

                ShortcutBadger.applyCount(
                    requireContext(),
                    state
                )
            }
        }
    }

    private fun saveNotificationsCountInSharedPreferences(count: Int) {
        val editor = sharedPref?.edit()
        editor?.apply {
            remove(Constants.NOTIFICATIONS_COUNT_SHARED_PREFERENCES_KEY)
            putInt(Constants.NOTIFICATIONS_COUNT_SHARED_PREFERENCES_KEY, count)
            apply()
        }

    }

    private fun updateFcmToken() {
        if (MySingleton.firebaseToken != "" && UserUtil.isUserLogin()) {
            viewModelShared.updateFcmToken(MySingleton.firebaseToken)
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

    private fun handleBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
    }

    private fun switchOfflineCart() {
        viewModel.getCartDataDB()
        fetchCartDataState()
        fetchAddToCartState()
        fetchClearCartDBState()
    }

    private fun fetchCartDataState() {
        lifecycleScope.launchWhenStarted {
            viewModel.cartDataModelState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        if (!state.data?.productList.isNullOrEmpty()) {
                            val jsonArray = arrayListOf<JSONObject>()

                            for (i in state.data?.productList!!) {
                                val product =
                                    createJsonObject(i?.id!!, i.quantity!!, i.colorId!!.toInt(), i.sizeId!!.toInt())
                                jsonArray.add(product)
                            }

//                            viewModel.addToCart(jsonArray.toString())

                            viewModel.clearCartDB()
                        }

                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    is UiState.Empty -> {
                        LoadingDialog.dismissDialog()
                    }
                }
            }
        }
    }

    private fun fetchAddToCartState() {
        lifecycleScope.launchWhenStarted {
            viewModel.addToCartState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        viewModel.clearCartDB()
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
                        viewModel.clearCartDB()
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchClearCartDBState() {
        lifecycleScope.launchWhenStarted {
            viewModel.clearCartDBState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
//                        showToast(getString(R.string.your_cart_is_active_now))
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchAdRemoveItemWishlistDBState() {
        lifecycleScope.launchWhenStarted {
            viewModel.addRemoveItemWishlistStateDB.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        viewModel.getFavouriteListDB()
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchGetFavouriteListDBState() {
        lifecycleScope.launchWhenStarted {
            viewModel.getFavouriteListDBState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()
                        val list = mutableListOf<Int>()

                        for (i in state.data!!) {
                            list.add(i.productId!!)
                        }

                        MySingleton.favouriteDbIdsList = list

//                        viewModel.getFeaturedProducts()
                        featuredAdapter.notifyDataSetChanged()
                        dealAdapter.notifyDataSetChanged()
                        productAdapter.notifyDataSetChanged()

                        mostViewedAdapter.notifyDataSetChanged()

                        rvSecOneAdapter.notifyDataSetChanged()
                        rvSecTwoAdapter.notifyDataSetChanged()
                        rvSecThreeAdapter.notifyDataSetChanged()
                        rvSecFourAdapter.notifyDataSetChanged()

                        mostSaleAdapter.notifyDataSetChanged()

//                        recallTheProducts()

                        if (UserUtil.isUserLogin() && list.isNotEmpty()) {
//                            viewModel.addItemsListToWishlist(list.toString())
                            viewModel.clearFavouriteListDB()
                        }
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        showToast(state.message!!.asString(requireContext()))
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun fetchAddItemsListToWishlistState() {
        lifecycleScope.launchWhenStarted {
            viewModel.addItemsListToWishlistState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        LoadingDialog.dismissDialog()

                        viewModel.clearFavouriteListDB()
                    }

                    is UiState.Error -> {
                        LoadingDialog.dismissDialog()
                        try {
                            parseErrorResponse(state.message!!.asString(requireContext()))
                        } catch (e: Exception) {
                            showToast(state.message!!.asString(requireContext()))
                        }
                    }

                    is UiState.Loading -> {
                        LoadingDialog.showDialog()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun createJsonObject(product_id: Int, quantity: Int, color: Int, size: Int): JSONObject {
        val jsonObject = JSONObject()

        jsonObject.put("product_id", product_id)
        jsonObject.put("quantity", quantity)
        jsonObject.put("color", color)
        jsonObject.put("size", size)

        return jsonObject
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (!isPostNotificationPermissionGranted()) {
                requestNotificationPermission.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
            }
        }
    }

    private fun fetchFilterByCityEvent() {
        lifecycleScope.launchWhenStarted {
            viewModelShared.filterByCityEvent.collect { state ->
                if (state) {

                    setupFilterCity()
                    callHomeApis()
                }
            }
        }
    }

    @RequiresApi(33)
    private fun isPostNotificationPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
    //____________________________________________________________________________________________//
    // adapters click

    override fun onProductClick(model: MyProductModel) {
        findNavController().navigate(
            R.id.productDetailsFragment,
            ProductDetailsFragmentArgs(model.id.toString()).toBundle(),
            navOptionsAnimation()
        )
    }

    override fun onProductFavouriteClick(model: MyProductModel, isFav: Boolean) {
        if (UserUtil.isUserLogin()) {
            isFavPressedProduct = true
            if (isFav) {
                viewModel.addRemoveItemWishlist(model.id.toString(), "latest products")
            } else {
                viewModel.addRemoveItemWishlist(model.id.toString(), "latest products")
            }
//            viewModel.addRemoveItemWishlistDB(
//                MyFavouriteEntity(
//                    productId = model.id,
//                    productName = model.title,
//                    categoryName = model.category?.title,
//                    imageUrl = model.primaryImageUrl,
//                    modelNumber = "",
//                    price = model.realPrice!!.toFloat(),
//                    fakePrice = model.fakePrice!!.toFloat(),
//                    isNew = model.new,
//                    profitPercent = model.profitPercent
//                )
//            )
        } else {
            findNavController().navigate(R.id.loginDialog)

        }
    }

    override fun onDealClick(model: MyProductModel) {
        findNavController().navigate(
            R.id.productDetailsFragment,
            ProductDetailsFragmentArgs(model.id.toString()).toBundle(),
            navOptionsAnimation()
        )
    }

    override fun onDealFavouriteClick(model: MyProductModel, isFav: Boolean) {
        if (UserUtil.isUserLogin()) {
            isFavPressedDeal = true
            if (isFav) {
                viewModel.addRemoveItemWishlist(model.id.toString(), "latest deals")
            } else {
                viewModel.addRemoveItemWishlist(model.id.toString(), "latest deals")
            }
//            viewModel.addRemoveItemWishlistDB(
//                MyFavouriteEntity(
//                    productId = model.id,
//                    productName = model.title,
//                    categoryName = model.category?.title,
//                    imageUrl = model.primaryImageUrl,
//                    modelNumber = "",
//                    price = model.realPrice!!.toFloat(),
//                    fakePrice = model.fakePrice!!.toFloat(),
//                    isNew = model.new,
//                    profitPercent = model.profitPercent
//                )
//            )
        } else {
            findNavController().navigate(R.id.loginDialog)

        }
    }

    override fun onBannerClick(model: HomePageSliderResponse.Data?) {
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(model?.link))
        startActivity(i)
    }

    override fun onBrandClick(model: HomeBrandsModel) {
        findNavController().navigate(R.id.allCategoryFragment, AllCategoryFragmentArgs(model.id.toString()).toBundle())
        UserUtil.saveBrandId(model.id.toString())
    }

    override fun onFeaturedClick(model: MyProductModel) {
        findNavController().navigate(
            R.id.productDetailsFragment,
            ProductDetailsFragmentArgs(model.id.toString()).toBundle(),
            navOptionsAnimation()
        )
    }

    override fun onRecentCategoryClick(model: HomeBestCategoriesResponse.Data) {
        val subCategoriesList = model.subCatagories!!.map { it!!.toSubCategoryModel() }.toTypedArray()
        findNavController().navigate(
            R.id.subCategoriesFragment, SubCategoriesFragmentArgs(subCategoriesList).toBundle(),
            navOptionsAnimation()
        )
    }

    override fun onFeaturedFavouriteClick(model: MyProductModel, isFav: Boolean) {
        if (UserUtil.isUserLogin()) {
            isFavPressedFeatured = true
            viewModel.addRemoveItemWishlist(model.id.toString(), "featured products")
            viewModel.addRemoveItemWishlistDB(
                MyFavouriteEntity(
                    productId = model.id,
                    productName = model.title,
                    categoryName = model.category?.title,
                    imageUrl = model.primaryImageUrl,
                    modelNumber = "",
                    price = model.realPrice!!.toFloat(),
                    fakePrice = model.fakePrice!!.toFloat(),
                    isNew = model.new,
                    profitPercent = model.profitPercent
                )
            )
        } else {
            findNavController().navigate(R.id.loginDialog)

        }
    }

    override fun onOfferClick(model: OffersModel) {
        if (model.type == "url") {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(model.url)))
            return
        }

        if (model.type == "category") {
            findNavController().navigate(
                R.id.shopFragment,
                ShopFragmentArgs(model.id).toBundle()
            )
        }

        if (model.type == "product") {
            findNavController().navigate(
                R.id.productDetailsFragment,
                ProductDetailsFragmentArgs(model.id).toBundle()
            )
        }
    }

    override fun onMostViewedProductClick(model: MyProductModel) {
        findNavController().navigate(
            R.id.productDetailsFragment,
            ProductDetailsFragmentArgs(model.id.toString()).toBundle(),
            navOptionsAnimation()
        )
    }

    override fun onMostViewedProductFavouriteClick(model: MyProductModel, isFav: Boolean) {
        if (UserUtil.isUserLogin()) {
            isFavPressedFeatured = true
            viewModel.addRemoveItemWishlist(model.id.toString(), "most viewed products")
            viewModel.addRemoveItemWishlistDB(
                MyFavouriteEntity(
                    productId = model.id,
                    productName = model.title,
                    categoryName = model.category?.title,
                    imageUrl = model.primaryImageUrl,
                    modelNumber = "",
                    price = model.realPrice!!.toFloat(),
                    fakePrice = model.fakePrice!!.toFloat(),
                    isNew = model.new,
                    profitPercent = model.profitPercent
                )
            )
        } else {
            findNavController().navigate(R.id.loginDialog)

        }
    }

    override fun onMostSaleProductClick(model: MyProductModel) {
        findNavController().navigate(
            R.id.productDetailsFragment,
            ProductDetailsFragmentArgs(model.id.toString()).toBundle(),
            navOptionsAnimation()
        )
    }

    override fun onMostSaleProductFavouriteClick(model: MyProductModel, isFav: Boolean) {
        if (UserUtil.isUserLogin()) {
            isFavPressedFeatured = true
            viewModel.addRemoveItemWishlist(model.id.toString(), "most sale products")
            viewModel.addRemoveItemWishlistDB(
                MyFavouriteEntity(
                    productId = model.id,
                    productName = model.title,
                    categoryName = model.category?.title,
                    imageUrl = model.primaryImageUrl,
                    modelNumber = "",
                    price = model.realPrice!!.toFloat(),
                    fakePrice = model.fakePrice!!.toFloat(),
                    isNew = model.new,
                    profitPercent = model.profitPercent
                )
            )
        } else {
            findNavController().navigate(R.id.loginDialog)

        }
    }

    override fun onSecOneProductClick(model: MyProductModel) {
        findNavController().navigate(
            R.id.productDetailsFragment,
            ProductDetailsFragmentArgs(model.id.toString()).toBundle(),
            navOptionsAnimation()
        )
    }

    override fun onSecOneProductFavouriteClick(model: MyProductModel, isFav: Boolean) {
        if (UserUtil.isUserLogin()) {
            viewModel.addRemoveItemWishlistDB(
                MyFavouriteEntity(
                    productId = model.id,
                    productName = model.title,
                    categoryName = model.category?.title,
                    imageUrl = model.primaryImageUrl,
                    modelNumber = "",
                    price = model.realPrice!!.toFloat(),
                    fakePrice = model.fakePrice!!.toFloat(),
                    isNew = model.new,
                    profitPercent = model.profitPercent
                )
            )
            isFavPressedFeatured = true
            viewModel.addRemoveItemWishlist(model.id.toString(), "most sale products")
        } else {
            findNavController().navigate(R.id.loginDialog)

        }
    }

    override fun onSecFourProductClick(model: MyProductModel) {
        findNavController().navigate(
            R.id.productDetailsFragment,
            ProductDetailsFragmentArgs(model.id.toString()).toBundle(),
            navOptionsAnimation()
        )
    }

    override fun onSecFourProductFavouriteClick(model: MyProductModel, isFav: Boolean) {
        if (UserUtil.isUserLogin()) {
            viewModel.addRemoveItemWishlistDB(
                MyFavouriteEntity(
                    productId = model.id,
                    productName = model.title,
                    categoryName = model.category?.title,
                    imageUrl = model.primaryImageUrl,
                    modelNumber = "",
                    price = model.realPrice!!.toFloat(),
                    fakePrice = model.fakePrice!!.toFloat(),
                    isNew = model.new,
                    profitPercent = model.profitPercent
                )
            )
            isFavPressedFeatured = true
            viewModel.addRemoveItemWishlist(model.id.toString(), "most sale products")
        } else {
            findNavController().navigate(R.id.loginDialog)
        }
    }

    override fun onSecThreeProductClick(model: MyProductModel) {
        findNavController().navigate(
            R.id.productDetailsFragment,
            ProductDetailsFragmentArgs(model.id.toString()).toBundle(),
            navOptionsAnimation()
        )
    }

    override fun onSecThreeProductFavouriteClick(model: MyProductModel, isFav: Boolean) {
        if (UserUtil.isUserLogin()) {
            viewModel.addRemoveItemWishlistDB(
                MyFavouriteEntity(
                    productId = model.id,
                    productName = model.title,
                    categoryName = model.category?.title,
                    imageUrl = model.primaryImageUrl,
                    modelNumber = "",
                    price = model.realPrice!!.toFloat(),
                    fakePrice = model.fakePrice!!.toFloat(),
                    isNew = model.new,
                    profitPercent = model.profitPercent
                )
            )
            isFavPressedFeatured = true
            viewModel.addRemoveItemWishlist(model.id.toString(), "most sale products")
        } else {
            findNavController().navigate(R.id.loginDialog)

        }
    }

    override fun onSecTwoProductClick(model: MyProductModel) {
        findNavController().navigate(
            R.id.productDetailsFragment,
            ProductDetailsFragmentArgs(model.id.toString()).toBundle(),
            navOptionsAnimation()
        )
    }

    override fun onSecTwoProductFavouriteClick(model: MyProductModel, isFav: Boolean) {
        if (UserUtil.isUserLogin()) {
            viewModel.addRemoveItemWishlistDB(
                MyFavouriteEntity(
                    productId = model.id,
                    productName = model.title,
                    categoryName = model.category?.title,
                    imageUrl = model.primaryImageUrl,
                    modelNumber = "",
                    price = model.realPrice!!.toFloat(),
                    fakePrice = model.fakePrice!!.toFloat(),
                    isNew = model.new,
                    profitPercent = model.profitPercent
                )
            )
            isFavPressedFeatured = true
            viewModel.addRemoveItemWishlist(model.id.toString(), "most sale products")
        } else {
            findNavController().navigate(R.id.loginDialog)
        }
    }
}