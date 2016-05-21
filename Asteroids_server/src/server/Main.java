package server;

/**
 * 
 * @author Robin
 *
 */
public class Main {
	
	private static Server server;

	/**
	 * arguments: "-p puerto"
	 * @param s
	 */
	public static void main(String[] s) {
		
		try{
			if (s!=null && !s.equals("") && s.length==2){
				server = new Server (obtenerPuerto(s));
				server.start();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		

	}

	private static int obtenerPuerto(String[] s) {

		int port=MAP.PORT;
		
		try{
			
			String t = String.valueOf(s[0]);
			if (t!=null && t.toLowerCase().equals("-p")){
				port = Integer.parseInt(String.valueOf(s[1]));
			}
		}catch(Exception e){
			port=1;
			e.printStackTrace();
		}
		
		
		return port;
	}

}
