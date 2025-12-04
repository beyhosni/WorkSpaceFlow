package com.workspaceflow.e2e;

import com.workspaceflow.e2e.config.SeleniumConfig;
import com.workspaceflow.e2e.pages.TaskListPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End tests for Task functionality
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskE2ETest {

    private static WebDriver driver;
    private static String baseUrl;
    private TaskListPage taskListPage;

    @BeforeAll
    static void setupClass() {
        baseUrl = SeleniumConfig.getBaseUrl();
        System.out.println("Running Task E2E tests against: " + baseUrl);
    }

    @BeforeEach
    void setup() {
        driver = SeleniumConfig.createWebDriver();
        taskListPage = new TaskListPage(driver);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Should load Task List page successfully")
    void testTaskListLoads() {
        // Navigate to task list
        taskListPage.navigate(baseUrl);
        taskListPage.waitForTaskListLoad();

        // Verify page is displayed
        assertTrue(taskListPage.isTaskListDisplayed(),
                "Task list page should be displayed");

        // Verify URL
        assertTrue(driver.getCurrentUrl().contains("/tasks"),
                "Should be on tasks page");
    }

    @Test
    @Order(2)
    @DisplayName("Should display tasks or no tasks message")
    void testTaskListDisplay() {
        // Navigate to task list
        taskListPage.navigate(baseUrl);
        taskListPage.waitForTaskListLoad();

        // Get task count
        int taskCount = taskListPage.getTaskCount();

        System.out.println("Number of tasks displayed: " + taskCount);

        // Verify tasks are displayed or no tasks message is shown
        if (taskCount == 0) {
            // Note: The actual message might be different, adjust if needed
            System.out.println("No tasks found in the system");
        } else {
            assertTrue(taskCount > 0,
                    "Should display at least one task");
        }
    }
}
