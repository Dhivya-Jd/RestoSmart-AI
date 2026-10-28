package com.example;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import com.example.data.AppContainer;
import com.example.data.local.AppDatabase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34, application = SmartRestaurantApp.class)
public class FullAppSmokeTest {

    private SmartRestaurantApp app;

    @Before
    public void setup() {
        app = ApplicationProvider.getApplicationContext();
        assertNotNull("SmartRestaurantApp context should not be null", app);
    }

    @Test
    public void testAllRepositoriesInitialization() {
        AppContainer container = app.getContainer();
        assertNotNull("AppContainer should not be null", container);

        assertNotNull("AuthRepository", container.getAuthRepository());
        assertNotNull("RestaurantRepository", container.getRestaurantRepository());
        assertNotNull("BillingRepository", container.getBillingRepository());
        assertNotNull("MenuRepository", container.getMenuRepository());
        assertNotNull("InventoryRepository", container.getInventoryRepository());
        assertNotNull("PredictionRepository", container.getPredictionRepository());
        assertNotNull("PurchaseRecommendationRepository", container.getPurchaseRecommendationRepository());
        assertNotNull("WasteRepository", container.getWasteRepository());
        assertNotNull("SupplierRepository", container.getSupplierRepository());
        assertNotNull("DemoDataRepository", container.getDemoDataRepository());
    }

    @Test
    public void testDatabaseInstance() {
        AppDatabase db = AppDatabase.Companion.getInstance(app);
        assertNotNull("Room Database", db);
        assertNotNull("RestaurantDao", db.restaurantDao());
        assertNotNull("MenuItemDao", db.menuItemDao());
        assertNotNull("InventoryDao", db.inventoryDao());
        assertNotNull("BillDao", db.billDao());
        assertNotNull("SalesDao", db.salesDao());
        assertNotNull("SupplierDao", db.supplierDao());
        assertNotNull("SupplierPurchaseDao", db.supplierPurchaseDao());
        assertNotNull("SupplierIngredientDao", db.supplierIngredientDao());
        assertNotNull("WasteDao", db.wasteDao());
        assertNotNull("PredictionDao", db.predictionDao());
        assertNotNull("PurchaseRecommendationDao", db.purchaseRecommendationDao());
        assertNotNull("SessionDao", db.sessionDao());
        assertNotNull("IngredientDao", db.ingredientDao());
    }

    @Test
    public void testFullMainActivityLifecycle() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                assertNotNull("MainActivity should be created and not null", activity);
                assertFalse("MainActivity should not be finishing", activity.isFinishing());
            });
        }
    }
}
