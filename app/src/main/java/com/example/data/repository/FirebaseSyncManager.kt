package com.example.data.repository

import android.util.Log
import com.example.data.model.DealEntity
import com.example.data.model.FoodItem
import com.example.data.model.OrderEntity
import com.example.data.model.RestaurantContactEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Manages comprehensive real-time bi-directional cloud synchronization via Firebase Firestore
 * between the Customer/User App and the Restaurant Admin App.
 *
 * Real-Time Sync Streams:
 * 1. Orders: User App (Places Order) -> Firestore "orders" -> Admin App (Instant Kitchen Ringing Alert & Status Updates)
 * 2. Order Tracking: Admin App (Updates Status/ETA/Rider) -> Firestore "orders" -> User App (Live Tracking Map)
 * 3. Menu & Stock: Admin App (Updates Menu/Prices/In-Stock) -> Firestore "menu" -> User App (Live Menu & Sold-Out Tags)
 * 4. Deals & Coupons: Admin App (Adds/Toggles Promo Codes) -> Firestore "deals" -> User App (Instant Checkout Discounts)
 * 5. Store Operations: Admin App (Toggles Store Open/Close & Helpdesk) -> Firestore "settings" -> User App (Real-time Status)
 */
class FirebaseSyncManager(private val repository: ZaykaRepository) {

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (t: Throwable) {
            Log.w(TAG, "FirebaseFirestore initialization failed or unavailable: ${t.message}")
            null
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private var ordersListener: ListenerRegistration? = null
    private var menuListener: ListenerRegistration? = null
    private var dealsListener: ListenerRegistration? = null
    private var settingsListener: ListenerRegistration? = null

    private val _realtimeOrderAlertFlow = kotlinx.coroutines.flow.MutableSharedFlow<OrderEntity>(
        extraBufferCapacity = 64,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )
    val realtimeOrderAlertFlow: kotlinx.coroutines.flow.SharedFlow<OrderEntity> = _realtimeOrderAlertFlow

    private val _isCloudConnected = MutableStateFlow(false)
    val isCloudConnected: StateFlow<Boolean> = _isCloudConnected.asStateFlow()

    private val _isStoreOpenCloud = MutableStateFlow(true)
    val isStoreOpenCloud: StateFlow<Boolean> = _isStoreOpenCloud.asStateFlow()

    companion object {
        private const val TAG = "FirebaseSyncManager"
        const val COLLECTION_ORDERS = "orders"
        const val COLLECTION_MENU = "menu"
        const val COLLECTION_DEALS = "deals"
        const val COLLECTION_SETTINGS = "settings"
        const val DOC_RESTAURANT_INFO = "restaurant_info"
    }

    /**
     * Start all real-time Firestore synchronization listeners (Orders, Menu, Deals, Store Status).
     */
    fun startAllRealtimeSync() {
        val db = firestore
        if (db == null) {
            Log.w(TAG, "Firestore instance not available for sync.")
            _isCloudConnected.value = false
            return
        }

        _isCloudConnected.value = true
        startListeningForOrders()
        startListeningForMenu()
        startListeningForDeals()
        startListeningForRestaurantInfo()
    }

    /**
     * 1. Real-time Orders Listener:
     * Listens to live changes on the "orders" collection in Firestore.
     * When Admin accepts, changes status, updates ETA, or assigns a rider,
     * it instantly syncs into local Room DB and updates the user's live tracking screen.
     */
    fun startListeningForOrders() {
        val db = firestore ?: return
        if (ordersListener != null) return

        try {
            ordersListener = db.collection(COLLECTION_ORDERS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Firestore orders listener error: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        scope.launch {
                            for (doc in snapshot.documents) {
                                try {
                                    val orderId = doc.getLong("orderId") ?: doc.id.toLongOrNull() ?: continue
                                    val status = doc.getString("status") ?: continue
                                    val orderNum = doc.getString("orderNumber") ?: "ZYK-$orderId"
                                    val itemsSummary = doc.getString("itemsSummaryJson") ?: ""
                                    val total = doc.getDouble("total") ?: 0.0
                                    val eta = doc.getLong("etaMinutes")?.toInt() ?: 30
                                    val riderName = doc.getString("riderName") ?: "Ramesh Kumar"
                                    val riderPhone = doc.getString("riderPhone") ?: "+91 98765 43210"
                                    val riderRating = (doc.getDouble("riderRating") ?: 4.9).toFloat()

                                    val existing = repository.getOrderDirect(orderId)
                                    if (existing != null) {
                                        // Update if status, ETA or rider info changed by Admin
                                        if (existing.status != status || existing.etaMinutes != eta || existing.riderName != riderName) {
                                            val updated = existing.copy(
                                                status = status,
                                                etaMinutes = eta,
                                                riderName = riderName,
                                                riderPhone = riderPhone,
                                                riderRating = riderRating
                                            )
                                            repository.insertOrderDirect(updated)
                                            _realtimeOrderAlertFlow.tryEmit(updated)
                                            Log.d(TAG, "Real-time Order #$orderId updated to $status from cloud.")
                                        }
                                    } else {
                                        // Insert newly received order from cloud
                                        val newOrder = OrderEntity(
                                            orderId = orderId,
                                            orderNumber = orderNum,
                                            itemsSummaryJson = itemsSummary,
                                            subtotal = doc.getDouble("subtotal") ?: total,
                                            deliveryFee = doc.getDouble("deliveryFee") ?: 0.0,
                                            tax = doc.getDouble("tax") ?: 0.0,
                                            packagingFee = doc.getDouble("packagingFee") ?: 0.0,
                                            discount = doc.getDouble("discount") ?: 0.0,
                                            total = total,
                                            appliedCoupon = doc.getString("appliedCoupon") ?: "",
                                            deliveryType = doc.getString("deliveryType") ?: "DELIVERY",
                                            deliveryAddress = doc.getString("deliveryAddress") ?: "",
                                            deliveryInstruction = doc.getString("deliveryInstruction") ?: "",
                                            paymentMethod = doc.getString("paymentMethod") ?: "Cash on Delivery",
                                            status = status,
                                            orderTimestamp = doc.getLong("orderTimestamp") ?: System.currentTimeMillis(),
                                            etaMinutes = eta,
                                            riderName = riderName,
                                            riderPhone = riderPhone,
                                            riderRating = riderRating
                                        )
                                        val generatedId = repository.insertOrderDirect(newOrder)
                                        val savedOrder = newOrder.copy(orderId = generatedId)
                                        _realtimeOrderAlertFlow.tryEmit(savedOrder)
                                    }
                                } catch (t: Throwable) {
                                    Log.e(TAG, "Error parsing Firestore order doc: ${t.message}")
                                }
                            }
                        }
                    }
                }
            Log.d(TAG, "Firestore orders real-time synchronization active.")
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to start Firestore orders listener: ${t.message}")
        }
    }

