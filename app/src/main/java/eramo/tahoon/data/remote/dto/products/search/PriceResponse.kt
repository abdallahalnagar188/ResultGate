package eramo.tahoon.data.remote.dto.products.search

import com.google.gson.annotations.SerializedName

data class PriceResponse(
    @SerializedName("MaxPrice") var MaxPrice: String? = null,
    @SerializedName("MinPrice") var MinPrice: String? = null
)