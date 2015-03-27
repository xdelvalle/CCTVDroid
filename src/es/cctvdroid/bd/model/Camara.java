package es.cctvdroid.bd.model;

public class Camara {

	private int idCamara;
	private String tipoCamara;
	private String nombreCamara;
	private String ip;
	private String login;
	private String password;
	
	public Camara() {
		super();
	}

	public Camara(String tipoCamara, String nombreCamara, String ip, String login, String password) {
		super();
		this.nombreCamara = nombreCamara;
		this.ip = ip;
		this.login = login;
		this.password = password;
		this.tipoCamara = tipoCamara;
	}

	public int getIdCamara() {
		return idCamara;
	}

	public void setIdCamara(int idCamara) {
		this.idCamara = idCamara;
	}

	public String getNombreCamara() {
		return nombreCamara;
	}

	public void setNombreCamara(String nombreCamara) {
		this.nombreCamara = nombreCamara;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getTipoCamara() {
		return tipoCamara;
	}

	public void setTipoCamara(String tipoCamara) {
		this.tipoCamara = tipoCamara;
	}

	public String getMjpgString(String tipoCamara) {
		if(tipoCamara.equalsIgnoreCase("axis")) {
			if(login.equals("") && password.equals("")) 
				return String.format("http://%s/axis-cgi/mjpg/video.cgi?resolution=CIF", ip.trim());
			else
				return String.format("http://%s:%s@%s/axis-cgi/mjpg/video.cgi?resolution=CIF", login.trim(), password.trim(), ip.trim());
		}
		else if(tipoCamara.equalsIgnoreCase("otra")) {
			if(login.equals("") && password.equals("")) 
				return String.format("http://%s/axis-cgi/mjpg/video.cgi?resolution=CIF", ip.trim());
			else
				return String.format("http://%s:%s@%s/axis-cgi/mjpg/video.cgi?resolution=CIF", login.trim(), password.trim(), ip.trim());
		}
		else {
			return null;
		}
	}
	
	public String getMotionString(String tipoCamara) {
		
		if(tipoCamara.equalsIgnoreCase("axis")) {
			if(login.equals("") && password.equals("")) 
				return String.format("http://%s/axis-cgi/com/ptz.cgi?continuouspantiltmove=", ip.trim());
			else
				return String.format("http://%s:%s@%s/axis-cgi/com/ptz.cgi?continuouspantiltmove=", login.trim(), password.trim(), ip.trim());
				
		}
		else if(tipoCamara.equalsIgnoreCase("otra")) {
			if(login.equals("") && password.equals("")) 
				return String.format("http://%s/axis-cgi/com/ptz.cgi?continuouspantiltmove=", ip.trim());
			else
				return String.format("http://%s:%s@%s/axis-cgi/com/ptz.cgi?continuouspantiltmove=", login.trim(), password.trim(), ip.trim());
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return nombreCamara;
	}
	
	public String toStringCompleto() {
		return "Camara [idCamara=" + idCamara + ", tipoCamara=" + tipoCamara + ", nombreCamara=" + nombreCamara + ", ip=" + ip + ", login="	+ login + ", password=" + password + "]";
	}
}
