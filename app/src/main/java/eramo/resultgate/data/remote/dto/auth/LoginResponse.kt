package eramo.resultgate.data.remote.dto.auth


import com.google.gson.annotations.SerializedName
import eramo.resultgate.domain.model.auth.LoginModel

data class LoginResponse(
    @SerializedName("token")
    val token: String?,
    @SerializedName("member")
    val member: Member?
) {
    data class Member(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("lang")
        val lang: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("phone")
        val phone: String?,
        @SerializedName("birth_date")
        val birthDate: String?,
        @SerializedName("gender")
        val gender: String?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("country_id")
        val countryId: Int?,
        @SerializedName("city_id")
        val cityId: Int?,
        @SerializedName("region_id")
        val regionId: Int?,
        @SerializedName("first_name")
        val firstName: String?,
        @SerializedName("last_name")
        val lastName: String?,
        @SerializedName("verified_status")
        val verifiedStatus: Int?,
        @SerializedName("country")
        val country: Country?,
        @SerializedName("city")
        val city: City?,
        @SerializedName("region")
        val region: Region?,
        @SerializedName("image_url")
        val imageUrl: String?
    ) {
        data class Country(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("title")
            val title: String?
        )

        data class City(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("title")
            val title: String?
        )

        data class Region(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("title")
            val title: String?
        )
    }

    fun toLoginModel():LoginModel{
        return LoginModel(member,token)
    }
}