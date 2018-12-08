package com.arjaywalter.movlancer.ui

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arjaywalter.movlancer.model.Movie
import com.arjaywalter.movlancer.R
import com.arjaywalter.movlancer.getPosterUrl
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_movie.view.*


class MovieListAdapter(private val clickListener: (Movie) -> Unit) : ListAdapter<Movie, MovieListAdapter.ViewHolder>(DIFF_CALLBACK) {

    var requestOptions = RequestOptions()

    init {
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(24))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_movie, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener, requestOptions)
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