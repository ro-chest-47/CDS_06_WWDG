package gamePart;

import java.util.TimerTask;

public class TimerCountdown extends TimerTask{
	private int count;
	private GUIClient client;
	
	public TimerCountdown(GUIClient client) {
		this.count = -1;		// count�� -1�̾�� �ƹ� �ϵ� ���� �ʴ´�.
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
			client.setMyBattleCard(0);	// �� ��Ʋī�带 ��ȿ�� ī��� ����.
			count--;
		} else if(count <= -1) {		// ���� ī�带 ���鼭 count�� -1�� ����⶧���� �ƹ��͵� ���� �ʴ´�.
			// do nothing
		}
	}

}
