
//package gamePart;
import java.util.Iterator;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMInterestEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEventField;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

// add JM
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.entity.CMGroup;
import kr.ac.konkuk.ccslab.cm.entity.CMGroupInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMSession;
import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Vector;
// add JM

public class GUIClientEventHandler3 implements CMAppEventHandler{

	private GUIClient3 m_client;
	private CMClientStub m_clientStub;
	private long timer;
	
	public GUIClientEventHandler3(CMClientStub clientStub, GUIClient3 client) {
		// TODO Auto-generated constructor stub
		m_client = client;
		m_clientStub = clientStub;
		timer = 0;
	}
	
	private void printMessage(String strText) {
		m_client.printMessage(strText);
		
		return;
	}
	
	private void processInterestEvent(CMEvent cme) {
		CMInterestEvent ie = (CMInterestEvent) cme;
		switch(ie.getID()) {
		case CMInterestEvent.USER_TALK:
			printMessage(ie.getUserName() + ": " + ie.getTalk() + "\n");
			break;
			
		default:
			return;
		}
	}
	
	@Override
	public void processEvent(CMEvent cme) {
		// TODO Auto-generated method stub
		switch(cme.getType()) {
		case CMInfo.CM_DUMMY_EVENT:
			processDummyEvent(cme);
			break;
		case CMInfo.CM_USER_EVENT:
			processUserEvent(cme);
			break;
		case CMInfo.CM_SESSION_EVENT:
			processSessionEvent(cme);
			break;
		case CMInfo.CM_INTEREST_EVENT:
			processInterestEvent(cme);
			break;
		default:
			break;
		}
	}
	

	
	private void processDummyEvent(CMEvent cme) {
		CMDummyEvent de = (CMDummyEvent) cme;
		CMDummyEvent request = new CMDummyEvent();
		String reponse = de.getDummyInfo();
		String[] token = reponse.split("&");
		String senderSession = de.getHandlerSession();
		String senderGroup = de.getHandlerGroup();
		CMSession session = null;
		CMGroup group = null;
		Scanner scan = new Scanner(System.in);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		switch(token[0]) {
		
		case "0001":
			if (token[2].equals("YES")) {
				
				boolean result = m_clientStub.joinSession(senderSession);
				if (result) {
					m_client.setUserInfoBySessions("Session 접속 중.");
				}
				else {
					m_client.setUserInfoBySessions("에러발생.");
				}
		
			}
			else if (token[2].equals("NO")) {
				
				// 별도 쓰레드로 계속 보여줄 것. + 추가하기.
				m_client.setUserInfoBySessions("참여 실패. 가능한 방의 수가 0인 Session에는 참여할 수 없습니다.\n"
								+ "다시 한 번 Session Info 버튼을 클릭하여 session 정보를 새 받아온 후,\n"
								+ "원하는 Session을 선택하고 Enter button을 클릭하세요.\n");

			}
			else {
				
			}
			break;
		
			// group 참여까지 성공하면 이제 진짜로 성공한 것이므로 정보 요청.
		case "0011":
			m_client.setUserInfoBySessions("Session 접속 완료.");
			String tempLog = m_clientStub.getMyself().getName()+ "님이 입장하였습니다.";
			printMessage(tempLog);
			printMessage("\n");
			m_clientStub.chat("/g", tempLog);
			m_client.isSessionT();
			m_client.showUserInfo(m_clientStub);
			break;
			
		case "0021":
			
			m_clientStub.leaveSession();
			m_client.isSessionF();
			m_client.setUserInfoBySessions("Play 하던/예정이던 Session에서 나왔습니다.\n" 
					+ "계속 게임을 하실 예정이라면 다시 Session Info button을 클릭 후 \n"
					+ "Session 정보를 받아와 새로운 Session에 참여해주세요.\n");
			break;
			
		case "0051":
			
			String result = token[2].toString();
			String[] state = result.split(":");
			
			// 2의 배수.
			for (int i = 0; i<state.length; i += 2)
				m_client.printMessage(state[i] + "님 :" + state[i+1] + "\n");
				
			break;
		default:
			return;
		}
		
	}
	
