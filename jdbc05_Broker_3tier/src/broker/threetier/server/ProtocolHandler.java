package broker.threetier.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import broker.threetier.shares.Command;

/*
 * ���������� ���������� �����ϸ鼭 ����� �������ϴ� ������ ���μ���...main�� �����Ѵ�
 * Ŭ���̾�Ʈ�� ���� �Ҷ����� accept()�� �¾� �鿩�� Ŭ���̾�Ʈ�� ����Ǵ� ������ ����� ����.
 */
public class ProtocolHandler extends Thread{
	private ServerSocket server;
	private Socket s;
	private JuryThread jury;
	//private Database db;
	public static final int MIDDLE_PORT = 60000;
	
	public ProtocolHandler() {
		
		try {
			server = new ServerSocket(MIDDLE_PORT);
			//db = new Database(""); ����� �����ؾ� �ϳ� �̱������� ����Ǳ� ������ �ּ�ó��
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("start ProtocolHandler...service port :: "+MIDDLE_PORT);
			
	}
	
	public void run() {
		while(true) {
			try {
				s= server.accept();
				jury = new JuryThread(s,Database.getInstance());
				jury.start();
			}catch(IOException e) {
				
			}
			
		}
		
		
	}
	
	
	
	
	public static void main(String[] args) {
		
		ProtocolHandler handler = new ProtocolHandler();
		handler.start();
		

	}

}
