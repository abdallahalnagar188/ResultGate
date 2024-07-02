package eramo.tahoon.data.remote.dto.auth


import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.auth.OnBoardingModel

data class MyOnBoardingResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("data")
    val data: List<Data?>?,
    @SerializedName("message")
    val message: String?
) {
    data class Data(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("number")
        val number: String?,
        @SerializedName("title")
        val title: String?,
//        @SerializedName("image")
//        val image: String?,
        @SerializedName("details")
        val details: String?,
        @SerializedName("image")
        val image_url: String?
    ) {
        fun toOnBoardingModel(): OnBoardingModel {
            return OnBoardingModel(
                splashId = id.toString(),
                splashTitle = title,
                image = image_url,
                splashDetails = details
            )
        }
    }
}


//data class MyOnBoardingResponse(
//    @SerializedName("all_screens")
//    val allScreens: List<AllScreen?>?
//) {
//    data class AllScreen(
//        @SerializedName("id")
//        val id: Int?,
//        @SerializedName("number")
//        val number: String?,
//        @SerializedName("title")
//        val title: String?,
//        @SerializedName("image")
//        val image: String?,
//        @SerializedName("details")
//        val details: String?,
//        @SerializedName("image_url")
//        val image_url: String?
//    ){
//        fun toOnBoardingModel(): OnBoardingModel {
//            return OnBoardingModel(
//                splashId = id.toString(),
//                splashTitle = title,
//                image = image_url,
//                splashDetails = details
//            )
//        }
//    }
//}