	private void processUserEvent(CMEvent cme) {

		CMUserEvent ue = (CMUserEvent) cme;
		
		if(ue.getStringID().equals("Set Monster Order")) {
			int[] monsterOrder = new int[15];
			int monsterOrderIndex = 0;
			Iterator<CMUserEventField> iter = ue.getAllEventFields().iterator();
			
			while(iter.hasNext()) {
				CMUserEventField uef = iter.next();
				monsterOrder[monsterOrderIndex++] = Integer.parseInt(uef.strFieldValue); 
			}
			m_client.linkMonsterOrder(monsterOrder);
			m_client.proceedGame();
			
		//} else if(ue.getStringID().equals("Game Start")) {
		//	m_client.proceedGame();
			
		} else if(ue.getStringID().equals("Set Battle Card")) {
			String playerName = ue.getSender();
			int battleCard = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "battleCard"));
			m_client.setAllBattleCard(playerName, battleCard);
			
		} else if(ue.getStringID().equals("Win Gems")) {
			String numOfGems = ue.getEventField(CMInfo.CM_INT, "Num Of Gems");
			printMessage(numOfGems);

			
		} else if(ue.getStringID().equals("Lose Red Gems")) {
			int numOfRedGems = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "Num Of Red Gems"));
			printMessage(ue.getSender() + " loses " + numOfRedGems + " Red Gems.\n");
			if(m_client.getCurrentMonster().isNextMonsterExist()) {
				m_client.getCurrentMonster().getNextMonster().plusGems(numOfRedGems, 0, 0);
			}
			
		} else if(ue.getStringID().equals("Lose Yellow Gems")) {

			int numOfYellowGems = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "Num Of Yellow Gems"));
			printMessage(ue.getSender() + " loses " + numOfYellowGems + " Yellow Gems.\n");
			if(m_client.getCurrentMonster().isNextMonsterExist()) {
				m_client.getCurrentMonster().getNextMonster().plusGems(0, numOfYellowGems, 0);
			}
			
		} else if(ue.getStringID().equals("Lose Blue Gems")) {

			int numOfBlueGems = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "Num Of Blue Gems"));
			printMessage(ue.getSender() + " loses " + numOfBlueGems + " Blue Gems.\n");
			if(m_client.getCurrentMonster().isNextMonsterExist()) {
				m_client.getCurrentMonster().getNextMonster().plusGems(0, 0, numOfBlueGems);
			}
			
		} else if(ue.getStringID().equals("Show score")){

			int score = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "Score"));
			m_client.showAllScore(ue.getSender(), score);
			
		} else if(ue.getStringID().equals("Battle Finish")) {

			m_client.battleFinish();
		}
	}
	
	private void processSessionEvent(CMEvent cme) {
		CMSessionEvent se = (CMSessionEvent)cme;
		switch(se.getID()) {
		
		case CMSessionEvent.LOGIN_ACK:
			
			if (se.isLoginScheme() == 1) {
				if (se.isValidUser() == 0) {
					m_client.setUserInfoBySessions(m_clientStub.getMyself().getName()+"은(는) 로그인에 실패하였습니다.\n" 
							+ "다시 한 번 로그인 혹은 회원 가입을 해주세요. 회원 가입 시에는....\n"
							+ "원하시는 UserName을 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해주세요.\n" 
							+ "원하시는 Password를 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해주세요.\n");
				}
				else if (se.isValidUser() == -1) {
					m_client.setUserInfoBySessions(m_clientStub.getMyself().getName()+"은(는) 이미 로그인 된 유저입니다.\n"
							+ "다시 한 번 로그인 혹은 회원 가입을 해주세요. 회원 가입 시에는....\n"
							+ "원하시는 UserName을 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해주세요.\n" 
							+ "원하시는 Password를 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해주세요.\n");
				}
				else {
					m_client.setUserInfoBySessions(m_clientStub.getMyself().getName()+"은(는) 로그인에 성공 하였습니다.\n"
							+ "Session Info 버튼을 클릭하여 session 정보를 받아온 후,\n"
							+ "원하는 Session을 선택하고 Enter button을 클릭하세요.\n");
					m_client.isLoginT();
				}
				break;
			} else {
				m_client.setUserInfoBySessions(m_clientStub.getMyself().getName()+"은(는) 로그인에 성공 하였습니다.\n"
						+ "Session Info 버튼을 클릭하여 session 정보를 받아온 후,\n"
						+ "원하는 Session을 선택하고 Enter button을 클릭하세요.\n");
				m_client.isLoginT();
			}
			break;
		
			
		case CMSessionEvent.REGISTER_USER_ACK:
			if (se.getReturnCode() == 1) {
				m_client.setUserInfoBySessions(m_clientStub.getMyself().getName()+"은(는) 회원 가입에 성공하였습니다.\n" 
						+ "로그인 혹은 회원 가입을 해주세요. 회원 가입 시에는....\n"
						+ "원하시는 UserName을 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해주세요.\n" 
						+ "원하시는 Password를 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해주세요.\n");
			}
			else {
				m_client.setUserInfoBySessions(m_clientStub.getMyself().getName()+"은(는) 회원 가입에 실하였습니다.\n" 
						+ "다시 한 번 로그인 혹은 회원 가입을 해주세요. 회원 가입 시에는....\n"
						+ "원하시는 UserName을 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해주세요.\n" 
						+ "원하시는 Password를 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해주세요.\n");
			}
			break;
			
		case CMSessionEvent.DEREGISTER_USER_ACK:
			if (se.getReturnCode() == 1) {
				m_client.setUserInfoBySessions(m_clientStub.getMyself().getName()+"은(는) 회원 탈퇴에 성공하였습니다.\n" 
						+ "로그인 혹은 회원 가입을 해주세요. 회원 가입 시에는....\n"
						+ "원하시는 UserName을 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해주세요.\n" 
						+ "원하시는 Password를 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해주세요.\n");
			}
			else {
				m_client.setUserInfoBySessions(m_clientStub.getMyself().getName()+"은(는) 회원 탈퇴에 성공하였습니다.\n" 
						+ "로그인 혹은 회원 가입을 해주세요. 회원 가입 시에는....\n"
						+ "원하시는 UserName을 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해주세요.\n" 
						+ "원하시는 Password를 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해주세요.\n");
			}
			break;
			
		case CMSessionEvent.JOIN_SESSION_ACK:
			
			CMDummyEvent de = new CMDummyEvent();
			Iterator<CMGroupInfo> iter2 = se.getGroupInfoList().iterator();
			String groupName = null;
			int count = 0;
			
			while(iter2.hasNext()) {
				
				CMGroupInfo tInfo = iter2.next();
				groupName = tInfo.getGroupName();
				
				if (!groupName.equals("g1")) {
					if(tInfo.getGroupState()) {
						m_clientStub.changeGroup(groupName);
						
						de.setHandlerSession(m_clientStub.getMyself().getCurrentSession());
						de.setHandlerGroup(m_clientStub.getMyself().getCurrentGroup());
						de.setDummyInfo("0010&"+m_clientStub.getMyself().getName()+"&chagneGroupState");
						
						m_clientStub.send(de, "SERVER");					
						break;
					}
				}
			}	
			iter2.remove();
			de = null;
			break;
			
		default:
			return;
		}
	}

}

