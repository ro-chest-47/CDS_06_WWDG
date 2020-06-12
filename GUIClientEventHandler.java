package gamePart;
import java.util.Iterator;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEventField;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

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
	
	private void processDummyEvent(CMEvent cme) {
		// Do Somethinggggg
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
			
		} else if(ue.getStringID().equals("Game Start")) {
			m_client.proceedGame();
			
		} else if(ue.getStringID().equals("Set Battle Card")) {
			String playerName = ue.getSender();
			int battleCard = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "battleCard"));
			m_client.setOtherBattleCard(playerName, battleCard);
			
		} else if(ue.getStringID().equals("Lose Red Gems")) {
			int numOfRedGems = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "Num Of Red Gems"));
			if(m_client.getCurrentMonster().isNextMonsterExist()) {
				m_client.getCurrentMonster().getNextMonster().plusGems(numOfRedGems, 0, 0);
			}
			
		} else if(ue.getStringID().equals("Lose Yellow Gems")) {
			int numOfYellowGems = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "Num Of Yellow Gems"));
			if(m_client.getCurrentMonster().isNextMonsterExist()) {
				m_client.getCurrentMonster().getNextMonster().plusGems(0, numOfYellowGems, 0);
			}
			
		} else if(ue.getStringID().equals("Lose Blue Gems")) {
			int numOfBlueGems = Integer.parseInt(ue.getEventField(CMInfo.CM_INT, "Num Of Blue Gems"));
			if(m_client.getCurrentMonster().isNextMonsterExist()) {
				m_client.getCurrentMonster().getNextMonster().plusGems(0, 0, numOfBlueGems);
			}
			
		} else {
			
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
		}
	}

}
