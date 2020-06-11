import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMConfigurationInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMDBManager;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;

public class ServerEventHandler implements CMAppEventHandler {

	private CMServerStub s_stub;
	
	public ServerEventHandler(CMServerStub stub) {
		
		s_stub = stub;
	}
	
	
	@Override
	public void processEvent(CMEvent cme) {
		// TODO Auto-generated method stub
		
		switch(cme.getType()) 
		{
		case CMInfo.CM_SESSION_EVENT:
			processSessionEvent(cme);
			break;
		case CMInfo.CM_DUMMY_EVENT:
			dummyEvent(cme);
			break;
		default:
			return;
		
		}
	}
	
	private void processSessionEvent(CMEvent cme) {
		
		
		CMConfigurationInfo conInfo = s_stub.getCMInfo().getConfigurationInfo();
		CMSessionEvent se = (CMSessionEvent) cme;
		
		switch(cme.getID())
		{
		case CMSessionEvent.LOGIN:
			System.out.println("["+se.getUserName()+"] requests login.");
			
			if(conInfo.isLoginScheme()) {
				boolean ret = CMDBManager.authenticateUser(se.getUserName(), 
						se.getPassword(), s_stub.getCMInfo());
				if (!ret) {
					System.out.println("["+se.getUserName()+"] authentication fails!");
					s_stub.replyEvent(se, 0);
				} 
				else {
					System.out.println("["+se.getUserName()+"] authentication succeeded!");
					
					s_stub.replyEvent(se, 1);
				}		
			}
			break;
			
		default:
			return;
		}
		
	}
	
	private void dummyEvent(CMEvent cme) {
		
		CMDummyEvent de = (CMDummyEvent) cme;
		String request = de.getDummyInfo();
		String[] token = request.split("&");
		
		// token size == 4
		// token[0] = message type
		// token[1] = user ID
		// token[2] = user value
		// user session / group 의 경우 event에서 추출 가능.
		
		switch(token[0])
		{
		case "0000":
			break;
		default:
			return;
		}
	}

}
