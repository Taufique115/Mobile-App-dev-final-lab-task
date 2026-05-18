package com.university.newsapp.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.university.newsapp.databinding.ItemUserBinding
import com.university.newsapp.model.User
import com.university.newsapp.utils.AvatarUtils

class UserAdapter(
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val users = mutableListOf<User>()

    fun submitList(newUsers: List<User>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    inner class UserViewHolder(
        private val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.tvUserName.text = user.name
            binding.tvUsername.text = "@${user.username}"
            binding.tvUserEmail.text = user.email

            val initials = AvatarUtils.getInitials(user.name)
            binding.tvAvatarInitials.text = initials

            val background = binding.tvAvatarInitials.background as? GradientDrawable
                ?: GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                }
            background.setColor(AvatarUtils.getAvatarColor(user.name))
            binding.tvAvatarInitials.background = background

            binding.root.setOnClickListener { onUserClick(user) }
        }
    }
}
