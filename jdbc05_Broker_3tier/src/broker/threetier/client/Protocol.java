package broker.threetier.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import broker.threetier.exception.DuplicateSSNException;
import broker.threetier.exception.InvalidTransactionException;
import broker.threetier.exception.RecordNotFoundException;
import broker.threetier.shares.Command;
import broker.threetier.shares.Result;
import broker.threetier.vo.CustomerRec;
import broker.threetier.vo.SharesRec;
import broker.threetier.vo.StockRec;

public class Protocol implements DatabaseTemplate {
	/*
	 * Ŭ���̾�Ʈ ���� ����� ����ϴ� ��ǥ����
	 * ������ �����ؼ� ��Ʈ���� �޾Ƴ��� �� ��Ʈ���� ���ؼ� �������� ����Ÿ�� �ְ�޴����� �Ұ��̴�.
	 * 2tier���� broker�� database�� �޼ҵ带 �״�� ȣ�������� 3tier������ broker�� protocol�� �޼ҵ带 ȣ���Ұ��̱⋚����
	 * �Ѻ��⿡�� Database�� �����Ͻ� ������ �״�� ������ �־�� �Ѵ�.(����ε���)
	 * �̰��� databasetamplate�� ��ӹ��� ����
	 * protocol�� �����Ͻ� ���� �޼ҵ�ȿ��� �ؾ��ϴ� ���� ����
	 * 1)command ��ü ����(���ö� �Ѵ�|Data pack)
	 * 2)command ��ü�� server������ ������.
	 * --------------------------------------
	 * 3)�ٽ� ���������� ������� command�� �޾Ƽ� �����.(���ö��� Ǭ��|Data unpack)
	 * 
	 */
	private Socket s;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Command cmd;
	
	
	
	public Protocol(String serverIP) throws IOException {
		s = new Socket(serverIP,DatabaseTemplate.MIDDLE_PORT);
		oos = new ObjectOutputStream(s.getOutputStream());
		ois = new ObjectInputStream(s.getInputStream());
		
	}
	
	public void writeCommand(Command cmd) {
		try {
			oos.writeObject(cmd);
			System.out.println("client protocl writecommand...end");
		} catch (IOException e) {
			System.out.println("client catch writecommand..."+e);
		}
				
	}
	public int getResponse() {//readObject() + getResults() + getStatus();
		try {
			 cmd = (Command)ois.readObject();
			 System.out.println("client protocl getresponse...end");
		} catch (Exception e) {
			System.out.println("client catch getresponse..."+e);
		}
		 int status =cmd.getResults().getStatus();
		return status;
		
	}
	
	@Override
	public void buyShares(String ssn, String symbol, int quantity) throws RecordNotFoundException {
		cmd = new Command(Command.BUYSHARES);
		String [ ] strs = {ssn,symbol,String.valueOf(quantity)};
		cmd.setArgs(strs);
		
		writeCommand(cmd);
		
		int status = getResponse();
		if(status<0) throw new RecordNotFoundException("�� �ֽ��� ����");
	}

	@Override
	public void sellShares(String ssn, String symbol, int quantity)
			throws RecordNotFoundException, InvalidTransactionException {
		cmd = new Command(Command.SELLSHARES);
		String [ ] strs = {ssn,symbol,String.valueOf(quantity)};
		cmd.setArgs(strs);
		
		writeCommand(cmd);
		
		
		int status = getResponse();
		if(status==-1) throw new RecordNotFoundException("�ȷ��� �ֽ��� ����");
		if(status==-3) throw new InvalidTransactionException("�ȷ��� �ֽ��� �ʹ�����");
		
	}
	public ArrayList<SharesRec> getPortfolio(String ssn) {
		cmd = new Command(Command.GETPORTFOLIO);
		ArrayList<SharesRec> returnList;
		String [ ] strs = {ssn};
		cmd.setArgs(strs);
		writeCommand(cmd);
		getResponse();
		
		
		returnList=(ArrayList<SharesRec>)cmd.getResults().get(0);
		
		
		return returnList;
	}

	@Override
	public float getStockPrice(String symbol)  {
		cmd = new Command(Command.GETSTOCKPRICE);
		String [ ] strs = {symbol};
		cmd.setArgs(strs);
		
		writeCommand(cmd);
		
		
		getResponse();
				
		float price = (float)cmd.getResults().get(0);
		
		return price;
	}

	@Override
	public ArrayList<CustomerRec> getAllCustomers() {
		cmd = new Command(Command.GETALLCUSTOMERS);
		
		
		writeCommand(cmd);
		getResponse();
		 ArrayList<CustomerRec> returnList=(ArrayList<CustomerRec>)cmd.getResults().get(0);
		
		
		return returnList;
	}

	@Override
	public CustomerRec getCustomer(String ssn) throws RecordNotFoundException {
		cmd = new Command(Command.GETCUSTOMER);
		String [ ] strs = {ssn};
		cmd.setArgs(strs);
		CustomerRec cr=null;
		
		writeCommand(cmd);
		int status = getResponse();
		if(status==0) cr=(CustomerRec)cmd.getResults().get(0);
		else throw new RecordNotFoundException("�ش��ϴ� ���� �����ϴ�.");
		return cr;
	}

	@Override
	public void addCustomer(CustomerRec cust) throws DuplicateSSNException {
		cmd = new Command(Command.ADDCUSTOMER);
		String [ ] strs = {cust.getSsn(),cust.getName(),cust.getAddress()};
		cmd.setArgs(strs);
		CustomerRec cr=null;
		
		writeCommand(cmd);
		int status = getResponse();
		if(status==0) System.out.println("�ش� ���� �߰� �Ǿ����ϴ�.");
		else if(status==-1) throw new DuplicateSSNException("�ش��ϴ� ���� �̹� �ֽ��ϴ�.");
				
	}

	@Override
	public void deleteCustomer(String ssn) throws RecordNotFoundException {
			cmd = new Command(Command.DELETECUSTOMER);
			String [ ] strs = {ssn};
			cmd.setArgs(strs);
			CustomerRec cr=null;
			
			writeCommand(cmd);
			
			int status = getResponse();
			
			if(status==0) System.out.println("�ش� ���� ���� �Ǿ����ϴ�.");
			else if(status==-1) throw new RecordNotFoundException("�ش��ϴ� ���� �����ϴ�.");
		
		
	}

	@Override
	public void updateCustomer(CustomerRec cust) throws RecordNotFoundException {
		cmd = new Command(Command.UPDATECUSTOMER);
		String [ ] strs = {cust.getSsn(),cust.getName(),cust.getAddress()};
		cmd.setArgs(strs);
		CustomerRec cr=null;
		
		writeCommand(cmd);
		int status = getResponse();
		if(status==0) System.out.println("�ش� ���� �߰� �Ǿ����ϴ�.");
		else if(status==-1) throw new RecordNotFoundException("�ش��ϴ� ���� �����ϴ�.");
	}

	@Override
	public ArrayList<StockRec> getAllStocks() {
		cmd = new Command(Command.GETALLSTOCKS);
		writeCommand(cmd);
		getResponse();
		 ArrayList<StockRec> returnList=(ArrayList<StockRec>)cmd.getResults().get(0);
		
		
		return returnList;
		
	}
	
	
	
}
