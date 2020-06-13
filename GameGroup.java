import java.net.InetSocketAddress;
import java.util.ArrayList;

import kr.ac.konkuk.ccslab.cm.entity.CMChannelInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMGroup;
import kr.ac.konkuk.ccslab.cm.entity.CMMember;

public class GameGroup {

	private String Session_Name;
	//private CMGroup cm_group;
	private boolean isPlaying;
	private ArrayList<Integer> shuffledata;
	private String groupname;
	
	public GameGroup(CMGroup cmg) {
		//cm_group = cmg;
		groupname = cmg.getGroupName();
		isPlaying = false;
		Session_Name="";
		shuffledata = new ArrayList<Integer>();
	}

	public GameGroup(CMGroup cmg,String Session_Name) {
		//cm_group = cmg;
		groupname = cmg.getGroupName();
		isPlaying = false;
		this.Session_Name=Session_Name;
		shuffledata = new ArrayList<Integer>();
	
	}
	
	/*
	 * void refresh(CMGroup cmg) { cm_group = cmg; } void refresh(CMGroup cmg
	 * ,boolean isPlay) { cm_group = cmg; isPlaying = isPlay; }
	 */
	/*
	 * boolean match(String sessionname , String groupname ) {
	 * 
	 * if(this.Session_Name.equals(sessionname) && this.groupname.equals(groupname))
	 * return true; else return false; }
	 */	
	
	
	//Getter Setter
	

	public String getSession_Name() {
		return Session_Name;
	}
	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public void setSession_Name(String session_Name) {
		Session_Name = session_Name;
	}

	public ArrayList<Integer> getShuffledata() {
		return shuffledata;
	}
	public void setShuffledata(ArrayList<Integer> shuffledata) {
		this.shuffledata = shuffledata;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
}
