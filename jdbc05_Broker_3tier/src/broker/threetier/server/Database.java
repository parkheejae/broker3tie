package broker.threetier.server;
/*
 * Exception ::
 * DuplicateSSNException,
 * RecordNotFoundException,
 * InvalidTransactionException
 * : �ȷ��� �ֽ��� ���ڰ� ������ �ִ°� ���� �� ������
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import broker.config.OracleInfo;
import broker.threetier.exception.DuplicateSSNException;
import broker.threetier.exception.InvalidTransactionException;
import broker.threetier.exception.RecordNotFoundException;
import broker.threetier.vo.CustomerRec;
import broker.threetier.vo.SharesRec;
import broker.threetier.vo.StockRec;
import sql.QueryString;


/*
 * DB�� Access�ϴ� �����Ͻ� ������ ��� �ִ� Ŭ����
 * UseCase Diagram�� ���� ����� ����θ� �̾Ƴ��ڴ�.
 * ::
 * SQL������ �߸��� �κ��� �ִ���� �����Ͻÿ� �ɷ����� ���Ѵ�.
 * ����ÿ� ����μ� ��Ÿ�� ���̴�.
 * 
 * threetier���� �̱������� ����
 * 1)static private���� �ϴ� ��ü �ϳ��� ����
 * 2)������ �տ� private�� �ٿ��� �ٸ������� ��ü���� ���ϰ� �Ѵ�.
 * 3)public static Database getInstance(){}
 */
public class Database {
	static {
		 try {
			Class.forName(OracleInfo.DRIVER_NAME);
			System.out.println("����̹� �ε� ����...");
		} catch (ClassNotFoundException e) {
				e.printStackTrace();
		}
		 
	}
	 static private Database db = new Database("127.0.0.1");
	
	 private Database(String serverIP){
		System.out.println("Database Singltone");
	 }
	 
	 public static Database getInstance() {
		 return db;
	 }
	 
	 //////// �������� ���� /////////////////////
	 public Connection getConnect() throws SQLException{
		Connection conn = 
				DriverManager.getConnection(OracleInfo.URL, OracleInfo.USER, OracleInfo.PASS);
		System.out.println("��� ���� ����...getConnect()...");
		 return conn;
	 }
	 
	 public void closeAll(PreparedStatement ps, Connection conn)throws SQLException{
		 if(ps != null) ps.close();
		 if(conn != null) conn.close();
	 }
	 public void closeAll(ResultSet rs,PreparedStatement ps, Connection conn)throws SQLException{
		 if(rs != null) rs.close();
			closeAll(ps, conn);
	 }
	 	