/*
//package gamePart;
import java.util.Iterator;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMInterestEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEventField;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

// add JM
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.entity.CMGroup;
import kr.ac.konkuk.ccslab.cm.entity.CMGroupInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMSession;
import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Vector;
// add JM

public class GUIClientEventHandler implements CMAppEventHandler{

	private GUIClient m_client;
	private CMClientStub m_clientStub;
	private long timer;
	
	public GUIClientEventHandler(CMClientStub clientStub, GUIClient client) {
		// TODO Auto-generated constructor stub
		m_client = client;
		m_clientStub = clientStub;
		timer = 0;
	}
	
	private void printMessage(String strText) {
		m_client.printMessage(strText);
		
		return;
	}
	
	private void processInterestEvent(CMEvent cme) {
		CMInterestEvent ie = (CMInterestEvent) cme;
		switch(ie.getID()) {
		case CMInterestEvent.USER_TALK:
			printMessage(ie.getUserName() + ": " + ie.getTalk() + "\n");
			break;
			
		default:
			return;
		}
	}
	
	@Override
	public void processEvent(CMEvent cme) {
		// TODO Auto-generated method stub
		switch(cme.getType()) {
		case CMInfo.CM_INTEREST_EVENT:
			processInterestEvent(cme);
			break;
		case CMInfo.CM_DUMMY_EVENT:
			processDummyEvent(cme);
			break;
		case CMInfo.CM_USER_EVENT:
			processUserEvent(cme);
			break;
		case CMInfo.CM_SESSION_EVENT:
			processSessionEvent(cme);
			break;
		default:
			break;
		}
	}
	
	private void processDummyEvent(CMEvent cme) {
		CMDummyEvent de = (CMDummyEvent) cme;
		CMDummyEvent request = new CMDummyEvent();
		String reponse = de.getDummyInfo();
		String[] token = reponse.split("&");
		String senderSession = de.getHandlerSession();
		String senderGroup = de.getHandlerGroup();
		CMSession session = null;
		CMGroup group = null;
		Scanner scan = new Scanner(System.in);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		switch(token[0]) {
		
		case "0001":
			if (token[2].equals("YES")) {
				
				boolean result = m_clientStub.joinSession(senderSession);
				if (result) {
					m_client.setUserInfoBySessions("Session 李몄뿬 �꽦怨�.");
				}
				else {
					m_client.setUserInfoBySessions("�뿉�윭諛쒖깮.");
				}
		
			}
			else if (token[2].equals("NO")) {
				
				// 蹂꾨룄 �벐�젅�뱶濡� 怨꾩냽 蹂댁뿬以� 寃�. + 異붽��븯湲�.
				m_client.showSessionInfo("李몄뿬 �떎�뙣. Session�쓽 �씠由꾩쓣 �떎�떆 �븳 踰� �솗�븯�뿬 二쇱꽭�슂. �삉�븳 媛��뒫�븳 諛⑹쓽 �닔媛� 0�씤 Session�뿉�뒗 李몄뿬�븷 �닔 �뾾�뒿�땲�떎.");
				// �떎�떆 �엯�젰 諛쏅뒗 諛⑸쾿 怨좊�쇳븷 寃�. 
				try {
					senderSession = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				request.setHandlerSession(senderSession);
				request.setHandlerGroup("null");		
				request.setDummyInfo("0000&"+m_clientStub.getMyself().getName()+"&check Possible room");
				
				m_clientStub.send(request, "SERVER");
				
			}
			else {
				
			}
			break;
			
		case "0021":
			
			m_clientStub.leaveSession();
			
			break;
		default:
			return;
		}
		
	}
	
	private void processUserEvent(CMEvent cme) {

		CMUserEvent ue = (CMUserEvent) cme;
		
		if(ue.getStringID().equals("Set Monster Order")) {
			int[] monsterOrder = new int[15];
			int monsterOrderIndex = 0;
			Iterator<CMUserEventField> iter = ue.getAllEventFields().iterator();
			
			while(iter.hasNext()) {
				CMUserEventField uef = iter.next();
				monsterOrder[monsterOrderIndex++] = Integer.parseInt(uef.strFieldValue); 
			}
			m_client.linkMonsterOrder(monsterOrder);
			m_client.proceedGame();
			
		//} else if(ue.getStringID().equals("Game Start")) {
		//	m_client.proceedGame();
			
		} else if(ue.getStringID().equals("Set Battle Card")) {
			String playerName = ue.getSender();
			int battleCard = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "battleCard"));
			m_client.setAllBattleCard(playerName, battleCard);
			
		} else if(ue.getStringID().equals("Win Gems")) {
			String numOfGems = ue.getEventField(CMInfo.CM_INT, "Num Of Gems");
			printMessage(numOfGems);
			
		} else if(ue.getStringID().equals("Lose Red Gems")) {
			int numOfRedGems = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "Num Of Red Gems"));
			printMessage(ue.getSender() + " loses " + numOfRedGems + " Red Gems.\n");
			if(m_client.getCurrentMonster().isNextMonsterExist()) {
				m_client.getCurrentMonster().getNextMonster().plusGems(numOfRedGems, 0, 0);
			}
			
		} else if(ue.getStringID().equals("Lose Yellow Gems")) {
			int numOfYellowGems = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "Num Of Yellow Gems"));
			printMessage(ue.getSender() + " loses " + numOfYellowGems + " Yellow Gems.\n");
			if(m_client.getCurrentMonster().isNextMonsterExist()) {
				m_client.getCurrentMonster().getNextMonster().plusGems(0, numOfYellowGems, 0);
			}
			
		} else if(ue.getStringID().equals("Lose Blue Gems")) {
			int numOfBlueGems = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "Num Of Blue Gems"));
			printMessage(ue.getSender() + " loses " + numOfBlueGems + " Blue Gems.\n");
			if(m_client.getCurrentMonster().isNextMonsterExist()) {
				m_client.getCurrentMonster().getNextMonster().plusGems(0, 0, numOfBlueGems);
			}
			
		} else if(ue.getStringID().equals("Show score")){
			int score = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "Score"));
			m_client.showAllScore(ue.getSender(), score);
			
		} else if(ue.getStringID().equals("Battle Finish")) {
			m_client.battleFinish();
		}
	}
	
	private void processSessionEvent(CMEvent cme) {
		CMSessionEvent se = (CMSessionEvent)cme;
		switch(se.getID()) {

			
		case CMSessionEvent.REGISTER_USER_ACK:
			if (se.getReturnCode() == 1) {
				m_client.setUserInfoBySessions(se.getUserName()+"is registered in server.");
			}
			else {
				m_client.setUserInfoBySessions(se.getUserName()+"is fail to be registered in server");
			}
			break;
			
		case CMSessionEvent.DEREGISTER_USER_ACK:
			if (se.getReturnCode() == 1) {
				m_client.setUserInfoBySessions(se.getUserName()+"is deregistered from server.");
			}
			else {
				m_client.setUserInfoBySessions(se.getUserName()+"is fail to be deregistered from server.");
			}
			break;

			
		case CMSessionEvent.JOIN_SESSION_ACK:
			
			CMDummyEvent de = new CMDummyEvent();
			Iterator<CMGroupInfo> iter2 = se.getGroupInfoList().iterator();
			String groupName = null;
			int count = 0;
			
			while(iter2.hasNext()) {
				
				CMGroupInfo tInfo = iter2.next();
				groupName = tInfo.getGroupName();
				
				if (!groupName.equals("g1")) {
					if(tInfo.getGroupState()) {
						m_clientStub.changeGroup(groupName);
						
						de.setHandlerSession(m_clientStub.getMyself().getCurrentSession());
						de.setHandlerGroup(m_clientStub.getMyself().getCurrentGroup());
						de.setDummyInfo("0010&"+m_clientStub.getMyself().getName()+"&chagneGroupState");
						
						m_clientStub.send(de, "SERVER");					
						break;
					}
				}
			}	
			iter2.remove();
			de = null;
			break;
			
		default:
			return;
		}
	}

}
*/
