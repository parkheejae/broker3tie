package broker.threetier.vo;

import java.io.Serializable;

/*
 * Shares 테이블에 정보를 저장하는 클래스...
 * ssn...누가
 * symbol... 어떤 주식을
 * quantity... 몇개 보유하고 있는지의 여부
 * 
 */
public class SharesRec implements Serializable{
	private String ssn;
	private String symbol;
	private int quantity;
	
	public SharesRec(String ssn, String symbol, int quantity) {
		super();
		this.ssn = ssn;
		this.symbol = symbol;
		this.quantity = quantity;
	}
	
	public SharesRec() {
		this(" "," ",0);
	}
	
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	@Override
	public String toString() {
		return "SharesRec [ssn=" + ssn + ", symbol=" + symbol + ", quantity=" + quantity + "]";
	}
	
}
