import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class ClientEventHandler implements CMAppEventHandler {

	
	public CMClientStub c_stub;
	
	public ClientEventHandler(CMClientStub stub) {
		
		c_stub = stub;
		
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
		default:
			return;
		
		}
	}
	
	public void processSessionEvent(CMEvent cme) {
		CMSessionEvent se = (CMSessionEvent)cme;
		switch(se.getID()) {
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
			
		case CMSessionEvent.REGISTER_USER_ACK:
			if (se.getReturnCode() == 1) {
				System.out.println(se.getUserName()+"is registered in server.");
			}
			else {
				System.out.println(se.getUserName()+"is fail to be registered in server");
			}
			break;
			
		case CMSessionEvent.DEREGISTER_USER_ACK:
			if (se.getReturnCode() == 1) {
				System.out.println(se.getUserName()+"is deregistered from server.");
			}
			else {
				System.out.println(se.getUserName()+"is fail to be deregistered from server.");
			}
			break;
			
		default:
			return;
		}
	}
	
	private void dummyEvent(CMEvent cme) {
		
		CMDummyEvent de = (CMDummyEvent) cme;
		String reponse = de.getDummyInfo();
		String[] token = reponse.split("&");
		
		/*
		 
		 token[0] = message ID
		 token[1] = User ID
		 token[2] = message value.
		 
		 */
		
		switch(token[0]) {
		
		case "1000":
			break;
		default:
			return;
		}
		
	}
}
