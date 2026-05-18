package com.university.newsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.university.newsapp.databinding.ItemPostBinding
import com.university.newsapp.model.Post

class PostAdapter(
    private val onPostClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val posts = mutableListOf<Post>()

    fun submitList(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    inner class PostViewHolder(
        private val binding: ItemPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.tvPostTitle.text = post.title
            binding.tvPostBody.text = post.body
            binding.tvUserIdBadge.text = binding.root.context.getString(
                com.university.newsapp.R.string.user_id_badge,
                post.userId
            )
            binding.tvPostId.text = binding.root.context.getString(
                com.university.newsapp.R.string.post_id_label,
                post.id
            )
            binding.root.setOnClickListener { onPostClick(post) }
        }
    }
}
