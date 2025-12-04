# Tests Selenium E2E - WorkSpaceFlow

Ce document décrit comment utiliser et exécuter les tests Selenium end-to-end pour l'application WorkSpaceFlow.

## Prérequis

### 1. Application en cours d'exécution

Les tests Selenium nécessitent que l'application soit déployée et accessible. Utilisez Docker Compose pour lancer l'application :

```bash
docker-compose up -d
```

Attendez que tous les services soient opérationnels :
- **Frontend**: http://localhost:5173
- **Backend**: http://localhost:8080
- **MongoDB**: localhost:27017
- **Kafka**: localhost:9092

### 2. Dépendances Maven

Les dépendances Selenium sont déjà configurées dans le `pom.xml` :
- `selenium-java` (4.27.0) - Framework Selenium
- `webdrivermanager` (5.9.2) - Gestion automatique des drivers Chrome

### 3. Chrome/Chromium

Les tests utilisent Chrome en mode headless. Assurez-vous que Chrome ou Chromium est installé sur votre système.

## Structure des Tests

### Configuration

- **`SeleniumConfig.java`**: Configuration du WebDriver Chrome avec options headless
- **`BasePage.java`**: Classe de base pour le Page Object Model avec méthodes utilitaires

### Page Objects

- **`DashboardPage.java`**: Page Object pour le Dashboard
- **`WorkflowListPage.java`**: Page Object pour la liste des workflows
- **`CreateWorkflowPage.java`**: Page Object pour la création de workflow
- **`TaskListPage.java`**: Page Object pour la liste des tâches

### Tests E2E

- **`DashboardE2ETest.java`**: 4 tests pour le Dashboard
  - Chargement de la page
  - Affichage des statistiques
  - Navigation vers Instances
  - Navigation vers Tasks

- **`WorkflowE2ETest.java`**: 5 tests pour les Workflows
  - Chargement de la liste
  - Navigation vers création
  - Création d'un workflow
  - Affichage de la liste
  - Annulation de création

- **`TaskE2ETest.java`**: 2 tests pour les Tasks
  - Chargement de la liste
  - Affichage des tâches

## Exécution des Tests

### Exécuter tous les tests E2E

```bash
cd backend
mvn test -Dtest="*E2ETest"
```

### Exécuter un test spécifique

```bash
# Dashboard tests
mvn test -Dtest="DashboardE2ETest"

# Workflow tests
mvn test -Dtest="WorkflowE2ETest"

# Task tests
mvn test -Dtest="TaskE2ETest"
```

### Exécuter un test individuel

```bash
mvn test -Dtest="WorkflowE2ETest#testCreateWorkflow"
```

### Configuration personnalisée

Vous pouvez modifier l'URL de l'application via une propriété système :

```bash
mvn test -Dtest="*E2ETest" -Dapp.url="http://localhost:5173"
```

## Attributs data-testid

Les composants React ont été améliorés avec des attributs `data-testid` pour faciliter les tests :

### Dashboard
- `data-testid="total-instances"` - Compteur d'instances totales
- `data-testid="active-instances"` - Compteur d'instances actives
- `data-testid="pending-tasks"` - Compteur de tâches en attente
- `data-testid="instance-item-{id}"` - Chaque instance
- `data-testid="task-item-{id}"` - Chaque tâche

### Workflow List
- `data-testid="create-workflow-button"` - Bouton de création
- `data-testid="workflow-item-{id}"` - Chaque workflow

### Create Workflow
- `data-testid="workflow-name-input"` - Champ nom
- `data-testid="workflow-description-input"` - Champ description
- `data-testid="submit-workflow-button"` - Bouton de soumission

## Bonnes Pratiques

### 1. Page Object Model

Utilisez toujours le pattern Page Object pour organiser vos tests :

```java
public class MyPage extends BasePage {
    private static final By MY_ELEMENT = By.cssSelector("[data-testid='my-element']");
    
    public MyPage(WebDriver driver) {
        super(driver);
    }
    
    public void clickMyElement() {
        click(MY_ELEMENT);
    }
}
```

### 2. Waits explicites

Utilisez les méthodes de `BasePage` qui incluent des waits :

```java
// Bon
waitForElement(MY_LOCATOR);

// Éviter
driver.findElement(MY_LOCATOR);
```

### 3. Sélecteurs robustes

Privilégiez les `data-testid` pour les sélecteurs :

```java
// Bon - Stable
By.cssSelector("[data-testid='submit-button']")

// Éviter - Fragile
By.cssSelector(".btn.btn-primary.submit")
```

### 4. Tests indépendants

Chaque test doit être indépendant et ne pas dépendre de l'ordre d'exécution :

```java
@BeforeEach
void setup() {
    driver = SeleniumConfig.createWebDriver();
    // Initialiser l'état nécessaire
}

@AfterEach
void tearDown() {
    if (driver != null) {
        driver.quit();
    }
}
```

## Dépannage

### Les tests échouent avec "Connection refused"

Vérifiez que l'application est bien démarrée :
```bash
docker-compose ps
curl http://localhost:5173
```

### Chrome driver not found

WebDriverManager télécharge automatiquement le driver. Si cela échoue :
```bash
# Vérifier la connexion internet
# Ou télécharger manuellement ChromeDriver
```

### Tests lents

Les tests en mode headless sont généralement rapides. Si trop lents :
- Réduisez les `Thread.sleep()` (utilisés uniquement pour les transitions)
- Optimisez les waits avec des timeouts plus courts

### Échec intermittent

Si un test échoue de manière intermittente :
- Augmentez les timeouts dans `SeleniumConfig`
- Ajoutez des waits explicites supplémentaires
- Vérifiez que l'application est complètement chargée

## Intégration CI/CD

Pour intégrer les tests dans un pipeline CI/CD :

```yaml
# Exemple GitHub Actions
- name: Start Application
  run: docker-compose up -d
  
- name: Wait for Application
  run: |
    timeout 60 bash -c 'until curl -f http://localhost:5173; do sleep 2; done'
  
- name: Run E2E Tests
  run: cd backend && mvn test -Dtest="*E2ETest"
  
- name: Stop Application
  run: docker-compose down
```

## Ajout de Nouveaux Tests

### 1. Créer un Page Object

```java
package com.workspaceflow.e2e.pages;

public class MyNewPage extends BasePage {
    private static final By MY_LOCATOR = By.cssSelector("[data-testid='my-element']");
    
    public MyNewPage(WebDriver driver) {
        super(driver);
    }
    
    public void performAction() {
        click(MY_LOCATOR);
    }
}
```

### 2. Créer la classe de test

```java
package com.workspaceflow.e2e;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MyNewE2ETest {
    private static WebDriver driver;
    private static String baseUrl;
    private MyNewPage myNewPage;
    
    @BeforeAll
    static void setupClass() {
        baseUrl = SeleniumConfig.getBaseUrl();
    }
    
    @BeforeEach
    void setup() {
        driver = SeleniumConfig.createWebDriver();
        myNewPage = new MyNewPage(driver);
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("Should perform my test")
    void testMyFeature() {
        myNewPage.navigate(baseUrl);
        // Assertions...
    }
}
```

### 3. Ajouter data-testid au frontend

```tsx
<button data-testid="my-button" onClick={handleClick}>
    Click Me
</button>
```

## Ressources

- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [WebDriverManager](https://github.com/bonigarcia/webdrivermanager)
- [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
- [Page Object Model](https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/)
