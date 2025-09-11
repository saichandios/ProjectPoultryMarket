package com.gsggroups.poultrymarket.DashboardView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gsggroups.poultrymarket.Common.DropDownManager
import com.gsggroups.poultrymarket.Model.UserModel
import com.gsggroups.poultrymarket.R

class RecyclerAdapter(
    private val userList: ArrayList<UserModel>,
    private val itemClickListener: (UserModel) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var filteredList: ArrayList<UserModel> = ArrayList(userList)
    private var showLoadingFooter = false

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    override fun getItemViewType(position: Int): Int {
        return if (position < filteredList.size) VIEW_TYPE_ITEM else VIEW_TYPE_LOADING
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.dashboard_recycler_row, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder && position < filteredList.size) {
            val user = filteredList[position]
            holder.bind(user, itemClickListener)
        }
        // LoadingViewHolder does not need binding
    }

    override fun getItemCount(): Int {
        return filteredList.size + if (showLoadingFooter) 1 else 0
    }

    fun showLoadingFooter(show: Boolean) {
        if (show == showLoadingFooter) return
        showLoadingFooter = show
        if (show) notifyItemInserted(filteredList.size)
        else notifyItemRemoved(filteredList.size)
    }

    fun updateList(newList: List<UserModel>) {
        userList.clear()
        userList.addAll(newList)

        filteredList.clear()
        filteredList.addAll(newList)

        notifyDataSetChanged()
    }

    fun appendList(newItems: List<UserModel>) {
        val start = filteredList.size
        userList.addAll(newItems)
        filteredList.addAll(newItems)
        notifyItemRangeInserted(start, newItems.size)
    }

    @Suppress("NotifyDataSetChanged")
    fun filter(query: String) {
        val lowerQuery = query.lowercase().trim()
        filteredList.clear()

        if (lowerQuery.isEmpty()) {
            filteredList.addAll(userList)
        } else {
            val filtered = when (lowerQuery) {
                "batchready" -> userList.filter { it.batchReady }
                "needload" -> userList.filter { it.needLoad }
                "goingforload" -> userList.filter { it.goingForLoad }
                else -> userList.filter {
                    val stateName = DropDownManager.getStateNameById(it.stateID)?.lowercase()
                    val districtName = DropDownManager.getDistrictNameByIds(it.stateID, it.districtID)?.lowercase()
                    it.name.contains(lowerQuery, ignoreCase = true) ||
                            it.detail.contains(lowerQuery, ignoreCase = true) ||
                            stateName?.contains(lowerQuery) ?: false ||
                            districtName?.contains(lowerQuery) ?: false
                }
            }
            filteredList.addAll(filtered)
        }
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: UserModel, clickListener: (UserModel) -> Unit) {
            val topBorder = itemView.findViewById<LinearLayout>(R.id.top_border_layout)
            val colorResId = itemView.context.resources.getIdentifier(
                user.colorTemp, "color", itemView.context.packageName
            )
            val color = if (colorResId != 0) ContextCompat.getColor(itemView.context, colorResId)
            else ContextCompat.getColor(itemView.context, R.color.orange)
            topBorder.setBackgroundColor(color)

            itemView.findViewById<TextView>(R.id.tvHeading).text = user.role
            itemView.findViewById<TextView>(R.id.tvName).text = user.name
            itemView.findViewById<TextView>(R.id.tvProperty).text = user.detail2
            val stateName = DropDownManager.getStateNameById(user.stateID+1) ?: "Unknown"
            val districtName = DropDownManager.getDistrictNameByIds(user.stateID+1, user.districtID) ?: "Unknown"
            itemView.findViewById<TextView>(R.id.tvDistrict).text = "Location: $districtName, $stateName"
            val statusText = itemView.findViewById<TextView>(R.id.tvStatus)
            statusText.text = user.status
            statusText.setTextColor(color)

            itemView.setOnClickListener { clickListener(user) }
        }
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
