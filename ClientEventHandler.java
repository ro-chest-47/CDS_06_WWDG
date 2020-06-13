import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import kr.ac.konkuk.ccslab.cm.entity.CMGroup;
import kr.ac.konkuk.ccslab.cm.entity.CMGroupInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMSession;
import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

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
	
	private void processSessionEvent(CMEvent cme) {
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
						c_stub.changeGroup(groupName);
						
						de.setHandlerSession(c_stub.getMyself().getCurrentSession());
						de.setHandlerGroup(c_stub.getMyself().getCurrentGroup());
						de.setDummyInfo("0010&"+c_stub.getMyself().getName()+"&chagneGroupState");
						
						c_stub.send(de, "SERVER");					
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
	
	private void dummyEvent(CMEvent cme) {
		
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
		
		/*
		 
		 token[0] = message ID
		 token[1] = User ID
		 token[2] = message value.
		 
		 */
		
		switch(token[0]) {
		
		case "0001":
			if (token[2].equals("YES")) {
				
				boolean result = c_stub.joinSession(senderSession);
				if (result) {
					System.out.println("Session 참여 성공.");
				}
				else {
					System.out.println("에러발생.");
				}
		
			}
			else if (token[2].equals("NO")) {
				
				// 별도 쓰레드로 계속 보여줄 것. + 추가하기.
				c_stub.requestSessionInfo();
				
				System.out.println("참여 실패. Session의 이름을 다시 한 번 입력하여 주세요. 또한 가능한 방의 수가 0인 Session에는 참여할 수 없습니다.");
				try {
					senderSession = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				request.setHandlerSession(senderSession);
				request.setHandlerGroup("null");		
				request.setDummyInfo("0000&"+c_stub.getMyself().getName()+"&check Possible room");
				
				c_stub.send(request, "SERVER");
				
			}
			else {
				
			}
			break;
			
		case "0021":
			
			c_stub.leaveSession();
			
			break;
		default:
			return;
		}
		
	}
}
