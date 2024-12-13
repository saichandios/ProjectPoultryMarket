package com.gsggroups.poultrymarket.DashboardView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import Person
import android.annotation.SuppressLint
import com.gsggroups.poultrymarket.Model.UserModel
import com.gsggroups.poultrymarket.R

class RecyclerAdapter(private var userList: ArrayList<UserModel>,
                      private val itemClickListener: (UserModel) -> Unit
): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var filteredList: ArrayList<UserModel> = ArrayList(userList)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflator = LayoutInflater.from(parent.context)
        val view = layoutInflator.inflate(R.layout.dashboard_recycler_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = filteredList[position]
        holder.bind(user, itemClickListener)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(userList)
        } else {
            val filtered = userList.filter {
                it.name.contains(query, ignoreCase = true) || it.mobileNumber.contains(query, ignoreCase = true)
            }
            filteredList.addAll(filtered)
        }
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(user: UserModel, clickListener: (UserModel) -> Unit) {
            val roleText = itemView.findViewById<TextView>(R.id.tvHeading)
            roleText.text = user.roleID.toString()

            val nameText = itemView.findViewById<TextView>(R.id.tvName)
            nameText.text = user.name

            val detailText = itemView.findViewById<TextView>(R.id.tvCity)
            detailText.text = user.mobileNumber

            itemView.setOnClickListener {
                clickListener(user) // Invoke the click listener when item is clicked
            }
        }
    }
}