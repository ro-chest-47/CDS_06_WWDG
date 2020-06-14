//package gamePart;

import java.util.TimerTask;

public class TimerCountdown extends TimerTask{
	private int cardDrawCount;
	private int infoShowCount;
	private String monsterInfo;
	private boolean isCardDrawCount;
	private GUIClient client;
	
	public TimerCountdown(GUIClient client) {
		this.cardDrawCount = -1;		// count占쏙옙 -1占싱억옙占� 占싣뱄옙 占싹듸옙 占쏙옙占쏙옙 占십는댐옙.
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
			client.setMyBattleCard(0);	// 占쏙옙 占쏙옙틀카占썲를 占쏙옙효占쏙옙 카占쏙옙占� 占쏙옙占쏙옙.
			cardDrawCount--;
		} else if(cardDrawCount <= -1) {		// 占쏙옙占쏙옙 카占썲를 占쏙옙占썽서 count占쏙옙 -1占쏙옙 占쏙옙占쏙옙粹㏆옙占쏙옙占� 占싣뱄옙占싶듸옙 占쏙옙占쏙옙 占십는댐옙.
			// do nothing
		}
		
		if(infoShowCount > 0) {
			infoShowCount--;
		} else if (infoShowCount == 0) {
			client.setOtherPlayersCard("");
			client.setMonsterInfo(monsterInfo);
//			client.setIsThisPlayerChooseCard(false);
			infoShowCount--;
			
		} else if (infoShowCount <= -1) {
			
		}
	}

}
