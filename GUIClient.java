
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




// 각종 event boolean 추가
// session 접속하는 logic에 대해 다시 고민해볼 것.

public class GUIClient extends JFrame {
	private JTextArea otherPlayersCard = new JTextArea(3, 20);
	private JTextArea dungeonAndStageInfo = new JTextArea(3, 10);
	private JTextArea monsterInfo = new JTextArea(30, 30);
	private JTextArea myDrawableCards = new JTextArea(3, 30);
	private JButton[] myBattleCards = new JButton[8];
	private JTextArea myScore = new JTextArea(3, 30);
	private JButton startButton = new JButton("Game Start / Ready");
	private JTextPane chatLog = new JTextPane();
	private JTextField myChatMessage = new JTextField();
	
	private JButton enterButton = new JButton("Enter / leave");	//
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
	private JPasswordField loginPw = new JPasswordField(10);
	//private JTextField loginPw = new JTextField(10);
	
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
	private boolean isPlay;
	private boolean isReady;
	
	
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
		userInfoBySessions.setText("로그인 혹은 회원 가입을 해주세요. 회원 가입 시에는....\n"+
				"원하시는 UserName을 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해주세요.\n" + 
				"원하시는 Password를 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해주세요.\n");
		
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
		loginPw.setEchoChar('*');
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
		isPlay = false;
		isReady = false;
		
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
		stageNum = 0;		// ��� ���Ϳ� �� �� �οﶧ���� increase ������� �Ѵ�.
		myAggScore = 0;		// ���� ���ھ�.
		otherPlayerEventCount = 0;
		otherPlayerName = new String[5];
		otherPlayerScore = new int[5];
		for(int i=0; i<5; i++) {
			otherPlayerName[i] = "";
			otherPlayerScore[i] = 0;
		}
		
		// �� ���� (�ټ� ��������)���� �ʱ�ȭ ����� �ϴ� ��
		isMyBattleCardPossible = new boolean[NUMOFBATTLECARD + 1];
		for(int i = 0; i<NUMOFBATTLECARD + 1; i++) {
			isMyBattleCardPossible[i] = true;
		}

		// �� ������������ �ʱ�ȭ ����� �ϴ� �͵�
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
		timer.scheduleAtFixedRate(timerCountdown, 0, 1000);		// 1�� Ÿ�̸Ӹ� �������ش�.
		
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
	
	public void isLoginT() {
		isLogin = true;
	}
	
	public void isLoginF() {
		isLogin = false;
	}
	
	public void isSessionT() {
		isSession = true;
	}
	
	public void isSessionF() {
		isSession = false;
	}
	
	public void isPlayT() {
		isPlay = true;
	}
	
	public void isPlayF() {
		isPlay = false;
	}
	
	public void isReadyT() {
		isReady = true;
	}
	
	public void isReadyF() {
		isReady = false;
	}
	
	// 출력 창은 setUserInfoBySessions 사용하고...
	// 각종 input 받는 방법 고려할 것.
	public void login(CMClientStub cmstub) {
		
		String strUserName = null;
		String strPassword = null;
		CMSessionEvent reply = null;
		
		// 결과 창을 별도로 만들면 좋을 것.
		strUserName = loginId.getText();
		strPassword = String.valueOf(loginPw.getPassword());
		cmstub.loginCM(strUserName, strPassword);
		
		// ID/PW 초기화 시킬 것.
		loginId.setText("");
		loginPw.setText("");
	}
	
	public void logOut(CMClientStub cmstub) {
		
		isLoginF();
		setUserInfoBySessions("로그 아웃 되었습니다.");
		chatLog.setText("");
		cmstub.logoutCM();
	}
	
