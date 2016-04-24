package server;

import java.awt.Color;


class Driver implements MAP{

	
	private String name="notSpecified";
	private long idMapa = -1;
	private Color color = null;
	private int points = -1;
	private Client client = null;
	private int lifes = -1;
	
	
	protected Driver(Client client, String name, String color){
		this.client=client;
		this.name=name;
		this.color=Color.decode(color);
		
	}
	protected Driver(Client client,String name, String color, long idMapa){
		this.client=client;
		this.name=name;
		this.color=Color.decode(color);
		this.idMapa=idMapa;
	}

	protected int getPoints() {
		return points;
	}


	protected void setPoints(int points) {
		this.points = points;
	}


	protected int getLifes() {
		return lifes;
	}


	protected void setLifes(int lifes) {
		this.lifes = lifes;
	}


	protected Client getClient() {
		return client;
	}


	protected String getName() {
		return name;
	}


	protected void setName(String name) {
		this.name = name;
	}


	protected Color getColor() {
		return color;
	}


	protected void setColor(Color color) {
		this.color = color;
	}


	@Override
	public String toString() {
		return MAP.SHIP+getClient().getId()+MAP.CONCAT+getName()+MAP.CONCAT+getColor().getRGB()+MAP.CONCAT+getPoints()+MAP.CONCAT+getLifes();
	}
	protected long getIdMapa() {
		return idMapa;
	}
	protected void setIdMapa(long idMapa) {
		this.idMapa = idMapa;
	}

}
