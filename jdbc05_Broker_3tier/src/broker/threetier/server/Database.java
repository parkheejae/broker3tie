package broker.threetier.server;
/*
 * Exception ::
 * DuplicateSSNException,
 * RecordNotFoundException,
 * InvalidTransactionException
 * : 팔려는 주식의 숫자가 가지고 있는것 보다 더 많을떄
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
 * DB에 Access하는 비지니스 로직을 담고 있는 클래스
 * UseCase Diagram을 보고서 기능의 선언부를 뽑아내겠다.
 * ::
 * SQL문에서 잘못된 부분이 있더라고 컴파일시에 걸러내지 못한다.
 * 실행시에 결과로서 나타날 뿐이다.
 * 
 * threetier에서 싱글톤으로 변경
 * 1)static private으로 일단 객체 하나를 생성
 * 2)생성자 앞에 private을 붙여서 다른곳에서 객체생성 못하게 한다.
 * 3)public static Database getInstance(){}
 */
public class Database {
	static {
		 try {
			Class.forName(OracleInfo.DRIVER_NAME);
			System.out.println("드라이버 로딩 성공...");
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
	 
	 //////// 공통적인 로직 /////////////////////
	 public Connection getConnect() throws SQLException{
		Connection conn = 
				DriverManager.getConnection(OracleInfo.URL, OracleInfo.USER, OracleInfo.PASS);
		System.out.println("디비 연결 성공...getConnect()...");
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
	 //////////////// 비지니스 로직 ////////////////////////////////////
	 public void addCustomer(CustomerRec cust)throws SQLException, DuplicateSSNException{
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = getConnect();
			if(!isExist(conn, cust.getSsn())) { //추가하려는 사람이 없다면
				String query = QueryString.ADDCUSTOMER;
				ps = conn.prepareStatement(query);
				ps.setString(1, cust.getSsn());
				ps.setString(2, cust.getName());
				ps.setString(3, cust.getAddress());
				
				System.out.println(ps.executeUpdate()+" row addCustomer()..."+cust.getName());
				
			}else {
				throw new DuplicateSSNException(cust.getName()+", 그런 사람 이미 있어여");
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
			 if(isExist(conn, ssn)) { //삭제할 사람이 있다면
				 String query = QueryString.DELETECUSTOMER;
				 ps = conn.prepareStatement(query);
			 	 ps.setString(1, ssn);
			 	 System.out.println(ps.executeUpdate()+" row deleteCustomer...."+ssn);
			 }else {
				 throw new RecordNotFoundException("삭제할 대상이 없어여....");
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
			 if(isExist(conn, cust.getSsn())) { //수정할 대상이 있다면
				 String query = QueryString.UPDATECUSTOMER;
				 ps = conn.prepareStatement(query);
				 ps.setString(1, cust.getName());
				 ps.setString(2, cust.getAddress());
				 ps.setString(3, cust.getSsn());
				 
				 System.out.println(ps.executeUpdate()+" row updateCustomer..."+cust.getName());
			 }else {
				 throw new RecordNotFoundException("수정할 대상이 없어여...."); 
			 }
		 }finally{
			 closeAll(ps, conn);
		 }
	 }
	 /*
	  * 고객이 보유한 주식 정보(shares)....
	  * 한명의 고객이 여러개의 주식종복을 보유할수 있기 때문에...Vector에 담았다.
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
		 return v; //while문 바깥에서...
	 }
	 /*
	  * 순수 고객에 대한 정보(customer) + 고객이 보유한 주식 정보(shares)....
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
			 else throw new RecordNotFoundException("없는 주식입니다....");
		 }finally{
			 closeAll(rs, ps, conn);
		 }
		 return price;
	 }
	 
	
	 //누가 어떤 주식을 몇개 살거냐...
	 //가지고 있냐없냐를 먼저 알아본다.
	 //있으면...update / 없으면...insert
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
			 if(rs.next()) { //살려는 주식이 있다면...UPDATE
				 int q = rs.getInt(1); //현재 가지고 있는 주식의 수량
				 int updateQ=q + quantity;
				 String query1 = QueryString.BUYSHARES_UPDATE;
				 ps = conn.prepareStatement(query1);
				 ps.setInt(1, updateQ);
				 ps.setString(2, ssn);
				 ps.setString(3, symbol);
				 System.out.println(ps.executeUpdate()+" row buyShares..."+symbol);
			 }else {   //살려는 주식이 없다면(quantity가 0인 경우...INSERT INTO...
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
	//누가 어떤 주식을 몇개 팔거냐...몇개를 현재 가지고 있는지...quantity
	 /*
	  * 100개 가지고 있다(현재 보유하고 있는 주식의 수량)
	  * 1) 100개 팔았다면-----delete
	  * 2) 200개 팔았다면----InvalidTransactionE~~
	  * 3) 20개 팔았다면 ---- update
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
			 if(rs.next()) { //주식이 있다면
				 int q = rs.getInt(1); //현재 가지고 있는 주식의 수량...100
				 int updateQ = q-quantity;
				 if(q==quantity) { //100를 팔려면...delete
					 String query1 = QueryString.SELLSHARES_DELETE;
					 ps = conn.prepareStatement(query1);
					 ps.setString(1, ssn);
					 ps.setString(2, symbol);
					 System.out.println(ps.executeUpdate()+" row sellShares...."+symbol);	 
				 }else if(q>quantity) {//50개를 팔려면...update
					 String query2=  QueryString.SELLSHARES_UPDATE;
					 ps = conn.prepareStatement(query2);
					 ps.setInt(1, updateQ);
					 ps.setString(2, ssn);
					 ps.setString(3, symbol);
					 System.out.println(ps.executeUpdate()+" row sellShares..."+symbol);
				 }else { //200를 팔려는 경우...폭탄
					 throw new InvalidTransactionException("팔려는 주식이 너무 많아요");
				 }
			 }else { //주식이 없다면
				 throw new RecordNotFoundException("팔려는 주식이 없어요...");
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
	 //이곳에 main을 달아서 메소드마다마다 테스트를 진행한다.
	 //나중에 Gui 와 붙일때는 이부분을 주석처리한다.
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


















