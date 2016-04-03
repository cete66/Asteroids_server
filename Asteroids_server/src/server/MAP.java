package server;

/**
 * Multiplayer Asteroids Protocol
 * @author Robin
 * {@literal // event de tecla (pitjada o amollada)
	   // ja no s'utilitzaran, enviar 0 si release i 1 si pressed; intensitat segueix igual
	   public static final String PRESSED = "prsd";
	   public static final String RELEASED = "rlsd";}
 * 
 *
 */
interface MAP {
	
	/**
	 * Envia / recibe periodicamente
	 * usat per comprovar possibles desconnexions del servidor
	   // respondre amb un altre ping. No s'ha de fer res, es per evitar timeouts
	   // si no s'envies res durant cert periode de temps, per comprovar que la connexio segueix activa
	 */
	public static final String PING="ping";
	/**
	 * broadcast port
	 */
	public static final int BROAD_PORT = 36560;

	/**
	 * server port
	 */
	public static final int PORT = 36555;
	
	   /** per concatenejar comandos diferents (encara unused, pot ser que usat en el futur)
	   // exemple: keye:[*id]:powr:(*intensitat)&keye:[*id]:righ:(*intensitat)*/
	   public static final String CONCAT_COMMANDS = "&";

	   /**
	   // quan es connecta un client d'algun tipus envia aquest string*/
	   public static final String CONTROLER_MDL = "contmdl"; //"controller module" etc
	   public static final String SCREEN_MDL = "screenmdl";
	   public static final String SCORE_MDL = "scoremdl";
	   public static final String SOUND_MDL = "soundmdl";
	   public static final String LIGHT_MDL = "lightmdl";

	   /**
	   // events teclat*/
	   public static final String KEY_EVENT = "key";
	   public static final String UP = "up";
	   public static final String RIGHT = "righ";
	   public static final String LEFT = "left";
	   public static final String SHOOT = "shot";
	   public static final String HYPERSPACE = "hypr";

	   /**
	   
	   // exemple d'us: key:[*id]:up:prsd:(*intensitat)
	   // intensitats: 1 max, 0 min. S'usa a tecles powr, left, righ. No és necessari enviar a un event de release.

	   // usat per crear una nova nau al connectar-se
	   // o ho envia servidor per crear un ufo / asteroide
	   // crea:[*id]:*nom:*color
	   // crea:ast1
	   // crea:ufo*/
	   public static final String CREATE = "crea";

	   /**
	   // per desconnectar una nau o qualsevol client. Ús: disc:[*id]*/
	   public static final String DISCONNECT = "disc";


	   /**
	   // quan una nau es queda sense vides (servidor -> clients)
	   // gaov:sh_# per una nau*/
	   public static final String GAME_OVER = "gaov";

	   /**
	   // quan acaba la partida per a tots*/
	   public static final String END_GAME = "endg";

	   /**
	   // quan ha d'aparèixer una nau perquè encara té vides
	   // (servidor -> clients) spwn:sh_#*/
	   public static final String SPAWN = "spwn";


	   /**
	   // Servidor assigna els punts que tengui que assignar mitjançant comando kill
	   // i envia canvis a marcador mitjançant aquests dos
	   // pnts:*id:add|rem|set:*+-quantitat*/
	   public static final String POINTS = "pnts";
	   public static final String LIFES = "life"; // life:sh_#:+-=quantitat

	   public static final String ADD = "add";
	   public static final String REMOVE = "rem";
	   public static final String SET = "set";
	   
	   /**
	    * "crea","sh_#"
	    */
	   public static final String [] SONIDO_CREAR ={"crea","sh_#"};
	   
	   public static final String [] SONIDO_DISPARO = {"geve","shot"};
	   
	   public static final String [] SONIDO_EXP_GRAN = {"geve","kill","*algo","ast1"};
	   
	   public static final String [] SONIDO_EXP_MEDI = {"geve","kill","*algo","ast2"};
	   
	   public static final String [] SONIDO_EXP_PEQ = {"geve","kill","*algo","ast3"};
	   
	   public static final String [] SONIDO_UFO_ACTIVO = {"crea","ufo"};
	   
	   public static final String [] SONIDO_UFO_MUERE = {"geve","kill","*algo","ufo"};
	   
	   public static final String [] SONIDO_UFO_SALE = {"geve","out","ufo"};
	   
	   public static final String [] SONIDO_NAVE_0VIDA = {"geve","gaov","sh_#"};
	   
	   public static final String [] SONIDO_DESC_JUGADOR = {"disc","sh_#"};
	   
	   public static final String SONIDO_BEGIN_GAME = "begin";
	   
	   public static final String SONIDO_END_GAME = "endg";



	   /**
	   // usat per indicar events que formin part del joc propiament dit
	   // es a dir, quan en el joc es donin dispars, kills, hiperespai... s'envia de SCREEN_MDL a server.*/
	   public static final String GAME_EVENT = "geve";

