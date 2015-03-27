package es.cctvdroid.gui;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import es.cctvdroid.R;
import es.cctvdroid.bd.GestorDBCamaras;
import es.cctvdroid.bd.model.Camara;
import es.cctvdroid.mjpg.MjpegInputStream;
import es.cctvdroid.mjpg.MjpegView;

public class MultivideoActivity extends Activity {

//	String CAM1 = "http://83.64.164.6/axis-cgi/mjpg/video.cgi?resolution=CIF";
//	String CAM2 = "http://69.224.130.150/axis-cgi/mjpg/video.cgi?resolution=CIF";
//	String CAM3 = "http://129.22.128.85/axis-cgi/mjpg/video.cgi?resolution=CIF";
//	String CAM4 = "http://83.64.164.6/axis-cgi/mjpg/video.cgi?resolution=CIF";
	
//	String CAM1 = "http://root:bondia@172.19.138.174/axis-cgi/mjpg/video.cgi?resolution=CIF";
//	String CAM2 = "http://root:bondia@172.19.138.178/axis-cgi/mjpg/video.cgi?resolution=CIF";
//	String CAM3 = "http://root:bondia@172.19.138.175/axis-cgi/mjpg/video.cgi?resolution=CIF";
//	String CAM4 = "http://root:bondia@172.19.138.208/axis-cgi/mjpg/video.cgi?resolution=CIF";
	
	private MjpegView[] viewer;
	
	private static final int CONTEXT_MENU_STOP_VIDEO  = 0;
	
	private static final int NUM_VISORES = 4;
	
	private PowerManager.WakeLock wl;
	
	private ArrayList<Camara> listaCamaras;
	private CharSequence[] sCamaras;
	
	private AlertDialog.Builder builder;
	
	private Hashtable<Integer, Camara> camaraVisor = new Hashtable<Integer, Camara>();
	
	private MjpegInputStream mis;
	
