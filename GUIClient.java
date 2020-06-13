package gamePart;
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
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

import java.security.SecureRandom;

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
	private JButton sessionEnter = new JButton("Enter Session");
	private JButton sessionInfo = new JButton("Session Info");
	private JRadioButton session[] 
			= { new JRadioButton("Session 1"), new JRadioButton("Session 2"), new JRadioButton("Session 3") }; 	// {"Session 1", "Session 2", "Session 3"}
	private ButtonGroup sessionButtonGroup = new ButtonGroup();
	private int sessionIndex;
//	private JButton showSession2Button = new JButton("Session 2");
//	private JButton showSession3Button = new JButton("Session 3");
	private JTextArea userInfoBySessions = new JTextArea(5, 50);
	
	private MyMouseListener cmMouseListener;
	private CMClientStub m_clientStub;
	private GUIClientEventHandler m_eventHandler;
	
	
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
//	private int[] otherBattleCard;
	private int otherBattleCardIndex;
	private int myCurrentBattleCard;
	private boolean isMyBattleCardActive;
	private int[] allPlayerCard;
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
	
	private Timer timer;
	private TimerCountdown timerCountdown;
	
	private SecureRandom random;
	
	public GUIClient() {
		MyKeyListener cmKeyListener = new MyKeyListener();
		MyActionListener cmActionListener = new MyActionListener();
		cmMouseListener = new MyMouseListener();
		
		setTitle("WA! Dungeon");
		Container pane = this.getContentPane();
		otherPlayersCard.setEditable(false);
		dungeonAndStageInfo.setEditable(false);
		monsterInfo.setEditable(false);
		myBattleCards[0] = new JButton("선택");
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
		
		otherPlayersCard.setText("otherPlayersCard");
		dungeonAndStageInfo.setText("dungeonAndStageInfo");
		monsterInfo.setText("monsterInfo");
		myDrawableCards.setText("myDrawableCards");
		myScore.setText("myScore");
//		chatLog.setText("chatLog");
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
		southPanelCenter.add(sessionEnter);
		sessionButtonGroup.add(session[0]);
		sessionButtonGroup.add(session[1]);
		sessionButtonGroup.add(session[2]);
//		southPanelCenter.add(sessionButtonGroup);
		southPanelCenter.add(session[0]);
		southPanelCenter.add(session[1]);
		southPanelCenter.add(session[2]);
//		southPanelCenter.add(userInfoBySessions);
//		southPanelCenter.add(showSession2Button);
//		southPanelCenter.add(showSession3Button);
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
		sessionEnter.addActionListener(cmActionListener);
		session[0].addActionListener(cmActionListener);
		session[1].addActionListener(cmActionListener);
		session[2].addActionListener(cmActionListener);
		sessionInfo.addActionListener(cmActionListener);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1000, 600);
		setVisible(true);
		
		m_clientStub = new CMClientStub();
		m_eventHandler = new GUIClientEventHandler(m_clientStub, this);
		
		myChatMessage.requestFocus();
		
