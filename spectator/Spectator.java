import java.io.*;
import java.net.Socket;
import javax.swing.*;
import java.util.Vector;
import java.awt.FlowLayout;
import java.awt.Dimension;

/**
	Spectator app for the Kalaha game server. Connects to the server and displays the current 
	board state without playing the game.
	
	@author Johan Hagelbäck (jhg@bth.se)
*/
public class Spectator implements Runnable {

	private Thread thr;
	private BufferedReader reader;
	private PrintWriter writer;
	
	private JLabel[] labels_N;
	private JLabel[] labels_S;
	private JLabel ambo_N;
	private JPanel ambo_NP;
	private JLabel ambo_S;
	
	public static void main(String[] args) {
		if (args.length == 0) {
			Spectator spectator = new Spectator("localhost", 8888);
		}
		else if (args.length == 2) {
			try {
				Spectator spectator = new Spectator(args[0], Integer.parseInt(args[1]));
			}
			catch (Exception ex) {
				System.out.println("Usage: java Spectator [host] [port]");	
			}
		}
		else {
			System.out.println("Usage: java Spectator [host] [port]");
		}
	}
	
	public Spectator(String host, int port) {
		//Init readers and sockets
		try {
			Socket socket = new Socket(host, port);
			writer = new PrintWriter(socket.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch (Exception ex) {
			System.out.println("Unable to connect to KalahaServer at " + host + ":" + port);
			System.exit(0);
		}
		
		//Init GUI
		initGUI();
		
		//Start thread
		thr = new Thread(this);
		thr.start();
	}
	
	public void run() {
		while(true) {
			try {
				//Ask for current board
				writer.println("BOARD");
				String board = reader.readLine();
				
				//Display board
				fill(board);
				updateAll();
				
				//Wait a bit
				Thread.sleep(500);
			}
			catch (Exception ex) {
				System.out.println("Disconnected from server");
				System.exit(0);
			}
		}	
	}
	
	public void updateAll() {
		ambo_N.updateUI();
		ambo_S.updateUI();
		
		for (int i = 0; i < 6; i++) {
			labels_S[i].updateUI();
			labels_N[i].updateUI();
		}
	}
	
	public void initGUI() {
		JFrame myFrame = new JFrame("Kalaha Spectator Client");
		myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		myFrame.setLayout(null);
		myFrame.setBounds(50, 50, 864, 334);
		myFrame.getContentPane().setBackground(java.awt.Color.white);
		
		//Add houses for north player
		labels_N = new JLabel[6];
		for (int i = 0; i < 6; i++) {
			JLabel tmp = new JLabel(new ImageIcon("house_N.png")); 
			tmp.setBounds(138 + i * 96 , 5, 92, 92);
			tmp.setLayout(null);
			myFrame.getContentPane().add(tmp);
			labels_N[i] = tmp;
		}
		
		//Add houses for south player
		labels_S = new JLabel[6];
		for (int i = 0; i < 6; i++) {
			JLabel tmp = new JLabel(new ImageIcon("house_S.png"));
			tmp.setBounds(138 + i * 96, 197, 92, 92);
			tmp.setLayout(null);
			myFrame.getContentPane().add(tmp);
			labels_S[i] = tmp;
		}
		
		//Ambo N
		ambo_N = new JLabel(new ImageIcon("ambo_N.png"));
		ambo_N.setBounds(5, 67, 128, 160);
		ambo_N.setLayout(null);
		myFrame.getContentPane().add(ambo_N);
		
		//Ambo S
		ambo_S = new JLabel(new ImageIcon("ambo_S.png"));
		ambo_S.setBounds(714, 67, 128, 160);
		ambo_S.setLayout(null);
		myFrame.getContentPane().add(ambo_S);
		
		//fill("0;6;6;6;6;6;6;0;6;6;6;6;6;6");
		
		myFrame.setVisible(true);
	}
	
	public void fill(String board) {
		String[] tokens = board.split(";");
		int val;
		
		//Ambo_N
		val = Integer.parseInt(tokens[0]);
		fillAmbo(ambo_N, val);
		
		//South player
		for (int i = 0; i < 6; i++) {
			val = Integer.parseInt(tokens[i + 1]);
			fillHouse(labels_S[i], val);	
		}
		
		//Ambo_S
		val = Integer.parseInt(tokens[7]);
		fillAmbo(ambo_S, val);
		
		//North player
		for (int i = 0; i < 6; i++) {
			val = Integer.parseInt(tokens[13 - i]);
			fillHouse(labels_N[i], val);	
		}
	}
	
	public void fillAmbo(JLabel ambo, int val) {
		ambo.removeAll();
		
		int cX = 12;
		int cY = 12;
		
		for (int i = 0; i < val; i++) {
			JLabel tmp = new JLabel(new ImageIcon("pebble.gif"));	
			tmp.setBounds(cX, cY, 14, 14);
			cX += 15;
			if (cX >= 105) {
				cX = 12;
				cY += 15;	
			}
			ambo.add(tmp);
		}
	}
	
	public void fillHouse(JLabel house, int val) {
		house.removeAll();
		
		int cX = 9;
		int cY = 8;
		
		for (int i = 0; i < val; i++) {
			JLabel tmp = new JLabel(new ImageIcon("pebble.gif"));	
			tmp.setBounds(cX, cY, 14, 14);
			cX += 15;
			if (cX >= 80) {
				cX = 9;
				cY += 15;	
			}
			house.add(tmp);
		}
	}
}