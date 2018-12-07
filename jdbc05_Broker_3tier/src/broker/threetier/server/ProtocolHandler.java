package broker.threetier.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import broker.threetier.shares.Command;

/*
 * 서버측에서 서버소켓을 생성하면서 통신을 가능케하는 서버측 프로세스...main이 존재한다
 * 클라이언트가 접속 할때마다 accept()로 맞아 들여서 클라이언트와 연결되는 소켓을 만들어 낸다.
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
			//db = new Database(""); 여기다 선언해야 하나 싱글톤으로 진행되기 때문에 주석처리
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
