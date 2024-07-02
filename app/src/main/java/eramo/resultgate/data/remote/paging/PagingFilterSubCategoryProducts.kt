package eramo.resultgate.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import eramo.resultgate.data.remote.EramoApi
import eramo.resultgate.domain.model.products.ShopProductModel
import eramo.resultgate.util.Constants
import eramo.resultgate.util.UserUtil
import retrofit2.HttpException
import java.io.IOException


class PagingFilterSubCategoryProducts(
    private val eramoApi: EramoApi,
    private val subCategoryId: String,
    private val type: String,
    private val value: String,
    private val max: String,
    private val min: String
) :
    PagingSource<Int, ShopProductModel>() {

    override fun getRefreshKey(state: PagingState<Int, ShopProductModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ShopProductModel> {
        val page = params.key ?: Constants.PAGING_START_INDEX

        return try {
            val result = eramoApi.filterSubCategoryProducts(
                if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null,
                subCategoryId,
                if (type == "") null else type,
                if (value == "") null else value,
                max,
                min,
                page.toString()
//            ).body()!!.data?.get(0)?.data!!.map { it.toShopProductModel() }
            ).body()!!.data?.data!!.map { it!!.toShopProductModel() }

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

}