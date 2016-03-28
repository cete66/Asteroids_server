package server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * Cliente del servidor, independientemente del origen.
 * @author Robin
 *
 */
class Cliente extends Thread implements MAP{
	
	private BufferedReader in;
	private PrintWriter out;
	private Socket client;
	private boolean done=false;
	private int ordenMapa;
	
	public Cliente (BufferedReader in, PrintWriter out, Socket client){
		this.ordenMapa=-1;
		this.in=in;
		this.out=out;
		this.client=client;
	}
	@Override
	public void run() {
		done=false;
		String line=null;
		try {
			this.client.setSoTimeout(6000000);
		} catch (SocketException e1) {
			done=true;
			this.out.println("EXT");
			e1.printStackTrace();
		}
		while (done==false){
			try {
				System.out.println("TRATAR CLIENTE");
				if (in!=null){
					line=in.readLine();
					if (line!=null){
						line=line.trim();
						procesarMsg(line,this);

					}else{
						client.close();done=true;
					}
				}else{
					client.close();done=true;
				}
				if (client.isInputShutdown()){
					client.close();done=true;
				}
				if (!client.isConnected()){
					client.close();done=true;
				}
				

			} catch (Exception e) {
				e.printStackTrace();
				done=true;
				eliminarCliente(this);
				cerrarSesion(this);
				actualizarPanel();
			}
			
		}
		
	}
	private void procesarMsg(String line, Cliente cliente) {
		// TODO Auto-generated method stub
		
	}
	private void actualizarPanel() {
		// TODO Auto-generated method stub
		
	}
	private void cerrarSesion(Cliente cliente) {
		// TODO Auto-generated method stub
		
	}
	private void eliminarCliente(Cliente cliente) {
		// TODO Auto-generated method stub
		
	}
}