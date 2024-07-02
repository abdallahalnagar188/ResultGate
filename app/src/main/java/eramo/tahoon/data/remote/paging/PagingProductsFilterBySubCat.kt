package eramo.tahoon.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import eramo.tahoon.data.remote.EramoApi
import eramo.tahoon.domain.model.products.ShopProductModel
import eramo.tahoon.domain.model.request.SearchRequest
import eramo.tahoon.util.Constants
import eramo.tahoon.util.UserUtil
import retrofit2.HttpException
import java.io.IOException


class PagingProductsFilterBySubCat(
    private val eramoApi: EramoApi,
    private val  searchRequest: SearchRequest
) : PagingSource<Int, ShopProductModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ShopProductModel> {
        val page = params.key ?: Constants.PAGING_START_INDEX

        return try {
            val result = eramoApi.productsFilterBySubCat(
                if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null,
                if (searchRequest.subCats.isNullOrEmpty()) null else searchRequest.subCats.toString(),
                searchRequest.priceFrom!!, searchRequest.priceTo!!,page.toString()
            ).body()!!.data!!.data!!.map { it!!.toShopProductModel() }

//            Log.e("filter", "category_id ${ if (searchRequest.subCats.isNullOrEmpty()) null else searchRequest.subCats.toString()}")
//            Log.e("filter", "min_price ${ searchRequest.priceFrom!!}")
//            Log.e("filter", "max_price ${ searchRequest.priceTo!!}")
//            Log.e("filter", "page ${page.toString()}")

            LoadResult.Page(
                data = result,
                prevKey = if (page == Constants.PAGING_START_INDEX) null else page - 1,
                nextKey = if (result.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {

            e.printStackTrace()
            LoadResult.Error(e)

        } catch (e: HttpException) {


            e.printStackTrace()
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