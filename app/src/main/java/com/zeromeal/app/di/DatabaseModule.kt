package com.zeromeal.app.di

import android.content.Context
import androidx.room.Room
import com.zeromeal.app.data.local.dao.*
import com.zeromeal.app.data.local.database.ZeroMealDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ZeroMealDatabase {
        return Room.databaseBuilder(
            context,
            ZeroMealDatabase::class.java,
            "zeromeal_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideFoodItemDao(database: ZeroMealDatabase): FoodItemDao {
        return database.foodItemDao()
    }

    @Provides
    fun provideShoppingItemDao(database: ZeroMealDatabase): ShoppingItemDao {
        return database.shoppingItemDao()
    }

    @Provides
    fun provideRecipeDao(database: ZeroMealDatabase): RecipeDao {
        return database.recipeDao()
    }

    @Provides
    fun provideNotificationDao(database: ZeroMealDatabase): NotificationDao {
        return database.notificationDao()
    }

    @Provides
    fun provideFavoriteRecipeDao(database: ZeroMealDatabase): FavoriteRecipeDao {
        return database.favoriteRecipeDao()
    }
}

