package eramo.resultgate.data.remote.dto.products.search


import com.google.gson.annotations.SerializedName
import eramo.resultgate.data.remote.dto.products.common.AllImagesDto
import eramo.resultgate.domain.model.products.ProductModel
import eramo.resultgate.util.state.UiText

class MyHomePageSearchResponse : ArrayList<MyHomePageSearchResponse.MyHomePageSearchResponseItem>(){
    data class MyHomePageSearchResponseItem(
        @SerializedName("add_products_together")
        val addProductsTogether: String?,
        @SerializedName("admin_id")
        val adminId: Int?,
        @SerializedName("average_rating")
        val averageRating: Int?,
        @SerializedName("category_id")
        val categoryId: Int?,
        @SerializedName("city_id")
        val cityId: Int?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("deleted_at")
        val deletedAt: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("details")
        val details: String?,
        @SerializedName("extras")
        val extras: String?,
        @SerializedName("fake_price")
        val fakePrice: Int?,
        @SerializedName("featured_product")
        val featuredProduct: String?,
        @SerializedName("features")
        val features: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("instructions")
        val instructions: String?,
        @SerializedName("keywords")
        val keywords: String?,
        @SerializedName("limitation")
        val limitation: Int?,
        @SerializedName("main_category_id")
        val mainCategoryId: Int?,
        @SerializedName("material")
        val material: String?,
        @SerializedName("model_number")
        val modelNumber: String?,
        @SerializedName("number_of_sales")
        val numberOfSales: Int?,
        @SerializedName("price_after_taxes")
        val priceAfterTaxes: String?,
        @SerializedName("primary_image")
        val primaryImage: String?,
        @SerializedName("primary_image_url")
        val primaryImageUrl: String?,
        @SerializedName("profit_percent")
        val profitPercent: Int?,
        @SerializedName("purchase_price")
        val purchasePrice: Int?,
        @SerializedName("real_price")
        val realPrice: Int?,
        @SerializedName("shipping")
        val shipping: String?,
        @SerializedName("sku_number")
        val skuNumber: String?,
        @SerializedName("slug")
        val slug: String?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("stock")
        val stock: Int?,
        @SerializedName("summary")
        val summary: String?,
        @SerializedName("taxes")
        val taxes: List<Taxe?>?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("to")
        val to: String?,
        @SerializedName("updated_at")
        val updatedAt: String?,
        @SerializedName("video")
        val video: String?,
        @SerializedName("views")
        val views: Int?
    ) {
        data class Taxe(
            @SerializedName("admin_id")
            val adminId: Int?,
            @SerializedName("created_at")
            val createdAt: String?,
            @SerializedName("deleted_at")
            val deletedAt: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("percentage")
            val percentage: Int?,
            @SerializedName("status")
            val status: String?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("updated_at")
            val updatedAt: String?
        )

        fun toProductModel(): ProductModel {
            return ProductModel(
                productId = id.toString(),
                productName =  title?:"",
                slugTitle =  slug?: "",
                mainCatId = mainCategoryId.toString(),
                subCatId = "",
                manufacturerIdFk = "",
                supplierIdFk = "",
                mainTypeIdFk = "",
                subTypeIdFk = "",

                modelIdFk = modelNumber ?: "",

                powerIdFk = "",
                installationIdFk = "",

                realPrice = realPrice.toString() ,
                displayPrice = purchasePrice.toString() ,
                totalTax = "",

                displayTodate = "",
                displayTodateS = "",

                description = description ?: "",

                comments = "",

                updatedAt = updatedAt ?: "",
                status =  UiText.DynamicString(stock.toString()) ,
                featured = featuredProduct ?: "",
                hotCold = "",
                hotColdName = "",
                inStockValue = stock.toString(),
                inStockText = UiText.DynamicString(stock.toString()),
                availableAmount = "",
                userId = "",
                shipping = shipping.toString(),
                features = features ?: "",
                extras = extras ?: "",
                instructionsUse = instructions ?: "",
                modelNumber = modelNumber ?: "",
                sku = skuNumber ?: "",
                mainCat =  "",
                subCat =  "",
                mainActypeName = "",
                subActypeName = "",
                manufacturerName = "",
                modelName = "",
                powerName = "",
                installationName = "",
                installationCost = "",
                coverFromArea = "",
                coverToArea = "",
                taxesPrice = taxes?.get(0)?.percentage?.toDouble() ?: 0.0,
                allImageDtos = listOf(AllImagesDto(image = primaryImageUrl)),
                isNew = false,
                discount = 0,
                productExtraModels = emptyList(),
                productFeatureModels = emptyList(),
                isFav = false,
                extraProducts = emptyList(),
                productSizes = emptyList(),
                productColors = emptyList(),
                primaryimage = primaryImage ?: "",
            )
        }

    }
}