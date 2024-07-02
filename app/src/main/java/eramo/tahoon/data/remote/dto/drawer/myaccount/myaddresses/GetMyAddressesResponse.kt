package eramo.tahoon.data.remote.dto.drawer.myaccount.myaddresses


import com.google.gson.annotations.SerializedName

data class GetMyAddressesResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("data")
    val `data`: List<Data?>?
) {
    data class Data(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("address")
        val address: String?,
        @SerializedName("address_type")
        val addressType: String?,
        @SerializedName("city_id")
        val cityId: Int?,
        @SerializedName("country_id")
        val countryId: Int?,
        @SerializedName("region_id")
        val regionId: Int?,
        @SerializedName("subregion_id")
        val subRegionId: Int?,
        @SerializedName("shipping")
        val shipping: String?,
        @SerializedName("city")
        val city: City?,
        @SerializedName("country")
        val country: Country?,
        @SerializedName("region")
        val region: Region?  ,
        @SerializedName("subregion")
        val subRegion: SubRegion?
    ) {
        data class City(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("title")
            val title: String?
        )

        data class Country(
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

        data class SubRegion(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("title")
            val title: String?
        )
    }
}