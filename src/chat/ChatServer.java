package chat;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer {

	private static final String TAG = "ChatServer: ";
	private ServerSocket serverSocket;
	private Vector<ClientInfo> vc; // 연결된 클라이언트 소켓을 담는 컬렉션
	static FileWriter fileWriter;

	public ChatServer() {

		try {
			serverSocket = new ServerSocket(10000);
			System.out.println(TAG + "클라이언트 연결 대기중...");
			vc = new Vector<>();
			// 메인쓰레드의 역할
			while (true) {
				Socket socket = serverSocket.accept(); // 클라이언트 연결 대기
				ClientInfo clientInfo = new ClientInfo(socket);
				clientInfo.start();
				vc.add(clientInfo);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	class ClientInfo extends Thread {

		Socket socket;
		BufferedReader reader;
		PrintWriter writer; // BufferedWriter와 다른 점은 내려쓰기 함수를 지원+객체 만들기 편안

		public ClientInfo(Socket socket) {
			this.socket = socket;

			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream());
				System.out.println("연결 성공");

			} catch (Exception e) {
				System.out.println("서버 연결 실패: " + e.getMessage());
			}

		}

		// 역할: 클라이언트로 부터 받은 메시지를 모든 클라이언트에게 재전송
		@Override
		public void run() {
			String line = null;
			String ChatLog = "";

			try {
				fileWriter = new FileWriter("D:\\workspace/채팅로그.txt", true);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				while ((line = reader.readLine()) != null) {
					
					router(line);
					fileWriter.write("[클라이언트" + this.getName() + "] " +line);
					fileWriter.write("\r\n", 0, 2);
					fileWriter.flush();
					if (line.contains(Protocol.Exit)) {
						break;
					}
				}

				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		public void router(String line) {
			for (int i = 0; i < vc.size(); i++) {
				if (vc.get(i) != this && line.contains(Protocol.ALL)) {
					line = line.replaceAll(Protocol.ALL, "");
					vc.get(i).writer.println("[클라이언트" + this.getName() + "] " + line);
					vc.get(i).writer.flush();
				} else if (!line.contains(Protocol.ALL) && vc.get(i) == this) {
					vc.get(i).writer.println("이 메시지는 개인에게만 보입니다.");
					vc.get(i).writer.flush();
				}
			}

		}

	}

	public static void main(String[] args) {
		new ChatServer();
	}
}
