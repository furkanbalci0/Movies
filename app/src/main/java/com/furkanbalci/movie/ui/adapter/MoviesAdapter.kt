package com.furkanbalci.movie.ui.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.furkanbalci.movie.R
import com.furkanbalci.movie.data.model.PopularMoviesModel
import com.furkanbalci.movie.databinding.ItemLoadMoreLayoutBinding
import com.furkanbalci.movie.databinding.ItemMovieLayoutBinding
import com.furkanbalci.movie.util.BaseDiffCallback

class MoviesAdapter(
    private val onItemClick: (model: PopularMoviesModel.MovieModel) -> Unit,
    private val onStarClick: (model: PopularMoviesModel.MovieModel) -> Unit,
    private val onLoadMoreClick: (() -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val MOVIE_VIEW_TYPE = 0
    private val FOOTER_VIEW_TYPE = if (onLoadMoreClick == null) -1 else 1

    var list: MutableList<PopularMoviesModel.MovieModel> = mutableListOf()
        set(value) {
            val oldSet = field.toSet()
            val newSet = value.toSet()
            val diffCallback = BaseDiffCallback(
                oldList = oldSet.toList(),
                newList = newSet.toList(),
                areItemsTheSame = { old, new -> old.id == new.id },
                areContentsTheSame = { old, new ->
                    old.title == new.title && old.posterPath == new.posterPath && old.isFavorite == new.isFavorite
                }
            )
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newSet.toMutableList()
            diffResult.dispatchUpdatesTo(this)
        }

    override fun getItemViewType(position: Int): Int {
        return if (position == list.size) FOOTER_VIEW_TYPE else MOVIE_VIEW_TYPE
    }

    inner class FooterItemViewHolder(private val binding: ItemLoadMoreLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.root.setOnClickListener {
                onLoadMoreClick?.invoke()
            }
        }
    }

    inner class MovieItemViewHolder(
        private val binding: ItemMovieLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movieModel: PopularMoviesModel.MovieModel) {
            binding.apply {
                Glide.with(posterImageView)
                    .load("https://image.tmdb.org/t/p/w400${movieModel.posterPath}")
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(posterImageView)

                titleTextView.text = movieModel.originalTitle
                voteCountTextView.text = binding.root.context.getString(R.string.vote_count, movieModel.voteCount)

                if (movieModel.isFavorite) {
                    starButton.setImageResource(R.drawable.ic_star_filled)
                } else {
                    starButton.setImageResource(R.drawable.ic_star_outlined)
                }

                starButton.setOnClickListener {
                    onStarClick.invoke(movieModel)
                }

                root.setOnClickListener {
                    onItemClick.invoke(movieModel)
                }

            }
        }
    }

    fun updateItem(movieModel: PopularMoviesModel.MovieModel) {
        val indexOf = list.indexOfFirst { it.id == movieModel.id }
        if (indexOf != -1) {
            list[indexOf] = movieModel
            notifyItemChanged(indexOf)
        }
    }

    fun removeItem(movieModel: PopularMoviesModel.MovieModel) {
        val indexOf = list.indexOfFirst { it.id == movieModel.id }
        if (indexOf != -1) {
            list.removeAt(indexOf)
            notifyItemRemoved(indexOf)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == MOVIE_VIEW_TYPE) {
            val binding = ItemMovieLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MovieItemViewHolder(binding)
        } else {
            val binding = ItemLoadMoreLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            FooterItemViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = if (onLoadMoreClick == null) list.size else list.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieItemViewHolder) {
            holder.bind(list[position])
        } else if (holder is FooterItemViewHolder) {
            holder.bind()
        }
    }
}

class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = space
        outRect.right = space
        outRect.bottom = space
        outRect.top = space
    }
}
