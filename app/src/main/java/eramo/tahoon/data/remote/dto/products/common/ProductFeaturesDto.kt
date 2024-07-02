package eramo.tahoon.data.remote.dto.products.common

import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.products.common.ProductFeaturesModel
import eramo.tahoon.util.LocalHelperUtil

data class ProductFeaturesDto(
    @SerializedName("id") var id: String? = null,
    @SerializedName("product_id_fk") var productIdFk: String? = null,
    @SerializedName("feature_id_fk") var featureIdFk: String? = null,
    @SerializedName("feature_name_ar") var featureNameAr: String? = null,
    @SerializedName("feature_name_en") var featureNameEn: String? = null
) {
    fun toProductFeaturesModel(): ProductFeaturesModel {
        return ProductFeaturesModel(
            id = id ?: "",
            productIdFk = productIdFk ?: "",
            featureIdFk = featureIdFk ?: "",
            featureName =
            if (LocalHelperUtil.isEnglish()) featureNameEn ?: "" else featureNameAr ?: ""
        )
    }
}
