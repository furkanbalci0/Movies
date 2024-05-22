package com.furkanbalci.movie.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.furkanbalci.movie.R
import com.furkanbalci.movie.data.model.MovieDetailModel
import com.furkanbalci.movie.databinding.FragmentDetailBinding
import com.furkanbalci.movie.ui.viewmodel.SharedViewModel
import com.furkanbalci.movie.util.Constants
import com.furkanbalci.movie.util.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private val detailViewModel: DetailViewModel by viewModel {
        parametersOf(Constants.BASE_URL)
    }

    private val movieId: Int by lazy {
        arguments?.getInt(Constants.MOVIE_ID_KEY) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        observeFavorites()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            detailViewModel.getDetail(movieId, Constants.LANGUAGE, Constants.API_KEY)
            detailViewModel.movieDetailLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Error -> {
                        Snackbar.make(binding.root, "$it", Snackbar.LENGTH_LONG).show()
                    }

                    is Resource.Loading -> {
                        if (it.loading) {
                            binding.progressBar.visibility = View.VISIBLE
                        } else {
                            binding.progressBar.visibility = View.GONE
                        }
                    }

                    is Resource.Success -> {
                        it.data?.let { data ->
                            setUI(data)
                        }
                    }
                }
            }
        }
    }

    private fun observeFavorites() {
        lifecycleScope.launch {
            detailViewModel.favoriteMovieLiveData.observe(viewLifecycleOwner) {
                if (it.isFavorite) {
                    binding.starButton.setImageResource(R.drawable.ic_star_filled)
                } else {
                    binding.starButton.setImageResource(R.drawable.ic_star_outlined)
                }
            }
        }
    }

    private fun setUI(movieDetailModel: MovieDetailModel) {
        binding.apply {
            Glide.with(posterImageView)
                .load("https://image.tmdb.org/t/p/w400${movieDetailModel.posterPath}")
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(posterImageView)

            descriptionTextView.text = movieDetailModel.overview
            titleTextView.text = movieDetailModel.originalTitle
            topAppBar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            voteCountTextView.text = binding.root.context.getString(R.string.vote_count, movieDetailModel.voteCount)

            starButton.setOnClickListener {
                detailViewModel.setFavoriteItem(movieDetailModel)
            }

            if (movieDetailModel.isFavorite) {
                starButton.setImageResource(R.drawable.ic_star_filled)
            } else {
                starButton.setImageResource(R.drawable.ic_star_outlined)
            }
        }
    }
}