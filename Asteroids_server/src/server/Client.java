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
	private Server server;
	
	/** Test - Alpha 1.0 - 24/4/16 **/
	
	public Client (BufferedReader in, PrintWriter out, Socket client,Server server){
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
			
			if(code[0].equals(MAP.CHANGE_SCREEN)){
				changeScreen(code,c);
				
			}else if (code[0].equals(MAP.CONTROLER_MDL)){
				newControler(code,c);
				adviseOfNewDriver(c);
				
			}else if (code[0].equals(MAP.GAME_EVENT)){
				gameEvent(code,c);
				
			}else{
				switch (code.length) {
				case 1:unToken(code[0],c);break;
				case 2:dosTokens(code,c);break;
				case 3:tresTokens(code,c);break;
				case 4:cuatroTokens(code,c);break;
				case 5:cincoTokens(code,c);break;
				default:System.err.println("UNHANDLED MSG >> "+msg);break;
				}
			}
			
		}else{
			System.err.println("UNHANDLED code[0] in MSG >> "+msg);
		}
		
	}
	private void changeScreen(String[] code, Client mapa) {
		
		long idMando=-1; 
		boolean isMando=false;
		try{
			
			if (isMando(code[1])){ //ver si contiene sh_
				isMando=true;
				idMando=descartarSH_(code[1]);
			}
			long nuevaIdMapa=-1;
			String lado = code[2];
			String msg = obtenerMsg(code);
			
			if (isMando ==true && !this.server.getMapas().isEmpty()){
				
				for (int i = 0; i < this.server.getMapas().size(); i++) {
					Client c = this.server.getMapas().get(i);
					if (lado.equals(MAP.RIGHT)){
						if (c.getId()==mapa.getId() && i<(this.server.getMapas().size()-1)){
							this.server.getMapas().get((i+1)).getOut().println(msg);nuevaIdMapa=this.server.getMapas().get((i+1)).getId();break;
						}else if (c.getId()==mapa.getId() && i>0){
							this.server.getMapas().get(0).getOut().println(msg);nuevaIdMapa=this.server.getMapas().get(0).getId();break;
						}
					}else if  (lado.equals(MAP.LEFT)){
						if (c.getId()==mapa.getId() && i>0){
							this.server.getMapas().get((i-1)).getOut().println(msg);nuevaIdMapa=this.server.getMapas().get((i-1)).getId();break;
						}else if (c.getId()==mapa.getId() && i<(this.server.getMapas().size()-1)){
							this.server.getMapas().get((this.server.getMapas().size()-1)).getOut().println(msg);nuevaIdMapa=this.server.getMapas().get((this.server.getMapas().size()-1)).getId();break;
						}
					}
				}
				Driver d = obtenerDriver(idMando);
				d.setIdMapa(nuevaIdMapa);
				notifChsc();
			}
			

		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	private void notifChsc() {
		try{
			String msg = MAP.CHANGE_SCREEN;
			Random rand = new Random();
			int r = rand.nextInt(Client.this.server.getMluces().size());
			if (r>0){
				--r;
			}
			Client.this.server.getMluces().get(r).getOut().println(msg);
			
			r = rand.nextInt(Client.this.server.getMsonidos().size());
			if (r>0){
				--r;
			}
			Client.this.server.getMsonidos().get(r).getOut().println(msg);
			rand=null;
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	protected void avisarSpwn(String tipo) {
		
		try{
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
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	private boolean isMando(String string) throws Exception{
		//sh_
		return string.substring(0, 3).equals(MAP.SHIP);
	}
	private void gameEvent(String [] code, Client c) {

		new Thread(new Runnable() {
			public void run() {
				String s = obtenerMsg(code);
				try{
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
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
		
		
		switch (code[1]) {
		case MAP.KILLING:geve_kill(code,c);break;
		case MAP.SHOOT:geve_shoot(code,c);break;
		case MAP.HYPERSPACE:geve_hypr(code,c);break;
		default:System.out.println("UNHANDLED GAME_EVENT WITH LENGTH "+code.length+" AND CODE[1] = "+code[1]);break;
		}
		
		
	}
	private void geve_kill(String[] code, Client c) {

		//c == pantalla 
		String killer = code[2];
		String victim = "";
		if (code.length==4){
			victim = code[3];
		}
		
		long idMandoKiller=0,idMandoVictim=0;
		Driver driverKiller=null,driverVictim=null;
		
		try{
			
			if (isMando(killer)){
				idMandoKiller = descartarSH_(killer);
				driverKiller = obtenerDriver(idMandoKiller);
				if (driverKiller!=null){
					int add = obtenerPuntos(code[3]);
					driverKiller.setPoints(driverKiller.getPoints()+ add);
					Client cl = obtenerClient(idMandoKiller);
					notifChangesShip(new String [] {MAP.POINTS+MAP.CONCAT+MAP.SHIP+cl.getId()+MAP.CONCAT+MAP.ADD+add}, cl);//Cambio de puntos
				}
			}
			if (!victim.equals("") && isMando(victim)){
				idMandoVictim = descartarSH_(victim);
				driverVictim = obtenerDriver(idMandoVictim);
				if (driverVictim!=null){
					Client cl = obtenerClient(idMandoVictim);
					if (driverVictim.getLifes()>0){
						driverVictim.setLifes(driverVictim.getLifes()-1);
						notifChangesShip(new String [] {MAP.LIFES+MAP.CONCAT+MAP.SHIP+cl.getId()+MAP.CONCAT+MAP.REMOVE+1}, cl);//Cambio de vidas
						spawnShip(c,obtenerClient(idMandoVictim));// LE QUEDAN VIDAS. AL MAPA LE DIGO QUÉ ID DE NAVE SPAWN
						
					}else{
						notifGaov(new String []{MAP.GAME_OVER+MAP.CONCAT+MAP.SHIP+cl.getId()},cl);//Avisar de un mando -> game over
					}
				}
				
				
			}else if (!victim.equals("")){
				spawnObject(c,code[3]);// AVISAR SPAWN DE AST O UFO
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		
	}
	private void spawnObject(Client mapa, String tipo) {
		
		mapa.getOut().print(MAP.SPAWN+MAP.CONCAT+tipo);
		
	}
	private void notifGaov(String[] msg, Client mando) {
		String s = obtenerMsg(msg);

		new Thread(new Runnable() {
			
			@Override
			public void run() {

				try{
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
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
			}
		}).start();
		
		try{
			long idMapa = obtenerDriver(mando.getId()).getIdMapa();
			mando.getOut().println(s);
			for (int i=0;i<this.server.getMapas().size();i++){
				if (this.server.getMapas().get(i).getId()==idMapa){
					this.server.getMapas().get(i).getOut().println(s);break;
				}
			}
			discMando(mando);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	/*private void gaovMando(Client mapa, Client mandoConex, Driver mandoDatos) {
		
		
		
	}*/
	private void spawnShip(Client mapa, Client mando) {

		long idMapa = obtenerDriver(mando.getId()).getIdMapa();
		String msg = MAP.SPAWN+MAP.CONCAT+MAP.SHIP+mando.getId();
		for (int i=0;i<this.server.getMapas().size();i++){
			if (this.server.getMapas().get(i).getId()==idMapa){
				this.server.getMapas().get(i).getOut().println(msg);break;
			}
		}
		
	}
	private Client obtenerClient(long idMapa) {
		Client client = null;
		
		for(Client c : this.server.getMapas()){
			if (c.getId()==idMapa){
				client=c;break;
			}
		}
		
		return client;
	}
	private int obtenerPuntos(String code3) {
		int cant=0;
		try{
			if (isMando(code3)){
				cant=MAP.KILL_SHIP;
			}else{
				switch (code3) {
				case MAP.ASTEROID+1:cant=MAP.KILL_AST_G;break;
				case MAP.ASTEROID+2:cant=MAP.KILL_AST_M;break;
				case MAP.ASTEROID+3:cant=MAP.KILL_AST_P;break;
				case MAP.UFO:cant=MAP.KILL_UFO;break;
				default:System.out.println("UNHANDLED CODE[3] UNKWON TYPE OF OBJECT: "+code3);break;
				}
			}
		}catch(Exception e){
			cant=-1;
			e.printStackTrace();
		}
		
		
		
		
		return cant;
	}
	private Driver obtenerDriver(long idMando) {
		Driver d = null;
		
		for (int i=0;i<this.server.getDrivers().size();i++){
			if (this.server.getDrivers().get(i).getClient().getId()==idMando){
				d = this.server.getDrivers().get(i);break;
			}
		}
		return d;
	}
	private void geve_shoot(String[] code, Client c) {
		// YA ESTA NOTIFICADO EN GAME_EVENT
		
	}
	private void geve_hypr(String[] code, Client c) {
		// YA ESTA NOTIFICADO EN GAME_EVENT
		
	}
	private void dosTokens(String[] code, Client c) {

		switch (code[0]) {
		case MAP.DISCONNECT:discMando(c);break;//MSG DE 2 TOKENS CON CABECERA == MAP.DISCONNECT -- SOLO LO RECIBO DE MANDOS
		default:System.err.println("UNHANDLED MSG ON  'dosTokens' method with code[0] = "+code[0]);break;
		}
		
	}
	/*private void gaovMando(String[] code, Client c) {
		
		String s = obtenerMsg(code);

		new Thread(new Runnable() {
			
			@Override
			public void run() {

				try{
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
				}catch(Exception e){
					e.printStackTrace();
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
		
	}*/
	private void discMando(Client c) {
			
		try{
			for (int i = 0; i < this.server.getMandos().size(); i++) {
				if (this.server.getMandos().get(i).getId()==c.getId()){
					Driver d = obtenerDriver(this.server.getMandos().get(i).getId());
					d=null;
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
		// POR AHORA TRATADO EN 'gameEvent' ya que solo hay geve en esta condicion
		
		switch (code[0]) {
		case MAP.KEY_EVENT:sendKeyEvent(code,c);break;
		default:System.err.println("UNHANDLED MSG ON  'tresTokens' method with code[0] = "+code[0]);break;
		}	
	}
	private void cuatroTokens(String[] code, Client c) {
		// TODOS DE 4 SALVO EL GEVE DE 4 TOKENS
		
		switch (code[0]) {
		default:System.err.println("UNHANDLED MSG ON  'cuatroTokens' method with code[0] = "+code[0]);break;
		}
	}
	private void notifChangesShip(String[] code, Client c) {
		
		String string = obtenerMsg(code);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {

				try{
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
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
		
		
		try{
			c.getOut().println(string);
			long shId =obtenerDriver(c.getId()).getIdMapa();
			
			for (int i = 0; i < this.server.getMandos().size() && shId!=-1; i++) {
				if (this.server.getMandos().get(i).getId()==shId){
					c.getOut().println(string);break;	
				}
			}		
		}catch(Exception e){
			e.printStackTrace();
		}	
	}
	/**
	 * Falta la id del mapa 
	 * @param code
	 * @param c
	 */
	private void sendKeyEvent(String[] code, Client c) {
		//String msg = obtenerMsg(code);
		try{
			long screenId = obtenerDriver(c.getId()).getIdMapa();
			
			for (int i = 0; i < this.server.getMapas().size() && screenId!=-1; i++) {
				if (this.server.getMapas().get(i).getId()==screenId){
					this.server.getMapas().get(i).getOut().println(code[0]+MAP.CONCAT+MAP.SHIP+c.getId()+MAP.CONCAT+code[1]+MAP.CONCAT+code[2]);break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private long descartarSH_(String string) throws Exception{
		
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
		// POR AHORA SON SOLO TODOS LOS DE CHSC
		switch (code[0]) {
		default:System.err.println("UNHANDLED MSG ON  'cincoTokens' method with code[0] = "+code[0]);break;
		}
		
	}
	private void unToken(String s, Client c) {
		
		switch (s) {
		case MAP.SCREEN_MDL:newScreen(c);break;
		case MAP.SCORE_MDL:newScoreScr(c);updateRecordsModules(c);break;
		case MAP.SOUND_MDL:newSoundM(c);break;
		case MAP.LIGHT_MDL:newLightM(c);break;
		case MAP.PING:c.getOut().println(MAP.PING);break;
		case MAP.CLIENTE_DISC:descNoMando(c);break;
		default:System.err.println("UNHANDLED MSG ON  'unToken' method with msg = "+s);break;
		}
		
	}
	/**
	 * a luz, sonido y datos
	 * @param c
	 */
	private void adviseOfNewDriver(Client c) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {

				try{
					Driver d = obtenerDriver(c.getId());
					Random rand = new Random();
					int r = rand.nextInt(Client.this.server.getMluces().size());
					if (r>0){
						--r;
					}
					Client.this.server.getMluces().get(r).getOut().println(MAP.CREATE);
					
					r = rand.nextInt(Client.this.server.getMsonidos().size());
					if (r>0){
						--r;
					}
					Client.this.server.getMsonidos().get(r).getOut().println(MAP.CREATE);
					rand=null;
					
					for (int i = 0; i < Client.this.server.getMpuntuaciones().size(); i++) {
						Client.this.server.getMpuntuaciones().get(i).getOut().println(MAP.CREATE+MAP.CONCAT+MAP.SHIP+c.getId()+d.getName()+d.getColor().getRGB());
						/*for (int j=0;j<Client.this.server.getDrivers().size();j++){
							Client.this.server.getMpuntuaciones().get(i).getOut().println(MAP.CREATE+MAP.CONCAT+Client.this.server.getDrivers().get(j).toString());
						}*/
						
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
			}
		}).start();
		
	}
	/**
	 * Cada vez que se conecte un Mpuntuaciones
	 * @param c 
	 */
	private void updateRecordsModules(Client c){
		
		String boundedDrivers ="";
		final String head=MAP.CREATE+MAP.CONCAT;
		boolean firstTime=true;
		
		if (!this.server.getDrivers().isEmpty()){
			
			boundedDrivers=head+this.server.getDrivers().get(0).toString();
			
			if (this.server.getDrivers().size()>1){
				//for (int i=0;i<this.server.getMpuntuaciones().size();i++){
					for (int j=0;j<this.server.getDrivers().size();j++){
						if (firstTime==true){
							++j;firstTime=false;
						}
						if (boundedDrivers.equals("")){
							boundedDrivers+=head+this.server.getDrivers().get(j).toString();
						}else{
							boundedDrivers+=MAP.CONCAT_COMMANDS+head+this.server.getDrivers().get(j).toString();
						}
						
					}
					//this.server.getMpuntuaciones().get(i).getOut().println(boundedDrivers);boundedDrivers="";
					c.getOut().println(boundedDrivers);
				//}
				
			}else{/**SI SOLO HAY UN MANDO**/
				//for (int i=0;i<this.server.getMpuntuaciones().size();i++){
					//this.server.getMpuntuaciones().get(i).getOut().println(boundedDrivers);
				//}
				c.getOut().println(boundedDrivers);
			}
			
			
		}
		
	}
	/*private boolean isNewClient(String s) {
		return s.equals(MAP.CONTROLER_MDL);
	}*/
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
		
		notifyClientId(c);
		
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
		
		notifyClientId(c);
	}
	private void newScoreScr(Client c) {
		removeAnonimous(c);
		
		this.server.getMpuntuaciones().add(c);
		
		notifyClientId(c);
		
	}
	private void newScreen(Client c) {
		removeAnonimous(c);
		
		this.server.getMapas().add(c);
		
		notifyClientId(c);
		
	}
	private void newControler(String[] code, Client c) {
		removeAnonimous(c);
		
		this.server.getMandos().add(c);
		this.server.getDrivers().add(new Driver(c,code[1],code[2]));
		
		notifyClientId(c);
		
	}
	private void notifyClientId(Client c) {	
		c.getOut().println(MAP.BEGIN);
	}
	/*private void cerrarSesion(Client cliente) {
		
		
	}*/
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
