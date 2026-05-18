package com.university.newsapp.ui

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.university.newsapp.adapter.UserPostAdapter
import com.university.newsapp.databinding.ActivityUserProfileBinding
import com.university.newsapp.model.Post
import com.university.newsapp.model.User
import com.university.newsapp.repository.PostRepository
import com.university.newsapp.utils.AvatarUtils
import com.university.newsapp.utils.Constants
import com.university.newsapp.utils.NetworkErrorHandler
import kotlinx.coroutines.launch

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private val repository = PostRepository()
    private val userPostAdapter = UserPostAdapter { post -> openPostDetail(post) }

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        userId = intent.getIntExtra(Constants.EXTRA_USER_ID, -1)
        if (userId == -1) {
            Toast.makeText(this, com.university.newsapp.R.string.invalid_user, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.rvUserPosts.layoutManager = LinearLayoutManager(this)
        binding.rvUserPosts.adapter = userPostAdapter
        binding.rvUserPosts.isNestedScrollingEnabled = false

        binding.btnRetryProfile.setOnClickListener { loadUserProfile() }
        binding.btnRetryPosts.setOnClickListener { loadUserPosts() }

        loadUserProfile()
        loadUserPosts()
    }

    private fun loadUserProfile() {
        showProfileLoading()

        lifecycleScope.launch {
            try {
                val user = repository.getUserById(userId)
                showProfileSuccess(user)
            } catch (e: Exception) {
                showProfileError(NetworkErrorHandler.getErrorMessage(e))
            }
        }
    }

    private fun loadUserPosts() {
        showPostsLoading()

        lifecycleScope.launch {
            try {
                val posts = repository.getPostsByUser(userId)
                showPostsSuccess(posts)
            } catch (e: Exception) {
                showPostsError(NetworkErrorHandler.getErrorMessage(e))
            }
        }
    }

    private fun showProfileLoading() {
        binding.progressProfile.isVisible = true
        binding.layoutProfileContent.isVisible = false
        binding.layoutProfileError.isVisible = false
    }

    private fun showProfileSuccess(user: User) {
        binding.progressProfile.isVisible = false
        binding.layoutProfileError.isVisible = false
        binding.layoutProfileContent.isVisible = true

        binding.tvProfileName.text = user.name
        binding.tvProfileUsername.text = "@${user.username}"
        binding.tvProfileEmail.text = user.email
        binding.tvProfilePhone.text = user.phone
        binding.tvProfileWebsite.text = user.website
        binding.tvProfileCompany.text = user.company.name
        binding.tvProfileCatchphrase.text = user.company.catchPhrase

        val initials = AvatarUtils.getInitials(user.name)
        binding.tvProfileAvatar.text = initials

        val background = binding.tvProfileAvatar.background as? GradientDrawable
            ?: GradientDrawable().apply { shape = GradientDrawable.OVAL }
        background.setColor(AvatarUtils.getAvatarColor(user.name))
        binding.tvProfileAvatar.background = background
    }

    private fun showProfileError(message: String) {
        binding.progressProfile.isVisible = false
        binding.layoutProfileContent.isVisible = false
        binding.layoutProfileError.isVisible = true
        binding.tvProfileError.text = message
    }

    private fun showPostsLoading() {
        binding.progressUserPosts.isVisible = true
        binding.rvUserPosts.isVisible = false
        binding.layoutPostsError.isVisible = false
        binding.tvPostsEmpty.isVisible = false
    }

    private fun showPostsSuccess(posts: List<Post>) {
        binding.progressUserPosts.isVisible = false
        binding.layoutPostsError.isVisible = false

        if (posts.isEmpty()) {
            binding.rvUserPosts.isVisible = false
            binding.tvPostsEmpty.isVisible = true
        } else {
            binding.rvUserPosts.isVisible = true
            binding.tvPostsEmpty.isVisible = false
            userPostAdapter.submitList(posts)
        }
    }

    private fun showPostsError(message: String) {
        binding.progressUserPosts.isVisible = false
        binding.rvUserPosts.isVisible = false
        binding.tvPostsEmpty.isVisible = false
        binding.layoutPostsError.isVisible = true
        binding.tvPostsError.text = message
    }

    private fun openPostDetail(post: Post) {
        val intent = Intent(this, PostDetailActivity::class.java).apply {
            putExtra(Constants.EXTRA_POST_ID, post.id)
            putExtra(Constants.EXTRA_USER_ID, post.userId)
        }
        startActivity(intent)
    }
}
