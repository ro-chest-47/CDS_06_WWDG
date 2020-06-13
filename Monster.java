//package gamePart;
import java.security.SecureRandom;

public class Monster {
	private Monster nextMonster;
	private int redGem;
	private int yellowGem;
	private int blueGem;
	private int power;
	private int[] firstPrize;
	private int[] secondPrize;
	private int[] thirdPrize;
	private String name;
	private SecureRandom rand;
	
//	public Monster() {
//		rand = new SecureRandom();
//		int powerTmp;
//	}
	
	public Monster(int power) {
		rand = new SecureRandom();
		int redTmp = rand.nextInt(5);
		int yellowTmp = rand.nextInt(5);
		int blueTmp = rand.nextInt(5);
		createMonster("no name", power, redTmp, yellowTmp, blueTmp);
	}
	
	public Monster(int power, int red, int yellow, int blue) {
		createMonster("no name", power, red, yellow, blue);
	}
	
	public Monster(String name, int power, int red, int yellow, int blue) {
		createMonster(name, power, red, yellow, blue);
	}
	
	private void createMonster(String name, int power, int red, int yellow, int blue) {
		this.name = name;
		this.power = power;
		this.redGem = red;
		this.yellowGem = yellow;
		this.blueGem = blue;
		this.nextMonster = null;
	}
	
	public void setNextMonster(Monster next) {
		this.nextMonster = next;
	}
	
	public boolean isNextMonsterExist() {
		if(nextMonster == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public Monster getNextMonster() {
		return this.nextMonster;
	}
	
	public void plusGems(int red, int yellow, int blue) {
		this.redGem += red;
		this.yellowGem += yellow;
		this.blueGem += blue;
	}
	
	public int getPower() {
		return this.power;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getRedGem() {
		return this.redGem;
	}
	
	public int getYellowGem() {
		return this.yellowGem;
	}
	
	public int getBlueGem() {
		return this.blueGem;
	}
	
	
}
