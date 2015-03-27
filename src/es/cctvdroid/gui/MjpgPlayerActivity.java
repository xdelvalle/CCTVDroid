package es.cctvdroid.gui;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import es.cctvdroid.R;
import es.cctvdroid.bd.model.Camara;
import es.cctvdroid.bd.model.ControlCamara;
import es.cctvdroid.mjpg.MjpegInputStream;
import es.cctvdroid.mjpg.MjpegView;

public class MjpgPlayerActivity extends Activity {
	
	private MjpegView mv;
	private PowerManager.WakeLock wl;
	
	private ImageButton btnUp;
	private ImageButton btnDown;
	private ImageButton btnLeft;
	private ImageButton btnRight;
	
	private Camara camara = new Camara();
	
	private static MjpegInputStream mis;
	private static boolean salir = false;
	private static boolean finishedtask = false;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_viewer, menu);
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.quit:
				this.finish();
				return true;
			case R.id.controls:
				mostrarBotonesControlDomo();
				return true;
			}
		
		return false;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mjpg_viewer);
        
        ListaCamarasActivity.primeraEjecucion = false;
        
        try {
	        Bundle b = this.getIntent().getExtras();
	        
	        camara.setIdCamara(b.getInt("ID_CAMARA"));
	        camara.setTipoCamara(b.getString("TIPO_CAMARA"));
	        camara.setNombreCamara(b.getString("NOMBRE_CAMARA"));
	        camara.setIp(b.getString("IP"));
	        camara.setLogin(b.getString("LOGIN"));
	        camara.setPassword(b.getString("PASSWORD"));
	        
	        String url = camara.getMjpgString(camara.getTipoCamara());
	        
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Tags");
			
			btnUp    = (ImageButton)findViewById(R.id.btnUp);
			btnDown  = (ImageButton)findViewById(R.id.btnDown);
			btnLeft  = (ImageButton)findViewById(R.id.btnIzda);
			btnRight = (ImageButton)findViewById(R.id.btnDcha);
			
			esconderBotonesControlDomo();
			
			mv = (MjpegView) findViewById(R.id.surfaceView1);
			
			//is = MjpegInputStream.read(url);
			new DoRead().execute(url);
			
			while(!finishedtask) 
				sleep(50);
			
			if(mis == null) {
				Toast.makeText(this, "No se ha podido conectar con la camara:\n[" + camara.getNombreCamara() + "]", Toast.LENGTH_LONG).show();
				this.finish();
			}
			
			playVideo(mv);
			
			btnUp.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					ControlCamara.getInstance().moverDomo(camara, ControlCamara.UP);
				}
			});
			
			btnDown.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					ControlCamara.getInstance().moverDomo(camara, ControlCamara.DOWN);
				}
			});
			
			btnLeft.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					ControlCamara.getInstance().moverDomo(camara, ControlCamara.LEFT);
				}
			});
			
			btnRight.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					ControlCamara.getInstance().moverDomo(camara, ControlCamara.RIGHT);
				}
			});
        }
		catch(Exception ex) {
			Toast.makeText(this, "Error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
			this.finish();
		}
    }
    
	public void onResume() {
		super.onResume();
		wl.acquire();
		mv.resumePlayback();
	}

	public void onPause() {
		super.onPause();
		wl.release();
		mv.stopPlayback();
	}
	
	private void playVideo(MjpegView viewer) {
		if(viewer.isPlaying()) {
			viewer.stopPlayback();
		}
		
		viewer.setSource(mis);
		viewer.setDisplayMode(MjpegView.SIZE_BEST_FIT);
		viewer.showFps(true);
	}
	
	private void esconderBotonesControlDomo() {
		btnUp.setVisibility(View.GONE);
		btnDown.setVisibility(View.GONE);
		btnLeft.setVisibility(View.GONE);
		btnRight.setVisibility(View.GONE);
	}
	
	private void mostrarBotonesControlDomo() {
		btnUp.setVisibility(View.VISIBLE);
		btnDown.setVisibility(View.VISIBLE);
		btnLeft.setVisibility(View.VISIBLE);
		btnRight.setVisibility(View.VISIBLE);
	}
	
	public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
		@Override
		protected MjpegInputStream doInBackground(String... url) {

			salir = false;
			finishedtask = false;
			
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

			finishedtask = true;
			
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
	
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}