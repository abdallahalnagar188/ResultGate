package eramo.tahoon.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import eramo.tahoon.data.remote.EramoApi
import eramo.tahoon.domain.model.products.CategoryModel
import eramo.tahoon.util.Constants
import eramo.tahoon.util.UserUtil
import retrofit2.HttpException
import java.io.IOException

class PagingManufacturers(private val eramoApi: EramoApi) : PagingSource<Int, CategoryModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CategoryModel> {
        val page = params.key ?: Constants.PAGING_START_INDEX

        return try {
            val result =
                eramoApi.allProductsManufacturersByUserId(
                    page.toString(),
                    Constants.PAGING_PER_PAGE.toString(),
                    UserUtil.getUserId()
                ).body()!!.all_cats.map { it.toCategoryModel() }

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

    override fun getRefreshKey(state: PagingState<Int, CategoryModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}