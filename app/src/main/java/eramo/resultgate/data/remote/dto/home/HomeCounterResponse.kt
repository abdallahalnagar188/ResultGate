package eramo.resultgate.data.remote.dto.home


import com.google.gson.annotations.SerializedName

data class HomeCounterResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("message")
    val message: String?
) {
    data class Data(
        @SerializedName("days")
        val days: Int?,
        @SerializedName("hours")
        val hours: Int?,
        @SerializedName("minutes")
        val minutes: Int?,
        @SerializedName("seconds")
        val seconds: Int?
    )
}