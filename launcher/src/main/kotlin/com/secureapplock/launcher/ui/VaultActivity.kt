package com.secureapplock.launcher.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.secureapplock.launcher.R
import com.secureapplock.launcher.databinding.ActivityVaultBinding
import com.secureapplock.launcher.manager.VaultAdapter
import com.secureapplock.security.HiddenAppsManager
import kotlinx.coroutines.launch

/**
 * VaultActivity: Hidden apps vault.
 * Users must authenticate to view and unhide apps.
 */
class VaultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVaultBinding
    private lateinit var hiddenAppsManager: HiddenAppsManager
    private lateinit var adapter: VaultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVaultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hiddenAppsManager = HiddenAppsManager(this)
        setupRecyclerView()
        loadHiddenApps()
    }

    private fun setupRecyclerView() {
        adapter = VaultAdapter(emptyList(), hiddenAppsManager)
        binding.vaultRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.vaultRecyclerView.adapter = adapter
    }

    private fun loadHiddenApps() {
        lifecycleScope.launch {
            val hiddenApps = hiddenAppsManager.getHiddenApps()
            adapter.updateApps(hiddenApps)
        }
    }
}
