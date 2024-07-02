package eramo.resultgate.data.remote.dto.drawer.myaccount.myaddresses


import com.google.gson.annotations.SerializedName

data class UpdateAddressResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("data")
    val `data`: List<Any?>?,
    @SerializedName("message")
    val message: String?
)