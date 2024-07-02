package eramo.resultgate.data.remote.dto.products


import com.google.gson.annotations.SerializedName
import eramo.resultgate.domain.model.products.AdsModel
import eramo.resultgate.util.LocalHelperUtil

data class MyAdsDto(
    @SerializedName("homepage_sliders")
    val homepageSliders: List<HomepageSlider>
) {
    data class HomepageSlider(
        @SerializedName("big_text_ar")
        val bigTextAr: String?,
        @SerializedName("big_text_en")
        val bigTextEn: String?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("description_ar")
        val descriptionAr: String?,
        @SerializedName("description_en")
        val descriptionEn: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("intro_title_ar")
        val introTitleAr: String?,
        @SerializedName("intro_title_en")
        val introTitleEn: String?,
        @SerializedName("link")
        val link: String?,
        @SerializedName("main_image")
        val mainImage: String?,
        @SerializedName("updated_at")
        val updatedAt: String?
    ) {
        fun toAdsModel(): AdsModel {
            return AdsModel(
                adsId = id.toString(),
                adsIn = "",
                clientType = "",
                advName = if (LocalHelperUtil.isEnglish()) introTitleEn ?: "" else introTitleAr ?: "",
                adsLocation = "",
                advDate = "",
                advTime = "",
                spaceType = "",
                clientIdFk = "",
                type = "",
                iframeText = "",
                image = mainImage ?: "",
                numViews = "",
                numClick = "",
                fromDate = "",
                fromDateS = "",
                toDate = "",
                toDateS = "",
                status = "",
                userId = "",
                agentBrokFk = "",
                parentUser = "",
                actionDate = "",
                actionTime = "",
                actionUser = "",
                actionView = "",
                adsUrl = link ?: "",
                mainTitle = if (LocalHelperUtil.isEnglish()) introTitleEn ?: "" else introTitleAr ?: "",
                shortTitle = if (LocalHelperUtil.isEnglish()) introTitleEn ?: "" else introTitleAr ?: ""
            )
        }

    }
}