package eramo.tahoon.domain.model.products.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductExtrasModel(
    var id: String,
    var productIdFk: String,
    var extraIdFk: String,
    var extrasName: String
) : Parcelable