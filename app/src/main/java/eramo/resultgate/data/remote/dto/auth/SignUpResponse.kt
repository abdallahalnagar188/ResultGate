package eramo.resultgate.data.remote.dto.auth


import com.google.gson.annotations.SerializedName
import eramo.resultgate.domain.model.auth.SignUpModel

data class SignUpResponse(
    @SerializedName("data")
    val data: Data?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?
) {
    data class Data(
        @SerializedName("token")
        val token: String?,
        @SerializedName("user")
        val user: User?
    ) {
        data class User(
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
            val region: Region?,
            @SerializedName("job")
            val job: String?,
            @SerializedName("job_location")
            val jopLocation: String?,
            @SerializedName("research_interests")
            val researchInterests: String?,
            @SerializedName("become_vendor")
            val vendorType: String?,
            @SerializedName("academic_degree")
            val academicDegree: String?,
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
    }

    fun toSignUpModel(): SignUpModel {
        return SignUpModel(
            data?.user?.id ?: -1,
            data?.token ?: "",
            data?.user?.name ?: "",
            data?.user?.firstName ?: "",
            data?.user?.lastName ?: "",
            data?.user?.email ?: "",
            data?.user?.phone ?: "",
            data?.user?.birthDate ?: "",
            data?.user?.gender ?: "",
            data?.user?.lang ?: "ar",
            data?.user?.imageUrl ?: "",
            data?.user?.country?.id ?: -1,
            data?.user?.country?.title ?: "",
            data?.user?.city?.id ?: -1,
            data?.user?.city?.title ?: "",
            data?.user?.region?.id ?: -1,
            data?.user?.region?.title ?: "",
            message ?: "Error",
            job = data?.user?.job?:"",
            jobLocation = data?.user?.jopLocation?:"",
            researchInterests = data?.user?.researchInterests?:"",
            academicDegree = data?.user?.academicDegree?:"",
            vendorType = data?.user?.vendorType?:""
        )
    }
}