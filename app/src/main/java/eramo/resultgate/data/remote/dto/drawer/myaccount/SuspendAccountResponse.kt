package eramo.resultgate.data.remote.dto.drawer.myaccount


import com.google.gson.annotations.SerializedName

data class SuspendAccountResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?
)