package com.zeromeal.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zeromeal.app.data.local.dao.*
import com.zeromeal.app.data.local.entity.*

@Database(
    entities = [
        FoodItemEntity::class,
        ShoppingItemEntity::class,
        RecipeEntity::class,
        NotificationEntity::class,
        FavoriteRecipeEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ZeroMealDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao
    abstract fun shoppingItemDao(): ShoppingItemDao
    abstract fun recipeDao(): RecipeDao
    abstract fun notificationDao(): NotificationDao
    abstract fun favoriteRecipeDao(): FavoriteRecipeDao
}

