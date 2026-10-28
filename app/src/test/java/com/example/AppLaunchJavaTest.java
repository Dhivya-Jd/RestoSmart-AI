package com.example;

import android.app.Application;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = SmartRestaurantApp.class, sdk = 34)
public class AppLaunchJavaTest {

    @Test
    public void testApplicationInit() {
        System.out.println("--- Starting testApplicationInit ---");
        SmartRestaurantApp app = (SmartRestaurantApp) ApplicationProvider.getApplicationContext();
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.getContainer());
        System.out.println("--- App Container initialized successfully: " + app.getContainer() + " ---");
    }

    @Test
    public void testMainActivityLaunch() {
        System.out.println("--- Starting testMainActivityLaunch ---");
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                Assert.assertNotNull(activity);
                System.out.println("--- MainActivity launched successfully! ---");
            });
        }
    }
}
