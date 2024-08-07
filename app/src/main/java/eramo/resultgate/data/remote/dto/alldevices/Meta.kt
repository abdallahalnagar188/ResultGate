package eramo.resultgate.data.remote.dto.alldevices


import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("current_page")
    var currentPage: Int?,
    @SerializedName("from")
    var from: Int?,
    @SerializedName("last_page")
    var lastPage: Int?,
    @SerializedName("links")
    var links: List<Link?>?,
    @SerializedName("path")
    var path: String?,
    @SerializedName("per_page")
    var perPage: Int?,
    @SerializedName("to")
    var to: Int?,
    @SerializedName("total")
    var total: Int?
)