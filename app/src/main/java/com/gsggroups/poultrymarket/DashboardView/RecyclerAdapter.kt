package com.gsggroups.poultrymarket.DashboardView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import Person
import android.annotation.SuppressLint
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.gsggroups.poultrymarket.Common.DropDownManager
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

//    @SuppressLint("NotifyDataSetChanged")
//    fun filter(query: String) {
//        filteredList.clear()
//        if (query.isEmpty()) {
//            filteredList.addAll(userList)
//        } else {
//            val filtered = userList.filter {
//                it.name.contains(query, ignoreCase = true) ||
//                        it.detail.contains(query, ignoreCase = true)
//            }
//            filteredList.addAll(filtered)
//        }
//        notifyDataSetChanged()
//    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        val lowerQuery = query.lowercase().trim()
        filteredList.clear()

        if (lowerQuery.isEmpty()) {
            filteredList.addAll(userList)
        } else {
            val filtered = when (lowerQuery) {
                "batch" -> userList.filter { it.batchReady }
                "need" -> userList.filter { it.needLoad }
                "going" -> userList.filter { it.goingForLoad }
                else -> userList.filter {
                    val stateName = DropDownManager.getStateNameById(it.stateID)?.lowercase()
                    val districtName =
                        DropDownManager.getDistrictNameByIds(it.stateID, it.districtID)?.lowercase()

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

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<UserModel>) {
        userList.clear()
        userList.addAll(newList)

        filteredList.clear()
        filteredList.addAll(newList)

        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(user: UserModel, clickListener: (UserModel) -> Unit) {

            val topBorder = itemView.findViewById<LinearLayout>(R.id.top_border_layout)
            val colorResId = itemView.context.resources.getIdentifier(
                user.colorTemp, // `colorTemp` should match a color name in `colors.xml`
                "color", // Resource type should be "color", not the color name
                itemView.context.packageName
            )
            val color = if (colorResId != 0) {
                ContextCompat.getColor(itemView.context, colorResId)
            } else {
                ContextCompat.getColor(itemView.context, R.color.orange)
            }
            topBorder.setBackgroundColor(color)

            val roleText = itemView.findViewById<TextView>(R.id.tvHeading)
            roleText.text = user.role

            val nameText = itemView.findViewById<TextView>(R.id.tvName)
            nameText.text = user.name

            val farmText = itemView.findViewById<TextView>(R.id.tvProperty)
            farmText.text = user.detail2 // farm name
            val detailText = itemView.findViewById<TextView>(R.id.tvState)
            detailText.text = user.detail // mobile

            val detail2Text = itemView.findViewById<TextView>(R.id.tvDistrict)
            val stateName = DropDownManager.getStateNameById(user.stateID)
            val districtName = DropDownManager.getDistrictNameByIds(user.stateID, user.districtID)
            detail2Text.text = "Location: ${districtName}, ${stateName}"
            
            val statusText = itemView.findViewById<TextView>(R.id.tvStatus)
            statusText.text = user.status
            statusText.setTextColor(color)
            itemView.setOnClickListener {
                clickListener(user) // Invoke the click listener when item is clicked
            }
        }
    }
}