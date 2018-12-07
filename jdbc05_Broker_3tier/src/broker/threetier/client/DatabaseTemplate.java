package broker.threetier.client;

import java.util.ArrayList;
import broker.threetier.exception.DuplicateSSNException;
import broker.threetier.exception.InvalidTransactionException;
import broker.threetier.exception.RecordNotFoundException;
import broker.threetier.vo.CustomerRec;
import broker.threetier.vo.StockRec;


public interface DatabaseTemplate{

	public static final int MIDDLE_PORT = 60000;

	//////////// Database의 메소드를 동일하게 선언...////////////////////////////
	public void buyShares(String ssn, String symbol, int quantity)
										throws RecordNotFoundException;

	public void sellShares(String ssn, String symbol, int quantity)
									throws RecordNotFoundException, InvalidTransactionException;
	public ArrayList<StockRec> getAllStocks();
	
	public float getStockPrice(String symbol);

	public ArrayList<CustomerRec> getAllCustomers();	
		
	public CustomerRec getCustomer(String ssn)throws RecordNotFoundException;

	public void addCustomer(CustomerRec cust) throws DuplicateSSNException;

	public void deleteCustomer(String ssn)	throws RecordNotFoundException;

	public void updateCustomer(CustomerRec cust) throws RecordNotFoundException;

}




















