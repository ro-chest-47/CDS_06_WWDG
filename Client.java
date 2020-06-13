import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Vector;

import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

import kr.ac.konkuk.ccslab.cm.event.*;

public class Client {
	
	private CMClientStub c_stub;
	private ClientEventHandler c_event;
	private CMInteractionInfo c_info;
	private CMUser c_self;
	

	public Client() {
		
		c_stub = new CMClientStub();
		c_event = new ClientEventHandler(c_stub);
		c_info = c_stub.getCMInfo().getInteractionInfo();
		c_self = c_info.getMyself();
		
	}
	
	public CMClientStub getClientStub() {
		return c_stub;
	}
	
	public ClientEventHandler getClientEventHandler() {
		return c_event;
	}
	
	public CMInteractionInfo getClientInteractionInfo() {
		return c_info;
	}
	
	public CMUser getClientUserInfo() {
		return c_self;
	}
	
	public void JoinSession()
	{
		
		String strSessionName = null;
		boolean bRequestResult = false;
		Vector<String> server_list = new Vector<String>();
		Vector<Integer> server_per_group_num = new Vector<Integer>();
		Vector<Integer> group_mem_num = new Vector<Integer>();
		Vector<String> group_list = new Vector<String>();
		//Vector<Integer> 
		CMUserEvent cue_session = new CMUserEvent();
		
		CMUserEvent rue_session =  new CMUserEvent();
		CMUserEvent cue_group = new CMUserEvent();
		CMUserEvent rue_group = new CMUserEvent();
		
		//CMUserEvent;
		
		System.out.println("req session list ");

		
		cue_session.setStringID("session_list_req");
		rue_session = (CMUserEvent)c_stub.sendrecv(cue_session,c_stub.getDefaultServerName(), CMInfo.CM_USER_EVENT, 10, 10000);
		
		
		
		int server_size = Integer.parseInt(rue_session.getEventField(CMInfo.CM_STR,"session_size"));
		

		
		for( int i = 0 ; i < server_size ; i++) {
			
			server_list.add(rue_session.getEventField(CMInfo.CM_STR, "sessionname" + i));
		}
		
		cue_group.setStringID("group_list_req");
		rue_group = (CMUserEvent)c_stub.sendrecv(cue_group,c_stub.getDefaultServerName(), CMInfo.CM_USER_EVENT,20, 10000);
		
		
		
		for(int i = 0 ; i < server_size ; i++) {
		
			System.out.println(server_list.get(i) + " has total group of " +  Integer.parseInt(rue_group.getEventField(CMInfo.CM_STR, server_list.get(i)+"groupsize"+i)));
			
			int group_size = Integer.parseInt(rue_group.getEventField(CMInfo.CM_STR, server_list.get(i)+"groupsize"+i));
			server_per_group_num.add(group_size);
			
			for(int j =  0 ;  j < group_size ; j++) {
				
			int groupmem   = Integer.parseInt(rue_group.getEventField(CMInfo.CM_STR ,  server_list.get(i)+ "group_mem_num" +j));
			String gname = rue_group.getEventField(CMInfo.CM_STR,server_list.get(i)+"groupname"+j);
			
			System.out.println( rue_group.getEventField(CMInfo.CM_STR,server_list.get(i)+"groupname"+j) + "  has " +  Integer.parseInt(rue_group.getEventField(CMInfo.CM_STR ,  server_list.get(i)+ "group_mem_num" +j)));
				group_mem_num.add(groupmem);
				group_list.add(gname);
			}
			
		}
		
		
		/*
		 * System.out.println("session list ");
		 * 
		 * for( int i = 0 ; i < server_size ; i++) {
		 * System.out.println(rue_session.getEventField(CMInfo.CM_STR, "sessionname" +
		 * i));
		 * 
		 * }
		 */
		
		System.out.println("====== join a session");
		System.out.print("session name: ");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			strSessionName = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bRequestResult = c_stub.joinSession(strSessionName);
		if(bRequestResult)
			System.out.println("successfully sent the session-join request.");
		else
			System.err.println("failed the session-join request!");
		System.out.println("======");
		
		
	}
	

	
	public boolean login(CMClientStub cmstub) {
		
		String strUserName = null;
		String strPassword = null;
		CMSessionEvent reply = null;
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.print("user name: ");
		
		try {
			strUserName = br.readLine();
			System.out.print("password: ");
			strPassword = br.readLine();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		reply = cmstub.syncLoginCM(strUserName, strPassword);
		
		if(reply != null) {
			
			if (reply.isValidUser() == 0) {
				System.err.println("client fails to get authentiaction by server...");
				return true;
			}
			else if (reply.isValidUser() == -1) {
				System.err.println("client is already in the login-user list...");
				return false;
			}
			else {
				System.out.println("client successfully logs in to the server...");
				return false;
			}
			
		}
		else {
			System.err.println("client fails to get authentiaction by server...");
			return false;
		}
	}
	
	public void signUp(CMClientStub cmstub) {
		
		String strUserName = null;
		String strPassword = null;
		
		boolean isPossibleName = false;
		boolean isPossiblePass = false;
		
		Scanner scan = new Scanner(System.in);
		
		do {
			
			System.out.println("원하시는 UserName을 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해주세요.");
			strUserName = scan.nextLine();
			System.out.println("원하시는 Password를 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해주세요.");
			strPassword = scan.nextLine();
			
			if(!strUserName.isEmpty()) {
				if(strUserName.length() > 7) {
					if(strUserName.matches("^[0-9a-z]*$")){
						isPossibleName = true;
					}
					else {
						System.out.println("UserName에는 0~9 사이의 숫자와 영어 소문자만 사용할 수 있습니다.");
					}
				}
				else {
					System.out.println("UserName은 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해야 합니다.");
				}
			}
			else {
				System.out.println("UserName은 공백으로 입력할 수 없습니다.");
			}
			
			if(!strPassword.isEmpty()) {
				if(strPassword.length() > 7) {
					if(strPassword.matches("^[0-9a-z]*$")) {
						if(!strPassword.equals(strUserName)) {
							isPossiblePass = true;
						}
						else {
							System.out.println("Password는 UserName과 동일할 수 없습니다.");
						}
					}
					else {
						System.out.println("Password에는 0~9 사이의 숫자와 영어 소문자만 사용할 수 있습니다.");
					}
				}
				else {
					System.out.println("Password는 영문 소문자 및 숫자를 사용하여 8글자 이상으로 입력해야 합니다.");
				}
			}
			else {
				System.out.println("Password는 공백으로 입력할 수 없습니다.");
			}
			
		} while(!isPossibleName || !isPossiblePass);
		
		cmstub.registerUser(strUserName, strPassword);
		
	}
	
	
	public void Gamestart_sig() {
		
		/*
		 * CMUserEvent cmu = new CMUserEvent(); cmu.setHandlerGroup(c_stub.ses)
		 */
	}
	
	public void signOut(CMClientStub cmstub) {
		
		String strUserName = null;
		String strPassword = null;
		
		cmstub.deregisterUser(strUserName, strPassword);
		
	}
	
	public static void main(String args[]) {
		
		Client client = new Client();
		CMUser clientUser = client.getClientUserInfo();
		CMClientStub cmstub = client.getClientStub();
		cmstub.setAppEventHandler(client.getClientEventHandler());
		cmstub.startCM();
		
		Scanner scan = new Scanner(System.in);
		
		
		//while(true){
			
			System.out.print("input your choice. \n" +
							 "1. login / 2. sign up / 3. sing out");
			
			int option = scan.nextInt();
			
			switch (option){
			case 1:
				if(client.login(cmstub)) {
					break;
				}
				break;
			
			case 2:
				client.signUp(cmstub);
				break;
				
			case 3:
				client.signOut(cmstub);
				break;
			}
			
			
	
	//	}
			client.JoinSession();
			
			//client.
		
		
		
	}
}
