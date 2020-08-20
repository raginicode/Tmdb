package com.example.moviekotlin

import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import berhane.biniam.swipeview.utils.action
import berhane.biniam.swipeview.utils.snack
import berhane.biniam.swipy.swipe.whenSwipedTo
import com.example.moviekotlin.api.TheMovieDBClient
import com.example.moviekotlin.api.TheMovieDBInterface
import com.example.moviekotlin.repo.NetworkState
import com.example.moviekotlin.ui.now_playing.*
import kotlinx.android.synthetic.main.activity_main.*
import toastIt

class MainActivity : AppCompatActivity() {


    private lateinit var viewModel: MainActivityViewModel

    lateinit var movieRepository: MoviePagedListRepository



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apiService: TheMovieDBInterface = TheMovieDBClient.getClient()

        movieRepository = MoviePagedListRepository(apiService)

        viewModel = getViewModel()

        val movieAdapter = PopularMoviePagedListAdapter(this)


        val mLayoutManager = LinearLayoutManager(applicationContext)
        rv_movie_list!!.layoutManager = mLayoutManager
        rv_movie_list!!.itemAnimator = DefaultItemAnimator()
        rv_movie_list.setHasFixedSize(true)

        rv_movie_list.whenSwipedTo(this) {
            // when view is swiped to left
            left {
                setColor(R.color.colorPrimary)
                setIcon(R.drawable.ic_delete_black_24dp)
                callback {
                    movieAdapter!!.notifyDataSetChanged()
                    rv_movie_list.snack("Pinned ") {
                        action("Undo") {
                            toastIt("UnPinned ")
                        }
                    }
                }
            }
            // View swiped to right
            longLeft {
                setColor(R.color.blue)
                setIcon(R.drawable.ic_content_copy_black_24dp)
                callback {
                    movieAdapter!!.notifyDataSetChanged()
                    rv_movie_list.snack("Starred ") {
                        action("Undo") {
                            toastIt("UnStarred ")
                        }
                    }

                }
            }
            // when view is swiped to far Right
            right {
                setColor(R.color.colorPrimaryDark)
                setIcon(R.drawable.ic_share_black_24dp)
                callback {
                    val pos = it
                    movieAdapter!!.notifyDataSetChanged()
                    rv_movie_list.snack("Archived ") {
                        action("Undo") {
                            toastIt("UnArchived.")
                        }
                    }
                }
            }

        }

        rv_movie_list!!.adapter = movieAdapter




        viewModel.moviePagedList.observe(this, Observer {
            movieAdapter.submitList(it)
        })

        viewModel.networkState.observe(this, Observer {
            progress_bar_popular.visibility = if (viewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txt_error_popular.visibility = if (viewModel.listIsEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE

            if (!viewModel.listIsEmpty()) {
                movieAdapter.setNetworkState(it)
            }
        })

    }


    private fun getViewModel(): MainActivityViewModel {
        @Suppress("DEPRECATION")
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MainActivityViewModel(movieRepository) as T
            }
        })[MainActivityViewModel::class.java]
    }


}
