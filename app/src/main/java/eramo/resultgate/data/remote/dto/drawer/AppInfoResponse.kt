package eramo.resultgate.data.remote.dto.drawer

import com.google.gson.annotations.SerializedName

data class AppInfoResponse(
    @SerializedName("app_info") var appInfo: ArrayList<AppInfo> = arrayListOf()
)

data class AppInfo(
    @SerializedName("nameweb") var nameweb: String? = null,
    @SerializedName("abbreviation_name") var abbreviationName: String? = null,
    @SerializedName("slogan") var slogan: String? = null,
    @SerializedName("about_app") var aboutAppEn: String? = null,
    @SerializedName("about_app_ar") var aboutAppAr: String? = null,
    @SerializedName("website") var website: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("telepon") var telepon: String? = null,
    @SerializedName("mobile") var mobile: String? = null,
    @SerializedName("fax") var fax: String? = null,
    @SerializedName("logo") var logo: String? = null,
    @SerializedName("facebook") var facebook: String? = null,
    @SerializedName("twitter") var twitter: String? = null,
    @SerializedName("instagram") var instagram: String? = null,
    @SerializedName("google_map") var googleMap: String? = null,
    @SerializedName("company_video") var companyVideo: String? = null,
    @SerializedName("company_iframe") var companyIframe: String? = null
)