# Setup Backend API untuk ZeroMeal App

## Overview
Aplikasi Android ini menggunakan REST API untuk terhubung ke database MySQL. Backend API harus mengimplementasikan endpoint-endpoint berikut.

## Struktur Database MySQL

### 1. Tabel: `food_items`
```sql
CREATE TABLE food_items (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    image_url TEXT,
    category VARCHAR(100) NOT NULL,
    purchase_date DATE NOT NULL,
    expiration_date DATE NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    storage_location VARCHAR(100) NOT NULL,
    notes TEXT,
    is_finished BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 2. Tabel: `shopping_items`
```sql
CREATE TABLE shopping_items (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    quantity VARCHAR(100),
    is_checked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 3. Tabel: `recipes`
```sql
CREATE TABLE recipes (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    image_url TEXT,
    cooking_time INT NOT NULL,
    rating DECIMAL(3, 2) DEFAULT 0.0,
    review_count INT DEFAULT 0,
    difficulty VARCHAR(50) NOT NULL,
    meal_type VARCHAR(50) NOT NULL,
    description TEXT,
    calories INT,
    protein DECIMAL(10, 2),
    fat DECIMAL(10, 2),
    carbs DECIMAL(10, 2),
    fiber DECIMAL(10, 2),
    sodium DECIMAL(10, 2),
    sugar DECIMAL(10, 2),
    chef_name VARCHAR(255),
    chef_image_url TEXT,
    upload_date VARCHAR(100),
    ingredient_availability VARCHAR(50) DEFAULT 'tersedia',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 4. Tabel: `recipe_ingredients`
```sql
CREATE TABLE recipe_ingredients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    recipe_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    quantity VARCHAR(100) NOT NULL,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);
```

### 5. Tabel: `recipe_instructions`
```sql
CREATE TABLE recipe_instructions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    recipe_id VARCHAR(255) NOT NULL,
    step_number INT NOT NULL,
    instruction TEXT NOT NULL,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);
