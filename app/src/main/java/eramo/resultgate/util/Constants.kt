package eramo.resultgate.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import eramo.resultgate.R

object Constants {
    const val TOPIC = "/topics/MyTopic"
    const val Auth_API_URL="https://accept.paymobsolutions.com/api/auth/tokens"
    const val API_KEY = "ZXlKaGJHY2lPaUpJVXpVeE1pSXNJblI1Y0NJNklrcFhWQ0o5LmV5SmpiR0Z6Y3lJNklrMWxjbU5vWVc1MElpd2ljSEp2Wm1sc1pWOXdheUk2TmpVMk5UQTRMQ0p1WVcxbElqb2lNVGN3TnpjME56QXlPQzR4T0RNeE1EVWlmUS44dGVlV3lHRC1FcW1nTHhuRnZQbHhzV3B4ei1YTkFOZnd6VmhNUG9icjdDQklqU0tyMEMyajJ6X0NHeklfX0llR3hVUUYxQVJVUEVDeGpoYnY3MVZrUQ=="
    const val ORDER_REGISTER_URL="https://accept.paymobsolutions.com/api/ecommerce/orders"

    const val TYPE_UPLOAD_TAX = "commerical_img"
    const val TYPE_UPLOAD_COMMERCIAL = "tax_card_img"
    const val TYPE_UPLOAD_PROFILE = "profile_img"
    var uploadType = ""       // By default

    const val TEXT_YES = "yes"
    const val TEXT_NO = "no"

    const val ANIMATION_DELAY = 450L
    const val TAG = "Eramo-Log"

    const val PAGING_START_INDEX = 1
    const val PAGING_PER_PAGE = 4

    const val ERAMO_WEBSITE = "https://www.e-ramo.net"
    const val ERAMO_PHONE = "tel:+201011559674"

    const val IMG_KEY_IMAGE = "image"
    const val IMG_KEY_COMMERCIAL = "commerical_img"
    const val IMG_KEY_TAX = "tax_card_img"
    const val IMG_KEY_PROFILE = "image"

    const val SIGN_UP_SIGN_FROM = "ANDROID"
    const val DEFAULT_ADDRESS_TYPE_AR = "العنوان الأفتراضى"
    const val DEFAULT_ADDRESS_TYPE_EN = "Default address"

    const val API_HEADER_LANG_AR = "ar"
    const val API_HEADER_LANG_EN = "en"

    const val API_SUCCESS = 200

    const val CLICKED_TV_ADD_TO_CART = "tvAddToCart"
    const val CLICKED_BTN_BUY = "btnBuy"
    const val CLICKED_TV_ADD_TO_CART_EXTRA = "addToCartExtra"

    const val DESTINATION_FROM_SIGN_UP = "destinationFromSignUp"
    const val DESTINATION_FROM_LOGIN = "destinationFromLogin"

    const val NOTIFICATIONS_COUNT_SHARED_PREFERENCES_KEY = "notification_count_shared_preferences_key"

    const val PAYMENT_ONLINE = "Online Payment"
    const val PAYMENT_CASH_ON_DELIVERY = "Cash On Delivery"
    const val PAYMENT_SYMBL = "Symbl"

    const val CURRENCY_EGP = "EGP"
    const val CURRENCY_USD = "USD"

    const val MY_ADDRESSES_FRAGMENT = "myAddressesFragment"
    const val CHECKOUT_FRAGMENT = "checkoutFragment"


    const val Integration_ID_S = 3730968
    const val Integration_ID_O = 3798290
    const val CARD_PAYMENT_KEY_URL="https://accept.paymobsolutions.com/api/acceptance/payment_keys"


    const val PAYMOB_URL_S = "https://accept.paymob.com/api/acceptance/iframes/742783?payment_token="
    const val PAYMOB_URL_O = "https://accept.paymob.com/api/acceptance/iframes/713047?payment_token="


    const val GO_TO_ORDER_DETAILS = 301
    const val GO_TO_NOTIFICATION_INFO = 302
    const val GO_TO_NOTIFICATIONS = 303

    fun createSpinnerAdapter(context: Context, list: List<StringWithTag>) =
        ArrayAdapter(context, R.layout.layout_spinner_item, list)

    fun createCountrySpinnerAdapter(context: Context, list: List<StringIdShipping>) =
        ArrayAdapter(context, R.layout.layout_spinner_item, list)

    fun createServicesSpinnerAdapter(context: Context, list: List<StringWithKey>) =
        ArrayAdapter(context, R.layout.layout_spinner_item, list)

    fun setupLangChooser(
        activity: Activity,
        flagIcon: ImageView,
        header: CardView,
        body: CardView,
        iconArrow: ImageView,
        iconCheckEn: ImageView,
        iconCheckAr: ImageView,
        linChoiceEn: LinearLayout,
        linChoiceAr: LinearLayout,
    ) {
        var isLangOpened = false
        if (LocalHelperUtil.isEnglish()) {
            iconCheckEn.visibility = View.VISIBLE
            iconCheckAr.visibility = View.INVISIBLE
            flagIcon.setImageResource(R.drawable.pic_en)
        } else {
            iconCheckEn.visibility = View.INVISIBLE
            iconCheckAr.visibility = View.VISIBLE
            flagIcon.setImageResource(R.drawable.pic_egypt)
        }

        header.setOnClickListener {
            if (isLangOpened) closeChooser(body, iconArrow) else openChooser(body, iconArrow)
            isLangOpened = !isLangOpened
        }

        linChoiceEn.setOnClickListener {
            closeChooser(body, iconArrow)
            LocalHelperUtil.setLocal(activity, "en")
            ActivityCompat.recreate(activity)
        }

        linChoiceAr.setOnClickListener {
            closeChooser(body, iconArrow)
            LocalHelperUtil.setLocal(activity, "ar")
            ActivityCompat.recreate(activity)
        }
    }

    private fun closeChooser(body: CardView, iconArrow: ImageView) {
        body.visibility = View.GONE
        iconArrow.setImageResource(R.drawable.ic_arrow_down)
    }

    private fun openChooser(body: CardView, iconArrow: ImageView) {
        body.visibility = View.VISIBLE
        iconArrow.setImageResource(R.drawable.ic_arrow_up)
    }
}