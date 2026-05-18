package com.university.newsapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.university.newsapp.R
import com.university.newsapp.adapter.CommentAdapter
import com.university.newsapp.databinding.ActivityPostDetailBinding
import com.university.newsapp.model.Post
import com.university.newsapp.repository.PostRepository
import com.university.newsapp.utils.Constants
import com.university.newsapp.utils.NetworkErrorHandler
import kotlinx.coroutines.launch

class PostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding
    private val repository = PostRepository()
    private val commentAdapter = CommentAdapter()

    private var postId: Int = -1
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        postId = intent.getIntExtra(Constants.EXTRA_POST_ID, -1)
        userId = intent.getIntExtra(Constants.EXTRA_USER_ID, -1)

        if (postId == -1) {
            Toast.makeText(this, R.string.invalid_post, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupCommentsRecyclerView()
        loadPostDetails()
        loadComments()
    }

    private fun setupCommentsRecyclerView() {
        binding.rvComments.layoutManager = LinearLayoutManager(this)
        binding.rvComments.adapter = commentAdapter
        binding.rvComments.isNestedScrollingEnabled = false
    }

    private fun loadPostDetails() {
        showPostLoading()

        lifecycleScope.launch {
            try {
                val post = repository.getPostById(postId)
                userId = post.userId
                showPostSuccess(post)
                loadAuthor()
            } catch (e: Exception) {
                showPostError(NetworkErrorHandler.getErrorMessage(e))
            }
        }
    }

    private fun loadAuthor() {
        if (userId == -1) return

        showAuthorLoading()

        lifecycleScope.launch {
            try {
                val user = repository.getUserById(userId)
                showAuthorSuccess(user.name, user.email, user.company.name)
                binding.cardAuthor.setOnClickListener {
                    val intent = Intent(this@PostDetailActivity, UserProfileActivity::class.java).apply {
                        putExtra(Constants.EXTRA_USER_ID, user.id)
                    }
                    startActivity(intent)
                }
            } catch (e: Exception) {
                showAuthorError(NetworkErrorHandler.getErrorMessage(e))
            }
        }
    }

    private fun loadComments() {
        showCommentsLoading()

        lifecycleScope.launch {
            try {
                val comments = repository.getCommentsByPost(postId)
                showCommentsSuccess(comments.size)
                commentAdapter.submitList(comments)
            } catch (e: Exception) {
                showCommentsError(NetworkErrorHandler.getErrorMessage(e))
            }
        }
    }

    private fun showPostLoading() {
        binding.progressPost.isVisible = true
        binding.layoutPostContent.isVisible = false
        binding.tvPostError.isVisible = false
    }

    private fun showPostSuccess(post: Post) {
        binding.progressPost.isVisible = false
        binding.layoutPostContent.isVisible = true
        binding.tvPostError.isVisible = false
        binding.tvPostTitle.text = post.title
        binding.tvPostBody.text = post.body

        Glide.with(this)
            .load("https://picsum.photos/seed/post${post.id}/400/200")
            .placeholder(R.drawable.bg_avatar_circle)
            .error(R.drawable.bg_avatar_circle)
            .into(binding.ivPostImage)
    }

    private fun showPostError(message: String) {
        binding.progressPost.isVisible = false
        binding.layoutPostContent.isVisible = false
        binding.tvPostError.isVisible = true
        binding.tvPostError.text = message
    }

    private fun showAuthorLoading() {
        binding.progressAuthor.isVisible = true
        binding.cardAuthor.isVisible = false
        binding.tvAuthorError.isVisible = false
    }

    private fun showAuthorSuccess(name: String, email: String, company: String) {
        binding.progressAuthor.isVisible = false
        binding.cardAuthor.isVisible = true
        binding.tvAuthorError.isVisible = false
        binding.tvAuthorName.text = name
        binding.tvAuthorEmail.text = email
        binding.tvAuthorCompany.text = company
    }

    private fun showAuthorError(message: String) {
        binding.progressAuthor.isVisible = false
        binding.cardAuthor.visibility = View.GONE
        binding.tvAuthorError.isVisible = true
        binding.tvAuthorError.text = message
    }

    private fun showCommentsLoading() {
        binding.progressComments.isVisible = true
        binding.rvComments.isVisible = false
        binding.tvCommentsError.isVisible = false
        binding.tvCommentsEmpty.isVisible = false
    }

    private fun showCommentsSuccess(count: Int) {
        binding.progressComments.isVisible = false
        binding.tvCommentsError.isVisible = false
        if (count == 0) {
            binding.rvComments.isVisible = false
            binding.tvCommentsEmpty.isVisible = true
        } else {
            binding.rvComments.isVisible = true
            binding.tvCommentsEmpty.isVisible = false
        }
    }

    private fun showCommentsError(message: String) {
        binding.progressComments.isVisible = false
        binding.rvComments.isVisible = false
        binding.tvCommentsEmpty.isVisible = false
        binding.tvCommentsError.isVisible = true
        binding.tvCommentsError.text = message
    }
}
