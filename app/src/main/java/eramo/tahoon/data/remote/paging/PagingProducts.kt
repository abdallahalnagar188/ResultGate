package eramo.tahoon.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import eramo.tahoon.data.remote.EramoApi
import eramo.tahoon.domain.model.products.ShopProductModel
import eramo.tahoon.util.Constants
import eramo.tahoon.util.UserUtil
import retrofit2.HttpException
import java.io.IOException

class PagingProducts(private val eramoApi: EramoApi) :
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
            val result = eramoApi.getShopProducts(
                if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null,
                page.toString()
            ).body()!!.data?.data!!.map { it!!.toShopProductModel() }

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
            LoadResult.Error(e)
        }

    }

}