	public void signUp(CMClientStub cmstub) {
		
		String strUserName = loginId.getText();
		String strPassword = String.valueOf(loginPw.getPassword());
		String resultText = "";
		
		boolean isPossibleName = false;
		boolean isPossiblePass = false;
		
		if(!strUserName.isEmpty()) {
			if(strUserName.length() > 7) {
				if(strUserName.matches("^[0-9a-z]*$")){
					isPossibleName = true;
				}
				else {
					resultText += "UserName에는 0~9 사이의 숫자와 영어 소문자만 사용할 수 있습니다.\n";
				}
			}
			else {
				resultText += "UserName은 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해야 합니다.\n";
			}
		}
		else {
			resultText += "UserName은 공백으로 입력할 수 없습니다.\n";
		}
			
		if(!strPassword.isEmpty()) {
			if(strPassword.length() > 7) {
				if(strPassword.matches("^[0-9a-z]*$")) {
					if(!strPassword.equals(strUserName)) {
						isPossiblePass = true;
					}
					else {
						resultText += "Password는 UserName과 동일할 수 없습니다.\n";
					}
				}
				else {
					resultText += "Password에는 0~9 사이의 숫자와 영어 소문자만 사용할 수 있습니다.\n";
				}
			}
			else {
				resultText += "Password는 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해야 합니다.\n";
			}
		}
		else {
			resultText += "Password는 공백으로 입력할 수 없습니다.\n";
		}
			
		loginId.setText("");
		loginPw.setText("");
			
		if (isPossibleName && isPossiblePass) {
			cmstub.registerUser(strUserName, strPassword);
		}
		else {
			resultText += "다시 로그인 혹은 회원 가입을 해주세요. 회원 가입 시에는....\n"+
					"원하시는 UserName을 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해주세요.\n" + 
					"원하시는 Password를 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해주세요.\n";
			setUserInfoBySessions(resultText);
		}
	}
	
	public void signOut(CMClientStub cmstub) {
		
		String strUserName = loginId.getText();
		String strPassword = String.valueOf(loginPw.getPassword());
		
		cmstub.deregisterUser(strUserName, strPassword);
		
		loginId.setText("");
		loginPw.setText("");
		
	}
	
	public void showUserInfo(CMClientStub cmstub) {
		
		CMDummyEvent de = new CMDummyEvent();
		
		de.setHandlerSession(cmstub.getMyself().getCurrentSession());
		de.setHandlerGroup(cmstub.getMyself().getCurrentGroup());
		de.setDummyInfo("0050&"+cmstub.getMyself().getName()+"&please notify other users");
		
		cmstub.send(de, "SERVER");
	}
	
	public void joinPlayRoom(CMClientStub cmstub) {

		// use joinsession : if possible group = 0에 접근하려고 하면 안된다고 할 것.
		// use change group : group state = true인 group으로 바로 이동.
		// server :: change 그룹 받으면 해당 group의 인원수 확인 후 5를 초과하면 안된다고 할 것.
		//				5를 넘지 않을 시 받고 group 인원 수 +1하고 5면 false로 바꿀 것.
		
		String sessionName = "session"+Integer.toString(sessionIndex);
		CMDummyEvent de = new CMDummyEvent();
		
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
		// leave session 할 것.
		// server :: leave session 받으면 현재 group의 인원 수 -1할 것.
		//			:: -1한게 4이하면 flase에서 true로 바꿀 것
		//			:: 
		isReadyF();
		isPlayF();
		
	}
	
	public void readyToPlay(CMClientStub cmstub) {
		
		// server에 이벤트 보낼 것.
		// server :: event 받으면 해당 user의 state를 true 할 것.
		// 			참여중인 인원만큼 동적 array 선언하고 all true면 시작. group의 state false로 변경할 
		//			ready로 바꿀 때에만 한번 check함.
		
		CMDummyEvent de = new CMDummyEvent();
		
		de.setHandlerSession(cmstub.getMyself().getCurrentSession());
		de.setHandlerGroup(cmstub.getMyself().getCurrentGroup());
		de.setDummyInfo("0030&"+cmstub.getMyself().getName()+"&ready to play.");
		
		cmstub.send(de, "SERVER");
		isReadyT();
		
	}
	
