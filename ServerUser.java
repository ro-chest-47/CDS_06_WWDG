
public class ServerUser {
	
	String strUsername;
	boolean Userstate;
	
	
	public ServerUser() {
		
		strUsername = null;
		Userstate = false;
	}

	public ServerUser(String name) {
		strUsername = name;
		Userstate = false;
	}
	
	public void setStateTrue() {
		Userstate = true;
	}
	
	public void setStateFalse() {
		Userstate = false;
	}
	
	public boolean getState() {
		return Userstate;
	}
	
	public void setName(String name) {
		strUsername = name;
	}
	
	public String getName() {
		return strUsername;
	}
}
