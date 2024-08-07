package eramo.resultgate.data.remote.dto.alldevices


import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("average_rating")
    var averageRating: String?,
    @SerializedName("category")
    var category: List<Category?>?,
    @SerializedName("category_id")
    var categoryId: Int?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("discount")
    var discount: Int?,
    @SerializedName("fake_price")
    var fakePrice: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("is_fav")
    var isFav: Int?,

//    @SerializedName("new")
//    var newDevice: Int? = null,
//
//    @SerializedName("is_new")
//    var isNewDevice: Int? = null,

    @SerializedName("ofer")
    var ofer: Int?,
    @SerializedName("offer")
    var offer: Int?,
    @SerializedName("offer_end_at")
    var offerEndAt: String?,
    @SerializedName("price_after_taxes")
    var priceAfterTaxes: String?,
    @SerializedName("primary_image_url")
    var primaryImageUrl: String?,
    @SerializedName("product_type")
    var productType: String?,
    @SerializedName("profit_percent")
    var profitPercent: Int?,
    @SerializedName("real_price")
    var realPrice: String?,
    @SerializedName("subcategory_id")
    var subcategoryId: Int?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("to")
    var to: String?,
    @SerializedName("vendor_image")
    var vendorImage: String?,
    @SerializedName("vendor_name")
    var vendorName: String?,
    @SerializedName("vendor_phone")
    var vendorPhone: String?
)