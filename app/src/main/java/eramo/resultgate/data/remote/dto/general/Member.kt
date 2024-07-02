package eramo.resultgate.data.remote.dto.general

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Member(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("name")
    var name: String?= null,
    @SerializedName("lang")
    var lang: String?= null,
    @SerializedName("email")
    var email: String?= null,
    @SerializedName("phone")
    var phone: String?= null,
    @SerializedName("birth_date")
    var birthDate: String?= null,
    @SerializedName("gender")
    var gender: String?= null,
    @SerializedName("status")
    var status: String?= null,

    @SerializedName("first_name")
    var firstName: String?= null,
    @SerializedName("last_name")
    var lastName: String?= null,
    @SerializedName("image_url")
    var imageUrl: String?= null,

    @SerializedName("countryId")
    var countryId: Int?= null,
    @SerializedName("countryTitle")
    var countryTitle: String?= null,

    @SerializedName("cityId")
    var cityId: Int?= null,
    @SerializedName("cityTitle")
    var cityTitle: String?= null,

    @SerializedName("regionId")
    var regionId: Int?= null,
    @SerializedName("regionTitle")
    var regionTitle: String?= null
) : Parcelable

//@Parcelize
//data class Member(
//    @SerializedName("id") var userId: String? = null,
//    @SerializedName("user_type") var userType: String? = null,
//    @SerializedName("accepted_client") var acceptedClient: String? = null,
//    @SerializedName("company_name") var companyName: String? = null,
//    @SerializedName("user_address") var userAddress: String? = null,
//    @SerializedName("country_id") var country_id: String? = null,
//    @SerializedName("city_id") var city_id: String? = null,
//    @SerializedName("region_id") var region_id: String? = null,
//    @SerializedName("country_name_en") var countryNameEn: String? = null,
//    @SerializedName("country_name_ar") var countryNameAr: String? = null,
//    @SerializedName("city_name_en") var cityNameEn: String? = null,
//    @SerializedName("city_name_ar") var cityNameAr: String? = null,
//    @SerializedName("region_name_en") var regionNameEn: String? = null,
//    @SerializedName("region_name_ar") var regionNameAr: String? = null,
//    @SerializedName("name") var userName: String? = null,
//    @SerializedName("email") var userEmail: String? = null,
//    @SerializedName("phone") var userPhone: String? = null,
//    @SerializedName("password") var password: String? = null,
//    @SerializedName("user_pass") var userPass: String? = null,
//    @SerializedName("image") var image: String? = null,
//    @SerializedName("image_url") var mImage: String? = null,
//    @SerializedName("active") var active: String? = null,
//    @SerializedName("status") var status: String? = null,
//    @SerializedName("date_send") var dateSend: String? = null,
//    @SerializedName("time_send") var timeSend: String? = null,
//    @SerializedName("rand_key") var randKey: String? = null,
//    @SerializedName("user_city") var userCity: String? = null,
//    @SerializedName("user_from") var userFrom: String? = null
//) : Parcelable