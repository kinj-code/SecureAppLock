package com.secureapplock.launcher.manager

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.secureapplock.launcher.R
import com.secureapplock.security.HiddenAppsManager

class VaultAdapter(
    private var apps: List<String>,
    private val hiddenAppsManager: HiddenAppsManager
) : RecyclerView.Adapter<VaultAdapter.VaultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vault_app, parent, false)
        return VaultViewHolder(view as android.view.View)
    }

    override fun onBindViewHolder(holder: VaultViewHolder, position: Int) {
        holder.bind(apps[position], hiddenAppsManager)
    }

    override fun getItemCount() = apps.size

    fun updateApps(newApps: List<String>) {
        apps = newApps
        notifyDataSetChanged()
    }

    inner class VaultViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val appName: TextView = itemView.findViewById(R.id.vault_app_name)
        private val unhideButton: Button = itemView.findViewById(R.id.unhide_button)

        fun bind(packageName: String, manager: HiddenAppsManager) {
            appName.text = packageName
            unhideButton.setOnClickListener {
                manager.unhideApp(packageName)
                // Optionally remove from adapter
            }
        }
    }
}
