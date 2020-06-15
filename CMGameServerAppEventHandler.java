/*
package gamePart;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class CMGameServerAppEventHandler implements CMAppEventHandler{
	private CMServerStub m_serverStub;
	
	public CMGameServerAppEventHandler(CMServerStub serverStub) {
		m_serverStub = serverStub;
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

*/