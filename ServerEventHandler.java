import kr.ac.konkuk.ccslab.cm.entity.CMGroup;
import kr.ac.konkuk.ccslab.cm.entity.CMMember;
import kr.ac.konkuk.ccslab.cm.entity.CMSession;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMInterestEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEventField;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMConfigurationInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMDBManager;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Vector;
import java.time.ZoneId;

public class ServerEventHandler implements CMAppEventHandler {

	private CMServerStub s_stub;
	public GameGroup_Manager gm;
	public ServerEventHandler(CMServerStub stub, GameGroup_Manager gm) {
		
		s_stub = stub;
		this.gm = gm;
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
		case CMInfo.CM_USER_EVENT:
			processUserEvent(cme);
			break;
		case CMInfo.CM_INTEREST_EVENT:
			processInterestEvent(cme);
		default:
			return;
		
		}
		
		
	}
	
	
	private void processInterestEvent(CMEvent cme)
	{
		CMInterestEvent ie = (CMInterestEvent) cme;
		switch(ie.getID())
		{
		case CMInterestEvent.USER_ENTER:
			System.out.println("["+ie.getUserName()+"] enters group("+ie.getCurrentGroup()+") in session("
					+ie.getHandlerSession()+").");
			System.out.println(gm.GetGameGroup( ie.getDistributionSession().toString(),ie.getCurrentGroup().toString()));
			
			break;
		case CMInterestEvent.USER_LEAVE:
			System.out.println("["+ie.getUserName()+"] leaves group("+ie.getHandlerGroup()+") in session("
					+ie.getHandlerSession()+").");
			
			
			break;
		default:
			return;
		}
	}
	private void processUserEvent(CMEvent cme)
	{
		/*int nForwardType = -1;
		int id = -1;*/
		String strUser = null;
		
		CMUserEvent ue = (CMUserEvent) cme;
	
		
		
		if(ue.getStringID().equals("GameStart")){
		
			CMUserEvent to_user = new CMUserEvent();
			to_user.setStringID(" ");	
			
		}
		
		if(ue.getStringID().equals("session_list_req"))
		{
			System.out.println("session_list_req");
			CMUserEvent cue = new CMUserEvent();
			
		Vector<CMSession> cms	 = s_stub.getCMInfo().getInteractionInfo().getSessionList();
			
		for(int i = 0 ; i < cms.size(); i++) {
			
			cue.setEventField(CMInfo.CM_STR, "sessionname"+i, cms.get(i).getSessionName());
				
		}
		cue.setID(10);
		cue.setEventField(CMInfo.CM_STR,"session_size",Integer.toString(cms.size()));
		strUser = ue.getSender();
		
		s_stub.send(cue, strUser);
			

	}
		else if(ue.getStringID().equals("group_list_req")){
			
			System.out.println("group_list_req");
			CMUserEvent cue = new CMUserEvent();
			
		Vector<CMSession> cms	 = s_stub.getCMInfo().getInteractionInfo().getSessionList();
			
		for(int i = 0 ; i < cms.size(); i++) {
			
			
			//for( int j = 0 ; j< )
			
			Vector<CMGroup> cmg = cms.get(i).getGroupList();
			cue.setEventField(CMInfo.CM_STR, cms.get(i).getSessionName() + "groupsize" + i, Integer.toString(cmg.size()));
			
			for( int j = 0 ; j < cmg.size() ; j++) {
				
				 CMMember cmm = cmg.get(j).getGroupUsers();
				 Vector<CMUser> cmu =  cmm.getAllMembers();
			
					
					cue.setEventField(CMInfo.CM_STR, cms.get(i).getSessionName() + "groupname"+j, cmg.get(j).getGroupName());
					cue.setEventField(CMInfo.CM_STR, cms.get(i).getSessionName() + "group_mem_num"+j ,  Integer.toString(cmu.size()));
				
				
				
			}	
				//cue.setEventField(CMInfo.CM_STR, "session"+i, cms.get(i).getSessionName());
			
		}
		cue.setID(20);
		strUser = ue.getSender();
		
		System.out.println(gm.vec.size());
		s_stub.send(cue, strUser);
			
			
		}
		else if( ue.getStringID().contentEquals("GameStart")) {
			
			//ue.getHandlerSession();
			//ue.getHandlerGroup().;
			
		
			
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
			
		case CMSessionEvent.CHANGE_SESSION:
		
	
			
			
			break;
		case CMSessionEvent.JOIN_SESSION:
			
			
			break;
		case CMSessionEvent.LEAVE_SESSION:
			
			
			break;
		case CMSessionEvent.REQUEST_SESSION_INFO:
			
			
			break;
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
