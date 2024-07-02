package eramo.resultgate.data.remote.dto.cart


import com.google.gson.annotations.SerializedName
import eramo.resultgate.domain.model.CartProductModel
import eramo.resultgate.util.removePriceComma

data class MyCartResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("data")
    val `data`: Data?
) {
    data class Data(
        @SerializedName("product")
        val product: List<Product?>?,
        @SerializedName("total_taxes")
        val totalTaxes: String?,
        @SerializedName("total_price")
        val totalPrice: String?,
        @SerializedName("sub_total")
        val subTotal: String?
    ) {

        fun toCartProductModel(): CartProductModel {
            return CartProductModel(
                product?.map { it!!.toProductList() }, totalTaxes, totalPrice, subTotal
            )
        }

        data class Extra(
            @SerializedName("cost")
            val cost: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("quantity")
            val quantity: String?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("total_cost")
            val totalCost: Int?
        ) {
            fun toExtrasList(): CartProductModel.ProductList.Extra {
                return CartProductModel.ProductList.Extra(
                    cost ?: "", id ?: -1, quantity ?: "", title ?: "", totalCost ?: -1
                )
            }
        }

        inner class Product(
            @SerializedName("product_cart_id")
            val productCartId: Int?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("quantity")
            val quantity: Int?,

            @SerializedName("vendor_name")
            val vendorName: String?,
            @SerializedName("color")
            val color: String?,
            @SerializedName("size")
            val size: String?,
            @SerializedName("primary_image_url")
            val image: String?,
            @SerializedName("price")
            var price: String?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("product_category")
            val productCategory: String?,
            @SerializedName("model_number")
            val modelNumber: String?,
            @SerializedName("limitation")
            val limitation: Int?,
            @SerializedName("extras")
            val extras: List<Extra?>?,
            @SerializedName("has_extra")
            val hasExtra: String?

        ) {

            fun toProductList(): CartProductModel.ProductList {
                return CartProductModel.ProductList(
                    productCartId,
                    id,
                    quantity,
                    "-1",
                    "-1",
                    color,
                    size,
                    image,
                    removePriceComma(price ?: "0.0").toFloat(),
                    title,
                    productCategory,
                    modelNumber,
                    limitation,
                    extras?.map { it?.toExtrasList() },
                    hasExtra ?: "",
                    vendorName ?: ""
                )
            }
        }
    }
}