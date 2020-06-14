// package gamePart;
import java.util.Timer;
import java.util.Arrays;
import java.util.Iterator;
import java.awt.*;
import java.awt.event.*;
//import java.awt.Color.*;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import kr.ac.konkuk.ccslab.cm.entity.CMMember;
import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

import java.security.SecureRandom;

// add Jungmo
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import kr.ac.konkuk.ccslab.cm.event.*;
// add Jungmo




// 媛곸쥌 event boolean 異붽�
// session �젒�냽�븯�뒗 logic�뿉 ���빐 �떎�떆 怨좊�쇳빐蹂� 寃�.

public class GUIClient extends JFrame {
	private JTextArea otherPlayersCard = new JTextArea(3, 20);
	private JTextArea dungeonAndStageInfo = new JTextArea(3, 10);
	private JTextArea monsterInfo = new JTextArea(30, 30);
	private JTextArea myDrawableCards = new JTextArea(3, 30);
	private JButton[] myBattleCards = new JButton[8];
	private JTextArea myScore = new JTextArea(3, 30);
	private JButton startButton = new JButton("Game Start");
	private JTextPane chatLog = new JTextPane();
	private JTextField myChatMessage = new JTextField();
	
	private JButton enterButton = new JButton("Enter");	//
	private JButton loginButton = new JButton("Log in");
	private JButton sessionInfo = new JButton("Session Info");
	private int sessionIndex;
	
	private JRadioButton enterSession1Button = new JRadioButton("Session 1");
	private JRadioButton enterSession2Button = new JRadioButton("Session 2");
	private JRadioButton enterSession3Button = new JRadioButton("Session 3");
	private ButtonGroup sessionGroup = new ButtonGroup();
	private JTextArea userInfoBySessions = new JTextArea(5, 50);
	
	
	//add JM
	private JButton signupButton = new JButton("Sign up");
	private JButton signoutButton = new JButton("Sign out");
	private JButton logoutButton = new JButton("Log out");
	// add JM
	
	private JLabel loginIdLabel =new JLabel("ID:");
	private JTextField loginId = new JTextField(10);
	private JLabel loginPwLabel =new JLabel("PW:");
	private JTextField loginPw = new JTextField(10);
	
	private MyMouseListener cmMouseListener;
	private CMClientStub m_clientStub;
	private GUIClientEventHandler m_eventHandler;
	
	
	// add JM
	//private CMClientStub c_stub;
	//private ClientEventHandler c_event;
	private CMInteractionInfo c_info;
	private CMUser c_self;
	// add JM
	
	private Monster[] monster;
	private Monster currentMonster;
	private Monster lastMonster;
	private int[] monsterOrder;
	private int stageNum;
	private static final int NUMOFMONSTER = 15;
	private static final int NUMOFBATTLECARD = 7;
	private int numOfPlayer;
	
	private boolean isThisPlayerChooseCard;
	private boolean[] isMyBattleCardPossible;
	private String[] otherPlayerAndCard;
	private int[] otherBattleCard;
	private int otherBattleCardIndex;
	private int myCurrentBattleCard;
	private boolean isMyBattleCardActive;
//	private int[] allPlayerCard;
	private int allPlayerCardCount;
	private int myRedGem;
	private int myYellowGem;
	private int myBlueGem;
	private int myAggScore;
	private int otherPlayerEventCount;
	private int otherPlayerFinishCount;
	private String[] otherPlayerName;
	private int[] otherPlayerScore;
//	private String playerAndScore;
	private boolean isMyCardCheckedInBattleCardArray;
	
	private Timer timer;
	private TimerCountdown timerCountdown;
	
	private SecureRandom random;
	
	
	
// add event state boolean value
// add JM
	
	private boolean isLogin;
	private boolean isSession;
	
// add JM
	
	
	public GUIClient() {
		MyKeyListener cmKeyListener = new MyKeyListener();
		MyActionListener cmActionListener = new MyActionListener();
		MyRadioActionListener cmRadioActionListener = new MyRadioActionListener();
		cmMouseListener = new MyMouseListener();
		
		setTitle("WA! Dungeon");
		Container pane = this.getContentPane();
		otherPlayersCard.setEditable(false);
		dungeonAndStageInfo.setEditable(false);
		monsterInfo.setEditable(false);
		myBattleCards[0] = new JButton("my choice");
		for(int i = 1; i < 8; i++) {
			myBattleCards[i] = new JButton(Integer.toString(i));
		}
		myDrawableCards.setEditable(false);
		myScore.setEditable(false);
		chatLog.setEditable(false);
		chatLog.setSize(20, 50);
//		chatLog.setMinimumSize((int) 30, 80);
		StyledDocument doc = chatLog.getStyledDocument();
		addStylesToDocument(doc);
		//chatLog.setBackground(new Color(245, 245 245));
		
		userInfoBySessions.setEditable(false);
		
		////////// For Test //////////
		
		otherPlayersCard.setText("other Players Card");
		dungeonAndStageInfo.setText("dungeon And Stage Info");
		monsterInfo.setText("monster Info");
		myDrawableCards.setText("my Drawable Cards");
		myScore.setText("my Score");
//		chatLog.setText("chatLog");
		userInfoBySessions.setText("user Info By Sessions");
		
		//////////For Test //////////
		
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridLayout(3,1));
		JPanel southPanelTop = new JPanel();
		southPanelTop.setLayout(new FlowLayout());
		southPanelTop.add(enterButton);
		southPanelTop.add(loginButton);
		
		// add JM
		southPanelTop.add(logoutButton);
		southPanelTop.add(signupButton);
		southPanelTop.add(signoutButton);
		// add JM
		
		southPanelTop.add(loginIdLabel);
		southPanelTop.add(loginId);
		southPanelTop.add(loginPwLabel);
		southPanelTop.add(loginPw);
		
		JPanel southPanelCenter = new JPanel();
		southPanelCenter.setLayout(new FlowLayout());
		
		// add JM
		sessionGroup.add(enterSession1Button);
		sessionGroup.add(enterSession2Button);
		sessionGroup.add(enterSession3Button);
		
		// add JM
		southPanelCenter.add(enterSession1Button);
		southPanelCenter.add(enterSession2Button);
		southPanelCenter.add(enterSession3Button);
		JPanel southPanelBottom = new JPanel();
		southPanelBottom.setLayout(new FlowLayout());
		southPanelBottom.add(sessionInfo);
		southPanelBottom.add(userInfoBySessions);
		
		southPanel.add(southPanelTop);
		southPanel.add(southPanelCenter);
		southPanel.add(southPanelBottom);
		
		JPanel northPanel = new JPanel();
//		northPanel.setLayout(new BorderLayout());
//		northPanel.add(inGameInfo(), BorderLayout.CENTER);
//		northPanel.add(northRight(), BorderLayout.EAST);
		northPanel.setLayout(new GridLayout(1, 2));
		northPanel.add(inGameInfo());
		northPanel.add(northRight());
		myChatMessage.addKeyListener(cmKeyListener);
		
