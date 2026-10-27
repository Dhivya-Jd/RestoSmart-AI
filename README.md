# Food Wise — Smart Restaurant Inventory, POS & Waste Management System

Food Wise is a production-grade Android application built with Kotlin and Jetpack Compose for modern restaurant management. It delivers real-time multi-tenant operations, automated FEFO (First-Expired, First-Out) stock tracking, predictive waste management, point-of-sale billing, and cloud synchronization with Firebase Authentication and Firestore.

## Core Architecture & Key Modules

1. **Authentication & Multi-Tenant Security**:
   - Sole authentication authority: **Firebase Authentication**.
   - Immutable User UID $\rightarrow$ Restaurant ID mapping (`users/{uid}.restaurantId`).
   - Server-side tenant isolation enforced via `firestore.rules`.
   - Local Room database acts purely as an offline cache.

2. **Inventory Management & Smart Expiry (FEFO)**:
   - Automated expiration urgency classification: *Safe*, *Expiring Soon*, *Urgent*, *Expires Today*, *Expired*.
   - First-Expired, First-Out priority ordering.
   - Background monitoring via Android WorkManager (`ExpiryCheckWorker`).

3. **Menu, POS Billing & Order History**:
   - Dynamic menu management with multi-ingredient linking.
   - Point of Sale billing engine with automated bill number sequencing and receipt persistence.

4. **Predictive Analytics & Recommendations**:
   - AI demand prediction based on historical sales patterns.
   - Smart purchase recommendations balancing minimum stock thresholds and lead times.

5. **Cloud Synchronization**:
   - Immediate multi-tenant synchronization across Inventory, Menu, Billing, and Waste records.
   - Full offline functionality with Room local cache.

## Build & Test Instructions

```bash
# Run complete unit and Robolectric test suite:
./gradlew :app:testDebugUnitTest

# Assemble production debug APK:
./gradlew :app:assembleDebug
```

Built with ❤️ for Food Wise.
