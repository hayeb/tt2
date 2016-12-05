package deriha;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.Assert.fail;

//import org.openqa.selenium.firefox.FirefoxDriver;


public class Junitdriver {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  
  @Before
  public void setUp() throws Exception {
    System.setProperty("webdriver.chrome.driver", "C:/Program Files (x86)/TNO TorXakis/chromedriver.exe");
    driver = new ChromeDriver();
    baseUrl = "https://en.lichess.org/";
    driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
  }

  @Test
  public void testJunitdriver() throws Exception {
   /* driver.get(baseUrl + "/VG0LDLOuKB0e");
    driver.findElement(By.xpath("//div[@id='lichess']/div/div/div/div/div/div/div/piece[20]")).click();
    driver.findElement(By.xpath("//div[@id='lichess']/div/div/div/div/div/div/div/piece[20]")).click();*/
   
  driver.get(baseUrl + "/u6XnUIBq");
    driver.findElement(By.xpath("//div[@id='lichess']/div/div/div/div[2]/div[2]/div[2]/div[2]/div/div/nav/button[2]")).click();
     Thread.sleep(4000);
    driver.findElement(By.xpath("//div[@id='lichess']/div/div/div/div[2]/div[2]/div[2]/div[2]/div/div/nav/button[2]")).click();
     Thread.sleep(4000);
    driver.findElement(By.xpath("//div[@id='lichess']/div/div/div/div[2]/div[2]/div[2]/div[2]/div/div/nav/button[2]")).click();
     Thread.sleep(4000);
    driver.findElement(By.xpath("//div[@id='lichess']/div/div/div/div[2]/div[2]/div[2]/div[2]/div/div/nav/button[2]")).click();
     Thread.sleep(4000);
    driver.findElement(By.xpath("//div[@id='lichess']/div/div/div/div[2]/div[2]/div[2]/div[2]/div/div/nav/button[3]")).click();
  }

  @After
  public void tearDown() throws Exception {
   // driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
