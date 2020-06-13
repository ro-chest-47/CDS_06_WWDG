import java.util.ArrayList;
import java.util.Collections;

import kr.ac.konkuk.ccslab.cm.entity.CMGroup;

import kr.ac.konkuk.ccslab.cm.entity.CMSession;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMConfigurationInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMDBManager;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class ServerEventHandler implements CMAppEventHandler {

	private CMServerStub s_stub;
	private ArrayList<ArrayList<Integer>> shuffle = new ArrayList<>();
	private ArrayList<ArrayList<ServerUser>> ready = new ArrayList<>();
	
	// 15*15 int array
	// 15*5 boolean array 추가
	
	
	public ServerEventHandler(CMServerStub stub) {
		
		s_stub = stub;
		
		for (int i = 0; i<15; i++) {
			ArrayList<Integer> tshuffle = new ArrayList<>();
			shuffle.add(tshuffle);
			
			ArrayList<ServerUser> tready = new ArrayList<>();
			ready.add(tready);
		}
		
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
		CMDummyEvent reply = new CMDummyEvent();
		CMUserEvent ureply = new CMUserEvent();
		
		String senderGroup = de.getHandlerGroup();
		String senderSession = de.getHandlerSession();
		String request = de.getDummyInfo();
		String[] token = request.split("&");
		
		CMSession session = null;
		CMGroup group = null;
		int groupUser = 0;
		int sessionPos = -1;
		int groupPos = -1;
		
		ArrayList<ServerUser> tUser;
		ArrayList<Integer> tList;
		int target = 0;
		boolean start = false;
		
		// token size == 4
		// token[0] = message type
		// token[1] = user ID
		// token[2] = user value
		// user session / group 의 경우 event에서 추출 가능.
		
		switch(token[0])
		{
		// Session 참여 가능 먼저 확인.
		case "0000":
			reply.setHandlerSession(senderSession);
			reply.setHandlerGroup("null");
			
			session = s_stub.getCMInfo().getInteractionInfo().findSession(senderSession);
			if (session == null || session.getPossibleGroups() == 0) 
				reply.setDummyInfo("0001&SERVER&NO");
			else 
				reply.setDummyInfo("0001&SERVER&YES");

			s_stub.send(reply, token[1]);
			break;
			
		// group 참여 할 때 logic.
		case "0010":
			session = s_stub.getCMInfo().getInteractionInfo().findSession(senderSession);
			group = session.findGroup(senderGroup);
			groupUser = group.getPossibleUser();
			group.setPossibleUser(--groupUser);

			if(groupUser == 0) {
				group.setGroupStateFalse();
			}
			
			for (int i = 1; i < 4; i++) {
				if (("session"+i).equals(senderSession)) {
					sessionPos = i-1;
				}
			}
			
			for (int i = 1; i<6; i++) {
				if (("g"+i).contentEquals(senderGroup)) {
					groupPos = i-1;
				}
			}
			
			ready.get(sessionPos*5+groupPos).add(new ServerUser(token[1]));
			break;
			
		// leave Session 할 때 logic.
		case "0020":
			session = s_stub.getCMInfo().getInteractionInfo().findSession(senderSession);
			group = session.findGroup(senderGroup);
			groupUser = group.getPossibleUser();
			group.setPossibleUser(++groupUser);
			group.setGroupStateTrue();
			
			for (int i = 1; i<4; i++) {
				if (("session"+i).equals(senderSession)) {
					sessionPos = i-1;
				}
			}
			
			for (int i = 1; i<6; i++) {
				if (("g"+i).equals(senderGroup)) {
					groupPos = i-1;
				}
			}
				
			tUser = ready.get(sessionPos*5+groupPos);
			
			for (int i = 0; i < tUser.size(); i++) {
				
				if (tUser.get(i).getName().contentEquals(token[1]))
					target = i;
			}
			
			tUser.remove(target);
			
			reply.setHandlerSession(senderSession);
			reply.setHandlerGroup(senderGroup);
			reply.setDummyInfo("0021&SERVER&leave now");
			
			s_stub.send(reply, token[1]);
			break;
			
		// ready 하고 만약 all ready + 3명 이상이면 게임 시작하는 logic.
		case "0030":
			for (int i = 1; i<4; i++) {
				if (("session"+i).equals(senderSession)) {
					sessionPos = i-1;
				}
			}
			
			for (int i = 1; i<6; i++) {
				if (("g"+i).equals(senderGroup)) {
					groupPos = i-1;
				}
			}
			
			tUser = ready.get(sessionPos*5+groupPos);
			
			for (ServerUser each : tUser) {
				if (each.getName().equals(token[1])) {
					each.setStateTrue();
					break;
				}
			}
			
			if (tUser.size() > 2) {
				start = true;
				
				for (ServerUser each : tUser) {
					start = each.getState() && start;
				}
				
				if (start) {
					
					ureply.setHandlerSession("null");
					ureply.setHandlerGroup("null");
					
					tList = shuffle.get(sessionPos*5+groupPos);
					
					for (int i = 0; i<15; i++) {
						tList.add(i);
					}
					
					Collections.shuffle(tList);
					
					ureply.setStringID("Set Monster Order");
					for (int i = 0; i<15; i++) {
						ureply.setEventField(CMInfo.CM_INT, String.valueOf(i), String.valueOf(tList.get(i)));
					}
					
					s_stub.cast(ureply, senderSession, senderGroup);
					
					session = s_stub.getCMInfo().getInteractionInfo().findSession(senderSession);
					group = session.findGroup(senderGroup);
					
					if(group.getGroupState())
						group.setGroupStateFalse();
					
				}
			}
			break;
			
		// ready 취소하는 Logic.
		case "0040":
			
			for (int i = 1; i<4; i++) {
				if (("session"+i).equals(senderSession)) {
					sessionPos = i-1;
				}
			}
			
			for (int i = 1; i<6; i++) {
				if (("g"+i).equals(senderGroup)) {
					groupPos = i-1;
				}
			}
			
			tUser = ready.get(sessionPos*5+groupPos);
			
			for (ServerUser each : tUser) {
				if (each.getName().equals(token[1])) {
					each.setStateFalse();
					break;
				}
			}
			
			break;
			
		default:
			return;
		}
	}

}
