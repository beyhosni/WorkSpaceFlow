package com.workspaceflow.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object for Workflow List page
 */
public class WorkflowListPage extends BasePage {

    // Locators
    private static final By PAGE_TITLE = By.xpath("//h1[contains(text(), 'Workflows')]");
    private static final By CREATE_WORKFLOW_BUTTON = By.cssSelector("[data-testid='create-workflow-button']");
    private static final By CREATE_WORKFLOW_LINK = By.xpath("//a[contains(text(), 'Create Workflow')]");
    private static final By WORKFLOW_CARDS = By.cssSelector("[data-testid^='workflow-item-']");
    private static final By NO_WORKFLOWS_MESSAGE = By.xpath("//div[contains(text(), 'No workflows found')]");
    private static final By LOADING_INDICATOR = By.xpath("//div[contains(text(), 'Loading')]");

    public WorkflowListPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Navigate to Workflow List page
     */
    public WorkflowListPage navigate(String baseUrl) {
        driver.get(baseUrl + "/workflows");
        waitForPageLoad();
        return this;
    }

    /**
     * Wait for page to load
     */
    public WorkflowListPage waitForWorkflowListLoad() {
        waitForElement(PAGE_TITLE);
        return this;
    }

    /**
     * Click on Create Workflow button
     */
    public void clickCreateWorkflow() {
        click(CREATE_WORKFLOW_LINK);
    }

    /**
     * Get all workflow cards
     */
    public List<WebElement> getWorkflowCards() {
        return waitForElements(WORKFLOW_CARDS);
    }

    /**
     * Get workflow count
     */
    public int getWorkflowCount() {
        try {
            return getWorkflowCards().size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Check if no workflows message is displayed
     */
    public boolean isNoWorkflowsMessageDisplayed() {
        return isDisplayed(NO_WORKFLOWS_MESSAGE);
    }

    /**
     * Click on Start Instance button for a workflow
     */
    public void clickStartInstance(int workflowIndex) {
        List<WebElement> workflows = getWorkflowCards();
        if (workflowIndex < workflows.size()) {
            WebElement workflow = workflows.get(workflowIndex);
            WebElement startButton = workflow.findElement(By.xpath(".//a[contains(text(), 'Start Instance')]"));
            startButton.click();
        }
    }

    /**
     * Check if page is displayed
     */
    public boolean isWorkflowListDisplayed() {
        return isDisplayed(PAGE_TITLE);
    }
}
