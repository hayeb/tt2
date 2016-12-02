package deriha;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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

	private String[] whiteChessPieces = { ".pawn.white", ".king.white", ".queen.white", ".knight.white",
		".rook.white",
		".bishop.white" };

	private String[] allPieces = { " .pawn", " .king", " .queen", " .knight", " .rook", ".bishop" };

	private Random random = new Random();

	public ChessTest()
	{
		driver = new ChromeDriver();
	}

	/**
	 * Move a random piece to one of its random possible destinations TODO: Fix taken a different piece
	 * 
	 * @throws InterruptedException
	 */
	private void move() throws InterruptedException
	{

		List<WebElement> elements = Arrays.stream(whiteChessPieces).map(s -> driver.findElements(By.cssSelector(s)))
			.flatMap(List::stream)
			.collect(Collectors.toList());

		List<WebElement> targets;
		do
		{
			WebElement piece = elements.get(Math.abs(random.nextInt()) % elements.size());
			System.out.println("Select piece " + piece.getAttribute("class"));
			piece.click();
			targets = driver.findElements(By.cssSelector(".move-dest"));
		}
		while (targets.isEmpty());
		Thread.sleep(500);

		WebElement destination = targets.get(Math.abs(random.nextInt()) % targets.size());
		System.out.println("Move to " + destination.getAttribute("style"));
		destination.click();
	}

	private void start(int port)
	{
		try
		{
			ServerSocket servsock = new ServerSocket(port);
			Socket sock = servsock.accept();
			InputStream inStream = sock.getInputStream();
			BufferedReader sockin = new BufferedReader(new InputStreamReader(inStream));
			OutputStream outStream = sock.getOutputStream();
			PrintWriter sockout = new PrintWriter(new OutputStreamWriter(outStream));

			String received;
			boolean end = false;
			System.out.println("Started listening for command..");
			while (!end)
			{
				received = sockin.readLine().trim();
				System.out.println("Received command " + received);

				switch (received)
				{
				case "Move":
					move();
					Thread.sleep(1000);
					sendBoardCode(sockout);
					break;
				case "Back":
					back();
					Thread.sleep(500);
					sendBoardCode(sockout);
					break;
				case "BackStep":
					backStep();
					sendBoardCode(sockout);
					break;
				case "Forward":
					forward();
					sendBoardCode(sockout);
					break;
				case "ForwardStep":
					forwardStep();
					sendBoardCode(sockout);
					break;
				default:
					System.err.println("Received unknown command \"" + received + "\"");
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private String getBoardState()
	{
		List<WebElement> el = Arrays.stream(allPieces).map(s -> driver.findElements(By.cssSelector(s)))
			.flatMap(List::stream)
			.collect(Collectors.toList());

		StringBuilder sb = new StringBuilder();
		el.forEach(e -> sb.append(e.getAttribute("class")).append(" ").append(e.getAttribute("style")));
		return sb.toString();
	}

	private void end()
	{
		driver.close();
	}

	private void setup() throws InterruptedException
	{
		driver.get("https://en.lichess.org/");

		Thread.sleep(1000);

		// Select game vs. computer
		driver.findElement(By.xpath("//*[@id=\"start_buttons\"]/a[3]")).click();

		Thread.sleep(500);

		// Select game vs ai as white
		driver.findElement(By.xpath("//*[@id=\"hooks_wrap\"]/div[1]/form/div[5]/button[3]/i")).click();

		Thread.sleep(500);
	}

	public static void main(String[] args) throws InterruptedException
	{
		System.setProperty("webdriver.chrome.driver", "chromedriver.mac");

		if (args.length != 1)
		{
			System.err.println("Required port number as first argument");
			return;
		}

		int portNo = Integer.parseInt(args[0]);
		ChessTest ct = new ChessTest();
		ct.setup();
		ct.start(portNo);

		ct.end();
	}

	private void sendBoardCode(PrintWriter sockout)
	{
		int boardCode = getBoardState().hashCode();
		System.out.println("Board code after move: " + boardCode);
		sockout.println(boardCode);
		sockout.flush();
	}

	private void back()
	{
		driver.findElement(By.xpath("//*[@id=\"lichess\"]/div/div[1]/div/div[2]/div[2]/div[2]/div[2]/div[1]/div[1]/nav/button[1]")).click();
	}

	private void forward()
	{
		driver.findElement(By.xpath("//*[@id=\"lichess\"]/div/div[1]/div/div[2]/div[2]/div[2]/div[2]/div[1]/div[1]/nav/button[4]")).click();
	}

	private void backStep()
	{
		driver.findElement(By.xpath("//*[@id=\"lichess\"]/div/div[1]/div/div[2]/div[2]/div[2]/div[2]/div[1]/div[1]/nav/button[2]")).click();
	}

	private void forwardStep()
	{
		driver.findElement(By.xpath("//*[@id=\"lichess\"]/div/div[1]/div/div[2]/div[2]/div[2]/div[2]/div[1]/div[1]/nav/button[3]")).click();
	}
}
