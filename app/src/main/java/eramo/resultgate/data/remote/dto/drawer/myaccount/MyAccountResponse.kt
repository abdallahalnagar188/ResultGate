package eramo.resultgate.data.remote.dto.drawer.myaccount


import com.google.gson.annotations.SerializedName
import eramo.resultgate.data.remote.dto.general.Member

data class MyAccountResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("data")
    val `data`: Data?
) {
    data class Data(
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
        @SerializedName("image_url")
        val imageUrl: String?,
        @SerializedName("country")
        val country: Country?,
        @SerializedName("city")
        val city: City?,
        @SerializedName("region")
        val region: Region?
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

        fun totoMemberModel(): Member {
            return Member(
                id,
                name,
                lang,
                email,
                phone,
                birthDate,
                gender,
                status,
                firstName,
                lastName,
                imageUrl,
                country?.id,
                country?.title,
                city?.id,
                city?.title,
                region?.id,
                region?.title
            )
        }
    }
}