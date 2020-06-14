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
		/* use SyncLogin();
		case CMSessionEvent.LOGIN_ACK:
			
			if (se.isLoginScheme() == 1) {
				if (se.isValidUser() == 0) {
					System.err.println("client fails to get authentiaction by server...");
				}
				else if (se.isValidUser() == -1) {
					System.err.println("client is already in the login-user list...");
				}
				else {
					System.out.println("client successfully logs in to the server...");
				}
				break;
			} else {
				System.out.println("client successfully logs in to the server...");
			}
			break;
		*/
			
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
			
		/* �씪�떒 蹂대쪟...	
		case CMSessionEvent.RESPONSE_SESSION_INFO:
			Iterator<CMSessionInfo> iter = se.getSessionInfoList().iterator();
			System.out.format("%-60s%n", "------------------------------------------ ------------------------------");
			System.out.format("%-20s%-20s%-10s%-10s%-10s%n", "name", "address", "port", "user num", "possible");
			System.out.format("%-60s%n", "------------------------------------------ ------------------------------");
			while(iter.hasNext()) {
				CMSessionInfo tInfo = iter.next();
				System.out.format("%-20s%-20s%-10d%-10d%-10d%n", tInfo.getSessionName(), 
						tInfo.getAddress(), tInfo.getPort(), tInfo.getUserNum(), tInfo.getPossibleGroups());
			}
			break;
		*/
			
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