```

### 6. Tabel: `recipe_fun_facts`
```sql
CREATE TABLE recipe_fun_facts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    recipe_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    icon_type VARCHAR(50) NOT NULL,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);
```

### 7. Tabel: `notifications`
```sql
CREATE TABLE notifications (
    id VARCHAR(255) PRIMARY KEY,
    type VARCHAR(50) NOT NULL, -- URGENT, WARNING, INFO, REMINDER, RECIPE
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    action_text VARCHAR(100),
    action_data VARCHAR(255),
    is_read BOOLEAN DEFAULT FALSE,
    timestamp BIGINT NOT NULL, -- Unix timestamp in milliseconds
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## API Endpoints

Base URL: `http://your-server.com/api/`

### Format Response
Semua endpoint mengembalikan format JSON berikut:
```json
{
    "success": true,
    "data": {...},
    "message": "Success message (optional)"
}
```

Jika error:
```json
{
    "success": false,
    "data": null,
    "message": "Error message"
}
```

### 1. Food Items

#### GET `/food-items`
Mengambil semua food items.
**Response:**
```json
{
    "success": true,
    "data": [
        {
            "id": "item-1",
            "name": "Susu UHT",
            "image_url": "https://...",
            "category": "Minuman",
            "purchase_date": "2025-12-15",
            "expiration_date": "2025-12-25",
            "quantity": 1.0,
            "unit": "liter",
            "storage_location": "Kulkas",
            "notes": "Susu segar",
            "is_finished": false
        }
    ]
}
```

#### GET `/food-items/{id}`
Mengambil food item berdasarkan ID.

#### POST `/food-items`
Membuat food item baru.
**Request Body:**
```json
{
    "id": "item-1",
    "name": "Susu UHT",
    "category": "Minuman",
    "purchase_date": "2025-12-15",
    "expiration_date": "2025-12-25",
    "quantity": 1.0,
    "unit": "liter",
    "storage_location": "Kulkas"
}
```

#### PUT `/food-items/{id}`
Update food item.

#### DELETE `/food-items/{id}`
Hapus food item.

#### GET `/food-items/expiring?limit=3`
Mengambil food items yang akan kedaluwarsa.
**Query Parameters:**
- `limit` (optional): Jumlah maksimal items (default: 10)

### 2. Shopping Items

#### GET `/shopping-items`
Mengambil semua shopping items.

#### POST `/shopping-items`
Membuat shopping item baru.
**Request Body:**
```json
{
    "id": "shop-1",
    "name": "Beras",
    "quantity": "5 kg",
    "is_checked": false
}
```

#### PUT `/shopping-items/{id}`
Update shopping item.

#### DELETE `/shopping-items/{id}`
Hapus shopping item.

### 3. Recipes

#### GET `/recipes`
Mengambil semua recipes.

#### GET `/recipes/{id}`
Mengambil recipe berdasarkan ID.
**Response:**
```json
{
    "success": true,
    "data": {
        "id": "recipe-1",
        "name": "Nasi Goreng Ayam",
        "cooking_time": 25,
        "rating": 4.9,
        "review_count": 314,
        "difficulty": "Sedang",
        "meal_type": "Makan Siang",
        "description": "...",
        "calories": 420,
        "protein": 22.0,
        "fat": 17.0,
        "carbs": 58.0,
        "fiber": 3.0,
        "sodium": 890.0,
        "sugar": 8.0,
        "ingredients": [
            {
                "name": "Nasi putih",
                "quantity": "3 piring"
            }
        ],
        "instructions": [
            "Potong daging ayam...",
            "Tumis bawang..."
        ],
        "fun_facts": [
            {
                "title": "Sejarah Nasi Goreng",
                "description": "...",
                "icon_type": "history"
            }
        ],
        "chef_name": "Chef Sari Dewi",
        "upload_date": "13 Januari 2025"
    }
}
```

#### GET `/recipes/recommended?limit=10`
Mengambil recommended recipes.

#### GET `/recipes/available-ingredients`
Mengambil recipes berdasarkan bahan yang tersedia di inventory user.

### 4. Notifications

#### GET `/notifications`
Mengambil semua notifications.

#### POST `/notifications`
Membuat notification baru.
**Request Body:**
```json
{
    "id": "notif-1",
    "type": "URGENT",
    "title": "Produk Akan Kedaluwarsa",
    "message": "Stok Susu UHT akan kedaluwarsa pada 25 Desember 2025",
    "action_text": "Lihat Stok",
    "action_data": "item-1",
    "is_read": false,
    "timestamp": 1735084800000
}
```

#### PUT `/notifications/{id}/read`
Mark notification as read.

## Contoh Implementasi Backend (Node.js + Express)

```javascript
const express = require('express');
const mysql = require('mysql2/promise');
const app = express();

app.use(express.json());

// Database connection
const db = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: 'password',
    database: 'zeromeal_db'
});

// GET /api/food-items
app.get('/api/food-items', async (req, res) => {
    try {
        const [rows] = await db.execute('SELECT * FROM food_items ORDER BY expiration_date ASC');
        res.json({
            success: true,
            data: rows
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            data: null,
            message: error.message
        });
    }
});

// POST /api/food-items
app.post('/api/food-items', async (req, res) => {
    try {
        const { id, name, category, purchase_date, expiration_date, quantity, unit, storage_location, notes } = req.body;
        await db.execute(
            'INSERT INTO food_items (id, name, category, purchase_date, expiration_date, quantity, unit, storage_location, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)',
            [id, name, category, purchase_date, expiration_date, quantity, unit, storage_location, notes]
        );
        res.json({
            success: true,
            data: req.body,
            message: 'Food item created successfully'
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            data: null,
            message: error.message
        });
    }
});

// GET /api/food-items/expiring
app.get('/api/food-items/expiring', async (req, res) => {
    try {
        const limit = parseInt(req.query.limit) || 10;
        const [rows] = await db.execute(
            `SELECT * FROM food_items 
             WHERE expiration_date <= DATE_ADD(CURDATE(), INTERVAL 3 DAY)
             ORDER BY expiration_date ASC 
             LIMIT ?`,
            [limit]
        );
        res.json({
            success: true,
            data: rows
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            data: null,
            message: error.message
        });
    }
});

app.listen(3000, () => {
    console.log('Server running on http://localhost:3000');
});
```

## Konfigurasi di Android App

1. Buka file `NetworkModule.kt`
2. Ganti `BASE_URL` dengan URL server kamu:
   ```kotlin
   private const val BASE_URL = "http://192.168.1.100:3000/api/" // Development
   // atau
   private const val BASE_URL = "https://yourdomain.com/api/" // Production
   ```

3. Pastikan aplikasi memiliki permission INTERNET di `AndroidManifest.xml`:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   ```

## Testing

Gunakan Postman atau curl untuk test API:
```bash
# Test GET food-items
curl http://localhost:3000/api/food-items

# Test POST food-items
curl -X POST http://localhost:3000/api/food-items \
  -H "Content-Type: application/json" \
  -d '{
    "id": "item-1",
    "name": "Susu UHT",
    "category": "Minuman",
    "purchase_date": "2025-12-15",
    "expiration_date": "2025-12-25",
    "quantity": 1.0,
    "unit": "liter",
    "storage_location": "Kulkas"
  }'
```

## Catatan Penting

1. **CORS**: Jika menggunakan web browser untuk testing, pastikan backend mengizinkan CORS
2. **Authentication**: Untuk production, tambahkan authentication (JWT token)
3. **Error Handling**: Pastikan semua endpoint menangani error dengan baik
4. **Validation**: Validasi semua input data sebelum menyimpan ke database
5. **Security**: Gunakan HTTPS untuk production, sanitize input untuk mencegah SQL injection

