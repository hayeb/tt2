package deriha;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * @author haye
 */
public class ChessTest
{
	private WebDriver driver;

	private String[] whiteChessPieces = { ".pawn.white", ".king.white", ".queen.white", ".knight.white", ".tower.white",
		".rook.white",
		".bishop.white" };

	private String[] allPieces = { " .pawn", " .king", " .queen", " .knight", " .tower", " .rook" };

	private Random random = new Random();

	public ChessTest()
	{
		driver = new ChromeDriver();

		// Wait a second between actions
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
	}

	/**
	 * Move a random piece to one of its random possible destinations
	 * TODO: Fix taken a different piece
	 * @throws InterruptedException
	 */
	public void move() throws InterruptedException
	{

		List<WebElement> elements = Arrays.stream(whiteChessPieces).map(s -> driver.findElements(By.cssSelector(s)))
			.flatMap(List::stream)
			.collect(Collectors.toList());

		Thread.sleep(1000);

		List<WebElement> targets;
		do
		{
			WebElement piece = elements.get(Math.abs(random.nextInt()) % elements.size());
			System.out.println("Select piece " + piece.getAttribute("class"));
			piece.click();
			targets = driver.findElements(By.cssSelector(".move-dest"));
		}
		while (targets.isEmpty());

		WebElement destination = targets.get(Math.abs(random.nextInt()) % targets.size());
		System.out.println("Move to " + destination.getAttribute("style"));
		destination.click();

		Thread.sleep(2000);
		System.out.println(getBoardState());
	}

	public String getBoardState()
	{
		List<WebElement> el = Arrays.stream(allPieces).map(s -> driver.findElements(By.cssSelector(s)))
				.flatMap(List::stream)
				.collect(Collectors.toList());

		StringBuilder sb = new StringBuilder();
		el.forEach(e -> sb.append(e.getAttribute("class")).append(e.getAttribute("style")).append("\n"));
		return sb.toString();
	}

	private void end()
	{
		driver.close();
	}

	private void setup() throws InterruptedException
	{
		driver.get("https://en.lichess.org/");

		Thread.sleep(2000);

		// Select game vs. computer
		driver.findElement(By.xpath("//*[@id=\"start_buttons\"]/a[3]")).click();

		// Select game vs ai as white
		driver.findElement(By.xpath("//*[@id=\"hooks_wrap\"]/div[1]/form/div[5]/button[3]/i")).click();
	}

	public static void main(String[] args) throws InterruptedException
	{
		System.setProperty("webdriver.chrome.driver", "/Users/haye/local/chromedriver");

		ChessTest ct = new ChessTest();
		ct.setup();
		ct.move();
		ct.end();

	}
}
