package sql;

public interface QueryString {
	String Exist = "SELECT ssn FROM customer WHERE ssn=?";
	String ADDCUSTOMER = "INSERT INTO customer VALUES(?,?,?)";
	String DELETECUSTOMER = "DELETE FROM customer WHERE ssn=?";
	String UPDATECUSTOMER = "UPDATE customer SET cust_name=?, address=? WHERE ssn=?";
	String GETPORTFOLIO = "SELECT ssn, symbol, quantity FROM shares WHERE ssn=?";
	String GETACUSTOMER = "SELECT * FROM customer WHERE ssn=?";
	String GETALLCUSTOMER = "SELECT * FROM customer";
	String GETALLSTOCK = "SELECT * FROM stock";
	String BUYSHARES = "SELECT quantity FROM shares WHERE ssn=? AND symbol=?";
	String BUYSHARES_UPDATE = "UPDATE shares SET quantity=? WHERE ssn=? AND symbol=?";
	String BUYSHARES_INSERT = "INSERT INTO shares VALUES(?,?,?)";
	String SELLSHARES = "SELECT quantity FROM shares WHERE ssn=? AND symbol=?";
	String SELLSHARES_DELETE = "DELETE FROM shares WHERE ssn=? AND symbol=?";
	String SELLSHARES_UPDATE ="UPDATE shares SET quantity=? WHERE ssn=? AND symbol=?"; 		
	String GETSTOCKPRICE = "SELECT price FROM stock WHERE symbol=?";
	String UPDATESTOCKPRICE = "UPDATE stock SET price=? WHERE symbol=?";		
	/*
	 * 2개 정도의 메소드는 여러분이 알아서..
	 */
}




















