package eramo.tahoon.domain.model.products.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductFeaturesModel(
    var id: String,
    var productIdFk: String,
    var featureIdFk: String,
    var featureName: String
) : Parcelable