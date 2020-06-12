package gamePart;

import java.util.TimerTask;

public class TimerCountdown extends TimerTask{
	private int count;
	private GUIClient client;
	
	public TimerCountdown(GUIClient client) {
		this.count = -1;		// count가 -1이어야 아무 일도 하지 않는다.
		this.client = client;
	}
	
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(count > 0) {
			count--;
		} else if(count == 0) {
			client.setMyBattleCard(0);	// 내 배틀카드를 무효한 카드로 제출.
			count--;
		} else if(count <= -1) {		// 내가 카드를 내면서 count를 -1로 만들기때문에 아무것도 하지 않는다.
			// do nothing
		}
	}

}