	 private boolean isExist(Connection conn,String ssn) throws SQLException{
		//Connection conn = getConnect();
		String query = QueryString.Exist;
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setString(1, ssn);
		ResultSet rs = ps.executeQuery();
		return rs.next();
	 }
	 //////////////// �����Ͻ� ���� ////////////////////////////////////
	 public void addCustomer(CustomerRec cust)throws SQLException, DuplicateSSNException{
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = getConnect();
			if(!isExist(conn, cust.getSsn())) { //�߰��Ϸ��� ����� ���ٸ�
				String query = QueryString.ADDCUSTOMER;
				ps = conn.prepareStatement(query);
				ps.setString(1, cust.getSsn());
				ps.setString(2, cust.getName());
				ps.setString(3, cust.getAddress());
				
				System.out.println(ps.executeUpdate()+" row addCustomer()..."+cust.getName());
				
			}else {
				throw new DuplicateSSNException(cust.getName()+", �׷� ��� �̹� �־");
			}
		}finally{
			closeAll(ps, conn);
		}				 
	 }
	 public void deleteCustomer(String ssn)throws SQLException,RecordNotFoundException{
		 Connection conn = null;
		 PreparedStatement ps = null;		 
		 try{
			 conn=getConnect();
			 if(isExist(conn, ssn)) { //������ ����� �ִٸ�
				 String query = QueryString.DELETECUSTOMER;
				 ps = conn.prepareStatement(query);
			 	 ps.setString(1, ssn);
			 	 System.out.println(ps.executeUpdate()+" row deleteCustomer...."+ssn);
			 }else {
				 throw new RecordNotFoundException("������ ����� ���....");
			 }
		 }finally{
			 closeAll(ps, conn);			 
		 }
	 }
	 public void updateCustomer(CustomerRec cust)throws SQLException, RecordNotFoundException{
		 Connection conn = null;
		 PreparedStatement ps = null;		
		 try{
			 conn = getConnect();
			 if(isExist(conn, cust.getSsn())) { //������ ����� �ִٸ�
				 String query = QueryString.UPDATECUSTOMER;
				 ps = conn.prepareStatement(query);
				 ps.setString(1, cust.getName());
				 ps.setString(2, cust.getAddress());
				 ps.setString(3, cust.getSsn());
				 
				 System.out.println(ps.executeUpdate()+" row updateCustomer..."+cust.getName());
			 }else {
				 throw new RecordNotFoundException("������ ����� ���...."); 
			 }
		 }finally{
			 closeAll(ps, conn);
		 }
	 }
	 /*
	  * ���� ������ �ֽ� ����(shares)....
	  * �Ѹ��� ���� �������� �ֽ������� �����Ҽ� �ֱ� ������...Vector�� ��Ҵ�.
	  */
	 public ArrayList<SharesRec> getPortfolio(String ssn)throws SQLException{
		 Connection conn = null;
		 PreparedStatement ps = null;	
		 ResultSet rs = null;
		 ArrayList<SharesRec> v = new ArrayList<SharesRec>();
		 try{
			 conn = getConnect();
			 String quesy = QueryString.GETPORTFOLIO;
			 ps = conn.prepareStatement(quesy);
			 ps.setString(1, ssn);
			 rs = ps.executeQuery();
			 while(rs.next()) {
				 v.add(new SharesRec(ssn, 
						 			 rs.getString("symbol"), 
						 			 rs.getInt("quantity")));
			 }
		 }finally{
			 closeAll(rs, ps, conn);
		 }
		 return v; //while�� �ٱ�����...
	 }
	 /*
	  * ���� ���� ���� ����(customer) + ���� ������ �ֽ� ����(shares)....
	  */
	 public CustomerRec getCustomer(String ssn)throws SQLException{
		 Connection conn = null;
		 PreparedStatement ps = null;	
		 ResultSet rs = null;
		 CustomerRec cust = null;
		 try{
			 conn = getConnect();
			 String query  = QueryString.GETACUSTOMER;
			 ps = conn.prepareStatement(query);
			 ps.setString(1, ssn);
			 rs = ps.executeQuery();
			 if(rs.next()) {
				 cust = new CustomerRec(ssn,rs.getString("cust_name"),rs.getString("address"));
			 }
			 	cust.setPortfolio(getPortfolio(ssn));
			
		 }finally{
			 closeAll(rs, ps, conn);
		 }
		 return cust;
	 }
	 
	 
	 public ArrayList<CustomerRec> getAllCustomers()throws SQLException{
		 Connection conn = null;
		 PreparedStatement ps = null;	
		 ResultSet rs = null;
		 ArrayList<CustomerRec> list = new ArrayList<CustomerRec>();
		 try{			 
			 conn=  getConnect();
			 String query = QueryString.GETALLCUSTOMER;
			 ps=  conn.prepareStatement(query);
			 rs=  ps.executeQuery();
			 while(rs.next()) {
				 list.add(new CustomerRec(rs.getString(1),rs.getString(2),rs.getString(3),
						 		getPortfolio(rs.getString(1))));
			 }			 
		 }finally{
			 closeAll(rs, ps, conn);
		 }
		 return list;
	 }	 
	 public ArrayList<StockRec> getAllStocks()throws SQLException{
		 Connection conn = null;
		 PreparedStatement ps = null;	
		 ResultSet rs = null;
		 ArrayList<StockRec> list = new ArrayList<StockRec>();
		 try{
			 conn = getConnect();
			 String query = QueryString.GETALLSTOCK;
			 ps = conn.prepareStatement(query);
			 rs=  ps.executeQuery();
			 while(rs.next()) {
				 list.add(new StockRec(
						 				rs.getString("symbol"), 
						 				rs.getFloat("price")));
			 }
		 }finally{
			 closeAll(rs, ps, conn);
		 }
		 return list;
	 }
	 
	 public float getStockPrice(String symbol)throws SQLException,RecordNotFoundException{
		 Connection conn = null;
		 PreparedStatement ps = null;	
		 ResultSet rs = null;
		 float price = 0.0f;
		 
		 try{
			 conn = getConnect();
			 String query = QueryString.GETSTOCKPRICE;
			 ps = conn.prepareStatement(query);
			 ps.setString(1, symbol);
			 rs = ps.executeQuery();
			 if(rs.next()) price = rs.getFloat(1);
			 else throw new RecordNotFoundException("���� �ֽ��Դϴ�....");
		 }finally{
			 closeAll(rs, ps, conn);
		 }
		 return price;
	 }
	 
	
	 //���� � �ֽ��� � ��ų�...
	 //������ �ֳľ��ĸ� ���� �˾ƺ���.
	 //������...update / ������...insert
	 public void buyShares(String ssn, String symbol, int quantity)
	 				throws SQLException{
		 Connection conn = null;
		 PreparedStatement ps = null;	
		 ResultSet rs = null;
		 try{
			 conn = getConnect();
			 String query = QueryString.BUYSHARES;
			 ps = conn.prepareStatement(query);
			 ps.setString(1, ssn);
			 ps.setString(2, symbol);
			 rs = ps.executeQuery();
			 if(rs.next()) { //����� �ֽ��� �ִٸ�...UPDATE
				 int q = rs.getInt(1); //���� ������ �ִ� �ֽ��� ����
				 int updateQ=q + quantity;
				 String query1 = QueryString.BUYSHARES_UPDATE;
				 ps = conn.prepareStatement(query1);
				 ps.setInt(1, updateQ);
				 ps.setString(2, ssn);
				 ps.setString(3, symbol);
				 System.out.println(ps.executeUpdate()+" row buyShares..."+symbol);
			 }else {   //����� �ֽ��� ���ٸ�(quantity�� 0�� ���...INSERT INTO...
				 String query2 =QueryString.BUYSHARES_INSERT;
				 ps = conn.prepareStatement(query2);
				 ps.setString(1, ssn);
				 ps.setString(2, symbol);
				 ps.setInt(3, quantity);
				 
				 System.out.println(ps.executeUpdate()+" row buyShares..."+symbol);
			 }
		 }finally{
			 closeAll(rs, ps, conn);
		 }
				 
	 }
	//���� � �ֽ��� � �Ȱų�...��� ���� ������ �ִ���...quantity
	 /*
	  * 100�� ������ �ִ�(���� �����ϰ� �ִ� �ֽ��� ����)
	  * 1) 100�� �ȾҴٸ�-----delete
	  * 2) 200�� �ȾҴٸ�----InvalidTransactionE~~
	  * 3) 20�� �ȾҴٸ� ---- update
	  */
	 public void sellShares(String ssn, String symbol, int quantity)
			 throws SQLException,RecordNotFoundException,InvalidTransactionException{
		 Connection conn = null;
		 PreparedStatement ps = null;	
		 ResultSet rs = null;
		 try{
			 conn=  getConnect();
			 String query = QueryString.SELLSHARES;
			 ps = conn.prepareStatement(query);
			 ps.setString(1, ssn);
			 ps.setString(2, symbol);
			 rs = ps.executeQuery();
			 if(rs.next()) { //�ֽ��� �ִٸ�
				 int q = rs.getInt(1); //���� ������ �ִ� �ֽ��� ����...100
				 int updateQ = q-quantity;
				 if(q==quantity) { //100�� �ȷ���...delete
					 String query1 = QueryString.SELLSHARES_DELETE;
					 ps = conn.prepareStatement(query1);
					 ps.setString(1, ssn);
					 ps.setString(2, symbol);
					 System.out.println(ps.executeUpdate()+" row sellShares...."+symbol);	 
				 }else if(q>quantity) {//50���� �ȷ���...update
					 String query2=  QueryString.SELLSHARES_UPDATE;
					 ps = conn.prepareStatement(query2);
					 ps.setInt(1, updateQ);
					 ps.setString(2, ssn);
					 ps.setString(3, symbol);
					 System.out.println(ps.executeUpdate()+" row sellShares..."+symbol);
				 }else { //200�� �ȷ��� ���...��ź
					 throw new InvalidTransactionException("�ȷ��� �ֽ��� �ʹ� ���ƿ�");
				 }
			 }else { //�ֽ��� ���ٸ�
				 throw new RecordNotFoundException("�ȷ��� �ֽ��� �����...");
			 }
			
		 }finally{
			 closeAll(ps, conn);			 
		 }
	 }
	 
	 public void updateStockPrice(String symbol, float price) throws SQLException{
		 Connection conn=  null;
		 PreparedStatement ps = null;
		 try{
			 conn=  getConnect();
			 String query =QueryString.UPDATESTOCKPRICE;
			 ps = conn.prepareStatement(query);
			 ps.setFloat(1, price);
			 ps.setString(2, symbol);
			 System.out.println(ps.executeUpdate()+" row updateStockPrice()..ok");
		 }finally{
			 closeAll(ps, conn);
		 }		 
	 }
	 //�̰��� main�� �޾Ƽ� �޼ҵ帶�ٸ��� �׽�Ʈ�� �����Ѵ�.
	 //���߿� Gui �� ���϶��� �̺κ��� �ּ�ó���Ѵ�.
	/* public static void main(String[] args) throws Exception{
		 Database db = new Database("127.0.0.1");		
		//db.addCustomer(new CustomerRec("777-7778", "HaBaRee1", "Busan"));
		// db.deleteCustomer("777-7778");
		// db.updateCustomer(new CustomerRec("777-777", "HaBaRee", "Seoul1212"));
		Vector<SharesRec> v= db.getPortfolio("777-777");
		for(SharesRec sh : v) {
			System.out.println(sh);
		}
		 
		// System.out.println(db.getCustomer("777-777"));
		 
		ArrayList<CustomerRec> retList = db.getAllCustomers();
		for(CustomerRec c : retList){
			System.out.println(c);
		}
		 
		 ArrayList<StockRec> retList = db.getAllStocks();
		 for(StockRec sr : retList)
			 System.out.println(sr);
		 
		// System.out.println(db.getStcokPrice("DUKE"));
		 
		// db.buyShares("777-777", "SUNW", 100);
		// db.sellShares("777-777", "SUNW", 80);
	 }*/
}//class


















