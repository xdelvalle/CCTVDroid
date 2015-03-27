package es.cctvdroid.gui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import es.cctvdroid.R;
import es.cctvdroid.bd.CamarasSQLiteHelper;
import es.cctvdroid.bd.GestorDBCamaras;
import es.cctvdroid.bd.model.Camara;

public class ListaCamarasActivity extends Activity {
	
	private ListView listView;
	private ImageView backgroundImage;
	private TextView txtWelcome;
	private TextView txtPresentacion;
	
	private static final int CONTEXT_MENU_DELETE_ITEMS  = 0;
	private static final int CONTEXT_MENU_EDIT_ITEMS 	= 1;
	
	ArrayList<Camara> listaItems = new ArrayList<Camara>();
	
	private Activity activity;
	
	public static boolean primeraEjecucion = true;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.lista_camaras);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        activity = this;
        
        crearDatabase();
        
        listView = (ListView)findViewById(R.id.listView1);
        backgroundImage = (ImageView)findViewById(R.id.imageView1);
        backgroundImage.setAlpha(330);
        txtWelcome = (TextView)findViewById(R.id.txtWelcome);
        txtPresentacion = (TextView)findViewById(R.id.txtPresentacion);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	if(!primeraEjecucion) {
    		actualizarListaItems();
    		registerForContextMenu(listView);
    		
    		listView.setOnItemClickListener( new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					itemClick(position);
				}
	         });
    		
    		txtWelcome.setText("");
    		txtPresentacion.setText("");
    	}
    		
    }
   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch(item.getItemId())
    	{
    		case R.id.add:
    			Intent addCamara = new Intent(ListaCamarasActivity.this, AddCamaraActivity.class);
    			startActivity(addCamara);
    			break;
    			
    		case R.id.refresh:
    			actualizarListaItems();
    	    	registerForContextMenu(listView);
    	    	
    	    	listView.setOnItemClickListener( new OnItemClickListener() {
    				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    					itemClick(position);
    				}
    	         });
    			break;
    			
    		case R.id.quit:
    			this.finish();
    			break;
    			
    		case R.id.multivideo:
    			Intent multiVideo = new Intent(ListaCamarasActivity.this, MultivideoActivity.class);
    			startActivity(multiVideo);
    			break;
    			
			default:
				return false;
    	}

    	return true;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if(v.getId() == R.id.listView1) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	    	menu.setHeaderTitle(listaItems.get(info.position).getNombreCamara());
			menu.add(0, CONTEXT_MENU_DELETE_ITEMS, 0, "Eliminar elemento");
			menu.add(0, CONTEXT_MENU_EDIT_ITEMS, 1, "Editar elemento");
		}
	}
    
    @Override
	public boolean onContextItemSelected(MenuItem aItem) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem.getMenuInfo();
		Camara cam;
		switch (aItem.getItemId()) {
			case CONTEXT_MENU_DELETE_ITEMS:
				cam = (Camara) listView.getAdapter().getItem(menuInfo.position);
				GestorDBCamaras.getInstance().deleteCamara(cam.getIdCamara());
				actualizarListaItems();
				Toast.makeText(this, "Cámara [" + cam.getNombreCamara() + " eliminada...", Toast.LENGTH_SHORT).show();
				return true; 
				
			case CONTEXT_MENU_EDIT_ITEMS:
				cam = (Camara) listView.getAdapter().getItem(menuInfo.position);
				Intent intent = new Intent(ListaCamarasActivity.this, AddCamaraActivity.class);
				Bundle b = new Bundle();
				b.putString("TIPO_CAMARA", cam.getTipoCamara());
				b.putString("NOMBRE_CAMARA", cam.getNombreCamara());
		    	b.putString("IP", cam.getIp());
		    	b.putString("LOGIN", cam.getLogin());
		    	b.putString("PASSWORD", cam.getPassword());
		    	b.putInt("ID_CAMARA", cam.getIdCamara());
		    	intent.putExtras(b);
		    	startActivity(intent);
				return true;
		}
		
		return false;
	}
    
    private void itemClick(int position) {
    	Intent mjpgPlayerActivity = new Intent(ListaCamarasActivity.this, MjpgPlayerActivity.class);
    	
    	Bundle b = new Bundle();
    	b.putString("TIPO_CAMARA", listaItems.get(position).getTipoCamara());
    	b.putString("NOMBRE_CAMARA", listaItems.get(position).getNombreCamara());
    	b.putString("IP", listaItems.get(position).getIp());
    	b.putString("LOGIN", listaItems.get(position).getLogin());
    	b.putString("PASSWORD", listaItems.get(position).getPassword());
    	b.putInt("ID_CAMARA", listaItems.get(position).getIdCamara());
    	
    	mjpgPlayerActivity.putExtras(b);
    	startActivity(mjpgPlayerActivity);
    }
    
    public void actualizarListaItems() {
    	listaItems = new ArrayList<Camara>();
    	listaItems = GestorDBCamaras.getInstance().leerCamaras();
    	
    	if(listaItems.size() > 0) {
    		txtPresentacion.setText("");
    		txtWelcome.setText("");
    	}
    	else {
    		txtPresentacion.setText(R.string.Presentation);
    		txtWelcome.setText(R.string.welcome);
    	}
    	
    	//ArrayAdapter<Camara> adapter = new ArrayAdapter<Camara>(this, android.R.layout.simple_list_item_1, listaItems);
    	ListaCamarasArrayAdapter adapter = new ListaCamarasArrayAdapter(activity, this, R.layout.row, listaItems);
    	
    	listView.setAdapter(adapter);
    }
    
	private void crearDatabase() {
		GestorDBCamaras.getInstance().setHelper(new CamarasSQLiteHelper(this, "BDCamaras.db", null, 1));
	}
}