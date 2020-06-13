package kr.ac.konkuk.ccslab.cm.entity;
import java.net.InetSocketAddress;
import java.nio.channels.*;

public class CMGroup extends CMGroupInfo {
	private CMMember m_groupUsers;
	private CMChannelInfo<InetSocketAddress> m_mcInfo;
	private MembershipKey m_membershipKey;	// required for leaving a group
	
	// for our project
	//private boolean m_state;
	//private int m_possibleUser;
	// for our project
	
	public CMGroup()
	{
		super();
		m_groupUsers = new CMMember();
		m_mcInfo = new CMChannelInfo<InetSocketAddress>();
		m_membershipKey = null;
		
		// for our project
		//m_state = true;
		//m_possibleUser = 5;
		// for our project
		
		
	}
	
	public CMGroup(String strGroupName, String strAddress, int nPort)
	{
		super(strGroupName, strAddress, nPort);
		m_groupUsers = new CMMember();
		m_mcInfo = new CMChannelInfo<InetSocketAddress>();
		m_membershipKey = null;
		
		// for our project
		//m_state = true;
		//m_possibleUser = 5;
		// for our project
	}
	
	// set/get methods
	public synchronized CMMember getGroupUsers()
	{
		return m_groupUsers;
	}
	
	public synchronized CMChannelInfo<InetSocketAddress> getMulticastChannelInfo()
	{
		return m_mcInfo;
	}
	
	public synchronized MembershipKey getMembershipKey()
	{
		return m_membershipKey;
	}
	
	public synchronized void setMembershipKey(MembershipKey key)
	{
		m_membershipKey = key;
	}
	
	/* 
	// for our project
	public synchronized boolean getGroupState() {
		return m_state;
	}
	
	public synchronized void setGroupStateTrue() {
		m_state = true;
	}
	
	public synchronized void setGroupStateFalse() {
		m_state = false;
	}
	
	public synchronized int getPossibleUser() {
		return m_possibleUser;
	}
	
	public synchronized void setPossibleUser(int nUser) {
		m_possibleUser = nUser;
	}
	
	// for our project
	*/
	
}
