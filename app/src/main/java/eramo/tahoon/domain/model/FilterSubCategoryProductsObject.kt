package eramo.tahoon.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterSubCategoryProductsObject(
    val subCategoryId: String,
    val type: String,
    val value: String,
    val priceFrom: String,
    val priceTo: String,
):Parcelable