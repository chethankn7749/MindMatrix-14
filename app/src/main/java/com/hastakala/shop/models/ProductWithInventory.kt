package com.hastakala.shop.models

data class ProductWithInventory(
    val product: ProductEntity,
    val stock: List<InventoryEntity>
)
