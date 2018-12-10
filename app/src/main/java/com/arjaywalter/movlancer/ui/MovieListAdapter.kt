package com.arjaywalter.movlancer.ui

import android.arch.paging.PagedListAdapter
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arjaywalter.movlancer.model.Movie
import com.arjaywalter.movlancer.R
import com.arjaywalter.movlancer.getPosterUrl
import com.arjaywalter.movlancer.utils.NetworkState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_movie.view.*


class MovieListAdapter(private val clickListener: (Movie) -> Unit) : PagedListAdapter<Movie, MovieListAdapter.ViewHolder>(DIFF_CALLBACK) {

    var requestOptions = RequestOptions()

    private var networkState: NetworkState? = null
    private val TYPE_PROGRESS = 0
    private val TYPE_ITEM = 1

    init {
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(24))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_movie, parent, false))
        //TODO Add network state view holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, clickListener, requestOptions) }
        //TODO Add Bind network state view holder
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(movie: Movie, clickListener: (Movie) -> Unit, requestOptions: RequestOptions) {

            itemView.title.text = movie.title
            itemView.ratingBar.rating = (movie.voteAverage!! / 2).toFloat()


            Glide.with(itemView.context)
                    .load(movie.posterPath?.getPosterUrl())
                    .apply(requestOptions)
                    .into(itemView.imageView)

            itemView.setOnClickListener { clickListener(movie) }
        }
    }


    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState !== NetworkState.LOADED
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            TYPE_PROGRESS
        } else {
            TYPE_ITEM
        }
    }
    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = this.networkState
        val previousExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val newExtraRow = hasExtraRow()
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(itemCount)
            } else {
                notifyItemInserted(itemCount)
            }
        } else if (newExtraRow && previousState !== newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object :
                DiffUtil.ItemCallback<Movie>() {
            // Movie details may have changed if reloaded from the database,
            // but ID is fixed.
            override fun areItemsTheSame(oldItem: Movie,
                                         newItem: Movie): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Movie,
                                            newItem: Movie): Boolean =
                    oldItem == newItem
        }
    }
}