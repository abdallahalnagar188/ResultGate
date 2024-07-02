package eramo.resultgate.data.remote.dto.drawer.myaccount


import com.google.gson.annotations.SerializedName

data class TestResponse(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?
) {
    data class Data(
        @SerializedName("canceled")
        val canceled: List<Any?>?,
        @SerializedName("pendding")
        val pendding: List<Any?>?,
        @SerializedName("replyed")
        val replyed: List<Replyed?>?
    ) {
        data class Replyed(
            @SerializedName("air_condition_number")
            val airConditionNumber: Int?,
            @SerializedName("cancelled")
            val cancelled: String?,
            @SerializedName("created_at")
            val createdAt: String?,
            @SerializedName("email")
            val email: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("message")
            val message: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("phone")
            val phone: String?,
            @SerializedName("reply")
            val reply: String?,
            @SerializedName("type")
            val type: String?,
            @SerializedName("updated_at")
            val updatedAt: String?
        )
    }
}