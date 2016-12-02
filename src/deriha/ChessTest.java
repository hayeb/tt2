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
public class ChessTest {
	private WebDriver driver;

	private String[] whiteChessPieces = {".pawn.white", ".king.white", ".queen.white", ".knight.white",
			".rook.white",
			".bishop.white"};

	private String[] blackChessPieces = {".pawn.black", ".king.black", ".queen.black", ".knight.black",
			".rook.black",
			".bishop.black"};

	private String[] allPieces = {" .pawn", " .king", " .queen", " .knight", " .rook", ".bishop"};

	private Random random = new Random();

	public ChessTest() {
		driver = new ChromeDriver();
	}

	/**
	 * Move a random piece to one of its random possible destinations TODO: Fix taken a different piece
	 */
	private void move() throws InterruptedException {

		List<WebElement> elements = Arrays.stream(whiteChessPieces).map(s -> driver.findElements(By.cssSelector(s)))
				.flatMap(List::stream)
				.collect(Collectors.toList());

		List<WebElement> targets;
		int checked = 0;
		do {
			WebElement piece = elements.get(Math.abs(random.nextInt()) % elements.size());
			System.out.println("Select piece " + piece.getAttribute("class"));
			piece.click();
			Thread.sleep(200);
			targets = driver.findElements(By.cssSelector(".move-dest"));
			checked++;
		}
		while (targets.isEmpty() && checked <= elements.size());
		Thread.sleep(500);

		List<WebElement> blackPieces = Arrays.stream(blackChessPieces).map(s -> driver.findElements(By.cssSelector(s)))
				.flatMap(List::stream)
				.collect(Collectors.toList());

		WebElement toClick = toDestination(targets.get(Math.abs(random.nextInt()) % targets.size()), blackPieces);

		System.out.println("Move to " + toClick.getAttribute("style"));
		Thread.sleep(100);
		toClick.click();
	}

	/**
	 * Returns the element to be clicked on: Another piece if it is has the same location as the target, else the target.
	 */
	private WebElement toDestination(WebElement target, List<WebElement> elements) {
		WebElement t = target;
		for (WebElement e : elements) {
			if (e.getAttribute("style").equals(target.getAttribute("style"))) {
				t = e;
			}
		}
		return t;
		//return elements.stream().filter(webElement -> webElement.getAttribute("style").equals(target.getAttribute("style"))).findFirst().orElse(target);
	}

	private void start(int port) {
		try {
			ServerSocket servsock = new ServerSocket(port);
			Socket sock = servsock.accept();
			InputStream inStream = sock.getInputStream();
			BufferedReader sockin = new BufferedReader(new InputStreamReader(inStream));
			OutputStream outStream = sock.getOutputStream();
			PrintWriter sockout = new PrintWriter(new OutputStreamWriter(outStream));

			String received;
			boolean end = false;
			System.out.println("Started listening for command..");
			sendBoardCode(sockout);
			while (!end) {
				received = sockin.readLine();
				System.out.println("Received command \"" + received + "\"");
				received = received.trim();

				switch (received) {
					case "Move":
						move();
						Thread.sleep(1000);
						sendBoardCode(sockout);
						break;
					case "Back":
						back();
						Thread.sleep(1000);
						sendBoardCode(sockout);
						break;
					case "BackStep":
						backStep();
						backStep();
						Thread.sleep(2400);
						sendBoardCode(sockout);
						break;
					case "Forward":
						forward();
						Thread.sleep(2400);
						sendBoardCode(sockout);
						break;
					case "ForwardStep":
						forwardStep();
						forwardStep();
						Thread.sleep(2400);
						sendBoardCode(sockout);
						break;
					default:
						System.err.println("Received unknown command \"" + received + "\"");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getBoardState() {
		List<WebElement> el = Arrays.stream(allPieces).map(s -> driver.findElements(By.cssSelector(s)))
				.flatMap(List::stream)
				.collect(Collectors.toList());

		StringBuilder sb = new StringBuilder();
		el.forEach(e -> sb.append(e.getAttribute("class")).append(" ").append(e.getAttribute("style")));
		return sb.toString();
	}

	private void end() {
		driver.close();
	}

	private void setup() throws InterruptedException {
		driver.get("https://en.lichess.org/");

		Thread.sleep(1000);

		// Select game vs. computer
		driver.findElement(By.xpath("//*[@id=\"start_buttons\"]/a[3]")).click();

		Thread.sleep(500);

		// Select game vs ai as white
		driver.findElement(By.xpath("//*[@id=\"hooks_wrap\"]/div[1]/form/div[5]/button[3]/i")).click();

		Thread.sleep(500);
	}

	public static void main(String[] args) throws InterruptedException {
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");

		if (args.length != 1) {
			System.err.println("Required port number as first argument");
			return;
		}

		int portNo = Integer.parseInt(args[0]);
		ChessTest ct = new ChessTest();
		ct.setup();
		ct.start(portNo);

		ct.end();
	}

	private void sendBoardCode(PrintWriter sockout) {
		int boardCode = Math.abs(getBoardState().hashCode());
		System.out.println("Board code " + boardCode);
		sockout.print(boardCode + "\n");
		sockout.flush();
	}

	private void back() {
		driver.findElement(By.xpath("//*[@id=\"lichess\"]/div/div[1]/div/div[2]/div[2]/div[2]/div[2]/div[1]/div[1]/nav/button[1]")).click();
	}

	private void forward() {
		driver.findElement(By.xpath("//*[@id=\"lichess\"]/div/div[1]/div/div[2]/div[2]/div[2]/div[2]/div[1]/div[1]/nav/button[4]")).click();
	}

	private void backStep() {
		driver.findElement(By.xpath("//*[@id=\"lichess\"]/div/div[1]/div/div[2]/div[2]/div[2]/div[2]/div[1]/div[1]/nav/button[2]")).click();
	}

	private void forwardStep() {
		driver.findElement(By.xpath("//*[@id=\"lichess\"]/div/div[1]/div/div[2]/div[2]/div[2]/div[2]/div[1]/div[1]/nav/button[3]")).click();
	}
}
