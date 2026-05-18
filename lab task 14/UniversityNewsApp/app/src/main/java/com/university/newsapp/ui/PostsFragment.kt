package com.university.newsapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.university.newsapp.adapter.PostAdapter
import com.university.newsapp.databinding.FragmentPostsBinding
import com.university.newsapp.model.Post
import com.university.newsapp.repository.PostRepository
import com.university.newsapp.utils.Constants
import com.university.newsapp.utils.NetworkErrorHandler
import kotlinx.coroutines.launch

class PostsFragment : Fragment() {

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!

    private val repository = PostRepository()
    private val postAdapter = PostAdapter { post -> openPostDetail(post) }

    private var allPosts: List<Post> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        setupSwipeRefresh()
        binding.btnRetry.setOnClickListener { fetchPosts() }
        fetchPosts()
    }

    private fun setupRecyclerView() {
        binding.rvPosts.adapter = postAdapter
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterPosts(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterPosts(newText)
                return true
            }
        })
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener { fetchPosts(isRefresh = true) }
    }

    private fun filterPosts(query: String?) {
        val filtered = if (query.isNullOrBlank()) {
            allPosts
        } else {
            allPosts.filter { it.title.contains(query, ignoreCase = true) }
        }
        postAdapter.submitList(filtered)
        binding.rvPosts.isVisible = filtered.isNotEmpty() && !binding.progressBar.isVisible
    }

    private fun fetchPosts(isRefresh: Boolean = false) {
        if (!isRefresh) {
            showLoading()
        } else {
            binding.swipeRefresh.isRefreshing = true
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                allPosts = repository.getAllPosts()
                showSuccess()
                val query = binding.searchView.query?.toString()
                filterPosts(query)
            } catch (e: Exception) {
                showError(NetworkErrorHandler.getErrorMessage(e))
            } finally {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.rvPosts.isVisible = false
        binding.layoutError.isVisible = false
        binding.swipeRefresh.isVisible = false
    }

    private fun showSuccess() {
        binding.progressBar.isVisible = false
        binding.layoutError.isVisible = false
        binding.swipeRefresh.isVisible = true
        binding.rvPosts.isVisible = true
    }

    private fun showError(message: String) {
        binding.progressBar.isVisible = false
        binding.swipeRefresh.isVisible = false
        binding.rvPosts.isVisible = false
        binding.layoutError.isVisible = true
        binding.tvError.text = message
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun openPostDetail(post: Post) {
        val intent = Intent(requireContext(), PostDetailActivity::class.java).apply {
            putExtra(Constants.EXTRA_POST_ID, post.id)
            putExtra(Constants.EXTRA_USER_ID, post.userId)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
