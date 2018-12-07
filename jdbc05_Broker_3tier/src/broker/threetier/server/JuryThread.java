package broker.threetier.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

import broker.threetier.exception.DuplicateSSNException;
import broker.threetier.exception.InvalidTransactionException;
import broker.threetier.exception.RecordNotFoundException;
import broker.threetier.shares.Command;
import broker.threetier.shares.Result;
import broker.threetier.vo.CustomerRec;
import broker.threetier.vo.SharesRec;
import broker.threetier.vo.StockRec;

public class JuryThread extends Thread  {
	private Socket s;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Command cmd;
	private Database db;
	
	
	public JuryThread(Socket s, Database db) {
		this.s= s;
		this.db=db;
		try {
			ois = new ObjectInputStream(s.getInputStream());
			oos = new ObjectOutputStream(s.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Jury Creating");
	}//생성자
	
		
	public void run() {
		while(true) {
			//1.도시락 받는다.
			try {
				cmd = (Command)ois.readObject();
				System.out.println("cmd...jury readObject");
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
			
			//2.도시락 까본다.
			int comm = cmd.getCommandValue();
			String[] args = cmd.getArgs();
			Result r = cmd.getResults();
			//3.switch
			switch(comm){
				case 10 :
					try {
						db.buyShares(args[0], args[1], Integer.parseInt(args[2]));
						r.setStatus(0);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
					
				case 20 :
					
					try {
						db.sellShares(args[0], args[1], Integer.parseInt(args[2]));
						r.setStatus(0);
						}  catch (RecordNotFoundException e2) {
							// TODO Auto-generated catch block
							r.setStatus(-1);
						} catch (InvalidTransactionException e2) {
							// TODO Auto-generated catch block
							r.setStatus(-3);
						}catch (Exception e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						} 
					
						
					
					break;
					
				case 30 :
					
					ArrayList<StockRec> list;
					try {
						list = db.getAllStocks();
						r.setStatus(0);
						r.add(list);
						} catch (SQLException e2) {							
							e2.printStackTrace();
						}
					
					break;
					
				case 40 :
						//float price;
					try {	
							
							float price = db.getStockPrice(args[0]);
							r.setStatus(0);
							r.add(new Float(price));
						} catch (Exception e1) {
							e1.printStackTrace();
						} 
						break;
				case 50 :
					ArrayList<CustomerRec> list2;
					try {
						list2 = db.getAllCustomers();
						r.setStatus(0);
						r.add(list2);
						} catch (SQLException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					
					break;
				case 60 :
					CustomerRec cust;
					try {
						cust = db.getCustomer(args[0]);
						r.setStatus(0);
						r.add(cust);
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					break;
					
				case 70 :
				try {
					db.addCustomer(new CustomerRec(args[0],args[1],args[2]));
					r.setStatus(0);
					} catch (SQLException e1) {
						e1.printStackTrace();
					} catch (DuplicateSSNException e1) {
						r.setStatus(-1);
					}
					break;
					
				case 80 :
				try {
					db.deleteCustomer(args[0]);
					r.setStatus(0);
					} catch (SQLException e1) {
						e1.printStackTrace();
					} catch (RecordNotFoundException e1) {
						r.setStatus(-1);
					}
					break;
					
				case 90 :
					try {
						db.updateCustomer(new CustomerRec(args[0],args[1],args[2]));
						r.setStatus(0);
						} catch (SQLException e1) {
							e1.printStackTrace();
						} catch (RecordNotFoundException e1) {
							r.setStatus(-1);
						}
					break;
					
				case 100 :
					ArrayList<SharesRec> list3=null;
					
				try {
					list3 = db.getPortfolio(args[0]);
					r.setStatus(0);
					r.add(list3);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
					break;
			
			}//switch
			try {
				oos.writeObject(cmd);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//while
		
	}//run

}
