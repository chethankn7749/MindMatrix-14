package com.hastakala.shop.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hastakala.shop.R
import com.hastakala.shop.models.InventoryEntity
import com.hastakala.shop.models.ProductEntity

@Database(
    entities = [ProductEntity::class, InventoryEntity::class, com.hastakala.shop.models.SaleEntity::class, com.hastakala.shop.models.UserEntity::class],
    version = 3,
    exportSchema = false
)
abstract class HastaKalaDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao
    abstract fun inventoryDao(): InventoryDao

    companion object {
        @Volatile
        private var instance: HastaKalaDatabase? = null

        fun getInstance(context: android.content.Context): HastaKalaDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    HastaKalaDatabase::class.java,
                    "hasta_kala_shop.db"
                )
                    .addMigrations(MIGRATION_2_3)
                    .addCallback(seedCallback(context.applicationContext))
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }

        private fun seedCallback(context: android.content.Context): Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("INSERT INTO products (id, name, category, basePrice, imageRes, featuredColor) VALUES (1, 'Banana Fiber Bag', 'Bags', 850.0, ${R.drawable.ic_bag}, 'Blue')")
                db.execSQL("INSERT INTO products (id, name, category, basePrice, imageRes, featuredColor) VALUES (2, 'Handmade Keychain', 'Accessories', 150.0, ${R.drawable.ic_keychain}, 'Yellow')")
                db.execSQL("INSERT INTO products (id, name, category, basePrice, imageRes, featuredColor) VALUES (3, 'Wall Decor', 'Decor', 650.0, ${R.drawable.ic_decor}, 'Maroon')")
                db.execSQL("INSERT INTO products (id, name, category, basePrice, imageRes, featuredColor) VALUES (4, 'Table Basket', 'Storage', 420.0, ${R.drawable.ic_basket}, 'Natural')")

                db.execSQL("INSERT INTO inventory (productId, productName, color, quantity, reorderLevel, imageRes) VALUES (1, 'Banana Fiber Bag', 'Blue', 8, 3, ${R.drawable.ic_bag})")
                db.execSQL("INSERT INTO inventory (productId, productName, color, quantity, reorderLevel, imageRes) VALUES (1, 'Banana Fiber Bag', 'Red', 2, 3, ${R.drawable.ic_bag})")
                db.execSQL("INSERT INTO inventory (productId, productName, color, quantity, reorderLevel, imageRes) VALUES (2, 'Handmade Keychain', 'Yellow', 12, 3, ${R.drawable.ic_keychain})")
                db.execSQL("INSERT INTO inventory (productId, productName, color, quantity, reorderLevel, imageRes) VALUES (3, 'Wall Decor', 'Maroon', 4, 3, ${R.drawable.ic_decor})")
                db.execSQL("INSERT INTO inventory (productId, productName, color, quantity, reorderLevel, imageRes) VALUES (4, 'Table Basket', 'Natural', 6, 3, ${R.drawable.ic_basket})")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DELETE FROM users WHERE uid NOT IN (SELECT MIN(uid) FROM users GROUP BY email)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_users_email ON users(email)")
            }
        }
    }
}
