package com.hastakala.shop.repositories

import androidx.lifecycle.LiveData
import com.hastakala.shop.database.UserDao
import com.hastakala.shop.models.UserEntity

class UserRepository(private val userDao: UserDao) {
    fun observeUser(uid: Int): LiveData<UserEntity?> = userDao.observeUser(uid)

    suspend fun saveUser(user: UserEntity): Long = userDao.insert(user)
    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)
    suspend fun getUserById(uid: Int): UserEntity? = userDao.getUserById(uid)
    suspend fun updatePassword(email: String, password: String): Boolean =
        userDao.updatePassword(email.trim().lowercase(), password) > 0
}