	public void unreadyToPlay(CMClientStub cmstub) {
		
		// server에 이벤트 보낼 것.
		// server :: event 받으면 해당 user의 state를 false로 할 것.
		// 			 unready로 바꿀 때에는 check 할 필요가 없음.
		
		CMDummyEvent de = new CMDummyEvent();
		
		de.setHandlerSession(cmstub.getMyself().getCurrentSession());
		de.setHandlerGroup(cmstub.getMyself().getCurrentGroup());
		de.setDummyInfo("0040&"+cmstub.getMyself().getName()+"&unready to play.");
		
		cmstub.send(de, "SERVER");
		isReadyF();
		
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
		if(cardIndex == 0) {		// 0�� ��ư�� "����" ��ư�̶� 0���� �������� �ʴ´�.
			return;
		}
		myBattleCards[cardIndex].setEnabled(buttonSwitch);
	}
	
	public void setIsThisPlayerChooseCard(boolean b) {
		isThisPlayerChooseCard = b;
	}
	
	public void proceedGame() {		// ���� ����. ���������� ���� ���� ���� monster order�� �����־�� �Ѵ�.
		
		isPlayT();
		myRedGem = 0;
		myYellowGem = 0;
		myBlueGem = 0;
		numOfPlayer = m_clientStub.getGroupMembers().getMemberNum();		
		// getMemberNum()�� ���� ������ �׷� �ο� ���� ��ȯ�Ѵ�.
		stageNum = 0;
		myAggScore = 0;
		otherPlayerEventCount = 0;
		
		// error 가능성 존재 (5가 아니라 numOfPlayer 까지 도는게 맞을듯)
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
		
		// ù ��° ���ʹ� ���� ���� �� �ٷ� ���̰� �Ѵ�.
		String currentMonsterInfoText = currentMonster.getName()
				+ "\nPower: " + currentMonster.getPower() + "\tRed Gem: " + currentMonster.getRedGem() 
				+ "\tYellow Gem: " + currentMonster.getYellowGem() + "\tBlue Gem: " + currentMonster.getBlueGem();
		setMonsterInfo(currentMonsterInfoText);
			
		setDungeonAndStageInfo("Stage: " + (stageNum + 1));
		setMyScore("Red Gems: " + myRedGem + "\tYellow Gems: " + myYellowGem + "\tBlue Gems: " + myBlueGem);
			
		timerCountdown.setDrawCount(40);	// ī��Ʈ�ٿ� 30�� ����.  
			
			// ��������� ù ��° �������� �� �� !!!!!!!
			
			
			
			
//			stageNum++;  no no
			
//			if(currentMonster.isNextMonsterExist()) {	// ��Ʋ ���� �� ���� �� ���Ͱ� ������ ���Ͱ� �ƴϸ� ���� ���� ����ְ� �ٽ� 1�ܰ��.  
//				currentMonster = currentMonster.getNextMonster();
//			} else {					
//				// ������ ���͸� ���� ���� �� ���� ����.
//				myAggScore = getScore();
//				showMyScore();
//				
//				System.out.println("Game Over !");
//			}
		
	}
	
	public void proceedStage() {
		if(currentMonster.isNextMonsterExist()) {		// ���� ���Ͱ� ������ Stage ����
			stageNum++;
			
			if(stageNum % 5 == 0) {		// stage�� 5�� ������ �������� ���� ���� �Լ��̹Ƿ� ��Ʋ ī�� ����.
				for(int i = 0; i<NUMOFBATTLECARD + 1; i++) {
					isMyBattleCardPossible[i] = true;
					setMyCardButtonEnable(i, true);
				}
			}
			
			for(int i=0; i<5; i++) {
				otherPlayerAndCard[i] = "";
				otherBattleCard[i] = 0;
			}
			
			//setIsThisPlayerChooseCard(true);
			isThisPlayerChooseCard = false;
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
			// 10�� �Ŀ� ���ο� ���� ����, �� ��� ī�� â�� �����ش�. �׸��� 10�� �Ŀ� �ٽ� ���� ��ư�� Ȱ��ȭ �ȴ�.
			
			timerCountdown.setDrawCount(45);		// ���⼭ 40�ʷ� �� ������, ������ ���� ���� ���� �� 10�� ������ �߱� �����̴�.
			
		} else {		// ���� ���Ͱ� ������ Stage ����
			myAggScore = getScore();
			showMyScore();
			
			
			// ���� ���� �ڵ� !! 
		}
	}
	
