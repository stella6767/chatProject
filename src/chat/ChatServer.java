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
	private Vector<ClientInfo> vc; // ����� Ŭ���̾�Ʈ ������ ��� �÷���
	static FileWriter fileWriter;

	public ChatServer() {

		try {
			serverSocket = new ServerSocket(10000);
			System.out.println(TAG + "Ŭ���̾�Ʈ ���� �����...");
			vc = new Vector<>();
			// ���ξ������� ����
			while (true) {
				Socket socket = serverSocket.accept(); // Ŭ���̾�Ʈ ���� ���
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
		PrintWriter writer; // BufferedWriter�� �ٸ� ���� �������� �Լ��� ����+��ü ����� ���

		public ClientInfo(Socket socket) {
			this.socket = socket;

			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream());
				System.out.println("���� ����");

			} catch (Exception e) {
				System.out.println("���� ���� ����: " + e.getMessage());
			}

		}

		// ����: Ŭ���̾�Ʈ�� ���� ���� �޽����� ��� Ŭ���̾�Ʈ���� ������
		@Override
		public void run() {
			String line = null;
			String ChatLog = "";

			try {
				fileWriter = new FileWriter("D:\\workspace/ä�÷α�.txt", true);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				while ((line = reader.readLine()) != null) {
					
					router(line);
					fileWriter.write("[Ŭ���̾�Ʈ" + this.getName() + "] " +line);
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
					vc.get(i).writer.println("[Ŭ���̾�Ʈ" + this.getName() + "] " + line);
					vc.get(i).writer.flush();
				} else if (!line.contains(Protocol.ALL) && vc.get(i) == this) {
					vc.get(i).writer.println("�� �޽����� ���ο��Ը� ���Դϴ�.");
					vc.get(i).writer.flush();
				}
			}

		}

	}

	public static void main(String[] args) {
		new ChatServer();
	}
}
