package eramo.resultgate.data.remote.dto


import com.google.gson.annotations.SerializedName

data class SendQueryResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?
)