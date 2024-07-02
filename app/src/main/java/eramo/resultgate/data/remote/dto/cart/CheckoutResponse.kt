package eramo.resultgate.data.remote.dto.cart


import com.google.gson.annotations.SerializedName

data class CheckoutResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?
)