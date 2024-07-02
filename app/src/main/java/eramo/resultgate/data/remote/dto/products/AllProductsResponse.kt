package eramo.resultgate.data.remote.dto.products

import com.google.gson.annotations.SerializedName
import eramo.resultgate.R
import eramo.resultgate.data.remote.dto.products.common.AllImagesDto
import eramo.resultgate.data.remote.dto.products.common.ProductExtrasDto
import eramo.resultgate.data.remote.dto.products.common.ProductFeaturesDto
import eramo.resultgate.domain.model.products.ProductColorModel
import eramo.resultgate.domain.model.products.ProductModel
import eramo.resultgate.domain.model.products.ProductSizesModel
import eramo.resultgate.util.LocalHelperUtil
import eramo.resultgate.util.state.UiText
import kotlinx.parcelize.RawValue

data class AllProductsResponse(
    @SerializedName("all_products") var allProducts: ArrayList<AllProductsDto> = arrayListOf(),
    @SerializedName("latestDeals") var latestDeals: String? = null,
)

data class ProductSizes(
    @SerializedName("id") var id: String? = null,
    @SerializedName("product_id_fk") var productIdFk: String? = null,
    @SerializedName("extra_id_fk") var extraIdFk: String? = null,
    @SerializedName("ttype") var ttype: String? = null,
    @SerializedName("size_name_ar") var sizeNameAr: String? = null,
    @SerializedName("size_name_en") var sizeNameEn: String? = null
) {
    fun toProductSizesModel(): ProductSizesModel {
        return ProductSizesModel(
            id,
            productIdFk,
            extraIdFk,
            ttype,
            if (LocalHelperUtil.isEnglish()) sizeNameEn else sizeNameAr
        )
    }
}

data class ProductColors(
    @SerializedName("id") var id: String? = null,
    @SerializedName("product_id_fk") var productIdFk: String? = null,
    @SerializedName("extra_id_fk") var extraIdFk: String? = null,
    @SerializedName("ttype") var ttype: String? = null,
    @SerializedName("color_name_ar") var colorNameAr: String? = null,
    @SerializedName("color_name_en") var colorNameEn: String? = null,
    @SerializedName("hex_code") var hexCode: String? = null
) {
    fun toProductColorModel(): ProductColorModel {
        return ProductColorModel(
            id,
            productIdFk,
            extraIdFk,
            ttype,
            if (LocalHelperUtil.isEnglish()) colorNameEn else colorNameAr,
            hexCode
        )
    }
}

