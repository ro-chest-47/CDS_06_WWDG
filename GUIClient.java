import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUIClient extends JFrame {
	private JTextArea otherPlayersCard = new JTextArea(3, 30);
	private JTextArea monsterInfo = new JTextArea(30, 30);
	private JTextArea myDrawableCards = new JTextArea(3, 30);
	private JTextArea myScore = new JTextArea(3, 30);
	private JButton startButton = new JButton("Start / Shuffle");
	private JTextArea chatLog = new JTextArea(80, 20);
	private JTextField myChatMessage = new JTextField();
	
	private JButton enterButton = new JButton("Enter");	//
	private JButton loginButton = new JButton("Log in");
	private JButton enterSession1Button = new JButton("Session 1");
	private JButton enterSession2Button = new JButton("Session 2");
	private JButton enterSession3Button = new JButton("Session 3");
	private JTextArea userInfoBySessions = new JTextArea(4, 50);
	
	public GUIClient() {
		Container pane = this.getContentPane();
		otherPlayersCard.setEditable(false);
		monsterInfo.setEditable(false);
		myDrawableCards.setEditable(false);
		myScore.setEditable(false);
		chatLog.setEditable(false);
		userInfoBySessions.setEditable(false);
		
		////////// For Test //////////
		
		otherPlayersCard.setText("otherPlayersCard");
		monsterInfo.setText("monsterInfo");
		myDrawableCards.setText("myDrawableCards");
		myScore.setText("myScore");
		chatLog.setText("chatLog");
		userInfoBySessions.setText("userInfoBySessions");
		
		//////////For Test //////////
		
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridLayout(3,1));
		JPanel southPanelTop = new JPanel();
		southPanelTop.setLayout(new FlowLayout());
		southPanelTop.add(enterButton);
		southPanelTop.add(loginButton);
		JPanel southPanelCenter = new JPanel();
		southPanelCenter.setLayout(new FlowLayout());
		southPanelCenter.add(enterSession1Button);
		southPanelCenter.add(enterSession2Button);
		southPanelCenter.add(enterSession3Button);
		JPanel southPanelBottom = new JPanel();
		southPanelBottom.add(userInfoBySessions);
		
		southPanel.add(southPanelTop);
		southPanel.add(southPanelCenter);
		southPanel.add(southPanelBottom);
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		northPanel.add(inGameInfo(), BorderLayout.CENTER);
		northPanel.add(northRight(), BorderLayout.EAST);
		
		pane.setLayout(new BorderLayout());
		pane.add(northPanel, BorderLayout.CENTER);
		pane.add(southPanel, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1000, 600);
		setVisible(true);
	}
	
	private JPanel inGameInfo() {
		JPanel p1 = new JPanel();
		JPanel p2 = new JPanel();
		p1.setLayout(new BorderLayout());
		p1.add(otherPlayersCard, BorderLayout.NORTH);
		p1.add(monsterInfo, BorderLayout.CENTER);
		p2.setLayout(new GridLayout(2, 1));
		p2.add(myDrawableCards);
		p2.add(myScore);
		p1.add(p2, BorderLayout.SOUTH);
		
		return p1;
	}
	
	private JPanel northRight() {
		JPanel p = new JPanel();
		JScrollPane jsp = new JScrollPane(chatLog);
		p.setLayout(new BorderLayout());
		p.add(startButton, BorderLayout.NORTH);
		p.add(jsp, BorderLayout.CENTER);
		p.add(myChatMessage, BorderLayout.SOUTH);
		
		return p;
	}
	
	public static void main(String[] args) {
//		Frame frame;
//		frame.setVisible(true);
		GUIClient app = new GUIClient();
		
	}

}