//		monster = new Monster[] { new Monster(1, 3, 0, 0),  new Monster(2, 0, 4, 1), new Monster(3, 1, 0, 1), new Monster(4, 2, 2, 0), new Monster(5, 5, 0, 0)
//				, new Monster(7, 3, 2, 0), new Monster(8, 0, 1, 3), new Monster(10, 2, 3, 0), new Monster(11, 1, 0, 4), new Monster(12)
//				, new Monster(13), new Monster(15), new Monster(18, 1, 2, 2), new Monster(19, 1, 1, 1), new Monster(20, 2, 2, 1)};
		
		monster = new Monster[] 
				{ new Monster(1, 3, 0, 0),  new Monster(2, 0, 3, 1), new Monster(3, 1, 0, 1), new Monster(4, 2, 2, 0), new Monster(5, 5, 0, 2)
				, new Monster(7, 3, 2, 0), new Monster(8, 0, 1, 4), new Monster(10, 3, 3, 0), new Monster(11, 2, 0, 4), new Monster(12, 2, 2, 0)
				, new Monster(13, 1, 1, 2), new Monster(15, 2, 5, 0), new Monster(18, 1, 2, 2), new Monster(19, 2, 1, 2), new Monster(20, 3, 2, 2)};
		
		sessionIndex = 0;
		session[0].setSelected(true);
		session[1].setSelected(false);
		session[2].setSelected(false);
		
		// 한 게임마다 초기화 해줘야 하는 것
		monsterOrder = new int[NUMOFMONSTER];
		for(int i = 0; i < NUMOFMONSTER; i++) {
			monsterOrder[i] = i;
		}
		myRedGem = 0;
		myYellowGem = 0;
		myBlueGem = 0;
		numOfPlayer = 0;
		stageNum = 0;		// 얘는 몬스터와 한 번 싸울때마다 increase 시켜줘야 한다.
		myAggScore = 0;		// 종합 스코어.
		otherPlayerEventCount = 0;
		otherPlayerName = new String[5];
		otherPlayerScore = new int[5];
		for(int i=0; i<5; i++) {
			otherPlayerName[i] = "";
			otherPlayerScore[i] = 0;
		}
		
		// 한 던전 (다섯 스테이지)마다 초기화 해줘야 하는 것
		isMyBattleCardPossible = new boolean[NUMOFBATTLECARD + 1];
		for(int i = 0; i<NUMOFBATTLECARD + 1; i++) {
			isMyBattleCardPossible[i] = true;
		}

		// 한 스테이지마다 초기화 해줘야 하는 것들
		isThisPlayerChooseCard = false;
		otherPlayerAndCard = new String[] {"", "", "", "", ""};
