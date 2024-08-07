package eramo.resultgate.data.remote.dto.alldevices


import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("primary_image_url")
    var primaryImageUrl: String?,
    @SerializedName("title")
    var title: String?
)