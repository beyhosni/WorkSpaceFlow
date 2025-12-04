package com.workspaceflow.e2e.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Configuration class for Selenium WebDriver
 */
public class SeleniumConfig {

    private static final String BASE_URL = System.getProperty("app.url", "http://localhost:5173");
    private static final int IMPLICIT_WAIT_SECONDS = 10;
    private static final int EXPLICIT_WAIT_SECONDS = 15;

    /**
     * Create and configure a Chrome WebDriver instance
     * 
     * @return Configured WebDriver
     */
    public static WebDriver createWebDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--remote-allow-origins=*");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT_SECONDS));
        driver.manage().window().maximize();

        return driver;
    }

    /**
     * Create a WebDriverWait instance
     * 
     * @param driver WebDriver instance
     * @return WebDriverWait with configured timeout
     */
    public static WebDriverWait createWebDriverWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT_SECONDS));
    }

    /**
     * Get the base URL for the application
     * 
     * @return Base URL
     */
    public static String getBaseUrl() {
        return BASE_URL;
    }
}
