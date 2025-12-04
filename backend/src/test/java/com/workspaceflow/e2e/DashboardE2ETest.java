package com.workspaceflow.e2e;

import com.workspaceflow.e2e.config.SeleniumConfig;
import com.workspaceflow.e2e.pages.DashboardPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End tests for Dashboard functionality
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DashboardE2ETest {

    private static WebDriver driver;
    private static String baseUrl;
    private DashboardPage dashboardPage;

    @BeforeAll
    static void setupClass() {
        baseUrl = SeleniumConfig.getBaseUrl();
        System.out.println("Running E2E tests against: " + baseUrl);
        System.out.println("Make sure the application is running with: docker-compose up");
    }

    @BeforeEach
    void setup() {
        driver = SeleniumConfig.createWebDriver();
        dashboardPage = new DashboardPage(driver);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Should load Dashboard page successfully")
    void testDashboardLoads() {
        // Navigate to dashboard
        dashboardPage.navigate(baseUrl);
        dashboardPage.waitForDashboardLoad();

        // Verify dashboard is displayed
        assertTrue(dashboardPage.isDashboardDisplayed(),
                "Dashboard should be displayed");

        // Verify URL
        assertTrue(driver.getCurrentUrl().contains("/"),
                "Should be on dashboard page");
    }

    @Test
    @Order(2)
    @DisplayName("Should display statistics on Dashboard")
    void testDashboardStatistics() {
        // Navigate to dashboard
        dashboardPage.navigate(baseUrl);
        dashboardPage.waitForDashboardLoad();

        // Get statistics
        String totalInstances = dashboardPage.getTotalInstancesCount();
        String activeInstances = dashboardPage.getActiveInstancesCount();
        String pendingTasks = dashboardPage.getPendingTasksCount();

        // Verify statistics are displayed (should be numbers)
        assertNotNull(totalInstances, "Total instances count should be displayed");
        assertNotNull(activeInstances, "Active instances count should be displayed");
        assertNotNull(pendingTasks, "Pending tasks count should be displayed");

        System.out.println("Dashboard Statistics:");
        System.out.println("  Total Instances: " + totalInstances);
        System.out.println("  Active Instances: " + activeInstances);
        System.out.println("  Pending Tasks: " + pendingTasks);
    }

    @Test
    @Order(3)
    @DisplayName("Should navigate to Instances page from Dashboard")
    void testNavigateToInstances() {
        // Navigate to dashboard
        dashboardPage.navigate(baseUrl);
        dashboardPage.waitForDashboardLoad();

        // Click View All Instances
        dashboardPage.clickViewAllInstances();

        // Wait a bit for navigation
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify navigation
        assertTrue(driver.getCurrentUrl().contains("/instances"),
                "Should navigate to instances page");
    }

    @Test
    @Order(4)
    @DisplayName("Should navigate to Tasks page from Dashboard")
    void testNavigateToTasks() {
        // Navigate to dashboard
        dashboardPage.navigate(baseUrl);
        dashboardPage.waitForDashboardLoad();

        // Click View All Tasks
        dashboardPage.clickViewAllTasks();

        // Wait a bit for navigation
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify navigation
        assertTrue(driver.getCurrentUrl().contains("/tasks"),
                "Should navigate to tasks page");
    }
}