data class AllProductsDto(
    @SerializedName("id") var productId: String? = null,
    @SerializedName("title_en") var productNameEn: String? = null,
    @SerializedName("title_ar") var productNameAr: String? = null,
    @SerializedName("slug_en") var slugTitleEn: String? = null,
    @SerializedName("slug_ar") var slugTitleAr: String? = null,
    @SerializedName("primary_image") var primaryimage: String?= null,
    @SerializedName("main_cat_id") var mainCatId: String? = null,
    @SerializedName("sub_cat_id") var subCatId: String? = null,
    @SerializedName("manufacturer_id_fk") var manufacturerIdFk: String? = null,
    @SerializedName("supplier_id_fk") var supplierIdFk: String? = null,
    @SerializedName("main_type_id_fk") var mainTypeIdFk: String? = null,
    @SerializedName("sub_type_id_fk") var subTypeIdFk: String? = null,
    @SerializedName("model_id_fk") var modelIdFk: String? = null,
    @SerializedName("power_id_fk") var powerIdFk: String? = null,
    @SerializedName("installation_id_fk") var installationIdFk: String? = null,
    @SerializedName("real_price") var realPrice: String? = null,
    @SerializedName("fake_price") var displayPrice: String? = null,
    @SerializedName("total_tax") var totalTax: String? = null,
    @SerializedName("display_todate") var displayTodate: String? = null,
    @SerializedName("display_todate_s") var displayTodateS: String? = null,
    @SerializedName("descrioption_en") var descrioptionEn: String? = null,
    @SerializedName("descrioption_ar") var descrioptionAr: String? = null,
    @SerializedName("comments_en") var commentsEn: String? = null,
    @SerializedName("comments_ar") var commentsAr: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("featured") var featured: String? = null,
    @SerializedName("hot_cold") var hotCold: String? = null,
    @SerializedName("hot_cold_ar_name") var hotColdArName: String? = null,
    @SerializedName("hot_cold_en_name") var hotColdEnName: String? = null,
    @SerializedName("in_stock") var inStock: String? = null,
    @SerializedName("available_amount") var availableAmount: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("shipping_ar") var shippingAr: String? = null,
    @SerializedName("shipping_en") var shippingEn: String? = null,
    @SerializedName("features_ar") var featuresAr: String? = null,
    @SerializedName("features_en") var featuresEn: String? = null,
    @SerializedName("extras_ar") var extrasAr: String? = null,
    @SerializedName("extras_en") var extrasEn: String? = null,
    @SerializedName("instructions_use_en") var instructionsUseEn: String? = null,
    @SerializedName("instructions_use_ar") var instructionsUseAr: String? = null,
    @SerializedName("model_number") var modelRkm: String? = null,
    @SerializedName("sku_rkm") var skuRkm: String? = null,
    @SerializedName("main_cat_ar") var mainCatAr: String? = null,
    @SerializedName("main_cat_en") var mainCatEn: String? = null,
    @SerializedName("sub_cat_ar") var subCatAr: String? = null,
    @SerializedName("sub_cat_en") var subCatEn: String? = null,
    @SerializedName("main_actype_ar_name") var mainActypeArName: String? = null,
    @SerializedName("main_actype_en_name") var mainActypeEnName: String? = null,
    @SerializedName("sub_actype_ar_name") var subActypeArName: String? = null,
    @SerializedName("sub_actype_en_name") var subActypeEnName: String? = null,
    @SerializedName("manufacturer_ar_name") var manufacturerArName: String? = null,
    @SerializedName("manufacturer_en_name") var manufacturerEnName: String? = null,
    @SerializedName("model_ar_name") var modelArName: String? = null,
    @SerializedName("model_en_name") var modelEnName: String? = null,
    @SerializedName("power_ar_name") var powerArName: String? = null,
    @SerializedName("power_en_name") var powerEnName: String? = null,
    @SerializedName("installation_ar_name") var installationArName: String? = null,
    @SerializedName("installation_en_name") var installationEnName: String? = null,
    @SerializedName("installation_cost") var installationCost: String? = null,
    @SerializedName("cover_from_area") var coverFromArea: String? = null,
    @SerializedName("cover_to_area") var coverToArea: String? = null,
    @SerializedName("taxes_price") var taxesPrice: Double? = null,
    @SerializedName("media") var allImages: List<AllImagesDto>? = null,
    @SerializedName("is_new") var isNew: Boolean? = null,
    @SerializedName("discount") var discount: Int? = null,
    @SerializedName("product_extras") var productExtras: List<ProductExtrasDto>? = null,
    @SerializedName("product_features") var productFeatures: List<ProductFeaturesDto>? = null,
    @SerializedName("check_fav") var checkFav: String? = null,
    @SerializedName("products_with") var extraProducts: ArrayList<AllProductsDto> = arrayListOf(),
    @SerializedName("product_sizes") var productSizes: ArrayList<ProductSizes>? = null,
    @SerializedName("product_colors") var productColors: ArrayList<ProductColors>? = null,
) {
    fun toProductModel(): ProductModel {
        if (LocalHelperUtil.isEnglish()) productNameEn ?: "" else productNameAr ?: ""
        return ProductModel(
            productId = productId ?: "",
            productName = if (LocalHelperUtil.isEnglish()) productNameEn ?: "" else productNameAr
                ?: "",
            slugTitle = if (LocalHelperUtil.isEnglish()) slugTitleEn ?: "" else slugTitleAr ?: "",
            mainCatId = mainCatId ?: "",
            subCatId = subCatId ?: "",
            manufacturerIdFk = manufacturerIdFk ?: "",
            supplierIdFk = supplierIdFk ?: "",
            mainTypeIdFk = mainTypeIdFk ?: "",
            subTypeIdFk = subTypeIdFk ?: "",
            modelIdFk = modelIdFk ?: "",
            powerIdFk = powerIdFk ?: "",
            installationIdFk = installationIdFk ?: "",
            realPrice = realPrice ?: "",
            displayPrice = displayPrice ?: "",
            totalTax = totalTax ?: "",
            displayTodate = displayTodate ?: "",
            displayTodateS = displayTodateS ?: "",
            description = if (LocalHelperUtil.isEnglish()) descrioptionEn ?: "" else descrioptionAr
                ?: "",
            comments = if (LocalHelperUtil.isEnglish()) commentsEn ?: "" else commentsAr ?: "",
            updatedAt = updatedAt ?: "",
            status = getStatus(),
            featured = featured ?: "",
            hotCold = hotCold ?: "",
            hotColdName = if (LocalHelperUtil.isEnglish()) hotColdEnName ?: "" else hotColdArName
                ?: "",
            inStockValue = inStock ?: "",
            inStockText = getStockText(),
            availableAmount = availableAmount ?: "",
            userId = userId ?: "",
            shipping = if (LocalHelperUtil.isEnglish()) shippingEn ?: "" else shippingAr ?: "",
            features = if (LocalHelperUtil.isEnglish()) featuresEn ?: "" else featuresAr ?: "",
            extras = if (LocalHelperUtil.isEnglish()) extrasEn ?: "" else extrasAr ?: "",
            instructionsUse = if (LocalHelperUtil.isEnglish()) instructionsUseEn
                ?: "" else instructionsUseAr ?: "",
            modelNumber = modelRkm ?: "",
            sku = skuRkm ?: "",
            mainCat = if (LocalHelperUtil.isEnglish()) mainCatEn ?: "" else mainCatAr ?: "",
            subCat = if (LocalHelperUtil.isEnglish()) subCatEn ?: "" else subCatAr ?: "",
            mainActypeName = if (LocalHelperUtil.isEnglish()) mainActypeEnName
                ?: "" else mainActypeArName ?: "",
            subActypeName = if (LocalHelperUtil.isEnglish()) subActypeEnName
                ?: "" else subActypeArName ?: "",
            manufacturerName = if (LocalHelperUtil.isEnglish()) manufacturerEnName
                ?: "" else manufacturerArName ?: "",
            modelName = if (LocalHelperUtil.isEnglish()) modelEnName ?: "" else modelArName ?: "",
            powerName = if (LocalHelperUtil.isEnglish()) powerEnName ?: "" else powerArName ?: "",
            installationName = if (LocalHelperUtil.isEnglish()) installationEnName
                ?: "" else installationArName ?: "",
            installationCost = installationCost ?: "",
            coverFromArea = coverFromArea ?: "",
            coverToArea = coverToArea ?: "",
            taxesPrice = taxesPrice ?: 0.0,
            allImageDtos = allImages ?: emptyList(),
            isNew = isNew ?: false,
            discount = discount ?: 0,
            productExtraModels = productExtras?.map { it.toProductExtrasModel() } ?: emptyList(),
            productFeatureModels = productFeatures?.map { it.toProductFeaturesModel() }
                ?: emptyList(),
            isFav = checkFav.equals("in_fav"),
            extraProducts = extraProducts.map { it.toProductModel() },
            productSizes = productSizes?.map { it.toProductSizesModel() },
            productColors = productColors?.map { it.toProductColorModel() },
            primaryimage = primaryimage ?: ""
        )
    }

    private fun getStatus(): @RawValue UiText {
        status?.let {
            if (it == "active") return UiText.StringResource(R.string.active)
            else return UiText.StringResource(R.string.inactive)
        } ?: return UiText.StringResource(R.string.inactive)
    }

    private fun getStockText(): UiText {
        inStock?.let {
            if (inStock.equals("yes"))
                return UiText.StringResource(R.string.in_stock_value, availableAmount.toString())
            else return UiText.StringResource(R.string.out_of_stock)
        } ?: return UiText.DynamicString("")
    }
}