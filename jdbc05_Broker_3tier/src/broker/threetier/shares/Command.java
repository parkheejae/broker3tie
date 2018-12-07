package broker.threetier.shares;
/*
 * �ϸ� ���ö���
 * ::
 * �� �ȿ� 
 * � �̸��� �޼ҵ尡 ȣ��Ǿ�����
 * �׶� ���ڰ��� ��������
 * ����
 * (� �뵵���� �߿��մϴ�...��񰬴ٿͼ� ���� ������ �̰��� ���ϴ�)
 * 
 */
import java.io.Serializable;

public class Command implements Serializable{
	//�����Ͻ� ������ ������� ���ν��ѳ��´�...	
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
	
	//������ �̰ɷ� ��...DataPack
	public Command(int commandValue) {		
		this.commandValue = commandValue;
		results = new Result(); //Command�� ��������� ������ ���������. ��� �޾ƿ��°� �ƴϴ�.
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




























