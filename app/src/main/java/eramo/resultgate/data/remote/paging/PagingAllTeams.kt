package eramo.resultgate.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import eramo.resultgate.data.remote.EramoApi
import eramo.resultgate.domain.model.teams.TeamsModel
import eramo.resultgate.util.Constants
import eramo.resultgate.util.UserUtil
import retrofit2.HttpException
import java.io.IOException

class PagingAllTeams (
    val eramoApi: EramoApi
): PagingSource<Int, TeamsModel>() {
    override fun getRefreshKey(state: PagingState<Int, TeamsModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TeamsModel> {
        val page = params.key ?: Constants.PAGING_START_INDEX
        return try {
            val result = eramoApi.getALlTeams(
                if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null , page.toString()
            ).body()!!.data!!.data!!.map { it.toTeamModel() }
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