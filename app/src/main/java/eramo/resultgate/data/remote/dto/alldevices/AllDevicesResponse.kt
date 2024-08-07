package eramo.resultgate.data.remote.dto.alldevices


import com.google.gson.annotations.SerializedName

data class AllDevicesResponse(
    @SerializedName("data")
    var `data`: Data?,
    @SerializedName("max")
    var max: String?,
    @SerializedName("status")
    var status: Int?
)