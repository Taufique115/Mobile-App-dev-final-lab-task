package com.university.newsapp.ui.users

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.university.newsapp.adapter.UserAdapter
import com.university.newsapp.databinding.FragmentUsersBinding
import com.university.newsapp.model.User
import com.university.newsapp.repository.PostRepository
import com.university.newsapp.ui.UserProfileActivity
import com.university.newsapp.utils.Constants
import com.university.newsapp.utils.NetworkErrorHandler
import kotlinx.coroutines.launch

class UsersFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private val repository = PostRepository()
    private val userAdapter = UserAdapter { user -> openUserProfile(user) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvUsers.adapter = userAdapter
        binding.btnRetry.setOnClickListener { fetchUsers() }
        fetchUsers()
    }

    private fun fetchUsers() {
        showLoading()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val users = repository.getAllUsers()
                showSuccess(users)
            } catch (e: Exception) {
                showError(NetworkErrorHandler.getErrorMessage(e))
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.rvUsers.isVisible = false
        binding.layoutError.isVisible = false
    }

    private fun showSuccess(users: List<User>) {
        binding.progressBar.isVisible = false
        binding.layoutError.isVisible = false
        binding.rvUsers.isVisible = true
        userAdapter.submitList(users)
    }

    private fun showError(message: String) {
        binding.progressBar.isVisible = false
        binding.rvUsers.isVisible = false
        binding.layoutError.isVisible = true
        binding.tvError.text = message
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun openUserProfile(user: User) {
        val intent = Intent(requireContext(), UserProfileActivity::class.java).apply {
            putExtra(Constants.EXTRA_USER_ID, user.id)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
