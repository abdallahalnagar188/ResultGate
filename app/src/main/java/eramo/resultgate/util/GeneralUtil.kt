package eramo.resultgate.util

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.net.ParseException
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.paging.PagingConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import eramo.resultgate.R
import eramo.resultgate.util.state.ApiState
import eramo.resultgate.util.state.UiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale

fun Activity.hideSoftKeyboard() {
    currentFocus?.let {
        val inputMethodManager =
            ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}


fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

//fun Fragment.onBackPressed(code: () -> Unit) {
//    activity?.onBackPressedDispatcher?.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
//        override fun handleOnBackPressed() {
//            code()
//        }
//    })
//}

fun Fragment.onBackPressed(code: () -> Unit) {
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
        code()
    }
}

fun NavController.isFragmentExist(destinationId: Int) =
    try {
        getBackStackEntry(destinationId)
        true
    } catch (e: Exception) {
        false
    }

fun BottomNavigationView.uncheckBottomNavSelection() {
    menu.setGroupCheckable(0, true, false)
    for (i in 0 until menu.size()) {
        menu.getItem(i).isChecked = false
    }
    menu.setGroupCheckable(0, true, true)
}

fun navOptionsAnimation(): NavOptions {
    return NavOptions
        .Builder()
        .setEnterAnim(R.anim.from_right)
        .setExitAnim(R.anim.to_left)
        .setPopEnterAnim(R.anim.from_left)
        .setPopExitAnim(R.anim.to_right)
        .build()
}

    fun htmlFormatToString(htmlTxt: String): CharSequence {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(htmlTxt, Html.FROM_HTML_MODE_COMPACT)
        else Html.fromHtml(htmlTxt)
    }

@Throws(ParseException::class)
fun removePriceComma(value: String): String {
    return value.replace(",", "")
}

fun formatPrice(price: Double): String {
    return "%,.2f".format(Locale.ENGLISH, price)
}

fun parseErrorResponse(string: String): String {
    return JSONObject(string).getString("message")
}

fun convertToArabicNumber(englishNumber: Int): String {
    val arabicFormat = NumberFormat.getInstance(Locale("ar"))
    return arabicFormat.format(englishNumber)
}

fun getYoutubeUrlId(url: String): String? {
    return Uri.parse(url).getQueryParameter("v")
}

fun pagingConfig() = PagingConfig(pageSize = Constants.PAGING_PER_PAGE, enablePlaceholders = false)

fun <T> toResultFlow(call: suspend () -> Response<T>): Flow<ApiState<T>> = flow {
    emit(ApiState.Loading())
    try {
        val response = call()
        if (response.isSuccessful) {
            emit(ApiState.Success(response.body()))
            Log.e("networkResponse", "200 OK\n")
        } else {
            val errorBodyJson = JSONObject(response.errorBody()!!.charStream().readText())
            emit(ApiState.Error(UiText.DynamicString(errorBodyJson.toString())))
            Log.e("networkResponse", errorBodyJson.toString())
        }
    } catch (e: HttpException) {
        emit(ApiState.Error(UiText.StringResource(R.string.something_went_wrong)))
        Log.e("networkResponse", "HttpException\n ${e.message.toString()}")
    } catch (e: IOException) {
        //emit(ApiState.Error(UiText.StringResource(R.string.check_your_internet_connection)))
        Log.e("networkResponse", "IOException\n ${e.message.toString()}")
    } catch (e: Exception) {
        emit(ApiState.Error(UiText.StringResource(R.string.something_went_wrong)))
        Log.e("networkResponse", "Exception\n ${e.message.toString()}")
    }
}
