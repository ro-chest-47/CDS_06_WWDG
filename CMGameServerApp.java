package gamePart;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.Scanner;

import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.info.CMCommInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMCommManager;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class CMGameServerApp {
	private CMServerStub m_serverStub;
	private CMGameServerAppEventHandler m_eventHandler;
	private boolean m_bRun;
	
	private SecureRandom random;
	private int[] monsterOrder; 
	
	// Test Code
	private int numOfPlayer;
	
	public CMGameServerApp() {
		m_serverStub = new CMServerStub();
		m_eventHandler = new CMGameServerAppEventHandler(m_serverStub, this);
		m_bRun = true;
		
		random = new SecureRandom();
		monsterOrder = new int[15];
		for(int i=0; i<15; i++) {
			monsterOrder[i] = i;
		}
		
		// Test Code
		numOfPlayer = 0;
	}
	
	public int getNumOfPlayer() {
		return this.numOfPlayer;
	}
	
	public void setNumOfPlayer(int numOfPlayer) {
		this.numOfPlayer = numOfPlayer;
	}
	
	public CMServerStub getServerStub() {
		return m_serverStub;
	}
	
	public CMGameServerAppEventHandler getGameServerEventHandler() {
		return m_eventHandler;
	}
	
	
	//	==================================== GAME SERVER PART =========================================//
	
	public void sendMonsterOrder(String destSessionName, String destGroupName) {
		CMUserEvent use = new CMUserEvent();
		// send Monster Order만 실행해도 알아서 몬스터 카드를 셔플 시켜준다.
		monsterOrder = shuffleMonsterOrder(monsterOrder, 1);		
		
		use.setStringID("Set Monster Order");
		for(int i=0; i<15; i++) {
			use.setEventField(CMInfo.CM_INT, String.valueOf(i), String.valueOf(monsterOrder[i]));
		}
		
		use.setHandlerSession(destSessionName);
		use.setHandlerGroup(destGroupName);		
		m_serverStub.cast(use, destSessionName, destGroupName);
		use = null;
	}
	
	public void sendGameStart(String destSessionName, String destGroupName) {
		CMUserEvent use = new CMUserEvent();
		use.setStringID("Game Start");
		use.setHandlerSession(destSessionName);
		use.setHandlerGroup(destGroupName);
		m_serverStub.cast(use, destSessionName, destGroupName);
		use = null;
	}
	
	public int[] shuffleMonsterOrder(int[] arr, int count) {
		// parameter로 monsterOrder를 넣는다. 일단 send Monster 하면 자동으로 실행되게 코드 짜놨다.
		int tmp = 0, rand1 = 0, rand2 = 0;
		
		for(int i = 0; i < count; i++) {
			rand1 = random.nextInt(arr.length);
			rand2 = random.nextInt(arr.length);
			tmp = arr[rand1];
			arr[rand1] = arr[rand2];
			arr[rand2] = tmp;
		}
		return arr;
	}
	
	
//	==================================== GAME SERVER PART =========================================//
	
	
	// Test Code
	public void startServer() {
		Scanner scan = new Scanner(System.in);
		CMSessionInfo cms = new CMSessionInfo("session1", "192.168.99.1", 7777);
		
		System.out.println(cms.getSessionName() + " " + cms.getUserNum());
		while(m_bRun) {
//			System.out.println("Blocking Sequence.");
//			scan.nextInt();
		}
	}
	
	public void startCM() {
		// get current server info from the server configuration file
		String strSavedServerAddress = null;
		String strCurServerAddress = null;
		int nSavedServerPort = -1;
		String strNewServerAddress = null;
		String strNewServerPort = null;
		int nNewServerPort = -1;
				
		strSavedServerAddress = m_serverStub.getServerAddress();
		strCurServerAddress = CMCommManager.getLocalIP();
		nSavedServerPort = m_serverStub.getServerPort();
				
		// ask the user if he/she would like to change the server info
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("========== start CM");
		System.out.println("detected server address: "+strCurServerAddress);
		System.out.println("saved server port: "+nSavedServerPort);
				
		try {
			System.out.print("new server address (enter for detected value): ");
			strNewServerAddress = br.readLine().trim();
			if(strNewServerAddress.isEmpty()) strNewServerAddress = strCurServerAddress;

			System.out.print("new server port (enter for saved value): ");
			strNewServerPort = br.readLine().trim();
			try {
				if(strNewServerPort.isEmpty()) 
					nNewServerPort = nSavedServerPort;
				else
					nNewServerPort = Integer.parseInt(strNewServerPort);				
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
					
			// update the server info if the user would like to do
			if(!strNewServerAddress.equals(strSavedServerAddress))
				m_serverStub.setServerAddress(strNewServerAddress);
			if(nNewServerPort != nSavedServerPort)
				m_serverStub.setServerPort(Integer.parseInt(strNewServerPort));
					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
				
		boolean bRet = m_serverStub.startCM();
		if(!bRet)
		{
			System.err.println("CM initialization error!");
			return;
		}
		startServer();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CMGameServerApp server = new CMGameServerApp();
		CMServerStub cmStub = server.getServerStub();
		cmStub.setAppEventHandler(server.getGameServerEventHandler());
		server.startCM();
		
		
	}

}
