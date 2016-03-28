package server;

import java.net.ServerSocket;
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
	private volatile Stack<Cliente> Mpuntuaciones;
	private ControlPanel panel;
	
	
	private Server (int port, ControlPanel panel){
		this.port=port;
		this.panel=panel;
		mapas = new Stack<>();
		mandos = new Stack<>();
		Mluces = new Stack<>();
		Msonidos = new Stack<>();
		Mpuntuaciones = new Stack<>();
	}
	
	public void run (){
		
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return null;
	}
}
