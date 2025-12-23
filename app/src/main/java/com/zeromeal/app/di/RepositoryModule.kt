package com.zeromeal.app.di

import com.zeromeal.app.data.repository.*
import com.zeromeal.app.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindFoodItemRepository(
        foodItemRepositoryImpl: FoodItemRepositoryImpl
    ): FoodItemRepository

    @Binds
    @Singleton
    abstract fun bindShoppingItemRepository(
        shoppingItemRepositoryImpl: ShoppingItemRepositoryImpl
    ): ShoppingItemRepository

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(
        recipeRepositoryImpl: RecipeRepositoryImpl
    ): RecipeRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindThemeRepository(
        themeRepositoryImpl: ThemeRepositoryImpl
    ): ThemeRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRecipeRepository(
        favoriteRecipeRepositoryImpl: FavoriteRecipeRepositoryImpl
    ): FavoriteRecipeRepository

    @Binds
    @Singleton
    abstract fun bindBarangRepository(
        barangRepositoryImpl: BarangRepositoryImpl
    ): BarangRepository

    @Binds
    @Singleton
    abstract fun bindInventarisRepository(
        inventarisRepositoryImpl: InventarisRepositoryImpl
    ): InventarisRepository

    @Binds
    @Singleton
    abstract fun bindResepLaravelRepository(
        resepLaravelRepositoryImpl: ResepLaravelRepositoryImpl
    ): ResepLaravelRepository

    @Binds
    @Singleton
    abstract fun bindDaftarBelanjaRepository(
        daftarBelanjaRepositoryImpl: DaftarBelanjaRepositoryImpl
    ): DaftarBelanjaRepository
}
