package eramo.resultgate.data.remote.dto.products.search

import com.google.gson.annotations.SerializedName
import eramo.resultgate.data.remote.dto.products.AllProductsDto

data class SearchResponse(
    @SerializedName("success") var success: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data_founded") var dataFounded: ArrayList<AllProductsDto> = arrayListOf(),
    @SerializedName("all_data") var all_data: ArrayList<AllProductsDto> = arrayListOf()
)