	public void setMyBattleCard(int index) {		// index�� ���� �� ��Ʋ ī��. 0���� ��ȿ, 1~7 ���� �ִ�.
		if(isThisPlayerChooseCard) {	// �̹� ī�带 �����ؼ� �´ٸ� ������� �ʴ´�. �� ������ ���� �Ŀ� false�� �ٲ��.
			return;
		}
		if(index != 0) { // index == 0 �� ī��� ��ȿ ī��.
			if(isMyBattleCardPossible[index] == false) {	// ��ȿ ī�带 �����ϰ�, �̹� �� ī�带 �����ϸ� ������ �� ����.
				printMessage("이미 카드를 드로우 했거나, 다음 배틀 대기 시간입니다.\n");
				return;
			}
			setMyCardButtonEnable(index, false);
		}
		
		timerCountdown.setDrawCount(-1);	// Ÿ�̸� �½�ũ�� ī��Ʈ�� -1�̸� �ƹ��͵� ���� �ʴ´�. 
		isThisPlayerChooseCard = true;
		myCurrentBattleCard = index;
		isMyBattleCardPossible[index] = false;
//		allPlayerCard[allPlayerCardCount++] = index;		// ��� �÷��̾��� ī�带 ����ϴ� �迭
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
		
		System.out.println(m_clientStub.getMyself().getName() + " :" + allPlayerCardCount);
		System.out.println(m_clientStub.getMyself().getName() + " :" + numOfPlayer);
		
//		if(numOfPlayer == allPlayerCardCount) {	// ī�尡 ���� ���� �÷��̾� ���� ������ ��Ʋ ����
//			System.out.println("전투 시작~ 워후~");
//			battleWithMonster();
//		}
	}
	
	public void setAllBattleCard(String playerName, int battleCard) {
		// ������ �� �������� ������ �ʿ� !! Ȥ�� �ٸ� ������ ID���� ���� �޾ƿ;� �Ѵ�.
		if(otherBattleCardIndex >= 5) {
			return;
		}
		otherPlayerAndCard[otherBattleCardIndex] = playerName + ": " + String.valueOf(battleCard);
		otherBattleCard[otherBattleCardIndex] = battleCard;
		otherBattleCardIndex++;
		
//		allPlayerCard[allPlayerCardCount++] = battleCard;
		allPlayerCardCount++;
		
//		System.out.println(m_clientStub.getMyself().getName() + " :" + allPlayerCardCount);
//		System.out.println(m_clientStub.getMyself().getName() + " :" + numOfPlayer);
		
		System.out.println(allPlayerCardCount);
		
		if(numOfPlayer == allPlayerCardCount) {	// ī�尡 ���� ���� �÷��̾� ���� ������ ��Ʋ ����
//			System.out.println("전투 시작~ 워후~");
			battleWithMonster();
		}
	}
	
