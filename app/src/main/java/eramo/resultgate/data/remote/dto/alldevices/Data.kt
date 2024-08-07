package eramo.resultgate.data.remote.dto.alldevices


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("data")
    var `data`: List<DataX>?,
    @SerializedName("links")
    var links: Links?,
    @SerializedName("meta")
    var meta: Meta?
)