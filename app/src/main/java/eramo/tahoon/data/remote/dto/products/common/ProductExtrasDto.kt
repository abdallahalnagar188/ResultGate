package eramo.tahoon.data.remote.dto.products.common

import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.products.common.ProductExtrasModel
import eramo.tahoon.util.LocalHelperUtil

data class ProductExtrasDto(
    @SerializedName("id") var id: String? = null,
    @SerializedName("product_id_fk") var productIdFk: String? = null,
    @SerializedName("extra_id_fk") var extraIdFk: String? = null,
    @SerializedName("extras_name_ar") var extrasNameAr: String? = null,
    @SerializedName("extras_name_en") var extrasNameEn: String? = null
) {
    fun toProductExtrasModel(): ProductExtrasModel {
        return ProductExtrasModel(
            id = id ?: "",
            productIdFk = productIdFk ?: "",
            extraIdFk = extraIdFk ?: "",
            extrasName = if (LocalHelperUtil.isEnglish()) extrasNameEn ?: "" else extrasNameAr ?: ""
        )
    }
}