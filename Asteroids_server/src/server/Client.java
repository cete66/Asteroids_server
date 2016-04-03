package server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

/**
 * Cliente del servidor, independientemente del origen.
 * @author Robin
 *
 */
class Client extends Thread implements MAP{
	
	private BufferedReader in;
	private PrintWriter out;
	private Socket client;
	private boolean done=false;
	private int ordenMapa;
	private Server server;
	
	/**SEGUIR POR cincoTokens 3/4/16 **/
	
	//TODO EL SPWN ES PARA AVISAR A SONIDO Y LUZ DE CAMBIO DE PANTALLA
	//TODO EL BEGIN = CUANDO CONECTE UN MODULO DE LUZ Y NO HABIAN Y HAY MAPA Y JUGADOR AVISAR, LO MISMO EL DE LUZ
	//TODO EL ENDG = CUANDO HABIA MAPA Y JUGADOR Y SE VAN TODOS. AVISAR A SONIDO Y LUZ
	// EL GAOV = LO RECIBO DEL MAPA LO PASO AL MANDO DE LA NAVE Y A SONIDO Y LUZ
	
	public Client (BufferedReader in, PrintWriter out, Socket client,Server server){
		this.ordenMapa=-1;
		this.in=in;
		this.out=out;
		this.client=client;
		this.server=server;
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
	private void procesarMsg(String msg, Client c) {
		
		//int msgFrom=0;
		String []code = msg.toUpperCase().split(MAP.CONCAT);
		if (code!=null){
			switch (code.length) {
			case 1:unToken(code[0],c);break;
			case 2:dosTokens(code,c);break;
			case 3:if (code[0].equals(MAP.GAME_EVENT)){gameEvent(msg,c);}else{tresTokens(code,c);}break;
			case 4:if (code[0].equals(MAP.GAME_EVENT)){gameEvent(msg,c);}else{cuatroTokens(code,c);}break;
			case 5:cincoTokens(code,c);break;
			default:System.err.println("UNHANDLED MSG >> "+msg);break;
			}
		}
		
	}
	private void gameEvent(String string, Client c) {

		Random rand = new Random();
		int r = rand.nextInt(Client.this.server.getMluces().size());
		if (r>0){
			--r;
		}
		Client.this.server.getMluces().get(r).getOut().println(string);
		
		r = rand.nextInt(Client.this.server.getMsonidos().size());
		if (r>0){
			--r;
		}
		Client.this.server.getMsonidos().get(r).getOut().println(string);
		rand=null;
		
		for (int i = 0; i < Client.this.server.getMpuntuaciones().size(); i++) {
			Client.this.server.getMpuntuaciones().get(i).getOut().println(string);
		}
		
	}
	private void dosTokens(String[] code, Client c) {

		switch (code[0]) {
		case MAP.DISCONNECT:discMando(code[1],c);break;
		case MAP.GAME_OVER:gaovMando(code[1],c);break;

		default:
			break;
		}
		
	}
	private void gaovMando(String string, Client c) {

		new Thread(new Runnable() {
			
			@Override
			public void run() {

				Random rand = new Random();
				int r = rand.nextInt(Client.this.server.getMluces().size());
				if (r>0){
					--r;
				}
				Client.this.server.getMluces().get(r).getOut().println(MAP.GAME_OVER+":"+string);
				
				r = rand.nextInt(Client.this.server.getMsonidos().size());
				if (r>0){
					--r;
				}
				Client.this.server.getMsonidos().get(r).getOut().println(MAP.GAME_OVER+":"+string);
				rand=null;
				
				for (int i = 0; i < Client.this.server.getMpuntuaciones().size(); i++) {
					Client.this.server.getMpuntuaciones().get(i).getOut().println(MAP.GAME_OVER+":"+string);
				}
				
			}
		}).start();
		
		
		try{
			long shId =descartarSH_(string);
			
			for (int i = 0; i < this.server.getMandos().size() && shId!=-1; i++) {
				if (this.server.getMandos().get(i).getId()==shId){
					c.getOut().println(MAP.GAME_OVER+":"+string);break;
					
				}
			}
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	private void discMando(String string, Client c) {
			
		try{
			for (int i = 0; i < this.server.getMandos().size(); i++) {
				if (this.server.getMandos().get(i).getId()==c.getId()){
					this.server.getMandos().remove(i);
					c.client.close();
					c.done=true;break;
					
				}
			}
			Random rand = new Random();
			int r = rand.nextInt(this.server.getMluces().size());
			if (r>0){
				--r;
			}
			this.server.getMluces().get(r).getOut().println(MAP.DISCONNECT+":"+string);
			
			r = rand.nextInt(this.server.getMsonidos().size());
			if (r>0){
				--r;
			}
			this.server.getMsonidos().get(r).getOut().println(MAP.DISCONNECT+":"+string);
			rand=null;
			
			for (int i = 0; i < this.server.getMpuntuaciones().size(); i++) {
				this.server.getMpuntuaciones().get(i).getOut().println(MAP.DISCONNECT+":"+string);
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
			
		
	}
	private void tresTokens(String[] code, Client c) {
		// TODO POR AHORA TRATADOR EN 'gameEvent' ya que solo hay geve en esta condicion

		System.err.println("UNHANDLED MSG ON  'tresTokens' method with code[0] = "+code[0]);

		
	}
	private void cuatroTokens(String[] code, Client c) {
		// TODO TODOS DE 4 SALVO EL GEVE DE 4 TOKENS
		
		switch (code[0]) {
		case MAP.KEY_EVENT:sendKeyEvent(code,c);break;
		case MAP.POINTS:notifChangesShip(code,c);break;
		case MAP.LIFES:notifChangesShip(code,c);break;

		default:
			break;
		}
		
	}
	private void notifChangesShip(String[] code, Client c) {
		
		String string = obtenerMsg(code);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {

				Random rand = new Random();
				int r = rand.nextInt(Client.this.server.getMluces().size());
				if (r>0){
					--r;
				}
				Client.this.server.getMluces().get(r).getOut().println(string);
				
				r = rand.nextInt(Client.this.server.getMsonidos().size());
				if (r>0){
					--r;
				}
				Client.this.server.getMsonidos().get(r).getOut().println(string);
				rand=null;
				
				for (int i = 0; i < Client.this.server.getMpuntuaciones().size(); i++) {
					Client.this.server.getMpuntuaciones().get(i).getOut().println(string);
				}
				
			}
		}).start();
		
		
		try{
			long shId =descartarSH_(code[1]);
			
			for (int i = 0; i < this.server.getMandos().size() && shId!=-1; i++) {
				if (this.server.getMandos().get(i).getId()==shId){
					c.getOut().println(string);break;
					
				}
			}
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	private void sendKeyEvent(String[] code, Client c) {
		String msg = obtenerMsg(code);
		long screenId = descartarSH_(code[1]);
		
		for (int i = 0; i < this.server.getMapas().size() && screenId!=-1; i++) {
			if (this.server.getMapas().get(i).getId()==screenId){
				this.server.getMapas().get(i).getOut().println(msg);break;
			}
		}
		
	}
	private long descartarSH_(String string) {
		
		String id = "";
		long shId =-1;
		try{
			id = string.substring(4);
			shId=Long.parseLong(id);
			
			
		}catch(Exception e){
			shId=-1;
			e.printStackTrace();
		}
		
		System.out.println("id returned: "+shId);
		
		return shId;
	}
	private String obtenerMsg(String[] code) {
		String s="";
		
		for (int i = 0; i < code.length; i++) {
			s=s+MAP.CONCAT+code[i];
		}
		System.out.println("code tokenizado: "+s);
		return s;
	}
	private void cincoTokens(String[] code, Client c) {
		// TODO Auto-generated method stub
		
	}
	private void unToken(String s, Client c) {
		
		switch (s) {
		case MAP.CONTROLER_MDL:newControler(c);break;
		case MAP.SCREEN_MDL:newScreen(c);break;
		case MAP.SCORE_MDL:newScoreScr(c);break;
		case MAP.SOUND_MDL:newSoundM(c);break;
		case MAP.LIGHT_MDL:newLightM(c);break;
		case MAP.PING:c.getOut().println(MAP.PING);break;
		case MAP.CLIENTE_DISC:descNoMando(c);break;
		default:
			break;
		}
		
	}
	private void descNoMando(Client c) {
		
		try {
			c.client.close();
			
			
			for (int i = 0; i < this.server.getAnonimo().size() && c!=null && c.done==false; i++) {
				if (this.server.getAnonimo().get(i).getId()==c.getId()){
					this.server.getAnonimo().remove(i);
					c.done=true;break;
				}
			}
			for (int i = 0; i < this.server.getMandos().size() && c!=null && c.done==false; i++) {
				if (this.server.getMandos().get(i).getId()==c.getId()){
					this.server.getMandos().remove(i);
					c.done=true;break;
				}
			}
			for (int i = 0; i < this.server.getMapas().size() && c!=null && c.done==false; i++) {
				if (this.server.getMapas().get(i).getId()==c.getId()){
					this.server.getMapas().remove(i);
					c.done=true;break;
				}			
			}
			for (int i = 0; i < this.server.getMluces().size() && c!=null && c.done==false; i++) {
				if (this.server.getMluces().get(i).getId()==c.getId()){
					this.server.getMluces().remove(i);
					c.done=true;break;
				}
			}
			for (int i = 0; i < this.server.getMpuntuaciones().size() && c!=null && c.done==false; i++) {
				if (this.server.getMpuntuaciones().get(i).getId()==c.getId()){
					this.server.getMpuntuaciones().remove(i);
					c.done=true;break;
				}
			}
			for (int i = 0; i < this.server.getMsonidos().size() && c!=null && c.done==false; i++) {
				if (this.server.getMsonidos().get(i).getId()==c.getId()){
					this.server.getMsonidos().remove(i);
					c.done=true;break;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private void newLightM(Client c) {
		removeAnonimous(c);
		
		this.server.getMluces().add(c);
		
		notifyClientId(c,MAP.LIGHT_MDL);
		
	}
	private void removeAnonimous(Client c) {
		for (int i=0;i<this.server.getAnonimo().size();i++){
			if (this.server.getAnonimo().get(i).getId()==c.getId()){

					this.server.getAnonimo().remove(i);
					break;

				
			}
		}
		
	}
	private void newSoundM(Client c) {
		removeAnonimous(c);
		
		this.server.getMsonidos().add(c);
		
		notifyClientId(c,MAP.SOUND_MDL);
	}
	private void newScoreScr(Client c) {
		removeAnonimous(c);
		
		this.server.getMpuntuaciones().add(c);
		
		notifyClientId(c,MAP.SCORE_MDL);
		
	}
	private void newScreen(Client c) {
		removeAnonimous(c);
		
		this.server.getMapas().add(c);
		
		notifyClientId(c,MAP.SCREEN_MDL);
		
	}
	private void newControler(Client c) {
		removeAnonimous(c);
		
		this.server.getMandos().add(c);
		
		notifyClientId(c,MAP.CONTROLER_MDL);
		
	}
	private void notifyClientId(Client c, String s) {
		
		c.getOut().println(s+MAP.CONCAT+"sh_#"+c.getId());
		
	}
	private void actualizarPanel() {
		// TODO Auto-generated method stub
		
	}
	private void cerrarSesion(Client cliente) {
		// TODO Auto-generated method stub
		
	}
	private void eliminarCliente(Client cliente) {
		// TODO Auto-generated method stub
		
	}
	protected BufferedReader getIn() {
		return in;
	}
	protected PrintWriter getOut() {
		return out;
	}
	protected Socket getClient() {
		return client;
	}
}