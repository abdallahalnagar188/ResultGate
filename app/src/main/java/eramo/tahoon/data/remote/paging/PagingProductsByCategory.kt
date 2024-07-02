package eramo.tahoon.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import eramo.tahoon.data.remote.EramoApi
import eramo.tahoon.domain.model.products.ShopProductModel
import eramo.tahoon.util.Constants
import eramo.tahoon.util.MySingleton
import eramo.tahoon.util.UserUtil
import eramo.tahoon.util.removePriceComma
import retrofit2.HttpException
import java.io.IOException


class PagingProductsByCategory(
    private val eramoApi: EramoApi,
    private val catId: String,
    private val brandId:String
) : PagingSource<Int, ShopProductModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ShopProductModel> {
        val page = params.key ?: Constants.PAGING_START_INDEX

        return try {
            val request = eramoApi.allCategorizationByUserId(
                if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null,catId, brandId = brandId,page.toString()
            )

            val result = request.body()!!.data!!.data!!.map { it!!.toShopProductModel() }
            val max = removePriceComma(request.body()?.max?:"0.0").toDouble()

            if (max != 0.0 && max > removePriceComma(MySingleton.filterSubCategoryProductsMaxPrice).toDouble()){
                MySingleton.filterSubCategoryProductsMaxPrice = request.body()?.max ?: "0.0"
            }


            LoadResult.Page(
                data = result,
                prevKey = if (page == Constants.PAGING_START_INDEX) null else page - 1,
                nextKey = if (result.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ShopProductModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}