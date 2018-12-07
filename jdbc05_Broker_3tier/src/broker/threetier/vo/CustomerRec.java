package broker.threetier.vo;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * Customer ���̺��� ������ �����ϱ� ���� Ŭ����...
 * �ش� Customer�� �ֽ��� ��� / �ȼ� �ִ� ���ɼ��� ���� ��
 * 
 */
public class CustomerRec implements Serializable{
	private String ssn;
	private String name; //�÷���� �ʵ���� �ٸ�...
	private String address;
	//
	private ArrayList<SharesRec> portfolio;
	
	//�ֽ��� �������� ���� ��...
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
