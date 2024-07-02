package eramo.tahoon.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import eramo.tahoon.data.remote.EramoApi
import eramo.tahoon.domain.model.SortSearchResultObject
import eramo.tahoon.domain.model.products.ShopProductModel
import eramo.tahoon.util.Constants
import eramo.tahoon.util.UserUtil
import retrofit2.HttpException
import java.io.IOException


class SortSearchResult(
    private val eramoApi: EramoApi,
    private val obj: SortSearchResultObject,
    private val categoryId: String?
) : PagingSource<Int, ShopProductModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ShopProductModel> {
        val page = params.key ?: Constants.PAGING_START_INDEX

        return try {
            val result = eramoApi.sortSearchResult(
                if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null,
                obj.searchTerm,
                if (obj.type == "") null else obj.type,
                if (obj.value == "") null else obj.value,
                obj.priceFrom,
                obj.priceTo, page.toString(),
                categoryId
//            ).body()!!.data!!.get(0)?.data!!.map { it!!.toShopProductModel() }
            ).body()!!.data!!.data!!.map { it!!.toShopProductModel() }


            LoadResult.Page(
                data = result,
                prevKey = if (page == Constants.PAGING_START_INDEX) null else page - 1,
                nextKey =  if (result.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
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