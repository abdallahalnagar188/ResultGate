package eramo.resultgate.data.remote.dto.products

import com.google.gson.annotations.SerializedName

data class AllFavListResponse(
    @SerializedName("all_fav_list") var allFavList: ArrayList<AllProductsDto> = arrayListOf()
)