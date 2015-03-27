package es.cctvdroid.bd.model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;

public class ControlCamara {
	
	public static ControlCamara instance = new ControlCamara();
	
	public static final String LEFT  = "10,0";
	public static final String RIGHT = "-10,0";
	public static final String UP    = "0,10";
	public static final String DOWN  = "0,-10";
	public static final String STOP  = "0,0";
	
	private URL addr;
	private HttpURLConnection con;
	private Camara camera;
	
	private ControlCamara() {
		super();
	}
	
	public void moverDomo(Camara cam, String direccion) {
//		try {
//			addr = new URL(cam.getMotionString("axis") + direccion);
//			con = (HttpURLConnection) addr.openConnection();
//			con.connect();
//			System.out.println(con.getResponseMessage());
//			con.disconnect();
//			pararMovimientoCamara(cam);
//		}
//		catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//		catch(IOException ex) {
//			ex.printStackTrace();
//		}
		
		camera = cam;
		new DoMovement().execute(cam.getMotionString("axis") + direccion);
	}
	
//	private void pararMovimientoCamara(Camara cam) {
//		try {
//			addr = new URL(cam.getMotionString("axis") + STOP);
//			con = (HttpURLConnection) addr.openConnection();
//			con.connect();
//			System.out.println(con.getResponseMessage());
//			con.disconnect();
//		}
//		catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//		catch(IOException ex) {
//			ex.printStackTrace();
//		}
//	}

	
	public static ControlCamara getInstance() {
		return instance;
	}
	
	public class DoMovement extends AsyncTask<String, Void, Integer> {
		@Override
		protected Integer doInBackground(String... url) {

			try {
				addr = new URL(url[0]);
				con = (HttpURLConnection) addr.openConnection();
				con.connect();
				System.out.println(con.getResponseMessage());
				con.disconnect();
				pararMovimientoCamara(camera);
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
			
			return 0;
		}
		
		private void pararMovimientoCamara(Camara cam) {
			try {
				addr = new URL(cam.getMotionString("axis") + STOP);
				con = (HttpURLConnection) addr.openConnection();
				con.connect();
				System.out.println(con.getResponseMessage());
				con.disconnect();
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
