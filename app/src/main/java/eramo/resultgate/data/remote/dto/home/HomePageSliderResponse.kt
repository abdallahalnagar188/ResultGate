package eramo.resultgate.data.remote.dto.home


import com.google.gson.annotations.SerializedName

data class HomePageSliderResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("data")
    val `data`: List<Data?>?,
    @SerializedName("message")
    val message: String?
) {
    data class Data(
        @SerializedName("url")
        val link: String?,
        @SerializedName("image")
        val imageUrl: String?
    ) 
}