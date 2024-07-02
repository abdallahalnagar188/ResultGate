package eramo.tahoon.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import eramo.tahoon.databinding.LayoutLoadStateFooterBinding

class FooterLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<FooterLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) = LoadStateViewHolder(
        LayoutLoadStateFooterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class LoadStateViewHolder(private val binding: LayoutLoadStateFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.HFLayoutBtnRetry.setOnClickListener{
                retry.invoke()
            }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                HFLayoutPb.isVisible = loadState is LoadState.Loading
                HFLayoutTvNoLoad.isVisible = loadState !is LoadState.Loading
                HFLayoutBtnRetry.isVisible = loadState !is LoadState.Loading
            }
        }
    }
}