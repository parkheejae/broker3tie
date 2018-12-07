package broker.threetier.vo;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * Customer 테이블의 정보를 저장하기 위한 클래스...
 * 해당 Customer는 주식을 사고 / 팔수 있는 가능성을 가진 고객
 * 
 */
public class CustomerRec implements Serializable{
	private String ssn;
	private String name; //컬럼명과 필드명이 다름...
	private String address;
	//
	private ArrayList<SharesRec> portfolio;
	
	//주식을 보유하지 않은 고객...
	public CustomerRec(String ssn, String name, String address) {
		this(ssn, name, address, null);
	}
	
	public CustomerRec(String ssn, String name, String address, ArrayList<SharesRec> portfolio) {
		super();
		this.ssn = ssn;
		this.name = name;
		this.address = address;
		this.portfolio = portfolio;
	}
	
	public CustomerRec(String ssn) {
		this(ssn,"","");
	}

	public CustomerRec() {
		super();
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public ArrayList<SharesRec> getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(ArrayList<SharesRec> portfolio) {
		this.portfolio = portfolio;
	}

	@Override
	public String toString() {
		return "CustomerRec [ssn=" + ssn + ", name=" + name + ", address=" + address + ", portfolio=" + portfolio + "]";
	}
	
}
