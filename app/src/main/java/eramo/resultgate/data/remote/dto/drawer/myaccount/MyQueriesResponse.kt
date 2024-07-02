package eramo.resultgate.data.remote.dto.drawer.myaccount


import com.google.gson.annotations.SerializedName

data class MyQueriesResponse(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?
) {
    data class Data(
        @SerializedName("canceled")
        val canceled: List<MyQueryModel?>?,
        @SerializedName("pendding")
        val pendding: List<MyQueryModel?>?,
        @SerializedName("replyed")
        val replyed: List<MyQueryModel?>?
    ) {
        data class MyQueryModel(
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

//data class MyQueriesResponse(
//    @SerializedName("data")
//    val `data`: Data?,
//    @SerializedName("status")
//    val status: Int?,
//    @SerializedName("message")
//    val message: String?
//) {
//    data class Data(
//        @SerializedName("replyed")
//        val replyed: List<MyQueryModel?>?,
//        @SerializedName("pendding")
//        val pendding: List<MyQueryModel?>?,
//        @SerializedName("canceled")
//        val canceled: List<MyQueryModel?>?
//    )
//
//    data class MyQueryModel(
//        @SerializedName("id")
//        val id: Int?,
//        @SerializedName("type")
//        val type: String?,
//        @SerializedName("name")
//        val name: String?,
//        @SerializedName("email")
//        val email: String?,
//        @SerializedName("phone")
//        val phone: String?,
//        @SerializedName("air_condition_number")
//        val airConditionNumber: Int?,
//        @SerializedName("message")
//        val message: String?,
//        @SerializedName("reply")
//        val reply: String?,
//        @SerializedName("cancelled")
//        val cancelled: String?,
//        @SerializedName("created_at")
//        val createdAt: String?,
//        @SerializedName("created_at")
//        val updatedAt: String?
//
////        @SerializedName("id")
////        val id: Int?,
////        @SerializedName("subject")
////        val subject: String?,
////        @SerializedName("message")
////        val message: String?,
////        @SerializedName("status")
////        val status: String?,
////        @SerializedName("attachment")
////        val attachment: String?,
////        @SerializedName("reply")
////        val reply: String?
//    )
//}