	   /**
	   // comando de doble efecte, senyala que o qui mata a que o qui,
	   // servidor ha de tractar si hi ha variacions a vides o puntuacions
	   // format: geve:kill:*emisor:*receptor
	   // tant emisor com receptor poden ser o jugadors o objectes, en cas de ship sh_id
	   // en base a aixo, fer totes les opcions necessaries al servidor*/
	   public static final String KILLING = "kill";
	   
	   /**
	   // events de dispar i hiperespai reutilitzar strings SHOOT i HYPERSPACE

	   // quan es genera un ufo o asteroide


	   // comandos propiament multipantalla ///////////////////////

	   // usat per quan un element canvia de pantalla*/
	   public static final String CHANGE_SCREEN = "chsc";


	   /**
	   
	   asteroide: que necessitam guardar
	   - direccio cap on surt
	   - localitzacio de Y
	   - vector moviment
	   - angle direccio (tot i que es pot calcular a partir de moviment, es lineal)
	   - stage o mida, diguem
	   - punts que el formen
	   - rotacio actual propia de l'asteroide
	    
	   // chsc:ast#:righ:*locY:*movX:*movY:*dir:(*rotacio):(pts{pt,pt,pt,pt}) -> coordenades
	   // dels punts definits a la part visual, ja que cada asteroide es diferent

	   // un asteroide té tres fases: gran (ast1), mitjà (ast2), petit (ast3)*/
	   public static final String ASTEROID = "ast"; // + tamany


	   /**
	   ufo:
	   - direccio de sortida
	   - altura de Y
	   - vector moviment
	   - angle direccio
	   - temps restant per disparar
	    
	   // chsc:ufo:left:*locY:*movX:*movY:*dir:(*shootTimeLeft)*/
	   public static final String UFO = "ufo";

	   /**
	   projectil:
	   - direccio de sortida
	   - altura de Y
	   - vector moviment
	   - angle direccio
	   - id owner (ufo si es un ufo)
	   
	   // chsc:proj:righ:*locY:*movX:*movY:*dir:ship#id*/
	   public static final String PROJECTILE = "proj";

	   /**
	    *  // chsc:ship#id:left:*locY:*movX:*movY:*dir:*color:0110:(*intensRotacio):(*intensAccel)
	   ship:
	   - direccio de sortida
	   - altura de Y
	   - vector moviment
	   - angle direccio
	   - id
	   - informació de color en #rrggbb
	   - keys pitjades (left/up/right/shoot) en format 0 no 1 si
	   --- nom i puntuacio NO, seran a servidor ---
	   
	   * ID DE SHIP:
	   - String tipus sh_[comptador estatic de servidor]
	   p ex: sh_4
	   util per tractar amb startswith i coses aixi, a mes de ser mes facilment identificable

	   per tant, id tipus String
	   */
	    
	   public static final String SHIP = "sh_";

	/**
	 * mensaje de peticion por broadcast de un cliente potencial
	 */
	public static final String BROADCAST_SEARCH = "discover_asteroids_server";

	/**
	 * respuesta a peticion por broadcast
	 */
	public static final String BROADCAST_RESPONSE = "i_am_asteroids_server";

	/**
	 * para el split
	 */
	public static final String CONCAT = ":";
	/**
	 * Enviado por cliente a el server al principio
	 */
	public static final String BEGIN="begin";
	/**
	 * "crea","sh_# server","playerName","color"
	 */
	public static final String [] NUEVO_MANDO = {"crea","sh_#","playerName","color"};
	
	/**
	 * "key","sh_# server","tecla","valor"
	 */
	public static final String [] ACCION_MANDO = {"key","sh_# server","tecla","valor"};
	
	/**
	 * "geve","kill-shot-hypr","***","****"
	 */
	public static final String [] PANTALLA_GEVE = {"geve","kill-shot-hypr","***","****"};

	/**
	 * "chsc","****","right-left","locY","movX","movY","dir","......."
	 */
	public static final String [] PANTALLA_CHSC = {"chsc","****","right-left","locY","movX","movY","dir","......."};
	
	/**
	 * enviar a clientes cuando elemento tenga que aparecer
	 */
	public static final String SERVER_SPWN = "spwn";
	
	/**
	 * "pnts","sh_#","add-rem-set","quantity"
	 */
	public static final String [] SERVER_PNTS = {"pnts","sh_#","add-rem-set","quantity"};
	
	/**
	 * "life","sh_#","add-rem-set","quantity"
	 */
	public static final String [] SERVER_LIFE = {"life","sh_#","add-rem-set","quantity"};
	
	public static final String CLIENTE_DISC = "disc";
	
	/**
	 * "disc","sh_#"
	 */
	public static final String [] MANDO_DISC = {"disc","sh_#"};
	
	/**
	 * "gaov","sh_#"
	 */
	public static final String [] SERVER_GAOV = {"gaov","sh_#"};
	
	public static final String SERVER_ENDG = "engd";
		
	
}
