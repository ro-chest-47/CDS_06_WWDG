package gamePart;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
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
