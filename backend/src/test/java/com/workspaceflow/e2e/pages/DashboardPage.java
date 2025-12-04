package com.workspaceflow.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for Dashboard page
 */
public class DashboardPage extends BasePage {

    // Locators
    private static final By TOTAL_INSTANCES = By.cssSelector("[data-testid='total-instances']");
    private static final By ACTIVE_INSTANCES = By.cssSelector("[data-testid='active-instances']");
    private static final By PENDING_TASKS = By.cssSelector("[data-testid='pending-tasks']");
    private static final By LOADING_INDICATOR = By.xpath("//div[contains(text(), 'Loading')]");
    private static final By DASHBOARD_TITLE = By.xpath("//h1[contains(text(), 'Dashboard')]");
    private static final By VIEW_ALL_INSTANCES_LINK = By
            .xpath("//a[contains(text(), 'View All') and contains(@href, '/instances')]");
    private static final By VIEW_ALL_TASKS_LINK = By
            .xpath("//a[contains(text(), 'View All') and contains(@href, '/tasks')]");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Navigate to Dashboard
     */
    public DashboardPage navigate(String baseUrl) {
        driver.get(baseUrl + "/");
        waitForPageLoad();
        return this;
    }

    /**
     * Wait for dashboard to load
     */
    public DashboardPage waitForDashboardLoad() {
        waitForElement(DASHBOARD_TITLE);
        return this;
    }

    /**
     * Get total instances count
     */
    public String getTotalInstancesCount() {
        return getText(TOTAL_INSTANCES);
    }

    /**
     * Get active instances count
     */
    public String getActiveInstancesCount() {
        return getText(ACTIVE_INSTANCES);
    }

    /**
     * Get pending tasks count
     */
    public String getPendingTasksCount() {
        return getText(PENDING_TASKS);
    }

    /**
     * Check if dashboard is displayed
     */
    public boolean isDashboardDisplayed() {
        return isDisplayed(DASHBOARD_TITLE);
    }

    /**
     * Click on View All Instances link
     */
    public void clickViewAllInstances() {
        click(VIEW_ALL_INSTANCES_LINK);
    }

    /**
     * Click on View All Tasks link
     */
    public void clickViewAllTasks() {
        click(VIEW_ALL_TASKS_LINK);
    }

    /**
     * Check if loading indicator is displayed
     */
    public boolean isLoading() {
        return isDisplayed(LOADING_INDICATOR);
    }
}
