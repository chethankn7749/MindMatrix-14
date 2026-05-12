package com.hastakala.shop.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.hastakala.shop.database.HastaKalaDatabase
import com.hastakala.shop.repositories.SalesRepository
import com.hastakala.shop.repositories.UserRepository

class HastaKalaApplication : Application() {
    lateinit var database: HastaKalaDatabase
        private set
    lateinit var salesRepository: SalesRepository
        private set
    lateinit var userRepository: UserRepository
        private set
    lateinit var sessionManager: SessionManager
        private set

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        database = HastaKalaDatabase.getInstance(this)
        salesRepository = SalesRepository(
            database.productDao(),
            database.saleDao(),
            database.inventoryDao()
        )
        userRepository = UserRepository(database.userDao())
        sessionManager = SessionManager(this)
    }
}
