package eramo.resultgate.data.remote.dto.becomeavendor


import com.google.gson.annotations.SerializedName

data class BecomeAVendorResponse(
    @SerializedName("data")
    var `data`: List<Any?>?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Int?
)