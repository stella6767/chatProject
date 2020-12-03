package chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient extends JFrame {

	private final static String TAG = "ChatClient: ";
	private ChatClient chatClient = this;

	private static final int PORT = 10000;

	private JButton btnConnect, btnSend; // �տ� �������� �̸��� �ִ� �� ����, ã�� �� ����
	private JTextField tfHost, tfChat;
	private JTextArea taChatList;
	private ScrollPane scrollPane;
	private JPanel topPanel, bottomPanel;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;

	public ChatClient() {
		init();
		setting();
		batch();
		listener();

		setVisible(true);

	}

	private void init() {
		btnConnect = new JButton("connect");
		btnSend = new JButton("send");
		tfHost = new JTextField("127.0.0.1", 20);
		tfChat = new JTextField(20);
		taChatList = new JTextArea(10, 30); // row,column
		scrollPane = new ScrollPane();
		topPanel = new JPanel();
		bottomPanel = new JPanel();
	}

	private void setting() {
		setTitle("ä�� �ٴ�� Ŭ���̾�Ʈ");
		setSize(400, 350);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		taChatList.setBackground(Color.ORANGE);
		taChatList.setForeground(Color.BLUE);
	}

	private void batch() {
		topPanel.add(tfHost);
		topPanel.add(btnConnect);
		bottomPanel.add(tfChat);
		bottomPanel.add(btnSend);
		scrollPane.add(taChatList);

		add(topPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	private void listener() {
		btnConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				connect();

			}
		});

		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				writer.println(Protocol.Exit);
				writer.flush();
			}
		});

	}

	private void send() {

		String chat = tfChat.getText();
		// 1�� taChatList �Ѹ���
		taChatList.append("[�� �޽���] " + chat + "\n"); // �߰�
		// 2�� ������ ����
		writer.println(chat);
		writer.flush();

		// 3�� tfchat ����
		tfChat.setText(null);

	}

	private void connect() {

		String host = tfHost.getText();
		try {

			taChatList.append("ALL: ġ�� ���� �Է��ϼ���.(�������� ����) " + "\n");
			socket = new Socket(host, PORT);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
			ReaderThread rt = new ReaderThread();
			rt.start();
			taChatList.append("������ �����Ͽ����ϴ�." + "\n");

		} catch (Exception e1) {
			System.out.println(TAG + "���� ���� ����" + e1.getMessage());
			taChatList.append("���� ���� ����" + "\n");
		}

	}

	class ReaderThread extends Thread {

		// while�� ���鼭 �����κ��� �޽����� �޾Ƽ� taChatList�� �Ѹ���
		@Override
		public void run() {
			String input = null;

			try {

				while ((input = reader.readLine()) != null) {
					taChatList.append(input + "\n");

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public static void main(String[] args) {

		new ChatClient();

	}
}
