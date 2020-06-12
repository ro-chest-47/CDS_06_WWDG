package gamePart;

import java.util.TimerTask;

public class TimerCountdown extends TimerTask{
	private int cardDrawCount;
	private int infoShowCount;
	private String monsterInfo;
	private boolean isCardDrawCount;
	private GUIClient client;
	
	public TimerCountdown(GUIClient client) {
		this.cardDrawCount = -1;		// count가 -1이어야 아무 일도 하지 않는다.
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
			client.setMyBattleCard(0);	// 내 배틀카드를 무효한 카드로 제출.
//			isCardDrawCount = false;
			cardDrawCount--;
		} else if(cardDrawCount <= -1) {		// 내가 카드를 내면서 count를 -1로 만들기때문에 아무것도 하지 않는다.
			// do nothing
		}
		
		if(infoShowCount > 0) {
			infoShowCount--;
		} else if (infoShowCount == 0) {
			client.setOtherPlayersCard("");
			client.setMonsterInfo(monsterInfo);
			infoShowCount--;
			
		} else if (infoShowCount <= -1) {
			
		}
	}

}
