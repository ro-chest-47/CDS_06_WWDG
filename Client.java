import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
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
		
		
		while(true){
			
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
		}
		
		
		
	}
}