		pane.setLayout(new BorderLayout(10, 10));
		pane.add(northPanel, BorderLayout.CENTER);
		pane.add(southPanel, BorderLayout.SOUTH);
		
		for(int i=0; i<8; i++) {
			myBattleCards[i].addActionListener(cmActionListener);
		}
		startButton.addActionListener(cmActionListener);
		enterButton.addActionListener(cmActionListener);
		loginButton.addActionListener(cmActionListener);
		
		// add JM
		sessionInfo.addActionListener(cmActionListener);
		logoutButton.addActionListener(cmActionListener);
		signupButton.addActionListener(cmActionListener);
		signoutButton.addActionListener(cmActionListener);
		// add JM
		
		enterSession1Button.addActionListener(cmRadioActionListener);
		enterSession2Button.addActionListener(cmRadioActionListener);
		enterSession3Button.addActionListener(cmRadioActionListener);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1000, 600);
		setVisible(true);
		
		m_clientStub = new CMClientStub();
		m_eventHandler = new GUIClientEventHandler(m_clientStub, this);
		//c_stub = new CMClientStub();
		//c_event = new ClientEventHandler(c_stub);
		c_info = m_clientStub.getCMInfo().getInteractionInfo();
		c_self = c_info.getMyself();
		
		isLogin = false;
		isSession = false;
		
		myChatMessage.requestFocus();
		
//		monster = new Monster[] { new Monster(1, 3, 0, 0),  new Monster(2, 0, 4, 1), new Monster(3, 1, 0, 1), new Monster(4, 2, 2, 0), new Monster(5, 5, 0, 0)
//				, new Monster(7, 3, 2, 0), new Monster(8, 0, 1, 3), new Monster(10, 2, 3, 0), new Monster(11, 1, 0, 4), new Monster(12)
//				, new Monster(13), new Monster(15), new Monster(18, 1, 2, 2), new Monster(19, 1, 1, 1), new Monster(20, 2, 2, 1)};
		
		monster = new Monster[] 
				{ new Monster(1, 3, 0, 0),  new Monster(2, 0, 3, 1), new Monster(3, 1, 0, 1), new Monster(4, 2, 2, 0), new Monster(5, 5, 0, 2)
				, new Monster(7, 3, 2, 0), new Monster(8, 0, 1, 4), new Monster(10, 3, 3, 0), new Monster(11, 2, 0, 4), new Monster(12, 2, 2, 0)
				, new Monster(13, 1, 1, 2), new Monster(15, 2, 5, 0), new Monster(18, 1, 2, 2), new Monster(19, 2, 1, 2), new Monster(20, 3, 2, 2)};
		
		sessionIndex = 1;
		
		
		 enterSession1Button.setSelected(true);
		 enterSession1Button.setSelected(false);
		 enterSession1Button.setSelected(false);
		 
		
		// gks rp
		monsterOrder = new int[NUMOFMONSTER];
		for(int i = 0; i < NUMOFMONSTER; i++) {
			monsterOrder[i] = i;
		}
		myRedGem = 0;
		myYellowGem = 0;
		myBlueGem = 0;
		numOfPlayer = 0;
		stageNum = 0;		// 占쏙옙占� 占쏙옙占싶울옙 占쏙옙 占쏙옙 占싸울때占쏙옙占쏙옙 increase 占쏙옙占쏙옙占쏙옙占� 占싼댐옙.
		myAggScore = 0;		// 占쏙옙占쏙옙 占쏙옙占쌘억옙.
		otherPlayerEventCount = 0;
		otherPlayerName = new String[5];
		otherPlayerScore = new int[5];
		for(int i=0; i<5; i++) {
			otherPlayerName[i] = "";
			otherPlayerScore[i] = 0;
		}
		
		// 占쏙옙 占쏙옙占쏙옙 (占쌕쇽옙 占쏙옙占쏙옙占쏙옙占쏙옙)占쏙옙占쏙옙 占십깍옙화 占쏙옙占쏙옙占� 占싹댐옙 占쏙옙
		isMyBattleCardPossible = new boolean[NUMOFBATTLECARD + 1];
		for(int i = 0; i<NUMOFBATTLECARD + 1; i++) {
			isMyBattleCardPossible[i] = true;
		}

		// 占쏙옙 占쏙옙占쏙옙占쏙옙占쏙옙占쏙옙占쏙옙 占십깍옙화 占쏙옙占쏙옙占� 占싹댐옙 占싶듸옙
		isThisPlayerChooseCard = false;
		otherPlayerAndCard = new String[] {"", "", "", "", ""};
		otherBattleCard = new int[] {0, 0, 0, 0, 0};
