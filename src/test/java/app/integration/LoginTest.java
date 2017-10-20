package app.integration;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.FindsByXPath;
import org.openqa.selenium.remote.server.handler.SwitchToFrame;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;


/**
 * Created by Nathalie on 2/6/2017.
 */
public class LoginTest {
    private final int DEFAULT_TIMEOUT = 30;
    WebDriver driver;

    @Test
    public void simpleLoginTest (){

        System.setProperty("webdriver.chrome.driver", "C:\\Automation\\chromedriver_win32\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.get("https://learningplatform.openenglish.com");
        driver.manage().timeouts().implicitlyWait(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        WebElement email = driver.findElement(By.id("username"));
        email.sendKeys("email@gmail.com");
        WebElement password = driver.findElement(By.id("password"));
        password.sendKeys("password");
        WebElement cookies = driver.findElement(By.xpath("//a[text()='Continue']"));
        cookies.click();
        WebElement login = driver.findElement(By.id("login-btn"));
        login.click();



    }

        @Test
        public void resumeLesson (){ //This methods goes to the homepage and selects a lesson to start with

            simpleLoginTest();


            WebDriverWait wait = new WebDriverWait(driver, 10);// Webdriverweb es un clase de selenium, estamos creando un nuevo objeto "wait"
            WebElement resumeLesson  = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='main']/div[2]/div/div/div[2]/span/a"))); //Esta espera es para controlar un timeout que se presentaba al buscar el boton de ingresar a la lección


            if (driver.findElement(By.xpath("//*[text()='resume']")).isEnabled()) {
                clickWithJS("//*[text()='resume']");
            }

            else if (driver.findElement(By.xpath("//*[text()='Start']")).isEnabled()) {
                System.out.println("Hay una leccion por comenzar.");
            }
            else{
                System.out.println("Hay una leccion terminada");
            }

        }

    /**
     * Busca elementos en la pagina y retorna optional
     * @param xpathString
     * @return
     */
    private Optional<WebElement> findElementIfPresent(String xpathString) {//Retornamos un elemento optional cuando hay excepcion, asi no dejamos try and cathc en todo lado
        try {
            driver.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
            return Optional.of(driver.findElement(By.xpath(xpathString)));
        } catch (Exception exc) {
            //Logger.getGlobal().log(Level.INFO, exc.getMessage(), exc);
            return Optional.empty();
        } finally {
            driver.manage().timeouts().implicitlyWait(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        }

    }
    private void clickWithJS (String xpathString) {

        JavascriptExecutor js = (JavascriptExecutor ) driver;
        js.executeScript("document.evaluate(\"" + xpathString + "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null) .singleNodeValue.click(); " );
    }

    private void findWithJS (String xpathString) {

        JavascriptExecutor js = (JavascriptExecutor ) driver;
        Object returnJS = js.executeScript("return document.evaluate(\"" + xpathString + "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null) .singleNodeValue; " );
        System.out.println("return JS : " + returnJS);
    }

    @Test
    public void  lessonReview() {// Este es el metodo que identifica que tipo de actividad es

        resumeLesson();//Este metodo es el que busca y hace click en la lección que se debe empezar



        WebElement iframe = driver.findElement(By.xpath("//iframe"));
        //driver.switchTo().frame(iframe);


        System.out.println("iframe: " + iframe) ;

        WebElement slideDiv = driver.findElement(By.xpath(".//*[contains(@class, 'slide-wrapper slide-shown visited')]"));
        findWithJS(".//*[contains(@class, 'slide-wrapper slide-shown visited')]");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.printf("Error : %s", e);
        }



        if (slideDiv.findElements(By.xpath("*//div/section/div/div/p[text()='Listen and read along.']")).stream().anyMatch(x-> x.isDisplayed())) {
            System.out.println("Listen and read along.");
        }
        else if (slideDiv.findElements(By.xpath("*//div/section/div/div/p[text()='Click on each tab to learn some new words.']"))
                .stream().anyMatch(x-> x.isDisplayed())) {
            System.out.println("Click on each tab to learn some new words");
        }

         else if (slideDiv.findElements(By.xpath("*//div/section/div/div/p[text()='Match the words to the pictures.']"))
                .stream().anyMatch(x-> x.isDisplayed())){
            System.out.println("Match the words to the pictures.");
        }

        else if (slideDiv.findElements(By.xpath("*//div/section/div/div/p[text()='Drag the words to the correct blanks.']"))
                .stream().anyMatch(x-> x.isDisplayed()))
        {
            System.out.println("Drag the words to the correct blanks.");
        }

        else if (slideDiv.findElements(By.xpath("*//div/section/div/div/p[text()='Read, listen, and learn!']"))
                .stream().anyMatch(x-> x.isDisplayed()))
        {
            System.out.println("Read, listen, and learn!");
        }

        else if (slideDiv.findElements(By.xpath(".//div/section/div/div/p[text()='Choose the correct answer.']")).stream().anyMatch(x-> x.isDisplayed()))
        {
            System.out.println("Choose the correct answer.");
        }

        else if (slideDiv.findElements(By.xpath("*//div/section/div/div/p[text()='Make a sentence.']")).stream().anyMatch(x-> x.isDisplayed()))
        {
            System.out.println("Make a sentence.");
        }
        //else if (driver.findElement(By.xpath("*//div/section/div/div/p")).isDisplayed())
        else if (slideDiv.findElements(By.xpath(".//p[text()='Which picture matches the word?']")).stream().anyMatch(x-> x.isDisplayed()))
        {
            //System.out.println("Which picture matches the word?");
            whichPicMatchesTheWord ();
        }

        else if (slideDiv.findElements(By.xpath(".//p[text()='Which is correct?']")).stream().anyMatch(x-> x.isDisplayed()))
        {
            System.out.println("Which is correct?");

        }

        else if (slideDiv.findElements(By.xpath("*//div/section/div/div/p[text()='Type the correct words into the blanks.']")).stream().anyMatch(x-> x.isDisplayed()))
        {
            System.out.println("Type the correct words into the blanks.");
        }

        else if (slideDiv.findElements(By.xpath("*//div/section/div/div/p[text()='Good job!']")).stream().anyMatch(x-> x.isDisplayed()))
        {
            System.out.println("Good job!");
        }
        else if (slideDiv.findElements(By.xpath("*//div/section/div/div/p[text()='Please rate this lesson.']")).stream().anyMatch(x-> x.isDisplayed()))
        {
            System.out.println("Please rate this lesson.");
        }
        else {
            System.out.println("else leccion.");
           // clickWithJS("html/body/div[2]/div[1]/div/div[1]/div/div/button");
        }


        /*
        WebDriverWait wait = new WebDriverWait(driver, 30);// Webdriverweb es un clase de selenium, estamos creando un nuevo objeto "wait"
        try {
            WebElement resumeLesson = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("html/body/div[2]/div[1]/div/div[1]/div/div/button")));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("document.evaluate(\"html/body/div[2]/div[1]/div/div[1]/div/div/button\",document,null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.click();");
        } catch ( NoSuchMethodError error)   {
            WebElement nextvideo = driver.findElement(By.xpath("html/body/div[2]/div[6]/div/div[2]/span[2]"));
            nextvideo.click();
        }
        */

    }


    public void whichPicMatchesTheWord () {

        //WebElement unorderedList = driver.findElement(By.xpath("html/body/div[2]/div[17]/div/div[1]/div/div/div/div/div/div/div/div/ul"));
       WebElement slideDiv = driver.findElement(By.xpath("/div[contains(@class, 'slide-shown') and contains(@class, 'visited')]"));

        List<WebElement> listItems = slideDiv.findElements(By.tagName("li"));
        WebElement li = listItems.get(0);
        int i = 0;
        while (i < listItems.size() ) { // Las condiciones del ciclo: primera es mientras haya posiciones del array por recorrer y segunda, que no haya encontrado el correcto
            li.click();
            WebElement buttonCheck = slideDiv.findElement(By.xpath("//button[contains(@class, 'submit') and contains(@class, 'active')]"));
//            buttonCheck.click();

            //clickWithJS("html/body/div[2]/div[17]/div/div[2]/button[4]");
            clickWithJS("//button[contains(@class, 'submit active')]");

            if (findElementIfPresent("//h4[text()='Correct']").isPresent()) {
                break;// ya no haga más el while
            }

            WebElement buttonRefresh = slideDiv.findElement(By.xpath("//button[contains(@class, 'reset') and contains(@class, 'active')]"));
            clickWithJS("//button[contains(@class, 'reset active')]");
            //buttonRefresh.click();
            i ++ ;
            li = listItems.get(i);
        }


        WebElement next = slideDiv.findElement(By.xpath("//div[contains(@class, 'slide-shown')]/div/div[3]/span[2]"));
        next.click();

    }



    /* private void chooseTheAnswer() {
        WebElement opcion1 = driver.findElement(By.xpath("html/body/div[2]/div[9]/div/section/div/div/p"));
        opcion1.click();
     /*  WebElement check = driver.findElement(By.xpath("html/body/div[2]/div[9]/div/div[2]/button[4]"));
        check.click();*/

        //if (driver.findElement(By.xpath("html/body/div[2]/div[9]/div/div[1]/div/div/div/div/div/div/div/figure/div/section/h4")));

}