//		otherBattleCard = new int[] {0, 0, 0, 0, 0};
		allPlayerCard = new int[] {0, 0, 0, 0, 0};
		allPlayerCardCount = 0;
		otherBattleCardIndex = 0;
		myCurrentBattleCard = 0;
		isMyBattleCardActive = true;
		otherPlayerFinishCount = 0;
		

		
		timer = new Timer();
		timerCountdown = new TimerCountdown(this);
		timer.scheduleAtFixedRate(timerCountdown, 0, 1000);		// 1초 타이머를 설정해준다.
		
		random = new SecureRandom();
	}
	
	public Monster getCurrentMonster() {
		return this.currentMonster;
	}

	public void linkMonsterOrder(int[] monOrder) {
		for(int i = 0; i < NUMOFMONSTER; i++) {
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
		if(cardIndex == 0) {		// 0번 버튼은 "선택" 버튼이라서 0번은 해제되지 않는다.
			return;
		}
		myBattleCards[cardIndex].setEnabled(buttonSwitch);
	}
	
	public void setIsThisPlayerChooseCard(boolean b) {
		isThisPlayerChooseCard = b;
	}
	
	public void proceedGame() {		// 게임 시작. 서버에서는 게임 시작 전에 monster order를 보내주어야 한다.
		myRedGem = 0;
		myYellowGem = 0;
		myBlueGem = 0;
		numOfPlayer = m_clientStub.getGroupMembers().getMemberNum() + 1;		
		// getMemberNum()은 나를 제외한 그룹 인원 수를 반환한다.
		stageNum = 0;
		myAggScore = 0;
		otherPlayerEventCount = 0;
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
			allPlayerCard[i] = 0;
		}
		otherBattleCardIndex = 0;
		myCurrentBattleCard = 0;
		isMyBattleCardActive = true;
//		allPlayerCard = new int[] {0, 0, 0, 0, 0};
		allPlayerCardCount = 0;
		otherPlayerFinishCount = 0;
		
		// 첫 번째 몬스터는 게임 시작 후 바로 보이게 한다.
		String currentMonsterInfoText = currentMonster.getName()
				+ "\nPower: " + currentMonster.getPower() + "\tRed Gem: " + currentMonster.getRedGem() 
				+ "\tYellow Gem: " + currentMonster.getYellowGem() + "\tBlue Gem: " + currentMonster.getBlueGem();
		setMonsterInfo(currentMonsterInfoText);
			
		setDungeonAndStageInfo("Stage: " + (stageNum + 1));
		setMyScore("Red Gems: " + myRedGem + "\tYellow Gems: " + myYellowGem + "\tBlue Gems: " + myBlueGem);
			
		timerCountdown.setDrawCount(30);	// 카운트다운 30초 설정.  
			
			// 여기까지가 첫 번째 전투에서 할 일 !!!!!!!
			
			
			
			
//			stageNum++;  no no
			
//			if(currentMonster.isNextMonsterExist()) {	// 배틀 종료 후 만약 이 몬스터가 마지막 몬스터가 아니면 다음 몬스터 띄워주고 다시 1단계로.  
//				currentMonster = currentMonster.getNextMonster();
//			} else {					
//				// 마지막 몬스터면 점수 산정 후 게임 종료.
//				myAggScore = getScore();
//				showMyScore();
//				
//				System.out.println("Game Over !");
//			}
		
	}
	
	public void proceedStage() {
		if(currentMonster.isNextMonsterExist()) {		// 다음 몬스터가 있으면 Stage 진행
			if(stageNum % 5 == 0) {		// stage가 5로 나누어 떨어지면 다음 던전 입성이므로 배틀 카드 갱신.
				for(int i = 0; i<NUMOFBATTLECARD + 1; i++) {
					isMyBattleCardPossible[i] = true;
					setMyCardButtonEnable(i, true);
				}
			}
			
			for(int i=0; i<5; i++) {
				otherPlayerAndCard[i] = "";
				allPlayerCard[i] = 0;
			}
			otherBattleCardIndex = 0;
			myCurrentBattleCard = 0;
			isMyBattleCardActive = true;
			allPlayerCardCount = 0;
			stageNum++;
			
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
			timerCountdown.setInfoShowCount(10, currentMonsterInfoText);	
			// 10초 후에 새로운 몬스터 정보, 빈 상대 카드 창을 보여준다. 그리고 10초 후에 다시 선택 버튼이 활성화 된다.
			
			timerCountdown.setDrawCount(40);		// 여기서 40초로 한 이유는, 위에서 몬스터 정보 띄우는 걸 10초 딜레이 했기 때문이다.
			
		} else {		// 다음 몬스터가 없으면 Stage 종료
			myAggScore = getScore();
			showMyScore();
			
			
			// 게임 종료 코드 !! 
		}
	}
	
	public int[] shuffleMonsterOrder(int[] arr, int count) {	// parameter로 monsterOrder를 넣는다.
		// Server에서 실행한다. 여기서는 쓰이지 않으니까 무시.
		int tmp = 0, rand1 = 0, rand2 = 0;
		
		for(int i = 0; i < count; i++) {
			rand1 = random.nextInt(arr.length);
			rand2 = random.nextInt(arr.length);
			tmp = arr[rand1];
			arr[rand1] = arr[rand2];
			arr[rand2] = tmp;
		}
		return arr;
	}
	
	public void setMyBattleCard(int index) {		// index가 내가 낸 배틀 카드. 0번은 무효, 1~7 까지 있다.
		if(isThisPlayerChooseCard) {	// 이미 카드를 선택해서 냈다면 실행되지 않는다. 이 변수는 전투 후에 false로 바뀐다.
			return;
		}
		if(index != 0) { // index == 0 인 카드는 무효 카드.
			if(isMyBattleCardPossible[index] == false) {	// 무효 카드를 제외하고, 이미 쓴 카드를 선택하면 제출할 수 없다.
				return;
			}
		}
		timerCountdown.setDrawCount(-1);	// 타이머 태스크의 카운트가 -1이면 아무것도 하지 않는다. 
		isThisPlayerChooseCard = true;
		myCurrentBattleCard = index;
		isMyBattleCardPossible[index] = false;
		allPlayerCard[allPlayerCardCount++] = index;		// 모든 플레이어의 카드를 등록하는 배열
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
		
		if(numOfPlayer == allPlayerCardCount) {	// 카드가 뽑힌 수와 플레이어 수가 같으면 배틀 진행
			battleWithMonster();
		}
	}
	
	public void setOtherBattleCard(String playerName, int battleCard) {
		// 유저들 간 절대적인 순서가 필요 !! 혹은 다른 유저의 ID까지 같이 받아와야 한다.
		if(otherBattleCardIndex >= 5) {
			return;
		}
		otherPlayerAndCard[otherBattleCardIndex++] = playerName + ": " + String.valueOf(battleCard);
//		otherBattleCard[otherBattleCardIndex++] = battleCard;
//		otherBattleCardIndex++;
		
		allPlayerCard[allPlayerCardCount++] = battleCard;
//		allPlayerCardCount++;
		
		if(numOfPlayer == allPlayerCardCount) {	// 카드가 뽑힌 수와 플레이어 수가 같으면 배틀 진행
			battleWithMonster();
		}
	}
	
	public void battleWithMonster() {
		int[] activeBattleCard = Arrays.copyOf(allPlayerCard, allPlayerCard.length);
		int battleCardAgg = 0;			// 유효한 배틀카드 총합
		int minBattleCard = 8;
		
		String otherPlayerAndCardText = "";
		for(int i=0; i < otherBattleCardIndex - 1; i++) {
			otherPlayerAndCardText = otherPlayerAndCardText + otherPlayerAndCard[i] + " ";
		}
		// 모든 사람의 카드 공개
		setOtherPlayersCard(otherPlayerAndCardText);
		
		if(myCurrentBattleCard == 0) {	// 내 카드가 0이면 무효화.
			isMyBattleCardActive = false;
		}
		
		for(int i=0; i<5; i++) {		// 내 카드와 다른 사람들이 낸 카드 비교. 그리고 다른 사람이 낸 카드들 끼리도 비교.
			int tmp = allPlayerCard[i];
			if(tmp == myCurrentBattleCard) {
				isMyBattleCardActive = false;
			}
			
			for(int j=i+1; j<5; j++) {
				if(tmp == allPlayerCard[j]) {
					activeBattleCard[j] = -1;
					activeBattleCard[i] = -1;
				}
			}
		}
		
		for(int i = 0; i < 5; i++) {	// 유효한 카드의 전투력 합산.
			if(activeBattleCard[i] > 0) {
				battleCardAgg += activeBattleCard[i];
				if(minBattleCard > activeBattleCard[i]) {	// 내 카드를 제외한 유효한 카드 중에서 가장 숫자가 작은 카드 선택
					minBattleCard = activeBattleCard[i];
				}
			}
		}
		
		CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
		CMUser myself = interInfo.getMyself();
		CMUserEvent use = new CMUserEvent();
		CMUserEvent useForBattleFinish = new CMUserEvent();		// 기존 User Event를 재활용해도 되는지 확실치 않아서 새로 만든 유저 이벤트.
		use.setHandlerSession(myself.getCurrentSession());
		use.setHandlerGroup(myself.getCurrentGroup());
		
		if(battleCardAgg >= currentMonster.getPower()) {	// 승리 !!
			if(isMyBattleCardActive) {
				if( myCurrentBattleCard < minBattleCard ) {	// 내 카드가 가장 작다면 보상 받는다. 내가 이기면 내가 승리 Event 송출.
					String winGems = myself.getName() + " wins\nRed Gems: " + currentMonster.getRedGem() 
						+ "\nYellow Gems: " + currentMonster.getYellowGem() + "\nBlue Gems: " + currentMonster.getBlueGem()+"\n";
					this.myRedGem += currentMonster.getRedGem();
					this.myYellowGem += currentMonster.getYellowGem();
					this.myBlueGem +=  currentMonster.getBlueGem();
					
					use.setStringID("Win Gems");
					use.setEventField(CMInfo.CM_INT, "Num Of Gems", winGems);
					printMessage(winGems);
					m_clientStub.cast(use, myself.getCurrentSession(), myself.getCurrentGroup());
					
				}
			}
		} else {	// 패배. 내 보석이 뺏기면 Event 송출.
			if(isMyBattleCardActive == false || myCurrentBattleCard < minBattleCard) {
				int maxNumOfGem = this.myRedGem;	// 세 보석 종류 중 가장 많은 수의 보석 개수.
				int maxGemIndex = 4;				// 세 보석 종류 중 가장 많은 수의 보석. 3 bit로 bit-wise 계산. 4는 빨강, 2는 노랑, 1은 파랑.
				
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
				
				if((maxGemIndex & 4) == 4) {		// 빨강 보석 몰수 !!
					use.setStringID("Lose Red Gems");
					use.setEventField(CMInfo.CM_INT, "Num Of Red Gems", String.valueOf(this.myRedGem));
					m_clientStub.cast(use, myself.getCurrentSession(), myself.getCurrentGroup());
					
					if(currentMonster.isNextMonsterExist()) {
						this.currentMonster.getNextMonster().plusGems(myRedGem, 0, 0);
					}
					this.myRedGem = 0;
				}
				
				if((maxGemIndex & 2) == 2) {		// 노랑 보석 몰수 !!
					use.setStringID("Lose Yellow Gems");
					use.setEventField(CMInfo.CM_INT, "Num Of Yellow Gems", String.valueOf(this.myYellowGem));
					m_clientStub.cast(use, myself.getCurrentSession(), myself.getCurrentGroup());
					
					if(currentMonster.isNextMonsterExist()) {
						this.currentMonster.getNextMonster().plusGems(0, myYellowGem, 0);
					}
					this.myYellowGem = 0;
				}
				
				if((maxGemIndex & 1) == 1) {		// 파랑 보석 몰수 !!
					use.setStringID("Lose Blue Gems");
					use.setEventField(CMInfo.CM_INT, "Num Of Blue Gems", String.valueOf(this.myBlueGem));
					m_clientStub.cast(use, myself.getCurrentSession(), myself.getCurrentGroup());
					
					if(currentMonster.isNextMonsterExist()) {
						this.currentMonster.getNextMonster().plusGems(0, 0, myBlueGem);
					}
					this.myBlueGem = 0;
				}
			}
		}
		use = null;
		setMyScore("Red Gems: " + myRedGem + "\tYellow Gems: " + myYellowGem + "\tBlue Gems: " + myBlueGem);
		
		// 내 어플에서 전투가 끝나면 끝났다고 알려주는 코드.
		useForBattleFinish.setStringID("Battle Finish");
		useForBattleFinish.setHandlerSession(myself.getCurrentSession());
		useForBattleFinish.setHandlerGroup(myself.getCurrentGroup());
		useForBattleFinish.setEventField(CMInfo.CM_INT, "userName", myself.getName());
		m_clientStub.cast(useForBattleFinish, myself.getCurrentSession(), myself.getCurrentGroup());
		
		battleFinish();
	}
	
	public void battleFinish() {
		
		if(otherPlayerFinishCount >= numOfPlayer - 1) {	// 마지막에 입력한 정보는 카운트가 전체 플레이어 수 -1 일 때 입력하게 된다.
			otherPlayerFinishCount = 0;
			proceedStage();					// 
			
		} else {		// 플레이어 정보를 하나씩 입력할 때마다 카운트가 오른다.
			otherPlayerFinishCount++;
		}
	}
	
	public int getScore() {		// 보석 양으로 점수 합산.
		int score = 0;
		int minNumOfGem = this.myRedGem;	// 세 보석 종류 중 가장 적은 수의 보석 개수.
		
		if(minNumOfGem > this.myYellowGem) {
			minNumOfGem = this.myYellowGem;
		}
		if(minNumOfGem > this.myBlueGem) {
			minNumOfGem = this.myBlueGem;
		}
		score = (minNumOfGem * 3) + this.myRedGem + this.myYellowGem + this.myBlueGem;
		
		return score;
	}
	
	public void showMyScore() {		// 내 점수를 내 client에 입력하고 event로 뿌려주는 함수
		CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
		CMUser myself = interInfo.getMyself();
		
		CMUserEvent use = new CMUserEvent();
		use.setStringID("Show score");
		use.setEventField(CMInfo.CM_STR, "userName", myself.getName());
		use.setEventField(CMInfo.CM_INT, "Score", String.valueOf(myAggScore));
		m_clientStub.cast(use, myself.getCurrentSession(), myself.getCurrentGroup());
		use = null;
		
		showAllScore(myself.getName(), myAggScore);		// 내 점수를 뿌려주고 show all score에 내 점수를 입력해준다.
	}
	
	public void showAllScore(String userName, int score) {	// 게임이 다 끝나고 마지막 결과 보여주는 함수. 이벤트 핸들러가 부른다.

		if(otherPlayerEventCount >= numOfPlayer - 1) {	// 마지막에 입력한 정보는 카운트가 전체 플레이어 수 -1 일 때 입력하게 된다.
			otherPlayerName[otherPlayerEventCount] = userName;
			otherPlayerScore[otherPlayerEventCount] = score;
			int index = otherPlayerEventCount;
			int tmp, j;	// 정렬에 쓸 변수
			String tmpStr;	// 정렬에 쓰고 임시로 쓰는 스트링.
			String[] otherPlayerNameSortedByHighScore = Arrays.copyOf(otherPlayerName, otherPlayerName.length);
			int[] otherPlayerScoreSortedByHighScore = Arrays.copyOf(otherPlayerScore, otherPlayerScore.length);
			for(int i = 1; i < index; i++) {	// 삽입 정렬
				tmp = otherPlayerScoreSortedByHighScore[i];
				tmpStr = otherPlayerNameSortedByHighScore[i];
				for(j = i-1; j>=0 && otherPlayerScoreSortedByHighScore[j]>tmp; j--) {
					otherPlayerScoreSortedByHighScore[j+1] = otherPlayerScoreSortedByHighScore[j];
					otherPlayerNameSortedByHighScore[j+1] = otherPlayerNameSortedByHighScore[j];
				}
				otherPlayerScoreSortedByHighScore[j+1] = tmp;
				otherPlayerNameSortedByHighScore[j+1] = tmpStr;
			}

			tmpStr = "\n=========== Game Result ===========\n";
			for(int i=0; i<index; i++) {
				tmpStr = tmpStr + (i+1) + "등 " + otherPlayerNameSortedByHighScore[i] + ": " + otherPlayerScoreSortedByHighScore[i] + "점\n";
			}
			printMessage(tmpStr);
			otherPlayerEventCount = 0;
			
		} else {	// 플레이어 정보를 하나씩 입력할 때마다 카운트가 오른다.
			otherPlayerName[otherPlayerEventCount] = userName;
			otherPlayerScore[otherPlayerEventCount] = score;
			otherPlayerEventCount++;
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
		cardPanel.setLayout(new FlowLayout());
//		cardPanel.add(myBattleCardTopic);
		for(int i = 0; i < 8; i++) {
			cardPanel.add(myBattleCards[i]);
		}
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
	
	
	public class MyActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
//			JButton button = (JButton) e.getSource();
			String s = e.getActionCommand();
			if(s.equals("1")) {			// the battle cards
				myCurrentBattleCard = 1;
			} else if(s.equals("2")) {
				myCurrentBattleCard = 2;
			} else if(s.equals("3")) {
				myCurrentBattleCard = 3;
			} else if(s.equals("4")) {
				myCurrentBattleCard = 4;
			} else if(s.equals("5")) {
				myCurrentBattleCard = 5;
			} else if(s.equals("6")) {
				myCurrentBattleCard = 6;
			} else if(s.equals("7")) {
				myCurrentBattleCard = 7;
			} else if(s.equals("선택")) {
				setMyBattleCard(myCurrentBattleCard);
			} else if(s.equals("Game Start")) {		// 게임 시작 혹은 준비 버튼
//				startButton.setText("STARTTT");
				
			} else if(s.equals("Enter Session")) {
				// Session Enter !!
				// the session index variable is "sessionIndex (0~2)"
			
			} else if(s.equals("Session 1")) {
				sessionIndex = 0;
			} else if(s.equals("Session 2")) {
				sessionIndex = 1;
			} else if(s.equals("Session 3")) {
				sessionIndex = 2;
			} else if(s.equals("Session Info")) {
				String resultText = "Name\t   Number Of User"
						+ "\n=====================================================\n";
				CMSessionEvent se = null;
				se = m_clientStub.syncRequestSessionInfo();
				if(se == null) {		
					// 서버에 제대로 연결이 안 되어 있으면 Null Pointer Exception이 발생할 수 는 있으나 멈추지는 않는다.
					// 계속 버튼 눌러보면 실패 메세지를 제대로 출력해준다.
					resultText = resultText + "Failed to get session-info!!\n";
					setUserInfoBySessions(resultText);
					return;
				}
				Iterator<CMSessionInfo> iter = se.getSessionInfoList().iterator();
				while(iter.hasNext()) {
					CMSessionInfo itInfo = iter.next();
					resultText = resultText + itInfo.getSessionName() + "\t" + itInfo.getUserNum() + "\n";
				}
				setUserInfoBySessions(resultText);
			}
			
			myChatMessage.requestFocus();
		}
	}
	
	public class MyKeyListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_ENTER) {						// 엔터 누르면 채팅창의 메세지 전달.
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
	
	
	
	
	public static void main(String[] args) {
//		Frame frame;
//		frame.setVisible(true);
		GUIClient app = new GUIClient();
		
	}

}