//		allPlayerCard = new int[] {0, 0, 0, 0, 0};
		allPlayerCardCount = 0;
		otherBattleCardIndex = 0;
		myCurrentBattleCard = 0;
		isMyCardCheckedInBattleCardArray = false;
		isMyBattleCardActive = true;
		otherPlayerFinishCount = 0;
	
		timer = new Timer();
		timerCountdown = new TimerCountdown(this);
		timer.scheduleAtFixedRate(timerCountdown, 0, 1000);		// 1占쏙옙 타占싱머몌옙 占쏙옙占쏙옙占쏙옙占쌔댐옙.
		
		random = new SecureRandom();
	}

	// add JM
	public CMClientStub getClientStub() {
		return m_clientStub;
	}
	
	public GUIClientEventHandler getClientEventHandler() {
		return m_eventHandler;
	}
	
	public CMInteractionInfo getClientInteractionInfo() {
		return c_info;
	}
	
	public CMUser getClientUserInfo() {
		return c_self;
	}
	
	// 異쒕젰 李쎌� setUserInfoBySessions �궗�슜�븯怨�...
	// 媛곸쥌 input 諛쏅뒗 諛⑸쾿 怨좊젮�븷 寃�.
	public void login(CMClientStub cmstub) {
		
		String strUserName = null;
		String strPassword = null;
		CMSessionEvent reply = null;
		
		/* use GUI
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		//System.out.print("user name: ");
		// use GUI
		
		try {
			strUserName = br.readLine();
			
			//System.out.print("password: ");
			// use GUI
			strPassword = br.readLine();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		// 寃곌낵 李쎌쓣 蹂꾨룄濡� 留뚮뱾硫� 醫뗭쓣 寃�.
		strUserName = loginId.getText();
		strPassword = loginPw.getText();
		cmstub.loginCM(strUserName, strPassword);
		/*
		//while(true) {
			if(reply != null) {
				
				if (reply.isValidUser() == 0) {
					setUserInfoBySessions("client fails to get authentiaction by server...");
	//				break;
				}
				else if (reply.isValidUser() == -1) {
					setUserInfoBySessions("client is already in the login-user list...");
	//				break;
				}
				else {
					setUserInfoBySessions("client successfully logs in to the server...");
					isLogin = true;
	//				break;
				}
			}
			else {
				setUserInfoBySessions("client is too be fast to get authentiaction by server...");
			}
	//	}
	 * 
	 */
		
		// ID/PW 珥덇린�솕 �떆�궗 寃�.
	}
	
	public void signUp(CMClientStub cmstub) {
		
		String strUserName = null;
		String strPassword = null;
		String resultText = null;
		
		boolean isPossibleName = false;
		boolean isPossiblePass = false;
		
		Scanner scan = new Scanner(System.in);
		
		do {
			
			setUserInfoBySessions("�썝�븯�떆�뒗 UserName�쓣 �쁺臾� �냼臾몄옄 諛� �닽�옄瑜� �궗�슜�븯�뿬 8湲��옄 �씠�긽�쑝濡� �엯�젰�빐二쇱꽭�슂." + 
					"�썝�븯�떆�뒗 Password瑜� �쁺臾� �냼臾몄옄 諛� �닽�옄瑜� �궗�슜�븯�뿬 8湲��옄 �씠�긽�쑝濡� �엯�젰�빐二쇱꽭�슂.");
			//strUserName = scan.nextLine();
			System.out.println();
			//strPassword = scan.nextLine();
			
			if(!strUserName.isEmpty()) {
				if(strUserName.length() > 7) {
					if(strUserName.matches("^[0-9a-z]*$")){
						isPossibleName = true;
					}
					else {
						resultText += "UserName�뿉�뒗 0~9 �궗�씠�쓽 �닽�옄�� �쁺�뼱 �냼臾몄옄留� �궗�슜�븷 �닔 �엳�뒿�땲�떎.\n";
					}
				}
				else {
					resultText += "UserName�� �쁺臾� �냼臾몄옄 諛� �닽�옄瑜� �궗�슜�븯�뿬 8湲��옄 �씠�긽�쑝濡� �엯�젰�빐�빞 �빀�땲�떎.\n";
				}
			}
			else {
				resultText += "UserName�� 怨듬갚�쑝濡� �엯�젰�븷 �닔 �뾾�뒿�땲�떎.\n";
			}
			
			if(!strPassword.isEmpty()) {
				if(strPassword.length() > 7) {
					if(strPassword.matches("^[0-9a-z]*$")) {
						if(!strPassword.equals(strUserName)) {
							isPossiblePass = true;
						}
						else {
							resultText += "Password�뒗 UserName怨� �룞�씪�븷 �닔 �뾾�뒿�땲�떎.\n";
						}
					}
					else {
						resultText += "Password�뿉�뒗 0~9 �궗�씠�쓽 �닽�옄�� �쁺�뼱 �냼臾몄옄留� �궗�슜�븷 �닔 �엳�뒿�땲�떎.\n";
					}
				}
				else {
					resultText += "Password�뒗 �쁺臾� �냼臾몄옄 諛� �닽�옄瑜� �궗�슜�븯�뿬 8湲��옄 �씠�긽�쑝濡� �엯�젰�빐�빞 �빀�땲�떎.\n";
				}
			}
			else {
				resultText += "Password�뒗 怨듬갚�쑝濡� �엯�젰�븷 �닔 �뾾�뒿�땲�떎.\n";
			}
			
			setUserInfoBySessions(resultText);
			
		} while(!isPossibleName || !isPossiblePass);
		
		cmstub.registerUser(strUserName, strPassword);
		
	}
	
	public void signOut(CMClientStub cmstub) {
		
		String strUserName = null;
		String strPassword = null;
		
		cmstub.deregisterUser(strUserName, strPassword);
		
	}
	
	public void joinPlayRoom(CMClientStub cmstub) {

		// use joinsession : if possible group = 0�뿉 �젒洹쇳븯�젮怨� �븯硫� �븞�맂�떎怨� �븷 寃�.
		// use change group : group state = true�씤 group�쑝濡� 諛붾줈 �씠�룞.
		// server :: change 洹몃９ 諛쏆쑝硫� �빐�떦 group�쓽 �씤�썝�닔 �솗�씤 �썑 5瑜� 珥덇낵�븯硫� �븞�맂�떎怨� �븷 寃�.
		//				5瑜� �꽆吏� �븡�쓣 �떆 諛쏄퀬 group �씤�썝 �닔 +1�븯怨� 5硫� false濡� 諛붽� 寃�.
		
		String sessionName = "session"+Integer.toString(sessionIndex);
		boolean result = false;
		Scanner scan = new Scanner(System.in);
		CMDummyEvent de = new CMDummyEvent();
		
		setUserInfoBySessions("李몄뿬瑜� �썝�븯�뒗 Session�쓽 �씠由꾩쓣 �엯�젰�븯�꽭�슂.");
		//sessionName = scan.nextLine();
		
		de.setHandlerSession(sessionName);
		de.setHandlerGroup("null");
		de.setDummyInfo("0000&"+cmstub.getMyself().getName()+"&check Possible room");
		
		cmstub.send(de, "SERVER");
		
	}
	
	public void leavePlayRoom(CMClientStub cmstub) {
		
		CMDummyEvent de = new CMDummyEvent();
		
		de.setHandlerSession(cmstub.getMyself().getCurrentSession());
		de.setHandlerGroup(cmstub.getMyself().getCurrentGroup());
		de.setDummyInfo("0020&"+cmstub.getMyself().getName()+"&leave Room");
		
		cmstub.send(de, "SERVER");
		// leave session �븷 寃�.
		// server :: leave session 諛쏆쑝硫� �쁽�옱 group�쓽 �씤�썝 �닔 -1�븷 寃�.
		//			:: -1�븳寃� 4�씠�븯硫� flase�뿉�꽌 true濡� 諛붽� 寃�
		//			:: 
		
	}
	
	public void readyToPlay(CMClientStub cmstub) {
		
		// server�뿉 �씠踰ㅽ듃 蹂대궪 寃�.
		// server :: event 諛쏆쑝硫� �빐�떦 user�쓽 state瑜� true �븷 寃�.
		// 			李몄뿬以묒씤 �씤�썝留뚰겮 �룞�쟻 array �꽑�뼵�븯怨� all true硫� �떆�옉. group�쓽 state false濡� 蹂�寃쏀븷 
		//			ready濡� 諛붽� �븣�뿉留� �븳踰� check�븿.
		
		CMDummyEvent de = new CMDummyEvent();
		
		de.setHandlerSession(cmstub.getMyself().getCurrentSession());
		de.setHandlerGroup(cmstub.getMyself().getCurrentGroup());
		de.setDummyInfo("0030&"+cmstub.getMyself().getName()+"&ready to play.");
		
		cmstub.send(de, "SERVER");
		
	}
	
	public void unreadyToPlay(CMClientStub cmstub) {
		
		// server�뿉 �씠踰ㅽ듃 蹂대궪 寃�.
		// server :: event 諛쏆쑝硫� �빐�떦 user�쓽 state瑜� false濡� �븷 寃�.
		// 			 unready濡� 諛붽� �븣�뿉�뒗 check �븷 �븘�슂媛� �뾾�쓬.
		
		CMDummyEvent de = new CMDummyEvent();
		
		de.setHandlerSession(cmstub.getMyself().getCurrentSession());
		de.setHandlerGroup(cmstub.getMyself().getCurrentGroup());
		de.setDummyInfo("0040&"+cmstub.getMyself().getName()+"&unready to play.");
		
		cmstub.send(de, "SERVER");
		
	}
	
	// add JM
	public Monster getCurrentMonster() {
		return this.currentMonster;
	}

	public void linkMonsterOrder(int[] monOrder) {
		for(int i = 0; i < NUMOFMONSTER-1; i++) {
			monster[monOrder[i]].setNextMonster(monster[monOrder[i+1]]);
		}
		this.currentMonster = monster[monOrder[0]];
		this.lastMonster = monster[monOrder[NUMOFMONSTER-1]];
	}
	
	public void setOtherPlayersCard(String text) {
		otherPlayersCard.setText(text);
	}
	
	public void setDungeonAndStageInfo(String text) {
		dungeonAndStageInfo.setText(text);
	}
	
	public void setMonsterInfo(String text) {
		monsterInfo.setText(text);
	}
	
	public void setUserInfoBySessions(String text) {
		userInfoBySessions.setText(text);
	}
	
	public void setMyScore(String text) {
		myScore.setText(text);
	}
	
	public void setMyCardButtonEnable(int cardIndex, boolean buttonSwitch) {
		if(cardIndex == 0) {		// 0占쏙옙 占쏙옙튼占쏙옙 "占쏙옙占쏙옙" 占쏙옙튼占싱띰옙 0占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙占쏙옙 占십는댐옙.
			return;
		}
		myBattleCards[cardIndex].setEnabled(buttonSwitch);
	}
	
	public void setIsThisPlayerChooseCard(boolean b) {
		isThisPlayerChooseCard = b;
	}
	
	public void proceedGame() {		// 占쏙옙占쏙옙 占쏙옙占쏙옙. 占쏙옙占쏙옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 monster order占쏙옙 占쏙옙占쏙옙占쌍억옙占� 占싼댐옙.
		myRedGem = 0;
		myYellowGem = 0;
		myBlueGem = 0;
		numOfPlayer = m_clientStub.getGroupMembers().getMemberNum();		
		// getMemberNum()占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쌓뤄옙 占싸울옙 占쏙옙占쏙옙 占쏙옙환占싼댐옙.
		stageNum = 0;
		myAggScore = 0;
		otherPlayerEventCount = 0;
		
		// error 媛��뒫�꽦 議댁옱 (5媛� �븘�땲�씪 numOfPlayer 源뚯� �룄�뒗寃� 留욎쓣�벏)
		for(int i=0; i<5; i++) {
			otherPlayerName[i] = "";
			otherPlayerScore[i] = 0;
		}

		for(int i = 0; i<NUMOFBATTLECARD + 1; i++) {
			isMyBattleCardPossible[i] = true;
			setMyCardButtonEnable(i, true);
		}
		
		isThisPlayerChooseCard = false;
		for(int i=0; i<5; i++) {
			otherPlayerAndCard[i] = "";
			otherBattleCard[i] = 0;
//			allPlayerCard[i] = 0;
		}
		otherBattleCardIndex = 0;
		myCurrentBattleCard = 0;
		isMyCardCheckedInBattleCardArray = false;
		isMyBattleCardActive = true;
		allPlayerCardCount = 0;
		otherPlayerFinishCount = 0;
		
		// 첫 占쏙옙째 占쏙옙占싶댐옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙 占쌕뤄옙 占쏙옙占싱곤옙 占싼댐옙.
		String currentMonsterInfoText = currentMonster.getName()
				+ "\nPower: " + currentMonster.getPower() + "\tRed Gem: " + currentMonster.getRedGem() 
				+ "\tYellow Gem: " + currentMonster.getYellowGem() + "\tBlue Gem: " + currentMonster.getBlueGem();
		setMonsterInfo(currentMonsterInfoText);
			
		setDungeonAndStageInfo("Stage: " + (stageNum + 1));
		setMyScore("Red Gems: " + myRedGem + "\tYellow Gems: " + myYellowGem + "\tBlue Gems: " + myBlueGem);
			
		timerCountdown.setDrawCount(40);	// 카占쏙옙트占쌕울옙 30占쏙옙 占쏙옙占쏙옙.  
			
			// 占쏙옙占쏙옙占쏙옙占쏙옙占� 첫 占쏙옙째 占쏙옙占쏙옙占쏙옙占쏙옙 占쏙옙 占쏙옙 !!!!!!!
			
			
			
			
//			stageNum++;  no no
			
//			if(currentMonster.isNextMonsterExist()) {	// 占쏙옙틀 占쏙옙占쏙옙 占쏙옙 占쏙옙占쏙옙 占쏙옙 占쏙옙占싶곤옙 占쏙옙占쏙옙占쏙옙 占쏙옙占싶곤옙 占싣니몌옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙斂占� 占쌕쏙옙 1占쌤곤옙占�.  
//				currentMonster = currentMonster.getNextMonster();
//			} else {					
//				// 占쏙옙占쏙옙占쏙옙 占쏙옙占싶몌옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙.
//				myAggScore = getScore();
//				showMyScore();
//				
//				System.out.println("Game Over !");
//			}
		
	}
	
	public void proceedStage() {
		if(currentMonster.isNextMonsterExist()) {		// 占쏙옙占쏙옙 占쏙옙占싶곤옙 占쏙옙占쏙옙占쏙옙 Stage 占쏙옙占쏙옙
			stageNum++;
			
			if(stageNum % 5 == 0) {		// stage占쏙옙 5占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쌉쇽옙占싱므뤄옙 占쏙옙틀 카占쏙옙 占쏙옙占쏙옙.
				for(int i = 0; i<NUMOFBATTLECARD + 1; i++) {
					isMyBattleCardPossible[i] = true;
					setMyCardButtonEnable(i, true);
				}
			}
			
			for(int i=0; i<5; i++) {
				otherPlayerAndCard[i] = "";
				otherBattleCard[i] = 0;
			}
			
			setIsThisPlayerChooseCard(true);
			otherBattleCardIndex = 0;
			myCurrentBattleCard = 0;
			isMyCardCheckedInBattleCardArray = false;
			isMyBattleCardActive = true;
			allPlayerCardCount = 0;
			
			setDungeonAndStageInfo("Stage: " + (stageNum + 1));
			setMyScore("Red Gems: " + myRedGem + "\tYellow Gems: " + myYellowGem + "\tBlue Gems: " + myBlueGem);
			
			currentMonster = currentMonster.getNextMonster();
			String currentMonsterInfoText = currentMonster.getName()
					+ "\nPower: " + currentMonster.getPower() + "\tRed Gem: " + currentMonster.getRedGem() 
					+ "\tYellow Gem: " + currentMonster.getYellowGem() + "\tBlue Gem: " + currentMonster.getBlueGem();
			setMonsterInfo(currentMonsterInfoText);
			currentMonsterInfoText = currentMonster.getName()
					+ "\nPower: " + currentMonster.getPower() + "\tRed Gem: " + currentMonster.getRedGem() 
					+ "\tYellow Gem: " + currentMonster.getYellowGem() + "\tBlue Gem: " + currentMonster.getBlueGem();
			timerCountdown.setInfoShowCount(5, currentMonsterInfoText);	
			// 10占쏙옙 占식울옙 占쏙옙占싸울옙 占쏙옙占쏙옙 占쏙옙占쏙옙, 占쏙옙 占쏙옙占� 카占쏙옙 창占쏙옙 占쏙옙占쏙옙占쌔댐옙. 占쌓몌옙占쏙옙 10占쏙옙 占식울옙 占쌕쏙옙 占쏙옙占쏙옙 占쏙옙튼占쏙옙 활占쏙옙화 占싫댐옙.
			
			timerCountdown.setDrawCount(45);		// 占쏙옙占썩서 40占십뤄옙 占쏙옙 占쏙옙占쏙옙占쏙옙, 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙 10占쏙옙 占쏙옙占쏙옙占쏙옙 占쌩깍옙 占쏙옙占쏙옙占싱댐옙.
			
		} else {		// 占쏙옙占쏙옙 占쏙옙占싶곤옙 占쏙옙占쏙옙占쏙옙 Stage 占쏙옙占쏙옙
			myAggScore = getScore();
			showMyScore();
			
			
			// 占쏙옙占쏙옙 占쏙옙占쏙옙 占쌘듸옙 !! 
		}
	}
	
	public void setMyBattleCard(int index) {		// index占쏙옙 占쏙옙占쏙옙 占쏙옙 占쏙옙틀 카占쏙옙. 0占쏙옙占쏙옙 占쏙옙효, 1~7 占쏙옙占쏙옙 占쌍댐옙.
		if(isThisPlayerChooseCard) {	// 占싱뱄옙 카占썲를 占쏙옙占쏙옙占쌔쇽옙 占승다몌옙 占쏙옙占쏙옙占쏙옙占� 占십는댐옙. 占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙 占식울옙 false占쏙옙 占쌕뀐옙占�.
			return;
		}
		if(index != 0) { // index == 0 占쏙옙 카占쏙옙占� 占쏙옙효 카占쏙옙.
			if(isMyBattleCardPossible[index] == false) {	// 占쏙옙효 카占썲를 占쏙옙占쏙옙占싹곤옙, 占싱뱄옙 占쏙옙 카占썲를 占쏙옙占쏙옙占싹몌옙 占쏙옙占쏙옙占쏙옙 占쏙옙 占쏙옙占쏙옙.
				printMessage("이미 카드를 드로우 했거나, 다음 배틀 대기 시간입니다.\n");
				return;
			}
			setMyCardButtonEnable(index, false);
		}
		
		timerCountdown.setDrawCount(-1);	// 타占싱몌옙 占승쏙옙크占쏙옙 카占쏙옙트占쏙옙 -1占싱몌옙 占싣뱄옙占싶듸옙 占쏙옙占쏙옙 占십는댐옙. 
		isThisPlayerChooseCard = true;
		myCurrentBattleCard = index;
		isMyBattleCardPossible[index] = false;
//		allPlayerCard[allPlayerCardCount++] = index;		// 占쏙옙占� 占시뤄옙占싱억옙占쏙옙 카占썲를 占쏙옙占쏙옙求占� 占썼열
//		allPlayerCardCount++;
//		CMMember groupMembers = m_clientStub.getGroupMembers();
		
		CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
		CMUser myself = interInfo.getMyself();
		
		CMUserEvent use = new CMUserEvent();
		use.setStringID("Set Battle Card");
		use.setEventField(CMInfo.CM_STR, "userName", myself.getName());
		use.setEventField(CMInfo.CM_INT, "battleCard", String.valueOf(myCurrentBattleCard));
		m_clientStub.cast(use, myself.getCurrentSession(), myself.getCurrentGroup());
		use = null;
		
//		if(numOfPlayer == allPlayerCardCount) {	// 카占썲가 占쏙옙占쏙옙 占쏙옙占쏙옙 占시뤄옙占싱억옙 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙틀 占쏙옙占쏙옙
//			battleWithMonster();
//		}
	}
	
	public void setAllBattleCard(String playerName, int battleCard) {
		// 占쏙옙占쏙옙占쏙옙 占쏙옙 占쏙옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占십울옙 !! 혹占쏙옙 占쌕몌옙 占쏙옙占쏙옙占쏙옙 ID占쏙옙占쏙옙 占쏙옙占쏙옙 占쌨아와억옙 占싼댐옙.
		if(otherBattleCardIndex >= 5) {
			return;
		}
		otherPlayerAndCard[otherBattleCardIndex] = playerName + ": " + String.valueOf(battleCard);
		otherBattleCard[otherBattleCardIndex] = battleCard;
		otherBattleCardIndex++;
		
//		allPlayerCard[allPlayerCardCount++] = battleCard;
		allPlayerCardCount++;
		
		System.out.println(allPlayerCardCount);
		
		if(numOfPlayer == allPlayerCardCount) {	// 카占썲가 占쏙옙占쏙옙 占쏙옙占쏙옙 占시뤄옙占싱억옙 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙틀 占쏙옙占쏙옙
			battleWithMonster();
		}
	}
	
	public void battleWithMonster() {
		int[] activeBattleCard = Arrays.copyOf(otherBattleCard, otherBattleCard.length);
		int battleCardAgg = 0;			// 占쏙옙효占쏙옙 占쏙옙틀카占쏙옙 占쏙옙占쏙옙
		int minBattleCard = 8;
		
		// Test Code
		for(int i=0; i<5; i++) {
			System.out.println(otherPlayerAndCard[i]);
		}
		for(int i=0; i<5; i++) {
			System.out.println("Battle Cards: " + otherBattleCard[i]);
		}
		
		String otherPlayerAndCardText = "";
		for(int i=0; i < otherBattleCardIndex; i++) {
			otherPlayerAndCardText = otherPlayerAndCardText + otherPlayerAndCard[i] + " ";
		}
		// 占쏙옙占� 占쏙옙占쏙옙占� 카占쏙옙 占쏙옙占쏙옙
		setOtherPlayersCard(otherPlayerAndCardText);
		
		if(myCurrentBattleCard == 0) {	// 占쏙옙 카占썲가 0占싱몌옙 占쏙옙효화.
			isMyBattleCardActive = false;
		}
		
		for(int i=0; i<5; i++) {		// 占쏙옙 카占쏙옙占� 占쌕몌옙 占쏙옙占쏙옙占쏙옙占� 占쏙옙 카占쏙옙 占쏙옙. 占쌓몌옙占쏙옙 占쌕몌옙 占쏙옙占쏙옙占� 占쏙옙 카占쏙옙占� 占쏙옙占쏙옙占쏙옙 占쏙옙.
			int tmp = otherBattleCard[i];
			if(tmp == myCurrentBattleCard) {
				if(isMyCardCheckedInBattleCardArray == false) {
					isMyCardCheckedInBattleCardArray = true;
					continue;
				}
				isMyBattleCardActive = false;
				activeBattleCard[i] = -1;
			}
			
			for(int j=i+1; j<5; j++) {
				if(tmp == otherBattleCard[j]) {
					activeBattleCard[j] = -1;
					activeBattleCard[i] = -1;
				}
			}
		}
		
		// Test Code
		for(int i=0; i<5; i++) {
			System.out.println("Active Battle Cards: " + activeBattleCard[i]);
		}
		
//		if (isMyBattleCardActive) {
//			battleCardAgg += myCurrentBattleCard;
//		}
		
		for(int i = 0; i < 5; i++) {	// 占쏙옙효占쏙옙 카占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쌌삼옙.
			if(activeBattleCard[i] > 0) {
				battleCardAgg += activeBattleCard[i];
				if(minBattleCard > activeBattleCard[i]) {	// 占쏙옙 카占썲를 占쏙옙占쏙옙占쏙옙 占쏙옙효占쏙옙 카占쏙옙 占쌩울옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쌘곤옙 占쏙옙占쏙옙 카占쏙옙 占쏙옙占쏙옙
					minBattleCard = activeBattleCard[i];
				}
			}
		}
		
		CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
		CMUser myself = interInfo.getMyself();
		CMUserEvent use = new CMUserEvent();
		CMUserEvent useForBattleFinish = new CMUserEvent();		// 占쏙옙占쏙옙 User Event占쏙옙 占쏙옙활占쏙옙占쌔듸옙 占실댐옙占쏙옙 확占쏙옙치 占십아쇽옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占싱븝옙트.
		use.setHandlerSession(myself.getCurrentSession());
		use.setHandlerGroup(myself.getCurrentGroup());
		
		if(battleCardAgg >= currentMonster.getPower()) {	// 占승몌옙 !!
			if(isMyBattleCardActive) {
				if( myCurrentBattleCard == minBattleCard ) {	// 占쏙옙 카占썲가 占쏙옙占쏙옙 占쌜다몌옙 占쏙옙占쏙옙 占쌨는댐옙. 占쏙옙占쏙옙 占싱깍옙占� 占쏙옙占쏙옙 占승몌옙 Event 占쏙옙占쏙옙.
					String winGems = myself.getName() + " wins\nRed Gems: " + currentMonster.getRedGem() 
						+ "\nYellow Gems: " + currentMonster.getYellowGem() + "\nBlue Gems: " + currentMonster.getBlueGem()+"\n";
					this.myRedGem += currentMonster.getRedGem();
					this.myYellowGem += currentMonster.getYellowGem();
					this.myBlueGem +=  currentMonster.getBlueGem();
					
					use.setStringID("Win Gems");
					use.setEventField(CMInfo.CM_INT, "Num Of Gems", winGems);
//					printMessage(winGems);
					m_clientStub.cast(use, myself.getCurrentSession(), myself.getCurrentGroup());
					
				}
			}
		} else {	// 占싻뱄옙. 占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占� Event 占쏙옙占쏙옙.
			if(isMyBattleCardActive == false || myCurrentBattleCard < minBattleCard) {
				int maxNumOfGem = this.myRedGem;	// 占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙.
				int maxGemIndex = 4;				// 占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙. 3 bit占쏙옙 bit-wise 占쏙옙占�. 4占쏙옙 占쏙옙占쏙옙, 2占쏙옙 占쏙옙占�, 1占쏙옙 占식띰옙.
				
				if(maxNumOfGem < this.myYellowGem) {
					maxNumOfGem = this.myYellowGem;
					maxGemIndex = 2;
				} else if(maxNumOfGem == this.myYellowGem) {
					maxGemIndex |= 2;
				}
				
				if(maxNumOfGem < this.myBlueGem) {
					maxGemIndex = 1;
				} else if(maxNumOfGem == this.myBlueGem) {
					maxGemIndex |= 1;
				}
				
				if((maxGemIndex & 4) == 4) {		// 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 !!
					use.setStringID("Lose Red Gems");
					use.setEventField(CMInfo.CM_INT, "Num Of Red Gems", String.valueOf(this.myRedGem));
					m_clientStub.cast(use, myself.getCurrentSession(), myself.getCurrentGroup());
					
//					if(currentMonster.isNextMonsterExist()) {
//						this.currentMonster.getNextMonster().plusGems(myRedGem, 0, 0);
//					}
					this.myRedGem = 0;
				}
				
				if((maxGemIndex & 2) == 2) {		// 占쏙옙占� 占쏙옙占쏙옙 占쏙옙占쏙옙 !!
					use.setStringID("Lose Yellow Gems");
					use.setEventField(CMInfo.CM_INT, "Num Of Yellow Gems", String.valueOf(this.myYellowGem));
					m_clientStub.cast(use, myself.getCurrentSession(), myself.getCurrentGroup());
					
//					if(currentMonster.isNextMonsterExist()) {
//						this.currentMonster.getNextMonster().plusGems(0, myYellowGem, 0);
//					}
					this.myYellowGem = 0;
				}
				
				if((maxGemIndex & 1) == 1) {		// 占식띰옙 占쏙옙占쏙옙 占쏙옙占쏙옙 !!
					use.setStringID("Lose Blue Gems");
					use.setEventField(CMInfo.CM_INT, "Num Of Blue Gems", String.valueOf(this.myBlueGem));
					m_clientStub.cast(use, myself.getCurrentSession(), myself.getCurrentGroup());
					
//					if(currentMonster.isNextMonsterExist()) {
//						this.currentMonster.getNextMonster().plusGems(0, 0, myBlueGem);
//					}
					this.myBlueGem = 0;
				}
			}
		}
		use = null;
		setMyScore("Red Gems: " + myRedGem + "\tYellow Gems: " + myYellowGem + "\tBlue Gems: " + myBlueGem);
		
		// 占쏙옙 占쏙옙占시울옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쌕곤옙 占싯뤄옙占쌍댐옙 占쌘듸옙.
		useForBattleFinish.setStringID("Battle Finish");
		useForBattleFinish.setHandlerSession(myself.getCurrentSession());
		useForBattleFinish.setHandlerGroup(myself.getCurrentGroup());
		useForBattleFinish.setEventField(CMInfo.CM_INT, "userName", myself.getName());
		m_clientStub.cast(useForBattleFinish, myself.getCurrentSession(), myself.getCurrentGroup());
		
//		battleFinish();
	}
	
	public void battleFinish() {
		
		if(otherPlayerFinishCount >= numOfPlayer - 1) {	// 占쏙옙占쏙옙占쏙옙占쏙옙 占쌉뤄옙占쏙옙 占쏙옙占쏙옙占쏙옙 카占쏙옙트占쏙옙 占쏙옙체 占시뤄옙占싱억옙 占쏙옙 -1 占쏙옙 占쏙옙 占쌉뤄옙占싹곤옙 占싫댐옙.
			otherPlayerFinishCount = 0;
			proceedStage();					// 
			
		} else {		// 占시뤄옙占싱억옙 占쏙옙占쏙옙占쏙옙 占싹놂옙占쏙옙 占쌉뤄옙占쏙옙 占쏙옙占쏙옙占쏙옙 카占쏙옙트占쏙옙 占쏙옙占쏙옙占쏙옙.
			otherPlayerFinishCount++;
		}
	}
	
	public int getScore() {		// 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙 占쌌삼옙.
		int score = 0;
		int minNumOfGem = this.myRedGem;	// 占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙.
		
		if(minNumOfGem > this.myYellowGem) {
			minNumOfGem = this.myYellowGem;
		}
		if(minNumOfGem > this.myBlueGem) {
			minNumOfGem = this.myBlueGem;
		}
		score = (minNumOfGem * 3) + this.myRedGem + this.myYellowGem + this.myBlueGem;
		
		return score;
	}
	
	public void showMyScore() {		// 占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙 client占쏙옙 占쌉뤄옙占싹곤옙 event占쏙옙 占싼뤄옙占쌍댐옙 占쌉쇽옙
		CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
		CMUser myself = interInfo.getMyself();
		
		CMUserEvent use = new CMUserEvent();
		use.setStringID("Show score");
		use.setEventField(CMInfo.CM_STR, "userName", myself.getName());
		use.setEventField(CMInfo.CM_INT, "Score", String.valueOf(myAggScore));
		m_clientStub.cast(use, myself.getCurrentSession(), myself.getCurrentGroup());
		use = null;
		
//		showAllScore(myself.getName(), myAggScore);		// 占쏙옙 占쏙옙占쏙옙占쏙옙 占싼뤄옙占쌍곤옙 show all score占쏙옙 占쏙옙 占쏙옙占쏙옙占쏙옙 占쌉뤄옙占쏙옙占쌔댐옙.
	}
	
	public void showAllScore(String userName, int score) {	// 占쏙옙占쏙옙占쏙옙 占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占� 占쏙옙占쏙옙占쌍댐옙 占쌉쇽옙. 占싱븝옙트 占쌘들러占쏙옙 占싸몌옙占쏙옙.

		otherPlayerName[otherPlayerEventCount] = userName;
		otherPlayerScore[otherPlayerEventCount] = score;
		otherPlayerEventCount++;
		
		if(otherPlayerEventCount >= numOfPlayer - 1) {	// 占쏙옙占쏙옙占쏙옙占쏙옙 占쌉뤄옙占쏙옙 占쏙옙占쏙옙占쏙옙 카占쏙옙트占쏙옙 占쏙옙체 占시뤄옙占싱억옙 占쏙옙 -1 占쏙옙 占쏙옙 占쌉뤄옙占싹곤옙 占싫댐옙.
			int index = otherPlayerEventCount + 1;
			int tmp, j;	// 占쏙옙占식울옙 占쏙옙 占쏙옙占쏙옙
			String tmpStr;	// 占쏙옙占식울옙 占쏙옙占쏙옙 占쌈시뤄옙 占쏙옙占쏙옙 占쏙옙트占쏙옙.
			String[] otherPlayerNameSortedByHighScore = Arrays.copyOf(otherPlayerName, otherPlayerName.length);
			int[] otherPlayerScoreSortedByHighScore = Arrays.copyOf(otherPlayerScore, otherPlayerScore.length);
			for(int i = 1; i < index; i++) {	// 占쏙옙占쏙옙 占쏙옙占쏙옙
				tmp = otherPlayerScoreSortedByHighScore[i];
				tmpStr = otherPlayerNameSortedByHighScore[i];
				for(j = i-1; j>=0 && otherPlayerScoreSortedByHighScore[j] < tmp; j--) {
					otherPlayerScoreSortedByHighScore[j+1] = otherPlayerScoreSortedByHighScore[j];
					otherPlayerNameSortedByHighScore[j+1] = otherPlayerNameSortedByHighScore[j];
				}
				otherPlayerScoreSortedByHighScore[j+1] = tmp;
				otherPlayerNameSortedByHighScore[j+1] = tmpStr;
			}

			tmpStr = "\n=========== Game Result ===========\n";
			for(int i=0; i<index; i++) {
				tmpStr = tmpStr + (i+1) + "등 " + otherPlayerNameSortedByHighScore[i] + ": " + otherPlayerScoreSortedByHighScore[i] + "\n";
			}
			printMessage(tmpStr);
			otherPlayerEventCount = 0;
			leavePlayRoom(m_clientStub);
		} 
//		else {	// 占시뤄옙占싱억옙 占쏙옙占쏙옙占쏙옙 占싹놂옙占쏙옙 占쌉뤄옙占쏙옙 占쏙옙占쏙옙占쏙옙 카占쏙옙트占쏙옙 占쏙옙占쏙옙占쏙옙.
//		}
	}
	
	private void addStylesToDocument(StyledDocument doc)
	{
		Style defStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

		Style regularStyle = doc.addStyle("regular", defStyle);
		StyleConstants.setFontFamily(regularStyle, "SansSerif");
		
		Style boldStyle = doc.addStyle("bold", defStyle);
		StyleConstants.setBold(boldStyle, true);
		
		Style linkStyle = doc.addStyle("link", defStyle);
		StyleConstants.setForeground(linkStyle, Color.BLUE);
		StyleConstants.setUnderline(linkStyle, true);
	}
	
	private JPanel inGameInfo() {
		JPanel p1 = new JPanel();
		JPanel p2 = new JPanel();
		JPanel p1Top = new JPanel();
		JPanel myCardArea = new JPanel();
		JPanel cardPanel = new JPanel();
		JTextArea myBattleCardTopic = new JTextArea("My battle cards");
		myBattleCardTopic.setEditable(false);
		p1Top.setLayout(new GridLayout(1, 2));
		p1Top.add(otherPlayersCard);
		p1Top.add(dungeonAndStageInfo);
		p1.setLayout(new BorderLayout());
//		p1.add(otherPlayersCard, BorderLayout.NORTH);
		p1.add(p1Top, BorderLayout.NORTH);
		p1.add(monsterInfo, BorderLayout.CENTER);
		p2.setLayout(new GridLayout(2, 1));
		myCardArea.setLayout(new BorderLayout());
		
		cardPanel.setLayout(new GridLayout(2, 1));
		JPanel cardPanelTop = new JPanel();
		cardPanelTop.setLayout(new FlowLayout());
		
		for(int i = 0; i < 4; i++) {
			cardPanelTop.add(myBattleCards[i]);
		}
		
		JPanel cardPanelBottom = new JPanel();
		cardPanelBottom.setLayout(new FlowLayout());
		
		for(int i = 4; i < 8; i++) {
			cardPanelBottom.add(myBattleCards[i]);
		}
		
		cardPanel.add(cardPanelTop);
		cardPanel.add(cardPanelBottom);
		
//		cardPanel.setLayout(new FlowLayout());
//		for(int i = 0; i < 8; i++) {
//			cardPanel.add(myBattleCards[i]);
//		}
		// old code
		
		myCardArea.add(myBattleCardTopic, BorderLayout.WEST);
		myCardArea.add(cardPanel, BorderLayout.CENTER);
		p2.add(myCardArea);
//		p2.add(myDrawableCards);
		p2.add(myScore);
		p1.add(p2, BorderLayout.SOUTH);
		
		return p1;
	}
	
	private JPanel northRight() {
		JPanel p = new JPanel();
		JScrollPane jsp = new JScrollPane(chatLog, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		chatLog.setSize((int) 20, 80);
		p.setLayout(new BorderLayout());
		p.setBackground(Color.LIGHT_GRAY);
//		p.setSize((int) 30, 80);
		p.add(startButton, BorderLayout.NORTH);
		p.add(jsp, BorderLayout.CENTER);
		p.add(myChatMessage, BorderLayout.SOUTH);
		
		return p;
	}
	
	public void printMessage(String strText) {
		StyledDocument doc = chatLog.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), strText, null);
			chatLog.setCaretPosition(chatLog.getDocument().getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void showSessionInfo(String result) {
		
		String resultText = "Name\t		Numer Of User\t		possible Groups" + 
				"\n======================================================================\n";
		CMSessionEvent se = null;
		se = m_clientStub.syncRequestSessionInfo();
		if (se == null) {
			resultText = resultText + "Failed to get session-info!!\n";
			setUserInfoBySessions(resultText);
			return;
		}
		Iterator<CMSessionInfo> iter = se.getSessionInfoList().iterator();
		while(iter.hasNext()) {
			CMSessionInfo itInfo = iter.next();
			resultText = resultText + itInfo.getSessionName() + "\t" + itInfo.getUserNum() + "\t" + itInfo.getPossibleGroups() +"\n";
		}
		if (result != null)
			resultText += result;
		setUserInfoBySessions(resultText);
		
	}
	
	// overloading
	public void showSessionInfo() {
		
		String resultText = "Name\t	Numer Of User\t	possible Groups" + 
				"\n===================================================\n";
		CMSessionEvent se = null;
		se = m_clientStub.syncRequestSessionInfo();
		if (se == null) {
			resultText = resultText + "Failed to get session-info!!\n";
			setUserInfoBySessions(resultText);
			return;
		}
		Iterator<CMSessionInfo> iter = se.getSessionInfoList().iterator();
		while(iter.hasNext()) {
			CMSessionInfo itInfo = iter.next();
			resultText = resultText + itInfo.getSessionName() + "\t\t" + itInfo.getUserNum() + "\t\t" + itInfo.getPossibleGroups() +"\n";
		}
		setUserInfoBySessions(resultText);
		
	}
	
	public class MyRadioActionListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			JRadioButton button = (JRadioButton) e.getSource();
			String type = button.getText();
			
			switch(type) {
			case "Session 1":
				sessionIndex = 1;
				break;
			case "Session 2":
				sessionIndex = 2;
				break;
			case "Session 3":
				sessionIndex = 3;
				break;
			default:
				break;
			}
			
			myChatMessage.requestFocus();
			
		}
	}
	
	public class MyActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton button = (JButton) e.getSource();
			String type = button.getText();
			
			switch(type) {
			case "1":
				myCurrentBattleCard = 1;
				break;
			case "2":
				myCurrentBattleCard = 2;
				break;
			case "3":
				myCurrentBattleCard = 3;
				break;
			case "4":
				myCurrentBattleCard = 4;
				break;
			case "5":
				myCurrentBattleCard = 5;
				break;
			case "6":
				myCurrentBattleCard = 6;
				break;
			case "7":
				myCurrentBattleCard = 7;
				break;
			case "my choice":
				setMyBattleCard(myCurrentBattleCard);
				break;
			case "Game Start":
				readyToPlay(m_clientStub);
				break;
			case "Log in":
				login(m_clientStub);
				break;
			case "Log out":
				break;
			case "Sign up":
				break;
			case "Sign out":
				break;
			case "Enter":
				joinPlayRoom(m_clientStub);
				break;
			case "Session Info":
				showSessionInfo();
				/*
				String resultText = "Name\t		Numer Of User\t		possible Groups" + 
						"\n======================================================================\n";
				CMSessionEvent se = null;
				se = m_clientStub.syncRequestSessionInfo();
				if (se == null) {
					resultText = resultText + "Failed to get session-info!!\n";
					setUserInfoBySessions(resultText);
					return;
				}
				Iterator<CMSessionInfo> iter = se.getSessionInfoList().iterator();
				while(iter.hasNext()) {
					CMSessionInfo itInfo = iter.next();
					resultText = resultText + itInfo.getSessionName() + "\t" + itInfo.getUserNum() + "\t" + itInfo.getPossibleGroups();
				}
				setUserInfoBySessions(resultText);
				*/
				break;
			default:
				break;
			}
			
			myChatMessage.requestFocus();
		}
	}
	
	public class MyKeyListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_ENTER) {						// 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 채占쏙옙창占쏙옙 占쌨쇽옙占쏙옙 占쏙옙占쏙옙.
				JTextField input = (JTextField) e.getSource();
				String strText = input.getText();
				if(strText == null) {
					return;
				}
//				printMessage(strText + "\n");
				m_clientStub.chat("/g", strText);
				
				input.setText("");
				input.requestFocus();
			} else if(key == KeyEvent.VK_ALT) {
				
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public class MyMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			if(e.getSource() instanceof JLabel) {
				Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
				setCursor(cursor);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public static void main(String[] args) {
//		Frame frame;
//		frame.setVisible(true);
		GUIClient app = new GUIClient();
		CMUser clientUser = app.getClientUserInfo();
		CMClientStub cmstub = app.getClientStub();
		cmstub.setAppEventHandler(app.getClientEventHandler());
		
		
	}

}
