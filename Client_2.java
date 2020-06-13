import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

import kr.ac.konkuk.ccslab.cm.event.*;

public class Client_2 {
	
	private CMClientStub c_stub;
	private ClientEventHandler c_event;
	private CMInteractionInfo c_info;
	private CMUser c_self;
	

	public Client_2() {
		
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
				return false;
			}
			else if (reply.isValidUser() == -1) {
				System.err.println("client is already in the login-user list...");
				return false;
			}
			else {
				System.out.println("client successfully logs in to the server...");
				return true;
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
	
	public void signOut(CMClientStub cmstub) {
		
		String strUserName = null;
		String strPassword = null;
		
		cmstub.deregisterUser(strUserName, strPassword);
		
	}
	
	public void joinPlayRoom(CMClientStub cmstub) {

		// use joinsession : if possible group = 0에 접근하려고 하면 안된다고 할 것.
		// use change group : group state = true인 group으로 바로 이동.
		// server :: change 그룹 받으면 해당 group의 인원수 확인 후 5를 초과하면 안된다고 할 것.
		//				5를 넘지 않을 시 받고 group 인원 수 +1하고 5면 false로 바꿀 것.
		
		String sessionName = null;
		boolean result = false;
		Scanner scan = new Scanner(System.in);
		CMDummyEvent de = new CMDummyEvent();
		
		System.out.println("참여를 원하는 Session의 이름을 입력하세요.");
		sessionName = scan.nextLine();
		
		de.setHandlerSession(sessionName);
		de.setHandlerGroup("null");
		de.setDummyInfo("0000&"+cmstub.getMyself().getName()+"&check Possible room");
		
		cmstub.send(de, "SERVER");
		
	}
	
	public void leavePlayRoom(CMClientStub cmstub) {
		
		CMDummyEvent de = new CMDummyEvent();
		
		de.setHandlerSession(cmstub.getMyself().getCurrentSession());
		de.setHandlerGroup(cmstub.getMyself().getCurrentGroup());
		de.setDummyInfo("0020&"+cmstub.getMyself().getName()+"&leave Room");
		
		cmstub.send(de, "SERVER");
		// leave session 할 것.
		// server :: leave session 받으면 현재 group의 인원 수 -1할 것.
		//			:: -1한게 4이하면 flase에서 true로 바꿀 것
		//			:: 
		
	}
	
	public void readyToPlay(CMClientStub cmstub) {
		
		// server에 이벤트 보낼 것.
		// server :: event 받으면 해당 user의 state를 true 할 것.
		// 			참여중인 인원만큼 동적 array 선언하고 all true면 시작. group의 state false로 변경할 
		//			ready로 바꿀 때에만 한번 check함.
		
		CMDummyEvent de = new CMDummyEvent();
		
		de.setHandlerSession(cmstub.getMyself().getCurrentSession());
		de.setHandlerGroup(cmstub.getMyself().getCurrentGroup());
		de.setDummyInfo("0030&"+cmstub.getMyself().getName()+"&ready to play.");
		
		cmstub.send(de, "SERVER");
		
	}
	
	public void unreadyToPlay(CMClientStub cmstub) {
		
		// server에 이벤트 보낼 것.
		// server :: event 받으면 해당 user의 state를 false로 할 것.
		// 			 unready로 바꿀 때에는 check 할 필요가 없음.
		
		CMDummyEvent de = new CMDummyEvent();
		
		de.setHandlerSession(cmstub.getMyself().getCurrentSession());
		de.setHandlerGroup(cmstub.getMyself().getCurrentGroup());
		de.setDummyInfo("0040&"+cmstub.getMyself().getName()+"&unready to play.");
		
		cmstub.send(de, "SERVER");
		
	}
	

	public static void main(String args[]) {
		
		boolean isJoinSession;
		
		Client_2 client = new Client_2();
		CMUser clientUser = client.getClientUserInfo();
		CMClientStub cmstub = client.getClientStub();
		cmstub.setAppEventHandler(client.getClientEventHandler());
		cmstub.startCM();
		
		Scanner scan = new Scanner(System.in);
		
		
		while(true){
			
			System.out.println("input your choice. \n" +
							 "1. login / 2. sign up / 3. sing out");
			
			int option = scan.nextInt();
			
			if (option == 1) {

				if(client.login(cmstub)) {
					break;
				}
				
			}
			else if (option == 2) {
				client.signUp(cmstub);
			}
			else if (option == 3) {
				client.signOut(cmstub);
			}
		}
		// thread 별도로 만들어서 session 정보를 주기적으로 보여줄 것. 혹은 timer를 설정하거나.
		cmstub.requestSessionInfo();
		
		while(true) {
			
			System.out.println("input your choice. \n" +
					"1. join play / 2.log out??");
			
			int option = scan.nextInt();
			
			if (option == 1) {
				client.joinPlayRoom(cmstub);
				break;
				
			}
			else if (option == 2) {
				// 로그 아웃 은 어떻게??
			}
		}
		
		while(true) {
		
			System.out.println("input your choice. \n" +
					"1. leave room / 2. get ready / 3. cancle ready");
			int option = scan.nextInt();
			
			if (option == 1)
				client.leavePlayRoom(cmstub);
			else if (option == 2)
				client.readyToPlay(cmstub);
			else if (option == 3)
				client.unreadyToPlay(cmstub);
		
		}
	}
}
