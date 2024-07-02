package eramo.resultgate.data.remote.dto.cart


import com.google.gson.annotations.SerializedName

data class CheckPromoCodeResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val `data`: Data?
) {
    data class Data(
        @SerializedName("promo_code_value")
        val promoCodeValue: String?,
        @SerializedName("promo_code_type")
        val promoCodeType: String?
    )
}