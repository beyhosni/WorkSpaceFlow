package com.workspaceflow.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for Create Workflow page
 */
public class CreateWorkflowPage extends BasePage {

    // Locators
    private static final By PAGE_TITLE = By.xpath("//h1[contains(text(), 'Create Workflow')]");
    private static final By WORKFLOW_NAME_INPUT = By.cssSelector("[data-testid='workflow-name-input']");
    private static final By WORKFLOW_DESCRIPTION_INPUT = By.cssSelector("[data-testid='workflow-description-input']");
    private static final By SUBMIT_BUTTON = By.cssSelector("[data-testid='submit-workflow-button']");
    private static final By CANCEL_BUTTON = By.xpath("//button[contains(text(), 'Cancel')]");
    private static final By ADD_STEP_BUTTON = By.xpath("//button[contains(text(), 'Add Step')]");

    // Step fields (using index-based selectors)
    private static final String STEP_NAME_INPUT_PATTERN = "input[type='text'][required]";
    private static final String STEP_TYPE_SELECT_PATTERN = "select";

    public CreateWorkflowPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Navigate to Create Workflow page
     */
    public CreateWorkflowPage navigate(String baseUrl) {
        driver.get(baseUrl + "/workflows/create");
        waitForPageLoad();
        return this;
    }

    /**
     * Wait for page to load
     */
    public CreateWorkflowPage waitForCreateWorkflowLoad() {
        waitForElement(PAGE_TITLE);
        return this;
    }

    /**
     * Fill workflow name
     */
    public CreateWorkflowPage fillWorkflowName(String name) {
        // Fallback to finding by label if data-testid not available
        By nameInput = driver.findElements(WORKFLOW_NAME_INPUT).isEmpty()
                ? By.xpath("//label[contains(text(), 'Workflow Name')]/following-sibling::input")
                : WORKFLOW_NAME_INPUT;
        type(nameInput, name);
        return this;
    }

    /**
     * Fill workflow description
     */
    public CreateWorkflowPage fillWorkflowDescription(String description) {
        // Fallback to finding by label if data-testid not available
        By descInput = driver.findElements(WORKFLOW_DESCRIPTION_INPUT).isEmpty()
                ? By.xpath("//label[contains(text(), 'Description')]/following-sibling::textarea")
                : WORKFLOW_DESCRIPTION_INPUT;
        type(descInput, description);
        return this;
    }

    /**
     * Fill first step name
     */
    public CreateWorkflowPage fillFirstStepName(String stepName) {
        By stepNameInput = By.xpath("(//label[contains(text(), 'Step Name')]/following-sibling::input)[1]");
        type(stepNameInput, stepName);
        return this;
    }

    /**
     * Select first step type
     */
    public CreateWorkflowPage selectFirstStepType(String stepType) {
        By stepTypeSelect = By.xpath("(//label[contains(text(), 'Type')]/following-sibling::select)[1]");
        WebElement select = waitForElement(stepTypeSelect);
        select.click();
        By option = By.xpath("//option[@value='" + stepType + "']");
        click(option);
        return this;
    }

    /**
     * Fill assignee role for first step
     */
    public CreateWorkflowPage fillFirstStepAssigneeRole(String role) {
        By assigneeInput = By.xpath("(//label[contains(text(), 'Assignee Role')]/following-sibling::input)[1]");
        type(assigneeInput, role);
        return this;
    }

    /**
     * Click Add Step button
     */
    public CreateWorkflowPage clickAddStep() {
        click(ADD_STEP_BUTTON);
        return this;
    }

    /**
     * Submit the form
     */
    public void submitForm() {
        // Fallback to finding by text if data-testid not available
        By submitBtn = driver.findElements(SUBMIT_BUTTON).isEmpty()
                ? By.xpath("//button[@type='submit' and contains(text(), 'Create Workflow')]")
                : SUBMIT_BUTTON;
        click(submitBtn);
    }

    /**
     * Click Cancel button
     */
    public void clickCancel() {
        click(CANCEL_BUTTON);
    }

    /**
     * Check if page is displayed
     */
    public boolean isCreateWorkflowPageDisplayed() {
        return isDisplayed(PAGE_TITLE);
    }
}
