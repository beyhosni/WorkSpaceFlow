package com.workspaceflow.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object for Task List page
 */
public class TaskListPage extends BasePage {

    // Locators
    private static final By PAGE_TITLE = By.xpath("//h1[contains(text(), 'Tasks')]");
    private static final By TASK_ITEMS = By.cssSelector("[data-testid^='task-item-']");
    private static final By NO_TASKS_MESSAGE = By.xpath("//div[contains(text(), 'No tasks found')]");
    private static final By LOADING_INDICATOR = By.xpath("//div[contains(text(), 'Loading')]");

    public TaskListPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Navigate to Task List page
     */
    public TaskListPage navigate(String baseUrl) {
        driver.get(baseUrl + "/tasks");
        waitForPageLoad();
        return this;
    }

    /**
     * Wait for page to load
     */
    public TaskListPage waitForTaskListLoad() {
        waitForElement(PAGE_TITLE);
        return this;
    }

    /**
     * Get all task items
     */
    public List<WebElement> getTaskItems() {
        return waitForElements(TASK_ITEMS);
    }

    /**
     * Get task count
     */
    public int getTaskCount() {
        try {
            return getTaskItems().size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Check if no tasks message is displayed
     */
    public boolean isNoTasksMessageDisplayed() {
        return isDisplayed(NO_TASKS_MESSAGE);
    }

    /**
     * Check if page is displayed
     */
    public boolean isTaskListDisplayed() {
        return isDisplayed(PAGE_TITLE);
    }
}
