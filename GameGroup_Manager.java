import java.util.Iterator;
import java.util.Vector;

import kr.ac.konkuk.ccslab.cm.entity.CMGroup;
import kr.ac.konkuk.ccslab.cm.entity.CMSession;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class GameGroup_Manager {
	

	Vector<GameGroup> vec;
	
	public GameGroup_Manager(CMServerStub stub) {
		
		
		//m_serverStub = stub.getCMInfo();
		vec = new Vector<GameGroup>();
		  CMInteractionInfo interInfo = stub.getCMInfo().getInteractionInfo();
		  
		  Iterator<CMSession> iter = interInfo.getSessionList().iterator();
		  
		  while(iter.hasNext()) { 
			  
			  CMSession session = iter.next();
			  Iterator<CMGroup> iter2 = session.getGroupList().iterator();
			  
				while(iter.hasNext())
				{
					CMGroup gInfo = iter2.next();
					GameGroup gg = new GameGroup(gInfo,session.getSessionName());
					vec.add(gg);
				}
		  }
	
	}
	
	
	public GameGroup GetGameGroup(String servername , String groupname) {
	
		for(int i = 0 ; i < vec.size(); i++ ) {
		
			if(vec.get(i).getGroupname() == servername && vec.get(i).getSession_Name() == groupname) {
				
				return vec.get(i);
			}		
		}
		return null;
	}
	
	public boolean setisPlaying(String servername, String groupname,boolean state) {
	
		for(int i = 0 ; i < vec.size(); i++ ) {
			
			if(vec.get(i).getGroupname() == servername && vec.get(i).getSession_Name() == groupname) {
				
				vec.get(i).setPlaying(state);
				return true;
			}		
		}
		System.out.println(" Gamegroup cannot find group(set playing state)");
		return false;
		
	}
	public boolean getisPlaying(String servername, String groupname) {
		
		for(int i = 0 ; i < vec.size(); i++ ) {
			
			if(vec.get(i).getGroupname() == servername && vec.get(i).getSession_Name() == groupname) {
				
				return vec.get(i).isPlaying();
			}		
			
		}
		System.out.println(" Gamegroup cannot find group ( get playing state");
		return false;		
	}
	
	
public void  refresh(CMServerStub stub) {
		
		
		//m_serverStub = stub.getCMInfo();
		vec.clear();
	
		  CMInteractionInfo interInfo = stub.getCMInfo().getInteractionInfo();
		  
		  Iterator<CMSession> iter = interInfo.getSessionList().iterator();
		  
		  while(iter.hasNext()) { 
			  
			  CMSession session = iter.next();
			  Iterator<CMGroup> iter2 = session.getGroupList().iterator();
			  
				while(iter2.hasNext())
				{
					CMGroup gInfo = iter2.next();
					GameGroup gg = new GameGroup(gInfo,session.getSessionName());
					vec.add(gg);
				}
		  }
	
	}
	
	

}