	public void battleWithMonster() {
		int[] activeBattleCard = Arrays.copyOf(otherBattleCard, otherBattleCard.length);
		int battleCardAgg = 0;			// ��ȿ�� ��Ʋī�� ����
		int minBattleCard = 8;
		
		String otherPlayerAndCardText = "";
		for(int i=0; i < otherBattleCardIndex; i++) {
			otherPlayerAndCardText = otherPlayerAndCardText + otherPlayerAndCard[i] + " ";
		}
		// ��� ����� ī�� ����
		setOtherPlayersCard(otherPlayerAndCardText);
		
		if(myCurrentBattleCard == 0) {	// �� ī�尡 0�̸� ��ȿȭ.
			isMyBattleCardActive = false;
		}
		
		for(int i=0; i<5; i++) {		// �� ī��� �ٸ� ������� �� ī�� ��. �׸��� �ٸ� ����� �� ī��� ������ ��.
			int tmp = otherBattleCard[i];
			if(tmp == myCurrentBattleCard) {
				if (isMyCardCheckedInBattleCardArray == false) {
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
		
//		if (isMyBattleCardActive) {
//			battleCardAgg += myCurrentBattleCard;
//		}
		
		for(int i = 0; i < 5; i++) {	// ��ȿ�� ī���� ������ �ջ�.
			if(activeBattleCard[i] > 0) {
				battleCardAgg += activeBattleCard[i];
				if(minBattleCard > activeBattleCard[i]) {	// �� ī�带 ������ ��ȿ�� ī�� �߿��� ���� ���ڰ� ���� ī�� ����
					minBattleCard = activeBattleCard[i];
				}
			}
		}
		
		CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
		CMUser myself = interInfo.getMyself();
		CMUserEvent use = new CMUserEvent();
		CMUserEvent useForBattleFinish = new CMUserEvent();		// ���� User Event�� ��Ȱ���ص� �Ǵ��� Ȯ��ġ �ʾƼ� ���� ���� ���� �̺�Ʈ.
		use.setHandlerSession(myself.getCurrentSession());
		use.setHandlerGroup(myself.getCurrentGroup());
		
		if(battleCardAgg >= currentMonster.getPower()) {	// �¸� !!
			if(isMyBattleCardActive) {
				if( myCurrentBattleCard == minBattleCard ) {	// �� ī�尡 ���� �۴ٸ� ���� �޴´�. ���� �̱�� ���� �¸� Event ����.
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
		} else {	// �й�. �� ������ ����� Event ����.
			if(isMyBattleCardActive == false || myCurrentBattleCard < minBattleCard) {
				int maxNumOfGem = this.myRedGem;	// �� ���� ���� �� ���� ���� ���� ���� ����.
				int maxGemIndex = 4;				// �� ���� ���� �� ���� ���� ���� ����. 3 bit�� bit-wise ���. 4�� ����, 2�� ���, 1�� �Ķ�.
				
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
				
				if (maxNumOfGem != 0) {
					
					if((maxGemIndex & 4) == 4) {		// ���� ���� ���� !!
						use.setStringID("Lose Red Gems");
						use.setEventField(CMInfo.CM_INT, "Num Of Red Gems", String.valueOf(this.myRedGem));
						m_clientStub.cast(use, myself.getCurrentSession(), myself.getCurrentGroup());
					
	//				if(currentMonster.isNextMonsterExist()) {
	//					this.currentMonster.getNextMonster().plusGems(myRedGem, 0, 0);
	//				}
						this.myRedGem = 0;
					}
				
					if((maxGemIndex & 2) == 2) {		// ��� ���� ���� !!
						use.setStringID("Lose Yellow Gems");
						use.setEventField(CMInfo.CM_INT, "Num Of Yellow Gems", String.valueOf(this.myYellowGem));
						m_clientStub.cast(use, myself.getCurrentSession(), myself.getCurrentGroup());
					
	//				if(currentMonster.isNextMonsterExist()) {
	//					this.currentMonster.getNextMonster().plusGems(0, myYellowGem, 0);
	//				}
						this.myYellowGem = 0;
					}
				
					if((maxGemIndex & 1) == 1) {		// �Ķ� ���� ���� !!
						use.setStringID("Lose Blue Gems");
						use.setEventField(CMInfo.CM_INT, "Num Of Blue Gems", String.valueOf(this.myBlueGem));
						m_clientStub.cast(use, myself.getCurrentSession(), myself.getCurrentGroup());
					
	//				if(currentMonster.isNextMonsterExist()) {
	//					this.currentMonster.getNextMonster().plusGems(0, 0, myBlueGem);
	//				}
						this.myBlueGem = 0;
					}
				}
			}
		}
		
		use = null;
		setMyScore("Red Gems: " + myRedGem + "\tYellow Gems: " + myYellowGem + "\tBlue Gems: " + myBlueGem);
		
		// �� ���ÿ��� ������ ������ �����ٰ� �˷��ִ� �ڵ�.
		useForBattleFinish.setStringID("Battle Finish");
		useForBattleFinish.setHandlerSession(myself.getCurrentSession());
		useForBattleFinish.setHandlerGroup(myself.getCurrentGroup());
		useForBattleFinish.setEventField(CMInfo.CM_INT, "userName", myself.getName());
		m_clientStub.cast(useForBattleFinish, myself.getCurrentSession(), myself.getCurrentGroup());
		
//		battleFinish();
	}
	
	public void battleFinish() {
		
		// add Client
		int new_numOfPlayer = m_clientStub.getGroupMembers().getMemberNum();
	
		if(new_numOfPlayer != numOfPlayer)
			numOfPlayer = new_numOfPlayer;
		// add Client
		
		if(otherPlayerFinishCount >= numOfPlayer - 1) {	// �������� �Է��� ������ ī��Ʈ�� ��ü �÷��̾� �� -1 �� �� �Է��ϰ� �ȴ�.
			otherPlayerFinishCount = 0;
			
			proceedStage();					// 
			
		} else {		// �÷��̾� ������ �ϳ��� �Է��� ������ ī��Ʈ�� ������.
			otherPlayerFinishCount++;
		}
	}
	
	public int getScore() {		// ���� ������ ���� �ջ�.
		int score = 0;
		int minNumOfGem = this.myRedGem;	// �� ���� ���� �� ���� ���� ���� ���� ����.
		
		if(minNumOfGem > this.myYellowGem) {
			minNumOfGem = this.myYellowGem;
		}
		if(minNumOfGem > this.myBlueGem) {
			minNumOfGem = this.myBlueGem;
		}
		score = (minNumOfGem * 3) + this.myRedGem + this.myYellowGem + this.myBlueGem;
		
		return score;
	}
	
	public void showMyScore() {		// �� ������ �� client�� �Է��ϰ� event�� �ѷ��ִ� �Լ�
		CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
		CMUser myself = interInfo.getMyself();
		
		CMUserEvent use = new CMUserEvent();
		use.setStringID("Show score");
		use.setEventField(CMInfo.CM_STR, "userName", myself.getName());
		use.setEventField(CMInfo.CM_INT, "Score", String.valueOf(myAggScore));
		m_clientStub.cast(use, myself.getCurrentSession(), myself.getCurrentGroup());
		use = null;
		
		showAllScore(myself.getName(), myAggScore);		// �� ������ �ѷ��ְ� show all score�� �� ������ �Է����ش�.
	}
	
	public void showAllScore(String userName, int score) {	// ������ �� ������ ������ ��� �����ִ� �Լ�. �̺�Ʈ �ڵ鷯�� �θ���.

		otherPlayerName[otherPlayerEventCount] = userName;
		otherPlayerScore[otherPlayerEventCount] = score;
		otherPlayerEventCount++;
		
		if(otherPlayerEventCount > numOfPlayer - 1) {	// �������� �Է��� ������ ī��Ʈ�� ��ü �÷��̾� �� -1 �� �� �Է��ϰ� �ȴ�.
			int index = otherPlayerEventCount + 1;
			int tmp, j;	// ���Ŀ� �� ����
			String tmpStr;	// ���Ŀ� ���� �ӽ÷� ���� ��Ʈ��.
			String[] otherPlayerNameSortedByHighScore = Arrays.copyOf(otherPlayerName, otherPlayerName.length);
			int[] otherPlayerScoreSortedByHighScore = Arrays.copyOf(otherPlayerScore, otherPlayerScore.length);
			for(int i = 1; i < index; i++) {	// ���� ����
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
	/*
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
	*/
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
		
		String tempLog = "";

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton button = (JButton) e.getSource();
			String type = button.getText();
			
			switch(type) {
			case "1":
				if (isLogin && isSession && isPlay)
					myCurrentBattleCard = 1;
				break;
			case "2":
				if (isLogin && isSession && isPlay)
					myCurrentBattleCard = 2;
				break;
			case "3":
				if (isLogin && isSession && isPlay)
					myCurrentBattleCard = 3;
				break;
			case "4":
				if (isLogin && isSession && isPlay)
					myCurrentBattleCard = 4;
				break;
			case "5":
				if (isLogin && isSession && isPlay)
					myCurrentBattleCard = 5;
				break;
			case "6":
				if (isLogin && isSession && isPlay)
					myCurrentBattleCard = 6;
				break;
			case "7":
				if (isLogin && isSession && isPlay)
					myCurrentBattleCard = 7;
				break;
			case "my choice":
				if (isLogin && isSession && isPlay)
					setMyBattleCard(myCurrentBattleCard);
				break;
			case "Game Start / Ready":
				if (isLogin && isSession && !isPlay && !isReady) {
					tempLog = m_clientStub.getMyself().getName()+"님이 준비를 하였습니다.";
					printMessage(tempLog);
					printMessage("\n");
					m_clientStub.chat("/g", tempLog);
					tempLog = "";
					readyToPlay(m_clientStub);
				}
				else if (isLogin && isSession && !isPlay && isReady) {
					tempLog = m_clientStub.getMyself().getName()+"님이 준비를 취소하였습니다.";
					printMessage(tempLog);
					printMessage("\n");
					m_clientStub.chat("/g", tempLog);
					tempLog = "";
					unreadyToPlay(m_clientStub);
				}
				break;
			case "Log in":
				if (!isLogin)
					login(m_clientStub);
				break;
			case "Log out":
				if (isLogin && !isPlay) {
					if (isSession) {
						tempLog = m_clientStub.getMyself().getName()+ "님이 퇴장하였습니다.";
						//printMessage(tempLog);
						//printMessage("\n");
						m_clientStub.chat("/g", tempLog);
						tempLog = "";
						leavePlayRoom(m_clientStub);
						chatLog.setText("");
					}
					logOut(m_clientStub);
				}
				break;
			case "Sign up":
				if (!isLogin)
					signUp(m_clientStub);
				break;
			case "Sign out":
				if (!isLogin)
					signOut(m_clientStub);
				break;
			case "Enter / leave":
				if (isLogin && !isSession) {
					joinPlayRoom(m_clientStub);
					// user 정보 불러오면 좋을 것. 가능하면 ID와 ready 여부도.
				}
				if (isLogin && isSession && !isPlay) {
					tempLog = m_clientStub.getMyself().getName()+ "님이 퇴장하였습니다.";
					//printMessage(tempLog);
					//printMessage("\n");
					m_clientStub.chat("/g", tempLog);
					tempLog = "";
					leavePlayRoom(m_clientStub);
					chatLog.setText("");
				}
				break;
			case "Session Info":
				if(isLogin)
					showSessionInfo();
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
			if(key == KeyEvent.VK_ENTER) {						// ���� ������ ä��â�� �޼��� ����.
				JTextField input = (JTextField) e.getSource();
				String strText = input.getText();
				if(strText == null) {
					return;
				}
				printMessage(strText + "\n");
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
	
	// add Client
	public void addExitEvent(GUIClient client, CMClientStub cmstub) {
		
		//client.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		client.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				
				if(!client.isThisPlayerChooseCard) {
					client.setMyBattleCard(0);
				}
				//printMessage(tempLog);
				//printMessage("\n");
				cmstub.chat("/g", m_clientStub.getMyself().getName()+ "님이 퇴장하였습니다.");
				client.leavePlayRoom(cmstub);
				client.logOut(cmstub);
			}
		});
	}
	// add Client
	
	
	public static void main(String[] args) {
//		Frame frame;
//		frame.setVisible(true);
		GUIClient app = new GUIClient();
		CMUser clientUser = app.getClientUserInfo();
		CMClientStub cmstub = app.getClientStub();
		cmstub.setAppEventHandler(app.getClientEventHandler());
		app.addExitEvent(app, cmstub);
		cmstub.startCM();	
	}
}