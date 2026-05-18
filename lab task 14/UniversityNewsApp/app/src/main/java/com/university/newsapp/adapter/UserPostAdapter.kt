package com.university.newsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.university.newsapp.databinding.ItemUserPostBinding
import com.university.newsapp.model.Post

class UserPostAdapter(
    private val onPostClick: (Post) -> Unit
) : RecyclerView.Adapter<UserPostAdapter.UserPostViewHolder>() {

    private val posts = mutableListOf<Post>()

    fun submitList(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPostViewHolder {
        val binding = ItemUserPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserPostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    inner class UserPostViewHolder(
        private val binding: ItemUserPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.tvPostTitle.text = post.title
            binding.root.setOnClickListener { onPostClick(post) }
        }
    }
}
