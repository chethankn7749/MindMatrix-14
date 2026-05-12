package com.hastakala.shop.utils

import com.hastakala.shop.R

object ProductVisuals {

    fun iconFor(name: String, category: String?): Int {
        val key = "${name.lowercase()} ${category.orEmpty().lowercase()}"
        return when {
            key.contains("bag") || key.contains("purse") || key.contains("tote") -> R.drawable.ic_bag
            key.contains("keychain") || key.contains("key ring") || key.contains("keyring") -> R.drawable.ic_keychain
            key.contains("decor") || key.contains("wall") || key.contains("art") || key.contains("frame") -> R.drawable.ic_decor
            key.contains("basket") || key.contains("storage") || key.contains("box") || key.contains("bin") -> R.drawable.ic_basket
            key.contains("fabric") || key.contains("cloth") || key.contains("shawl") || key.contains("saree") -> R.drawable.ic_stock
            key.contains("gift") || key.contains("craft") || key.contains("handmade") || key.contains("accessor") -> R.drawable.ic_star_circle
            else -> R.drawable.ic_box_small
        }
    }

    fun iconSurfaceColor(name: String, category: String?): Int {
        val key = "${name.lowercase()} ${category.orEmpty().lowercase()}"
        return when {
            key.contains("bag") || key.contains("purse") || key.contains("tote") -> R.color.phonepe_icon_lilac
            key.contains("keychain") || key.contains("gift") || key.contains("accessor") -> R.color.phonepe_icon_gold
            key.contains("decor") || key.contains("wall") || key.contains("art") -> R.color.phonepe_icon_red
            key.contains("basket") || key.contains("storage") || key.contains("box") -> R.color.phonepe_icon_green
            else -> R.color.phonepe_surface_alt
        }
    }
}
