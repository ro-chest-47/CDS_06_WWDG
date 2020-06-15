package kr.ac.konkuk.ccslab.cm.entity;

/**
 * The CMGroupInfo class represents a group in a session.
 * 
 * @author CCSLab, Konkuk University
 *
 */
public class CMGroupInfo {
	protected String m_strGroupName;
	protected String m_strGroupAddress;
	protected int m_nGroupPort;
	
	// for our project
	protected boolean m_state;
	protected int m_possibleUser;
	// for our project
	
	
	/**
	 * Creates an instance of the CMGroupInfo class.
	 */
	public CMGroupInfo()
	{
		m_strGroupName = "";
		m_strGroupAddress = "";
		m_nGroupPort = -1;
		
		// for our project
		m_state = true;
		m_possibleUser = 5;
		// for our project
	}
	
	/**
	 * Creates an instance of the CMGroupInfo class.
	 * 
	 * @param gname - group name
	 * @param address - multicast address assigned to this group
	 * @param port - multicast port number of this group
	 */
	public CMGroupInfo(String gname, String address, int port)
	{
		m_strGroupName = gname;
		m_strGroupAddress = address;
		m_nGroupPort = port;
		
		// for our project
		m_state = true;
		m_possibleUser = 5;
		// for our project
	}

	public void setGroupName(String name)
	{
		m_strGroupName = name;
	}
	
	public void setGroupAddress(String addr)
	{
		m_strGroupAddress = addr;
	}
	
	public void setGroupPort(int port)
	{
		m_nGroupPort = port;
	}
	
	/**
	 * Returns the group name.
	 * 
	 * @return the group name.
	 */
	public String getGroupName()
	{
		return m_strGroupName;
	}
	
	/**
	 * Returns the multicast address assigned to this group.
	 * 
	 * @return the multicast address
	 */
	public String getGroupAddress()
	{
		return m_strGroupAddress;
	}
	
	/**
	 * Returns the multicast port number of this group.
	 * 
	 * @return the port number
	 */
	public int getGroupPort()
	{
		return m_nGroupPort;
	}
	
	
	// for our project
	
	public synchronized boolean getGroupState() {
		return m_state;
	}
	
	public void setGroupStateTrue() {
		m_state = true;
	}
	
	public void setGroupStateFalse() {
		m_state = false;
	}
	
	public int getPossibleUser() {
		return m_possibleUser;
	}
	
	public void setPossibleUser(int nUser) {
		m_possibleUser = nUser;
	}
	// for our project
	
}
