package eramo.tahoon.data.remote.dto.cart


import com.google.gson.annotations.SerializedName

data class CheckProductStockResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?   ,
    @SerializedName("clicked")
    var clicked: String?
)