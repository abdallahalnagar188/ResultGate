package eramo.resultgate.data.remote.dto.drawer.myaccount.myaddresses


import com.google.gson.annotations.SerializedName

data class AddToMyAddressesResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?
)