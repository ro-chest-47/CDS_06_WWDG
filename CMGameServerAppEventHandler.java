package gamePart;
import java.util.Iterator;

import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMInterestEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class CMGameServerAppEventHandler implements CMAppEventHandler{
	private CMServerStub m_serverStub;
	private CMGameServerApp m_server;
	
	public CMGameServerAppEventHandler(CMServerStub serverStub, CMGameServerApp server) {
		m_serverStub = serverStub;
		m_server = server;
	}
	
	private void processInterestEvent(CMEvent cme) {
		CMInterestEvent ie = (CMInterestEvent) cme;
		switch(ie.getID()){
		case CMInterestEvent.USER_ENTER:
			// Test Code
			if(m_server.getNumOfPlayer() < 2) {		// n명이서 플레이하고 싶으면  (플레이어수 < n-1) 일 때까진 아래처럼 실시.
				m_server.setNumOfPlayer(m_server.getNumOfPlayer() + 1);
			} else {	// n-1 보다 크거나 같아지면 게임 실행
				String dstSession = ie.getHandlerSession();
				String dstGroup = ie.getHandlerGroup();
				m_server.sendMonsterOrder(dstSession, dstGroup);
				m_server.sendGameStart(dstSession, dstGroup);
			}
			break;
			
		default:
			return;
		}
	}
	
	private void processSessionEvent(CMEvent cme) {
		CMSessionEvent se = (CMSessionEvent) cme;
		switch(se.getID()) {
		case CMSessionEvent.RESPONSE_SESSION_INFO:
			processRESPONSE_SESSION_INFO(se);
			break;
			
		case CMSessionEvent.JOIN_SESSION_ACK:
			
			break;
		}
	}
	
	private void processRESPONSE_SESSION_INFO(CMSessionEvent se) {
		Iterator<CMSessionInfo> iter = se.getSessionInfoList().iterator();
	}
	
	private void processDummyEvent(CMEvent cme) {
		
		return;
	}
	
	private void processUserEvent(CMEvent cme) {
		
		return;
	}
	
	@Override
	public void processEvent(CMEvent cme) {
		switch(cme.getType()) {
		case CMInfo.CM_SESSION_EVENT:
			processSessionEvent(cme);
			break;
		case CMInfo.CM_INTEREST_EVENT:
			processInterestEvent(cme);
			break;
		case CMInfo.CM_DUMMY_EVENT:
			processDummyEvent(cme);
			break;
		case CMInfo.CM_USER_EVENT:
			processUserEvent(cme);
			break;
		default:
			return;
		}
	}

}
