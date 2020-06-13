package gamePart;

import java.util.TimerTask;

public class TimerCountdown extends TimerTask{
	private int cardDrawCount;
	private int infoShowCount;
	private String monsterInfo;
	private boolean isCardDrawCount;
	private GUIClient client;
	
	public TimerCountdown(GUIClient client) {
		this.cardDrawCount = -1;		// count�� -1�̾�� �ƹ� �ϵ� ���� �ʴ´�.
		this.infoShowCount = -1;
		this.monsterInfo = "";
		this.isCardDrawCount = false;
		this.client = client;
	}
	
	public void setDrawCount(int cardDrawCount) {
		this.cardDrawCount = cardDrawCount;
	}
	
	public void setInfoShowCount(int infoShowCount, String monsterInfo) {
		this.infoShowCount = infoShowCount;
		this.monsterInfo = monsterInfo;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(cardDrawCount > 0) {
			cardDrawCount--;
		} else if(cardDrawCount == 0) {
			client.setMyBattleCard(0);	// �� ��Ʋī�带 ��ȿ�� ī��� ����.
//			isCardDrawCount = false;
			cardDrawCount--;
		} else if(cardDrawCount <= -1) {		// ���� ī�带 ���鼭 count�� -1�� ����⶧���� �ƹ��͵� ���� �ʴ´�.
			// do nothing
		}
		
		if(infoShowCount > 0) {
			infoShowCount--;
		} else if (infoShowCount == 0) {
			client.setOtherPlayersCard("");
			client.setMonsterInfo(monsterInfo);
			client.setIsThisPlayerChooseCard(false);
			infoShowCount--;
			
		} else if (infoShowCount <= -1) {
			
		}
	}

}
