package gamePart;

import java.util.TimerTask;

public class TimerCountdown extends TimerTask{
	private int count;
	
	public TimerCountdown() {
		this.count = 0;
	}
	
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(count > 0) {
			// do somethinggg
			count--;
		}
	}

}
