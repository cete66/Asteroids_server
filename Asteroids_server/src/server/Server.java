package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
	private volatile Stack<Cliente> mapas;
	private volatile Stack<Cliente> mandos;
	private volatile Stack<Cliente> Mluces;
	private volatile Stack<Cliente> Msonidos;
	private volatile Stack<Cliente> anonimo;
	private boolean active;
	private volatile Stack<Cliente> Mpuntuaciones;
	private ControlPanel panel;
	
	
	private Server (int port, ControlPanel panel){
		this.port=port;
		this.panel=panel;
		this.active=true;
		mapas = new Stack<>();
		mandos = new Stack<>();
		Mluces = new Stack<>();
		Msonidos = new Stack<>();
		Mpuntuaciones = new Stack<>();
	}
	
	public void run (){
		
		try {
			Server.Sserver = new ServerSocket(this.port);
		} catch (Exception e) {
			cambiarPuerto();
			e.printStackTrace();
		}
		while (this.isActive()) {
			
			try {
				Socket c = Sserver.accept();
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
		
	}
	
	private void cambiarPuerto() {
		
		boolean h = false;
		Random r = new Random();
		int i=0;
		try{
			while (h==false){
				Thread.sleep(6);
				i = r.nextInt(65500);
				i=i+1;
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
			h = false;
			e.printStackTrace();
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

	protected static ServerSocket getSserver() {
		return Sserver;
	}

	protected Stack<Cliente> getMapas() {
		return mapas;
	}

	protected Stack<Cliente> getMandos() {
		return mandos;
	}

	protected Stack<Cliente> getMluces() {
		return Mluces;
	}

	protected Stack<Cliente> getMsonidos() {
		return Msonidos;
	}

	protected Stack<Cliente> getAnonimo() {
		return anonimo;
	}

	protected Stack<Cliente> getMpuntuaciones() {
		return Mpuntuaciones;
	}

	protected ControlPanel getPanel() {
		return panel;
	}
}
