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
	//private int ordenMapa;
	private Server server;
	
	/**Pasar revision con los demas  - falta poner begin y endg - codigo corregido - 14/4/16 **/
	
	// EL SPWN ES PARA AVISAR A SONIDO Y LUZ DE CAMBIO DE PANTALLA
	//TODO EL BEGIN = CUANDO CONECTE UN MODULO DE LUZ Y NO HABIAN Y HAY MAPA Y JUGADOR AVISAR, LO MISMO EL DE LUZ
	//TODO EL ENDG = CUANDO HABIA MAPA Y JUGADOR Y SE VAN TODOS. AVISAR A SONIDO Y LUZ
	// EL GAOV = LO RECIBO DEL MAPA LO PASO AL MANDO DE LA NAVE Y A SONIDO Y LUZ
	
	public Client (BufferedReader in, PrintWriter out, Socket client,Server server){
		//this.ordenMapa=-1;
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
				//cerrarSesion(this);
				//actualizarPanel();
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
			case 5:if(code[0].equals(MAP.CHANGE_SCREEN)){changeScreen(code,c);}else{cincoTokens(code,c);}break;
			default:System.err.println("UNHANDLED MSG >> "+msg);break;
			}
		}
		
	}
	private void changeScreen(String[] code, Client cliente) {
		
		long idMando=-1; 
		boolean isMando=false;
		
		if (isMando(code[1])){ //ver si contiene sh_
			isMando=true;
			idMando=descartarSH_(code[1]);
		}
		long nuevaIdMapa=-1;
		String lado = code[2];
		String msg = obtenerMsg(code);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				avisarSpwn(code[1]);
				
			}
		}).start();
		
		if (!this.server.getMapas().isEmpty()){
			
			for (int i = 0; i < this.server.getMapas().size(); i++) {
				Client c = this.server.getMapas().get(i);
				if (lado.equals(MAP.RIGHT)){
					if (c.getId()==cliente.getId() && i<(this.server.getMapas().size()-1)){
						this.server.getMapas().get((i+1)).getOut().println(msg);nuevaIdMapa=this.server.getMapas().get((i+1)).getId();break;
					}else if (c.getId()==cliente.getId() && i>0){
						this.server.getMapas().get(0).getOut().println(msg);nuevaIdMapa=this.server.getMapas().get(0).getId();break;
					}
				}else if  (lado.equals(MAP.LEFT)){
					if (c.getId()==cliente.getId() && i>0){
						this.server.getMapas().get((i-1)).getOut().println(msg);nuevaIdMapa=this.server.getMapas().get((i-1)).getId();break;
					}else if (c.getId()==cliente.getId() && i<(this.server.getMapas().size()-1)){
						this.server.getMapas().get((this.server.getMapas().size()-1)).getOut().println(msg);nuevaIdMapa=this.server.getMapas().get((this.server.getMapas().size()-1)).getId();break;
					}
				}
			}
		}
		if (!this.server.getMandos().isEmpty() && nuevaIdMapa!=-1 && isMando==true){
			for (int i = 0; i < this.server.getMandos().size(); i++) {
				Client c = this.server.getMandos().get(i);
				if (c.getId()==idMando){
					c.getOut().println(MAP.CHANGE_SCREEN+MAP.CONCAT+MAP.SHIP+c.getId());break;
				}
			}
		}
		
	}
	protected void avisarSpwn(String tipo) {
		
		Random rand = new Random();
		int r = rand.nextInt(Client.this.server.getMluces().size());
		if (r>0){
			--r;
		}
		Client.this.server.getMluces().get(r).getOut().println(MAP.SPAWN+MAP.CONCAT+tipo);
		
		r = rand.nextInt(Client.this.server.getMsonidos().size());
		if (r>0){
			--r;
		}
		Client.this.server.getMsonidos().get(r).getOut().println(MAP.SPAWN+MAP.CONCAT+tipo);
		rand=null;
		
	}
	private boolean isMando(String string) {
		//sh_
		return string.substring(0, 3).equals(MAP.SHIP);
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
		case MAP.DISCONNECT:discMando(c);break;//MSG DE 2 TOKENS CON CABECERA == MAP.DISCONNECT -- SOLO LO RECIBO DE MANDOS
		case MAP.GAME_OVER:gaovMando(code,c);break;//MSG DE 2 TOKENS CON CABECERA == MAP.DISCONNECT -- quién me envia esto ??

		default:System.err.println("UNHANDLED MSG ON  'dosTokens' method with code[0] = "+code[0]);break;
		}
		
	}
	private void gaovMando(String[] code, Client c) {
		
		String s = obtenerMsg(code);

		new Thread(new Runnable() {
			
			@Override
			public void run() {

				Random rand = new Random();
				int r = rand.nextInt(Client.this.server.getMluces().size());
				if (r>0){
					--r;
				}
				Client.this.server.getMluces().get(r).getOut().println(s);
				
				r = rand.nextInt(Client.this.server.getMsonidos().size());
				if (r>0){
					--r;
				}
				Client.this.server.getMsonidos().get(r).getOut().println(s);
				rand=null;
				
				for (int i = 0; i < Client.this.server.getMpuntuaciones().size(); i++) {
					Client.this.server.getMpuntuaciones().get(i).getOut().println(s);
				}
				
			}
		}).start();
		
		
		try{
			long shId =descartarSH_(code[1]);
			
			for (int i = 0; i < this.server.getMandos().size() && shId!=-1; i++) {
				if (this.server.getMandos().get(i).getId()==shId){
					c.getOut().println(MAP.GAME_OVER+MAP.SHIP+shId);
					try{
						//discMando(c);
						eliminarCliente(c);
					}catch(Exception e){
						e.printStackTrace();
					}
					break;
					
				}
			}
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	private void discMando(Client c) {
			
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
			this.server.getMluces().get(r).getOut().println(MAP.DISCONNECT+MAP.CONCAT+MAP.SHIP);
			
			r = rand.nextInt(this.server.getMsonidos().size());
			if (r>0){
				--r;
			}
			this.server.getMsonidos().get(r).getOut().println(MAP.DISCONNECT+MAP.CONCAT+MAP.SHIP);
			rand=null;
			
			for (int i = 0; i < this.server.getMpuntuaciones().size(); i++) {
				this.server.getMpuntuaciones().get(i).getOut().println(MAP.DISCONNECT+MAP.CONCAT+MAP.SHIP+c.getId());
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

		default:System.err.println("UNHANDLED MSG ON  'cuatroTokens' method with code[0] = "+code[0]);break;
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
		long screenId = /*descartarSH_(code[1]);*/Long.parseLong(code[1]);
		
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
		s=code[0];
		if (code.length>1){
			
			for (int i = 1; i < code.length; i++) {
				s=s+MAP.CONCAT+code[i];
			}
		}
		
		System.out.println("code tokenizado: "+s);
		return s;
	}
	private void cincoTokens(String[] code, Client c) {
		// TODO POR AHORA SON SOLO TODOS LOS DE CHSC
		System.err.println("UNHANDLED MSG ON  'cincoTokens' method with code[0] = "+code[0]);
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
		default:System.err.println("UNHANDLED MSG ON  'unToken' method with msg = "+s);break;
		}
		
	}
	private void descNoMando(Client c) {
		
		try {
			
			
			
			for (int i = 0; i < this.server.getAnonimo().size() && c!=null && c.done==false; i++) {
				if (this.server.getAnonimo().get(i).getId()==c.getId()){
					this.server.getAnonimo().remove(i);
					c.client.close();
					c.done=true;break;
				}
			}
			for (int i = 0; i < this.server.getMapas().size() && c!=null && c.done==false; i++) {
				if (this.server.getMapas().get(i).getId()==c.getId()){
					this.server.getMapas().remove(i);
					c.client.close();
					c.done=true;break;
				}			
			}
			for (int i = 0; i < this.server.getMluces().size() && c!=null && c.done==false; i++) {
				if (this.server.getMluces().get(i).getId()==c.getId()){
					this.server.getMluces().remove(i);
					c.client.close();
					c.done=true;break;
				}
			}
			for (int i = 0; i < this.server.getMpuntuaciones().size() && c!=null && c.done==false; i++) {
				if (this.server.getMpuntuaciones().get(i).getId()==c.getId()){
					this.server.getMpuntuaciones().remove(i);
					c.client.close();
					c.done=true;break;
				}
			}
			for (int i = 0; i < this.server.getMsonidos().size() && c!=null && c.done==false; i++) {
				if (this.server.getMsonidos().get(i).getId()==c.getId()){
					this.server.getMsonidos().remove(i);
					c.client.close();
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
		
		c.getOut().println(MAP.CONTROLER_MDL+MAP.CONCAT+MAP.SHIP+c.getId());
		
	}
	private void notifyClientId(Client c, String s) {
		
		c.getOut().println(s+MAP.CONCAT+c.getId());
		
	}
	private void actualizarPanel() {
		
		
	}
	private void cerrarSesion(Client cliente) {
		
		
	}
	private void eliminarCliente(Client c) {
		
		try {
			
			
			for (int i = 0; i < this.server.getAnonimo().size() && c!=null && c.done==false; i++) {
				if (this.server.getAnonimo().get(i).getId()==c.getId()){
					this.server.getAnonimo().remove(i);
					c.client.close();
					c.done=true;break;
				}
			}
			for (int i = 0; i < this.server.getMandos().size() && c!=null && c.done==false; i++) {
				if (this.server.getMandos().get(i).getId()==c.getId()){
					this.server.getMandos().remove(i);
					c.client.close();
					c.done=true;break;
				}
			}
			for (int i = 0; i < this.server.getMapas().size() && c!=null && c.done==false; i++) {
				if (this.server.getMapas().get(i).getId()==c.getId()){
					this.server.getMapas().remove(i);
					c.client.close();
					c.done=true;break;
				}			
			}
			for (int i = 0; i < this.server.getMluces().size() && c!=null && c.done==false; i++) {
				if (this.server.getMluces().get(i).getId()==c.getId()){
					this.server.getMluces().remove(i);
					c.client.close();
					c.done=true;break;
				}
			}
			for (int i = 0; i < this.server.getMpuntuaciones().size() && c!=null && c.done==false; i++) {
				if (this.server.getMpuntuaciones().get(i).getId()==c.getId()){
					this.server.getMpuntuaciones().remove(i);
					c.client.close();
					c.done=true;break;
				}
			}
			for (int i = 0; i < this.server.getMsonidos().size() && c!=null && c.done==false; i++) {
				if (this.server.getMsonidos().get(i).getId()==c.getId()){
					this.server.getMsonidos().remove(i);
					c.client.close();
					c.done=true;break;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
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