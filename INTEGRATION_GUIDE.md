# Panduan Integrasi API MySQL ke ZeroMeal App

## Langkah-langkah Setup

### 1. Konfigurasi Base URL

Buka file `app/src/main/java/com/zeromeal/app/di/NetworkModule.kt` dan ubah `BASE_URL`:

```kotlin
private const val BASE_URL = "http://192.168.1.100:3000/api/" // Ganti dengan URL server kamu
```

**Untuk Development Lokal:**
- Gunakan IP address komputer kamu (bukan localhost)
- Contoh: `http://192.168.1.100:3000/api/`
- Pastikan Android device dan komputer dalam jaringan WiFi yang sama

**Untuk Production:**
- Gunakan domain yang sudah di-deploy
- Contoh: `https://api.zeromeal.com/api/`
- Pastikan menggunakan HTTPS

### 2. Tambahkan Permission Internet

Pastikan file `AndroidManifest.xml` sudah memiliki permission internet:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 3. Setup Backend API

Ikuti panduan di file `API_SETUP.md` untuk membuat backend API dengan MySQL.

### 4. Menggunakan Remote Data Source di Repository

Contoh modifikasi repository untuk menggunakan remote data source:

```kotlin
@Singleton
class FoodItemRepositoryImpl @Inject constructor(
    private val localDataSource: FoodItemDao, // Room database
    private val remoteDataSource: RemoteDataSource // API
) : FoodItemRepository {
    
    override fun getAllFoodItems(): Flow<List<FoodItem>> = flow {
        // Coba ambil dari remote (API) dulu
        val remoteResult = remoteDataSource.getAllFoodItems()
        if (remoteResult.isSuccess) {
            val items = remoteResult.getOrNull() ?: emptyList()
            // Simpan ke local database untuk offline access
            items.forEach { item ->
                localDataSource.insertOrUpdate(item.toEntity())
            }
            emit(items)
        } else {
            // Jika gagal, gunakan data dari local database
            emit(localDataSource.getAll().map { it.toDomain() })
        }
    }
    
    override suspend fun insertFoodItem(item: FoodItem) {
        // Simpan ke remote dulu
        val result = remoteDataSource.createFoodItem(item)
        if (result.isSuccess) {
            // Jika berhasil, simpan juga ke local
            localDataSource.insertOrUpdate(item.toEntity())
        } else {
            // Jika gagal, simpan ke local saja (offline mode)
            localDataSource.insertOrUpdate(item.toEntity())
        }
    }
}
```

### 5. Strategi Sync Data

Ada beberapa strategi yang bisa digunakan:

#### A. Remote First (Recommended)
- Selalu coba ambil data dari API terlebih dahulu
- Jika berhasil, update local database
- Jika gagal (offline), gunakan data dari local database
- Cocok untuk aplikasi yang membutuhkan data real-time

#### B. Local First
- Gunakan data dari local database terlebih dahulu
- Sync ke server di background
- Cocok untuk aplikasi yang bisa bekerja offline

#### C. Hybrid
- Kombinasi keduanya
- Data penting (seperti expiring items) ambil dari remote
- Data lain bisa dari local

### 6. Error Handling

Pastikan handle error dengan baik:

```kotlin
suspend fun syncData() {
    try {
        val result = remoteDataSource.getAllFoodItems()
        result.onSuccess { items ->
            // Update local database
        }.onFailure { error ->
            // Log error atau tampilkan notifikasi ke user
            Log.e("Sync", "Failed to sync: ${error.message}")
        }
    } catch (e: Exception) {
        // Handle network error
    }
}
```

### 7. Testing

#### Test dengan Postman:
1. Pastikan backend API sudah running
2. Test endpoint dengan Postman
3. Pastikan response format sesuai dengan yang diharapkan

#### Test di Android App:
1. Build dan install aplikasi
2. Pastikan device/emulator bisa mengakses server
3. Test dengan menambahkan data baru
4. Cek apakah data tersimpan di database MySQL

### 8. Authentication (Opsional untuk Production)

Jika ingin menambahkan authentication:

1. Tambahkan interceptor di `NetworkModule.kt`:

```kotlin
fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(authInterceptor) // Tambahkan auth interceptor
        .addInterceptor(loggingInterceptor)
        .build()
}
```

2. Buat AuthInterceptor:

```kotlin
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${tokenManager.getToken()}")
            .build()
        return chain.proceed(request)
    }
}
```

## Troubleshooting

### 1. Network Error
- Pastikan device dan server dalam jaringan yang sama (untuk development)
- Cek firewall dan port yang digunakan
- Pastikan server sudah running

### 2. CORS Error
- Jika testing dengan browser, pastikan backend mengizinkan CORS
- Untuk Android app, CORS tidak masalah karena menggunakan native HTTP client

### 3. SSL Certificate Error
- Untuk development, bisa disable SSL verification (HANYA untuk development!)
- Untuk production, pastikan menggunakan SSL certificate yang valid

### 4. Data Tidak Tersinkronisasi
- Cek log di Android Studio untuk melihat error
- Pastikan format data sesuai dengan yang diharapkan API
- Cek apakah response dari API sesuai format `ApiResponse`

## Next Steps

1. Setup backend API sesuai `API_SETUP.md`
2. Konfigurasi `BASE_URL` di `NetworkModule.kt`
3. Modifikasi repository untuk menggunakan `RemoteDataSource`
4. Test koneksi dan sinkronisasi data
5. Deploy backend ke production server
6. Update `BASE_URL` ke production URL

## Catatan Penting

- **Development**: Gunakan HTTP untuk testing lokal
- **Production**: WAJIB menggunakan HTTPS
- **Security**: Jangan hardcode credentials di aplikasi
- **Error Handling**: Selalu handle error dengan baik
- **Offline Support**: Pastikan aplikasi tetap bisa digunakan saat offline