	private static boolean salir = false;
	private static boolean finishedTask = false;
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_multivideo, menu);
        return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.visor1:
				playInViewer(viewer[0]);
				return true;
				
			case R.id.visor2:
				playInViewer(viewer[1]);
				return true;
				
			case R.id.visor3:
				playInViewer(viewer[2]);
				return true;
				
			case R.id.visor4:
				playInViewer(viewer[3]);
				return true;
		}
		return false;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multivideo);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Tags");

		viewer = new MjpegView[NUM_VISORES];
		
		viewer[0] = (MjpegView) findViewById(R.id.camera1);
		viewer[1] = (MjpegView) findViewById(R.id.camera2);
		viewer[2] = (MjpegView) findViewById(R.id.camera3);
		viewer[3] = (MjpegView) findViewById(R.id.camera4);
		
		viewer[0].setId(1);
		viewer[1].setId(2);
		viewer[2].setId(3);
		viewer[3].setId(4);
		
		registerForContextMenu(viewer[0]);
		registerForContextMenu(viewer[1]);
		registerForContextMenu(viewer[2]);
		registerForContextMenu(viewer[3]);
		
		listaCamaras = GestorDBCamaras.getInstance().leerCamaras();
		sCamaras = new CharSequence[listaCamaras.size()];
		
		for(int i=0; i<sCamaras.length; i++) {
			sCamaras[i] = listaCamaras.get(i).getNombreCamara();
		}
		
		camaraVisor.clear();
		
		prepareListeners();
	}

	public void onPause() {
		super.onPause();
		wl.release();
	}

	public void onResume() {
		super.onResume();
		wl.acquire();
		
		if(!camaraVisor.isEmpty()) {
			for(int i=1; i<=NUM_VISORES; i++) {
				if(camaraVisor.get(i) != null) {
					playVideo(camaraVisor.get(i), viewer[i-1]);
				}
			}
		}
	}
	
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	menu.setHeaderTitle("Visor " + v.getId());
		menu.add(0, CONTEXT_MENU_STOP_VIDEO, 0, "Parar Video " + v.getId());
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem aItem) {
		
		switch (aItem.getItemId()) {
			case CONTEXT_MENU_STOP_VIDEO:
				if(aItem.toString().contains("1")) {
					if(viewer[0].isPlaying()) {
						viewer[0].stopPlayback();
						camaraVisor.remove(viewer[0].getId());
					}
					else 
						Toast.makeText(getApplicationContext(), "No hay video reproduciendo en este visor [1]...", Toast.LENGTH_SHORT).show();
				}
				else if(aItem.toString().contains("2")) {
					if(viewer[1].isPlaying()) {
						viewer[1].stopPlayback();
						camaraVisor.remove(viewer[1].getId());
					}
					else 
						Toast.makeText(getApplicationContext(), "No hay video reproduciendo en este visor [2]...", Toast.LENGTH_SHORT).show();
				}
				else if(aItem.toString().contains("3")) {
					if(viewer[2].isPlaying()) {
						viewer[2].stopPlayback();
						camaraVisor.remove(viewer[2].getId());
					}
					else 
						Toast.makeText(getApplicationContext(), "No hay video reproduciendo en este visor [3]...", Toast.LENGTH_SHORT).show();
				}
				else if(aItem.toString().contains("4")) {
					if(viewer[3].isPlaying()) {
						viewer[3].stopPlayback();
						camaraVisor.remove(viewer[3].getId());
					}
					else 
						Toast.makeText(getApplicationContext(), "No hay video reproduciendo en este visor [4]...", Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(getApplicationContext(), "Id. de Visor incorrecto!!", Toast.LENGTH_SHORT).show();
					return false;
				}
				
				return true; 
		}
		return false;
	}
	
	private void prepareListeners() {
		viewer[0].setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(viewer[0].isPlaying()) {
					
					startFullScreenActivity(1);
			    	
			    	viewer[0].stopPlayback();
			    	
			    	if(viewer[1].isPlaying())
						viewer[1].stopPlayback();
					if(viewer[2].isPlaying())
						viewer[2].stopPlayback();
					if(viewer[3].isPlaying())
						viewer[3].stopPlayback();
				}
				else { 
					Toast.makeText(getApplicationContext(), "No hay video reproduciendo en este visor [1]...", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		viewer[1].setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(viewer[1].isPlaying()) {
					
					startFullScreenActivity(2);
			    	
			    	viewer[1].stopPlayback();
			    	
			    	if(viewer[0].isPlaying())
						viewer[0].stopPlayback();
					if(viewer[2].isPlaying())
						viewer[2].stopPlayback();
					if(viewer[3].isPlaying())
						viewer[3].stopPlayback();
				}
				else {
					Toast.makeText(getApplicationContext(), "No hay video reproduciendo en este visor [2]...", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		viewer[2].setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(viewer[2].isPlaying()) {
					
					startFullScreenActivity(3);
			    	
			    	viewer[2].stopPlayback();
			    	
			    	if(viewer[0].isPlaying())
						viewer[0].stopPlayback();
					if(viewer[1].isPlaying())
						viewer[1].stopPlayback();
					if(viewer[3].isPlaying())
						viewer[3].stopPlayback();
				}
				else { 
					Toast.makeText(getApplicationContext(), "No hay video reproduciendo en este visor [3]...", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		viewer[3].setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(viewer[3].isPlaying()) {
					
					startFullScreenActivity(4);
			    	
			    	viewer[3].stopPlayback();
			    	
			    	if(viewer[0].isPlaying())
						viewer[0].stopPlayback();
					if(viewer[1].isPlaying())
						viewer[1].stopPlayback();
					if(viewer[2].isPlaying())
						viewer[2].stopPlayback();
				}
				else { 
					Toast.makeText(getApplicationContext(), "No hay video reproduciendo en este visor [4]...", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	private void startFullScreenActivity(int idViewer) {
		Intent mjpgPlayerActivity = new Intent(MultivideoActivity.this, MjpgPlayerActivity.class);
    	
		Camara cam = camaraVisor.get(idViewer);
		
    	Bundle b = new Bundle();
    	b.putString("TIPO_CAMARA", cam.getTipoCamara());
    	b.putString("NOMBRE_CAMARA", cam.getNombreCamara());
    	b.putString("IP", cam.getIp());
    	b.putString("LOGIN", cam.getLogin());
    	b.putString("PASSWORD", cam.getPassword());
    	b.putInt("ID_CAMARA", cam.getIdCamara());
    	
    	mjpgPlayerActivity.putExtras(b);
    	startActivity(mjpgPlayerActivity);
	}
	
	private void playVideo(Camara cam, MjpegView mv) {
		try {
			if(mv.isPlaying())
				mv.stopPlayback();
			
			//is = MjpegInputStream.read(cam.getMjpgString(cam.getTipoCamara()));
			
			new DoRead().execute(cam.getMjpgString(cam.getTipoCamara()));
			
			while(!finishedTask)
				sleep(50);
			
			if(mis != null) {
				mv.setSource(mis);
				mv.setDisplayMode(MjpegView.SIZE_FULLSCREEN);
				mv.showFps(true);
				
				camaraVisor.put(mv.getId(), cam);
			}
			else 
				Toast.makeText(getApplicationContext(), "No se ha podido conectar con la cámara:\n " + cam.getNombreCamara(), Toast.LENGTH_SHORT).show();
		}
		catch(Exception ex) {
			Toast.makeText(getApplicationContext(), "No se ha podido conectar con la cámara:\n " + cam.getNombreCamara() + "\n" + ex.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void playInViewer(final MjpegView pViewer) {
		builder = new AlertDialog.Builder(this);
	    builder.setTitle("Seleccionar cámara");
	    builder.setItems(sCamaras, new DialogInterface.OnClickListener() {
	        
	    	public void onClick(DialogInterface dialog, int item) {
	        	playVideo(listaCamaras.get(item), pViewer);
	        }
	    	
	    }).show();
	}
	
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
		@Override
		protected MjpegInputStream doInBackground(String... url) {

			salir = false;
			finishedTask = false;
			
			// TODO Le asignamos un timeout a la conexión
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
			HttpConnectionParams.setSoTimeout(httpParameters, 5000);
			
			try {
				DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
				URI uri = URI.create(url[0]);
				HttpGet httpGet = new HttpGet(uri);
				HttpResponse res = httpclient.execute(httpGet);
				mis = new MjpegInputStream(res.getEntity().getContent());
			}
			catch (ClientProtocolException e) {
				mis = null;
			}
			catch(IOException ex) {
				mis = null;
			}
			catch(Exception ex) {
				Log.e("Error!", ex.getMessage(), ex);
				mis = null;
			}
			
			salir = true;
			
			if(!salir)
				sleep(100);

			finishedTask = true;
			
			return mis;
		}
		
		private void sleep(long millis) {
			try {
				Thread.sleep(millis);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
