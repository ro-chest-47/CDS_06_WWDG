package gamePart;
import java.util.Timer;
import java.util.Arrays;
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
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
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
	private JButton enterSession1Button = new JButton("Session 1");
	private JButton enterSession2Button = new JButton("Session 2");
	private JButton enterSession3Button = new JButton("Session 3");
	private JTextArea userInfoBySessions = new JTextArea(4, 50);
	
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
		myBattleCards[0] = new JButton("����");
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
		southPanelCenter.add(enterSession1Button);
		southPanelCenter.add(enterSession2Button);
		southPanelCenter.add(enterSession3Button);
		JPanel southPanelBottom = new JPanel();
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
		enterSession1Button.addActionListener(cmActionListener);
		enterSession2Button.addActionListener(cmActionListener);
		enterSession3Button.addActionListener(cmActionListener);
		
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
		
		
		// �� ���Ӹ��� �ʱ�ȭ ����� �ϴ� ��
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
//		otherBattleCard = new int[] {0, 0, 0, 0, 0};
		allPlayerCard = new int[] {0, 0, 0, 0, 0};
		allPlayerCardCount = 0;
		otherBattleCardIndex = 0;
		myCurrentBattleCard = 0;
		isMyBattleCardActive = true;
		otherPlayerFinishCount = 0;
		

		
		timer = new Timer();
		timerCountdown = new TimerCountdown(this);
		timer.scheduleAtFixedRate(timerCountdown, 0, 1000);		// 1�� Ÿ�̸Ӹ� �������ش�.
		
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
		myRedGem = 0;
		myYellowGem = 0;
		myBlueGem = 0;
		numOfPlayer = m_clientStub.getGroupMembers().getMemberNum() + 1;		
		// getMemberNum()�� ���� ������ �׷� �ο� ���� ��ȯ�Ѵ�.
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
		
		// ù ��° ���ʹ� ���� ���� �� �ٷ� ���̰� �Ѵ�.
		String currentMonsterInfoText = currentMonster.getName()
				+ "\nPower: " + currentMonster.getPower() + "\tRed Gem: " + currentMonster.getRedGem() 
				+ "\tYellow Gem: " + currentMonster.getYellowGem() + "\tBlue Gem: " + currentMonster.getBlueGem();
		setMonsterInfo(currentMonsterInfoText);
			
		setDungeonAndStageInfo("Stage: " + (stageNum + 1));
		setMyScore("Red Gems: " + myRedGem + "\tYellow Gems: " + myYellowGem + "\tBlue Gems: " + myBlueGem);
			
		timerCountdown.setDrawCount(30);	// ī��Ʈ�ٿ� 30�� ����.  
			
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
			if(stageNum % 5 == 0) {		// stage�� 5�� ������ �������� ���� ���� �Լ��̹Ƿ� ��Ʋ ī�� ����.
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
			// 10�� �Ŀ� ���ο� ���� ����, �� ��� ī�� â�� �����ش�. �׸��� 10�� �Ŀ� �ٽ� ���� ��ư�� Ȱ��ȭ �ȴ�.
			
			timerCountdown.setDrawCount(40);		// ���⼭ 40�ʷ� �� ������, ������ ���� ���� ���� �� 10�� ������ �߱� �����̴�.
			
		} else {		// ���� ���Ͱ� ������ Stage ����
			myAggScore = getScore();
			showMyScore();
			
			
			// ���� ���� �ڵ� !! 
		}
	}
	
	public int[] shuffleMonsterOrder(int[] arr, int count) {	// parameter�� monsterOrder�� �ִ´�.
		// Server���� �����Ѵ�. ���⼭�� ������ �����ϱ� ����.
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
	
	public void setMyBattleCard(int index) {		// index�� ���� �� ��Ʋ ī��. 0���� ��ȿ, 1~7 ���� �ִ�.
		if(isThisPlayerChooseCard) {	// �̹� ī�带 �����ؼ� �´ٸ� ������� �ʴ´�. �� ������ ���� �Ŀ� false�� �ٲ��.
			return;
		}
		if(index != 0) { // index == 0 �� ī��� ��ȿ ī��.
			if(isMyBattleCardPossible[index] == false) {	// ��ȿ ī�带 �����ϰ�, �̹� �� ī�带 �����ϸ� ������ �� ����.
				return;
			}
		}
		timerCountdown.setDrawCount(-1);	// Ÿ�̸� �½�ũ�� ī��Ʈ�� -1�̸� �ƹ��͵� ���� �ʴ´�. 
		isThisPlayerChooseCard = true;
		myCurrentBattleCard = index;
		isMyBattleCardPossible[index] = false;
		allPlayerCard[allPlayerCardCount++] = index;		// ��� �÷��̾��� ī�带 ����ϴ� �迭
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
		
		if(numOfPlayer == allPlayerCardCount) {	// ī�尡 ���� ���� �÷��̾� ���� ������ ��Ʋ ����
			battleWithMonster();
		}
	}
	
	public void setOtherBattleCard(String playerName, int battleCard) {
		// ������ �� �������� ������ �ʿ� !! Ȥ�� �ٸ� ������ ID���� ���� �޾ƿ;� �Ѵ�.
		if(otherBattleCardIndex >= 5) {
			return;
		}
		otherPlayerAndCard[otherBattleCardIndex++] = playerName + ": " + String.valueOf(battleCard);
//		otherBattleCard[otherBattleCardIndex++] = battleCard;
//		otherBattleCardIndex++;
		
		allPlayerCard[allPlayerCardCount++] = battleCard;
//		allPlayerCardCount++;
		
		if(numOfPlayer == allPlayerCardCount) {	// ī�尡 ���� ���� �÷��̾� ���� ������ ��Ʋ ����
			battleWithMonster();
		}
	}
	
	public void battleWithMonster() {
		int[] activeBattleCard = Arrays.copyOf(allPlayerCard, allPlayerCard.length);
		int battleCardAgg = 0;			// ��ȿ�� ��Ʋī�� ����
		int minBattleCard = 8;
		
		String otherPlayerAndCardText = "";
		for(int i=0; i < otherBattleCardIndex - 1; i++) {
			otherPlayerAndCardText = otherPlayerAndCardText + otherPlayerAndCard[i] + " ";
		}
		// ��� ����� ī�� ����
		setOtherPlayersCard(otherPlayerAndCardText);
		
		if(myCurrentBattleCard == 0) {	// �� ī�尡 0�̸� ��ȿȭ.
			isMyBattleCardActive = false;
		}
		
		for(int i=0; i<5; i++) {		// �� ī��� �ٸ� ������� �� ī�� ��. �׸��� �ٸ� ����� �� ī��� ������ ��.
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
				if( myCurrentBattleCard < minBattleCard ) {	// �� ī�尡 ���� �۴ٸ� ���� �޴´�. ���� �̱�� ���� �¸� Event ����.
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
				
				if((maxGemIndex & 4) == 4) {		// ���� ���� ���� !!
					use.setStringID("Lose Red Gems");
					use.setEventField(CMInfo.CM_INT, "Num Of Red Gems", String.valueOf(this.myRedGem));
					m_clientStub.cast(use, myself.getCurrentSession(), myself.getCurrentGroup());
					
					if(currentMonster.isNextMonsterExist()) {
						this.currentMonster.getNextMonster().plusGems(myRedGem, 0, 0);
					}
					this.myRedGem = 0;
				}
				
				if((maxGemIndex & 2) == 2) {		// ��� ���� ���� !!
					use.setStringID("Lose Yellow Gems");
					use.setEventField(CMInfo.CM_INT, "Num Of Yellow Gems", String.valueOf(this.myYellowGem));
					m_clientStub.cast(use, myself.getCurrentSession(), myself.getCurrentGroup());
					
					if(currentMonster.isNextMonsterExist()) {
						this.currentMonster.getNextMonster().plusGems(0, myYellowGem, 0);
					}
					this.myYellowGem = 0;
				}
				
				if((maxGemIndex & 1) == 1) {		// �Ķ� ���� ���� !!
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
		
		// �� ���ÿ��� ������ ������ �����ٰ� �˷��ִ� �ڵ�.
		useForBattleFinish.setStringID("Battle Finish");
		useForBattleFinish.setHandlerSession(myself.getCurrentSession());
		useForBattleFinish.setHandlerGroup(myself.getCurrentGroup());
		useForBattleFinish.setEventField(CMInfo.CM_INT, "userName", myself.getName());
		m_clientStub.cast(useForBattleFinish, myself.getCurrentSession(), myself.getCurrentGroup());
		
		battleFinish();
	}
	
	public void battleFinish() {
		
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

		if(otherPlayerEventCount >= numOfPlayer - 1) {	// �������� �Է��� ������ ī��Ʈ�� ��ü �÷��̾� �� -1 �� �� �Է��ϰ� �ȴ�.
			otherPlayerName[otherPlayerEventCount] = userName;
			otherPlayerScore[otherPlayerEventCount] = score;
			int index = otherPlayerEventCount;
			int tmp, j;	// ���Ŀ� �� ����
			String tmpStr;	// ���Ŀ� ���� �ӽ÷� ���� ��Ʈ��.
			String[] otherPlayerNameSortedByHighScore = Arrays.copyOf(otherPlayerName, otherPlayerName.length);
			int[] otherPlayerScoreSortedByHighScore = Arrays.copyOf(otherPlayerScore, otherPlayerScore.length);
			for(int i = 1; i < index; i++) {	// ���� ����
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
				tmpStr = tmpStr + (i+1) + ". " + otherPlayerNameSortedByHighScore[i] + ": " + otherPlayerScoreSortedByHighScore[i] + "��\n";
			}
			printMessage(tmpStr);
			otherPlayerEventCount = 0;
			
		} else {	// �÷��̾� ������ �ϳ��� �Է��� ������ ī��Ʈ�� ������.
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
			JButton button = (JButton) e.getSource();
			if(button.getText().equals("1")) {			// the battle cards
				myCurrentBattleCard = 1;
			} else if(button.getText().equals("2")) {
				myCurrentBattleCard = 2;
			} else if(button.getText().equals("3")) {
				myCurrentBattleCard = 3;
			} else if(button.getText().equals("4")) {
				myCurrentBattleCard = 4;
			} else if(button.getText().equals("5")) {
				myCurrentBattleCard = 5;
			} else if(button.getText().equals("6")) {
				myCurrentBattleCard = 6;
			} else if(button.getText().equals("7")) {
				myCurrentBattleCard = 7;
			} else if(button.getText().equals("����")) {
				setMyBattleCard(myCurrentBattleCard);
			} else if(button.getText().equals("Game Start")) {		// ���� ���� Ȥ�� �غ� ��ư
//				startButton.setText("STARTTT");
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
	
	
	
	
	public static void main(String[] args) {
//		Frame frame;
//		frame.setVisible(true);
		GUIClient app = new GUIClient();
		
	}

}
