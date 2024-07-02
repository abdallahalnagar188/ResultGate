package eramo.tahoon.data.remote.dto.cart


import com.google.gson.annotations.SerializedName

data class AddToCartResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?
)