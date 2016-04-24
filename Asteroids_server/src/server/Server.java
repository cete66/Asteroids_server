package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Stack;

/**
 * Servidor modular multicliente concurrente
 * @author Robin
 *
 */
class Server extends Thread implements MAP{

	private int port;
	private static ServerSocket Sserver;
	private volatile Stack<Client> mapas;
	private volatile Stack<Client> mandos;
	private volatile Stack<Client> Mluces;
	private volatile Stack<Client> Msonidos;
	private volatile Stack<Client> anonimo;
	private Thread listener;
	private DatagramSocket bcListener;
	private Thread server;
	private boolean active;
	private volatile Stack<Client> Mpuntuaciones;
	private volatile Stack<Driver> drivers;
	
	
	protected Server (int port){
		this.port=port;
		this.active=true;
		mapas = new Stack<>();
		mandos = new Stack<>();
		Mluces = new Stack<>();
		Msonidos = new Stack<>();
		Mpuntuaciones = new Stack<>();
		drivers = new Stack<>();
	}
	
	public void run (){
		this.setActive(true);
		initServer();
		initListener();
		
		
		
	}
	
	private void initServer() {
		
		try {
			Server.Sserver = new ServerSocket(this.port);
		} catch (Exception e) {
			cambiarPuerto();
			e.printStackTrace();
		}
		this.server = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				
				while (Server.this.isActive()){
					try{
						 Socket client;
						 System.out.println("Waiting for a client...");
						 client = Sserver.accept( );
						 System.out.println("Client connection from " +
								 client.getInetAddress( ).getHostAddress( ) );
						 BufferedReader in = new BufferedReader( new InputStreamReader(
								 client.getInputStream( ) ) );
								 PrintWriter out = new PrintWriter( client.getOutputStream( ), true );
						 tratarClient(in,out,client);
						 
					}catch(Exception e){
						//System.exit(1);
						active=false;
						closeServer();
						System.exit(0);
						e.printStackTrace();
					}
				}
				
			}
		});this.server.start();
	}

	protected void tratarClient(BufferedReader in, PrintWriter out, Socket client) {
		Client c = new Client(in, out, client,this);c.start();this.anonimo.add(c);
		
	}

	protected void closeServer() {
		// TODO Auto-generated method stub
		
	}

	private void initListener() {
		
		this.listener = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Server.this.bcListener = new DatagramSocket(Server.this.port, InetAddress.getByName("0.0.0.0"));
					Server.this.bcListener.setBroadcast(true);
					while (Server.this.isActive()){
						System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets! I'm on port "+Server.this.port);
						//Receive a packet
						
						        byte[] recvBuf = new byte[15000];
						
						        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
						
						        try {
									Server.this.bcListener.receive(packet);
								} catch (IOException e1) {
									e1.printStackTrace();
								}

						        //Packet received
						
						        System.out.println(getClass().getName() + ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
						
						        System.out.println(getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));
						 
						
						        //See if the packet holds the right command (message)
						
						        String message = new String(packet.getData()).trim();
						
						        if (message.equals(MAP.BROADCAST_SEARCH)) {
						        	System.out.println(MAP.BROADCAST_SEARCH);
						        	byte[] sendData = MAP.BROADCAST_RESPONSE.getBytes();
						
						          //Send a response
						
						          DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
					
						          try {
									Server.this.bcListener.send(sendPacket);
								} catch (IOException e) {
									e.printStackTrace();
								}
						          System.out.println(getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
						        }else{
						        	System.out.println(message);
						        }
						
						          

					}
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				
				
			}
		});this.listener.start();
	}

	private void cambiarPuerto() {
		
		boolean h = false;
		Random r = new Random();
		int i=0;
		while (this.active==false){
			try{
				while (h==false){
					Thread.sleep(6);
					i = r.nextInt(65500);
					++i;
					Sserver = new ServerSocket(i);
					if (Sserver!=null && Sserver.getLocalPort()==i){
						this.port=i;
						this.active=true;
						h=true;
					}else{
						this.active=false;
						h=false;
					}
					
				}
			}catch(Exception e){
				this.active=false;
				h = false;
				e.printStackTrace();
			}
		}
		
		
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return null;
	}

	protected boolean isActive() {
		return active;
	}

	protected void setActive(boolean active) {
		this.active = active;
	}

	protected int getPort() {
		return port;
	}

	protected ServerSocket getSserver() {
		return Sserver;
	}

	protected Stack<Client> getMapas() {
		return mapas;
	}

	protected Stack<Client> getMandos() {
		return mandos;
	}

	protected Stack<Client> getMluces() {
		return Mluces;
	}

	protected Stack<Client> getMsonidos() {
		return Msonidos;
	}

	protected Stack<Client> getAnonimo() {
		return anonimo;
	}

	protected Stack<Client> getMpuntuaciones() {
		return Mpuntuaciones;
	}

	protected Stack<Driver> getDrivers() {
		return drivers;
	}

}