    /**
     * 2. Real-time Menu Listener:
     * Listens for menu item updates, price changes, and stock availability toggles from Admin App.
     */
    fun startListeningForMenu() {
        val db = firestore ?: return
        if (menuListener != null) return

        try {
            menuListener = db.collection(COLLECTION_MENU)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Firestore menu listener error: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        scope.launch {
                            for (doc in snapshot.documents) {
                                try {
                                    val id = doc.getString("id") ?: doc.id
                                    val name = doc.getString("name") ?: continue
                                    val price = doc.getDouble("price") ?: continue
                                    val originalPrice = doc.getDouble("originalPrice") ?: price
                                    val description = doc.getString("description") ?: ""
                                    val category = doc.getString("category") ?: "Specialties"
                                    val isVeg = doc.getBoolean("isVeg") ?: false
                                    val isBestseller = doc.getBoolean("isBestseller") ?: false
                                    val isAvailable = doc.getBoolean("isAvailable") ?: true
                                    val imageDrawableName = doc.getString("imageDrawableName") ?: "biryani_hero"
                                    val rating = (doc.getDouble("rating") ?: 4.5).toFloat()
                                    val ratingCount = doc.getLong("ratingCount")?.toInt() ?: 100

                                    val item = FoodItem(
                                        id = id,
                                        name = name,
                                        description = description,
                                        price = price,
                                        originalPrice = originalPrice,
                                        category = category,
                                        isVeg = isVeg,
                                        isBestseller = isBestseller,
                                        isAvailable = isAvailable,
                                        imageDrawableName = imageDrawableName,
                                        rating = rating,
                                        ratingCount = ratingCount
                                    )
                                    repository.saveFoodItemLocally(item)
                                } catch (t: Throwable) {
                                    Log.e(TAG, "Error syncing menu item from cloud: ${t.message}")
                                }
                            }
                        }
                    }
                }
            Log.d(TAG, "Firestore menu real-time synchronization active.")
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to start Firestore menu listener: ${t.message}")
        }
    }

    /**
     * 3. Real-time Deals & Offers Listener:
     * Listens for promo codes, discount percentages, and coupon active status managed by Admin.
     */
    fun startListeningForDeals() {
        val db = firestore ?: return
        if (dealsListener != null) return

        try {
            dealsListener = db.collection(COLLECTION_DEALS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Firestore deals listener error: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        scope.launch {
                            for (doc in snapshot.documents) {
                                try {
                                    val code = doc.getString("code") ?: doc.id
                                    val title = doc.getString("title") ?: code
                                    val description = doc.getString("description") ?: ""
                                    val discountPercent = (doc.getDouble("discountPercent") ?: doc.getLong("discountPercent")?.toDouble()) ?: 0.0
                                    val maxDiscount = doc.getDouble("maxDiscount") ?: doc.getDouble("maxDiscountAmount") ?: 100.0
                                    val flatDiscount = doc.getDouble("flatDiscount") ?: 0.0
                                    val isFreeDelivery = doc.getBoolean("isFreeDelivery") ?: false
                                    val minOrder = doc.getDouble("minOrder") ?: 199.0
                                    val badgeTag = doc.getString("badgeTag") ?: "POPULAR"
                                    val isActive = doc.getBoolean("isActive") ?: true

                                    val deal = DealEntity(
                                        code = code,
                                        title = title,
                                        description = description,
                                        minOrder = minOrder,
                                        discountPercent = discountPercent,
                                        maxDiscount = maxDiscount,
                                        flatDiscount = flatDiscount,
                                        isFreeDelivery = isFreeDelivery,
                                        isActive = isActive,
                                        badgeTag = badgeTag
                                    )
                                    repository.saveDealLocally(deal)
                                } catch (t: Throwable) {
                                    Log.e(TAG, "Error syncing deal from cloud: ${t.message}")
                                }
                            }
                        }
                    }
                }
            Log.d(TAG, "Firestore deals real-time synchronization active.")
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to start Firestore deals listener: ${t.message}")
        }
    }

    /**
     * 4. Real-time Restaurant Info & Status Listener:
     * Listens to store status (Open/Closed) and official contact helpline from Admin App.
     */
    fun startListeningForRestaurantInfo() {
        val db = firestore ?: return
        if (settingsListener != null) return

        try {
            settingsListener = db.collection(COLLECTION_SETTINGS)
                .document(DOC_RESTAURANT_INFO)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Firestore settings listener error: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        scope.launch {
                            val isOpen = snapshot.getBoolean("isStoreOpen") ?: true
                            _isStoreOpenCloud.value = isOpen

                            val name = snapshot.getString("restaurantName") ?: "Zayka Chicken Cafe"
                            val phone = snapshot.getString("supportPhone") ?: snapshot.getString("primaryPhone") ?: "+91 98765 12345"
                            val whatsapp = snapshot.getString("whatsappNumber") ?: phone
                            val email = snapshot.getString("supportEmail") ?: snapshot.getString("email") ?: "help@zaykacafe.com"
                            val address = snapshot.getString("address") ?: "Near Metro Gate 2, Sector 18, Noida, UP - 201301"
                            val hours = snapshot.getString("operatingHours") ?: snapshot.getString("openingHours") ?: "10:00 AM - 11:30 PM (Mon-Sun)"

                            val contactInfo = RestaurantContactEntity(
                                id = 1,
                                restaurantName = name,
                                supportPhone = phone,
                                whatsappNumber = whatsapp,
                                supportEmail = email,
                                address = address,
                                operatingHours = hours
                            )
                            repository.updateContactInfoLocally(contactInfo)
                        }
                    }
                }
            Log.d(TAG, "Firestore restaurant info synchronization active.")
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to start Firestore settings listener: ${t.message}")
        }
    }

    /**
     * Pushes a placed order to Firestore so the Admin App receives it instantly in the cloud.
     */
    fun syncOrderToCloud(
        orderId: Long,
        order: OrderEntity,
        customerUid: String = "",
        customerName: String = "",
        customerEmail: String = "",
        customerPhone: String = ""
    ) {
        val db = firestore ?: return
        scope.launch {
            try {
                val data = hashMapOf(
                    "orderId" to orderId,
                    "orderNumber" to order.orderNumber,
                    "itemsSummaryJson" to order.itemsSummaryJson,
                    "subtotal" to order.subtotal,
                    "deliveryFee" to order.deliveryFee,
                    "tax" to order.tax,
                    "packagingFee" to order.packagingFee,
                    "discount" to order.discount,
                    "total" to order.total,
                    "appliedCoupon" to order.appliedCoupon,
                    "deliveryType" to order.deliveryType,
                    "deliveryAddress" to order.deliveryAddress,
                    "deliveryInstruction" to order.deliveryInstruction,
                    "paymentMethod" to order.paymentMethod,
                    "status" to order.status,
                    "orderTimestamp" to order.orderTimestamp,
                    "etaMinutes" to order.etaMinutes,
                    "riderName" to order.riderName,
                    "riderPhone" to order.riderPhone,
                    "riderRating" to order.riderRating,
                    "customerUid" to customerUid,
                    "customerName" to customerName,
                    "customerEmail" to customerEmail,
                    "customerPhone" to customerPhone,
                    "createdAt" to com.google.firebase.Timestamp.now()
                )
                db.collection(COLLECTION_ORDERS)
                    .document(orderId.toString())
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d(TAG, "Order #$orderId successfully synced to Firestore cloud.")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Firestore order sync warning: ${e.message}")
                    }
            } catch (t: Throwable) {
                Log.e(TAG, "Error pushing order to Firestore: ${t.message}")
            }
        }
    }

    /**
     * Updates an order's status in Firestore (e.g. from Admin App: PLACED -> CONFIRMED -> PREPARING)
     */
    fun updateOrderStatusInCloud(orderId: Long, newStatus: String) {
        val db = firestore ?: return
        scope.launch {
            try {
                db.collection(COLLECTION_ORDERS)
                    .document(orderId.toString())
                    .update("status", newStatus)
                    .addOnSuccessListener {
                        Log.d(TAG, "Order #$orderId status updated to $newStatus in Firestore.")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Firestore status update warning: ${e.message}")
                    }
            } catch (t: Throwable) {
                Log.e(TAG, "Error updating order status in Firestore: ${t.message}")
            }
        }
    }

    /**
     * Syncs updated menu item from Admin to Firestore
     */
    fun syncFoodItemToCloud(item: FoodItem) {
        val db = firestore ?: return
        scope.launch {
            try {
                val data = hashMapOf(
                    "id" to item.id,
                    "name" to item.name,
                    "description" to item.description,
                    "price" to item.price,
                    "originalPrice" to item.originalPrice,
                    "category" to item.category,
                    "isVeg" to item.isVeg,
                    "isBestseller" to item.isBestseller,
                    "isAvailable" to item.isAvailable,
                    "imageDrawableName" to item.imageDrawableName,
                    "rating" to item.rating,
                    "ratingCount" to item.ratingCount
                )
                db.collection(COLLECTION_MENU)
                    .document(item.id)
                    .set(data, SetOptions.merge())
            } catch (t: Throwable) {
                Log.e(TAG, "Error syncing food item to Firestore: ${t.message}")
            }
        }
    }

    /**
     * Stop Firestore listeners when component/app is destroyed
     */
    fun stopListening() {
        ordersListener?.remove()
        ordersListener = null
        menuListener?.remove()
        menuListener = null
        dealsListener?.remove()
        dealsListener = null
        settingsListener?.remove()
        settingsListener = null
        _isCloudConnected.value = false
    }
}
