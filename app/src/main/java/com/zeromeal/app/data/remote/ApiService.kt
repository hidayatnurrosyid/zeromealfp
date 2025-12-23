package com.zeromeal.app.data.remote

import com.zeromeal.app.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * API Service untuk komunikasi dengan backend MySQL
 * 
 * Pastikan backend API kamu mengimplementasikan endpoint-endpoint berikut:
 * - Base URL: http://your-server.com/api/
 * - Semua endpoint mengembalikan JSON
 */
interface ApiService {
    
    // ========== Auth ==========
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>
    
    @POST("register")
    suspend fun register(@Body user: UserDto): Response<ApiResponse<UserDto>>
    
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<ApiResponse<UserDto>>
    
    // ========== Forgot Password ==========
    @POST("forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponse<Any>>
    
    @POST("verify-reset-code")
    suspend fun verifyResetCode(@Body request: VerifyCodeRequest): Response<ApiResponse<Any>>
    
    @POST("reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse<Any>>
    
    // ========== Food Items ==========
    @GET("food-items")
    suspend fun getAllFoodItems(): Response<ApiResponse<List<FoodItemDto>>>
    
    @GET("food-items/{id}")
    suspend fun getFoodItemById(@Path("id") id: String): Response<ApiResponse<FoodItemDto>>
    
    @POST("food-items")
    suspend fun createFoodItem(@Body item: FoodItemDto): Response<ApiResponse<FoodItemDto>>
    
    @PUT("food-items/{id}")
    suspend fun updateFoodItem(
        @Path("id") id: String,
        @Body item: FoodItemDto
    ): Response<ApiResponse<FoodItemDto>>
    
    @DELETE("food-items/{id}")
    suspend fun deleteFoodItem(@Path("id") id: String): Response<ApiResponse<Unit>>
    
    @GET("food-items/expiring")
    suspend fun getExpiringItems(@Query("limit") limit: Int = 10): Response<ApiResponse<List<FoodItemDto>>>
    
    // ========== Shopping Items ==========
    @GET("shopping-items")
    suspend fun getAllShoppingItems(): Response<ApiResponse<List<ShoppingItemDto>>>
    
    @GET("shopping-items/{id}")
    suspend fun getShoppingItemById(@Path("id") id: String): Response<ApiResponse<ShoppingItemDto>>
    
    @POST("shopping-items")
    suspend fun createShoppingItem(@Body item: ShoppingItemDto): Response<ApiResponse<ShoppingItemDto>>
    
    @PUT("shopping-items/{id}")
    suspend fun updateShoppingItem(
        @Path("id") id: String,
        @Body item: ShoppingItemDto
    ): Response<ApiResponse<ShoppingItemDto>>
    
    @DELETE("shopping-items/{id}")
    suspend fun deleteShoppingItem(@Path("id") id: String): Response<ApiResponse<Unit>>
    
    // ========== Recipes ==========
    @GET("recipes")
    suspend fun getAllRecipes(): Response<ApiResponse<List<RecipeDto>>>
    
    @GET("recipes/{id}")
    suspend fun getRecipeById(@Path("id") id: String): Response<ApiResponse<RecipeDto>>
    
    @GET("recipes/recommended")
    suspend fun getRecommendedRecipes(@Query("limit") limit: Int = 10): Response<ApiResponse<List<RecipeDto>>>
    
    @GET("recipes/available-ingredients")
    suspend fun getAvailableIngredientRecipes(): Response<ApiResponse<List<RecipeDto>>>
    
    // ========== Notifications ==========
    @GET("notifications")
    suspend fun getAllNotifications(): Response<ApiResponse<List<NotificationDto>>>
    
    @POST("notifications")
    suspend fun createNotification(@Body notification: NotificationDto): Response<ApiResponse<NotificationDto>>
    
    @PUT("notifications/{id}/read")
    suspend fun markNotificationAsRead(@Path("id") id: String): Response<ApiResponse<Unit>>
    
    // ========== Barang (Laravel API) ==========
    @GET("barang")
    suspend fun getBarang(): Response<ApiResponse<List<BarangDto>>>
    
    @GET("barang/{id}")
    suspend fun getBarangById(@Path("id") id: Int): Response<ApiResponse<BarangDto>>
    
    // ========== Resep (Laravel API) ==========
    @GET("resep")
    suspend fun getResep(): Response<ApiResponse<List<ResepDto>>>
    
    @GET("resep/{id}")
    suspend fun getResepById(@Path("id") id: Int): Response<ApiResponse<ResepDto>>
    
    // ========== Inventaris User (Laravel API) ==========
    @GET("inventaris/{user_id}")
    suspend fun getInventarisByUser(@Path("user_id") userId: Int): Response<ApiResponse<List<InventarisDto>>>
    
    @POST("inventaris")
    suspend fun createInventaris(@Body item: InventarisDto): Response<ApiResponse<InventarisDto>>
    
    @PUT("inventaris/{id}")
    suspend fun updateInventaris(
        @Path("id") id: Int,
        @Body item: InventarisDto
    ): Response<ApiResponse<InventarisDto>>
    
    @DELETE("inventaris/{id}")
    suspend fun deleteInventaris(@Path("id") id: Int): Response<ApiResponse<Unit>>
    
    // ========== Daftar Belanja (Laravel API) ==========
    @GET("daftar-belanja/{user_id}")
    suspend fun getDaftarBelanjaByUser(@Path("user_id") userId: Int): Response<ApiResponse<List<DaftarBelanjaDto>>>
    
    @POST("daftar-belanja")
    suspend fun createDaftarBelanja(@Body item: DaftarBelanjaDto): Response<ApiResponse<DaftarBelanjaDto>>
    
    @PUT("daftar-belanja/{id}")
    suspend fun updateDaftarBelanja(
        @Path("id") id: Int,
        @Body item: DaftarBelanjaDto
    ): Response<ApiResponse<DaftarBelanjaDto>>
    
    @DELETE("daftar-belanja/{id}")
    suspend fun deleteDaftarBelanja(@Path("id") id: Int): Response<ApiResponse<Unit>>
}
