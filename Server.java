import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class Server {

	private CMServerStub s_stub;
	private ServerEventHandler s_event;
	
	public Server() {
		
		s_stub = new CMServerStub();
		s_event = new ServerEventHandler(s_stub);
		
	}
	
	public CMServerStub getServerStub() {
		return s_stub;
	}
	
	public ServerEventHandler getServerEvent() {
		return s_event;
	}
	
	public static void main(String[] args) {
		
		Server server = new Server();
		CMServerStub cmStub = server.getServerStub();
		cmStub.setAppEventHandler(server.getServerEvent());
		cmStub.startCM();
		
	}
	
	
	
}
