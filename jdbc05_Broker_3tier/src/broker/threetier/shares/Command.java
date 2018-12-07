package broker.threetier.shares;
/*
 * 일명 도시락통
 * ::
 * 이 안에 
 * 어떤 이름의 메소드가 호출되었는지
 * 그때 인자값이 무엇인지
 * 빈통
 * (어떤 용도인지 중요합니다...디비갔다와서 담을 내용이 이곳에 들어갑니다)
 * 
 */
import java.io.Serializable;

public class Command implements Serializable{
	//비지니스 로직과 상수값을 매핑시켜놓는다...	
	public static final int BUYSHARES = 10;
	public static final int SELLSHARES = 20;
	public static final int GETALLSTOCKS = 30;
	public static final int GETSTOCKPRICE = 40;
	public static final int GETALLCUSTOMERS = 50;
	public static final int GETCUSTOMER = 60;
	public static final int ADDCUSTOMER = 70;
	public static final int DELETECUSTOMER = 80;
	public static final int UPDATECUSTOMER = 90;	
	public static final int GETPORTFOLIO = 90;
	
	private int commandValue;
	private String[ ] args;
	private Result results;
	
	//주입은 이걸로 끝...DataPack
	public Command(int commandValue) {		
		this.commandValue = commandValue;
		results = new Result(); //Command가 만들어질때 무조건 만들어진다. 어디서 받아오는게 아니다.
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public int getCommandValue() {
		return commandValue;
	}

	public String[] getArgs() {
		return args;
	}

	public Result getResults() {
		return results;
	}	
	
}




























