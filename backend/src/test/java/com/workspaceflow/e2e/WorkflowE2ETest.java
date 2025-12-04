package com.workspaceflow.e2e;

import com.workspaceflow.e2e.config.SeleniumConfig;
import com.workspaceflow.e2e.pages.CreateWorkflowPage;
import com.workspaceflow.e2e.pages.WorkflowListPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End tests for Workflow functionality
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WorkflowE2ETest {

    private static WebDriver driver;
    private static String baseUrl;
    private WorkflowListPage workflowListPage;
    private CreateWorkflowPage createWorkflowPage;

    @BeforeAll
    static void setupClass() {
        baseUrl = SeleniumConfig.getBaseUrl();
        System.out.println("Running Workflow E2E tests against: " + baseUrl);
    }

    @BeforeEach
    void setup() {
        driver = SeleniumConfig.createWebDriver();
        workflowListPage = new WorkflowListPage(driver);
        createWorkflowPage = new CreateWorkflowPage(driver);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Should load Workflow List page successfully")
    void testWorkflowListLoads() {
        // Navigate to workflow list
        workflowListPage.navigate(baseUrl);
        workflowListPage.waitForWorkflowListLoad();

        // Verify page is displayed
        assertTrue(workflowListPage.isWorkflowListDisplayed(),
                "Workflow list page should be displayed");

        // Verify URL
        assertTrue(driver.getCurrentUrl().contains("/workflows"),
                "Should be on workflows page");
    }

    @Test
    @Order(2)
    @DisplayName("Should navigate to Create Workflow page")
    void testNavigateToCreateWorkflow() {
        // Navigate to workflow list
        workflowListPage.navigate(baseUrl);
        workflowListPage.waitForWorkflowListLoad();

        // Click Create Workflow
        workflowListPage.clickCreateWorkflow();

        // Wait for navigation
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify navigation
        assertTrue(driver.getCurrentUrl().contains("/workflows/create"),
                "Should navigate to create workflow page");
    }

    @Test
    @Order(3)
    @DisplayName("Should create a new workflow successfully")
    void testCreateWorkflow() {
        // Navigate to create workflow page
        createWorkflowPage.navigate(baseUrl);
        createWorkflowPage.waitForCreateWorkflowLoad();

        // Verify page is displayed
        assertTrue(createWorkflowPage.isCreateWorkflowPageDisplayed(),
                "Create workflow page should be displayed");

        // Fill workflow details
        String workflowName = "E2E Test Workflow " + System.currentTimeMillis();
        String workflowDescription = "This is a test workflow created by Selenium E2E test";

        createWorkflowPage
                .fillWorkflowName(workflowName)
                .fillWorkflowDescription(workflowDescription)
                .fillFirstStepName("Review Request")
                .fillFirstStepAssigneeRole("manager");

        // Submit the form
        createWorkflowPage.submitForm();

        // Wait for navigation back to workflow list
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify navigation to workflow list
        assertTrue(driver.getCurrentUrl().contains("/workflows") &&
                !driver.getCurrentUrl().contains("/create"),
                "Should navigate back to workflow list after creation");

        System.out.println("Successfully created workflow: " + workflowName);
    }

    @Test
    @Order(4)
    @DisplayName("Should display workflows in the list")
    void testWorkflowListDisplay() {
        // Navigate to workflow list
        workflowListPage.navigate(baseUrl);
        workflowListPage.waitForWorkflowListLoad();

        // Get workflow count
        int workflowCount = workflowListPage.getWorkflowCount();

        System.out.println("Number of workflows displayed: " + workflowCount);

        // Verify workflows are displayed or no workflows message is shown
        if (workflowCount == 0) {
            assertTrue(workflowListPage.isNoWorkflowsMessageDisplayed(),
                    "Should display 'No workflows found' message when no workflows exist");
        } else {
            assertTrue(workflowCount > 0,
                    "Should display at least one workflow");
        }
    }

    @Test
    @Order(5)
    @DisplayName("Should be able to cancel workflow creation")
    void testCancelWorkflowCreation() {
        // Navigate to create workflow page
        createWorkflowPage.navigate(baseUrl);
        createWorkflowPage.waitForCreateWorkflowLoad();

        // Fill some data
        createWorkflowPage.fillWorkflowName("Test Workflow to Cancel");

        // Click cancel
        createWorkflowPage.clickCancel();

        // Wait for navigation
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify navigation back to workflow list
        assertTrue(driver.getCurrentUrl().contains("/workflows") &&
                !driver.getCurrentUrl().contains("/create"),
                "Should navigate back to workflow list after cancellation");
    }
}
