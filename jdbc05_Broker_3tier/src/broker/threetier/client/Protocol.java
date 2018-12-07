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
	 * 클라이언트 측의 통신을 담당하는 대표주자
	 * 소켓을 생성해서 스트림을 받아내고 그 스트림을 통해서 서버측과 데이타를 주고받는일을 할것이다.
	 * 2tier에서 broker가 database의 메소드를 그대로 호출했지만 3tier에서는 broker가 protocol의 메소드를 호출할것이기떄문에
	 * 겉보기에는 Database의 비지니스 로직을 그대로 가지고 있어야 한다.(선언부동일)
	 * 이것이 databasetamplate을 상속받은 이유
	 * protocol의 비지니스 로직 메소드안에서 해야하는 일의 순서
	 * 1)command 객체 생성(도시락 싼다|Data pack)
	 * 2)command 객체를 server측으로 날린다.
	 * --------------------------------------
	 * 3)다시 서버측에서 날라오는 command를 받아서 열어본다.(도시락을 푼다|Data unpack)
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
		if(status<0) throw new RecordNotFoundException("살 주식이 없음");
	}

	@Override
	public void sellShares(String ssn, String symbol, int quantity)
			throws RecordNotFoundException, InvalidTransactionException {
		cmd = new Command(Command.SELLSHARES);
		String [ ] strs = {ssn,symbol,String.valueOf(quantity)};
		cmd.setArgs(strs);
		
		writeCommand(cmd);
		
		
		int status = getResponse();
		if(status==-1) throw new RecordNotFoundException("팔려는 주식이 없음");
		if(status==-3) throw new InvalidTransactionException("팔려는 주식이 너무많음");
		
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
		else throw new RecordNotFoundException("해당하는 고객이 없습니다.");
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
		if(status==0) System.out.println("해당 고객이 추가 되었습니다.");
		else if(status==-1) throw new DuplicateSSNException("해당하는 고객이 이미 있습니다.");
				
	}

	@Override
	public void deleteCustomer(String ssn) throws RecordNotFoundException {
			cmd = new Command(Command.DELETECUSTOMER);
			String [ ] strs = {ssn};
			cmd.setArgs(strs);
			CustomerRec cr=null;
			
			writeCommand(cmd);
			
			int status = getResponse();
			
			if(status==0) System.out.println("해당 고객이 삭제 되었습니다.");
			else if(status==-1) throw new RecordNotFoundException("해당하는 고객이 없습니다.");
		
		
	}

	@Override
	public void updateCustomer(CustomerRec cust) throws RecordNotFoundException {
		cmd = new Command(Command.UPDATECUSTOMER);
		String [ ] strs = {cust.getSsn(),cust.getName(),cust.getAddress()};
		cmd.setArgs(strs);
		CustomerRec cr=null;
		
		writeCommand(cmd);
		int status = getResponse();
		if(status==0) System.out.println("해당 고객이 추가 되었습니다.");
		else if(status==-1) throw new RecordNotFoundException("해당하는 고객이 없습니다.");
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
