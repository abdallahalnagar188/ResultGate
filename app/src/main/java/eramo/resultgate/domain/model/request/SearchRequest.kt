package eramo.resultgate.domain.model.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchRequest(
    @SerializedName("manufacturer") var subCats: ArrayList<String>? = null,
    @SerializedName("price_from") var priceFrom: String? = null,
    @SerializedName("price_to") var priceTo: String? = null
) : Parcelable