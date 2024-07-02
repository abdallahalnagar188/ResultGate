package eramo.tahoon.data.remote.dto.auth

import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.auth.OnBoardingModel
import eramo.tahoon.util.LocalHelperUtil

data class OnBoardingDto(
    @SerializedName("all_screens") var allScreens: ArrayList<AllScreens> = arrayListOf()
)

data class AllScreens(
    @SerializedName("spalsh_id") var spalshId: String? = null,
    @SerializedName("spalsh_title_en") var spalshTitleEn: String? = null,
    @SerializedName("spalsh_title_ar") var spalshTitleAr: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("spalsh_details_en") var spalshDetailsEn: String? = null,
    @SerializedName("spalsh_details_ar") var spalshDetailsAr: String? = null
) {
    fun toOnBoardingModel(): OnBoardingModel {
        return OnBoardingModel(
            splashId = spalshId,
            splashTitle = if (LocalHelperUtil.isEnglish()) spalshTitleEn else spalshTitleAr,
            image = image,
            splashDetails = if (LocalHelperUtil.isEnglish()) spalshDetailsEn else spalshDetailsAr
        )
    }
}