package eramo.resultgate.data.remote.dto.alldevices


import com.google.gson.annotations.SerializedName

data class Links(
    @SerializedName("first")
    var first: String?,
    @SerializedName("last")
    var last: String?,
    @SerializedName("next")
    var next: Any?,
    @SerializedName("prev")
    var prev: Any?
)