package es.cctvdroid.gui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import es.cctvdroid.R;
import es.cctvdroid.bd.GestorDBCamaras;
import es.cctvdroid.bd.model.Camara;

public class AddCamaraActivity extends Activity {

	private ImageButton btnGuardar;
	private EditText txtNombre;
	private EditText txtIP;
	private EditText txtLogin;
	private EditText txtPassword;
	private Spinner cmbTipoCamara;
	
	private int idCamara;
	private String tipoDeCamara = null;
	
	private final String[] tiposCamara = {"Axis", "Otra"};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_camara);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		btnGuardar 	  = (ImageButton) findViewById(R.id.btnGuardar);
		txtNombre 	  = (EditText)findViewById(R.id.txtNombre);
		txtIP 		  = (EditText)findViewById(R.id.txtIP);
		txtLogin 	  = (EditText)findViewById(R.id.txtLogin);
		txtPassword   = (EditText)findViewById(R.id.txtPassword);
		cmbTipoCamara = (Spinner)findViewById(R.id.tipoCamara); 
		
		btnGuardar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				guardarCamara();
				ListaCamarasActivity.primeraEjecucion = false;
			}
		});
		
		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tiposCamara);
		adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cmbTipoCamara.setAdapter(adaptador);
		
		cmbTipoCamara.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	        
        	public void onItemSelected(AdapterView<?> parent, android.view.View v, int position, long id) {    	        	
        		tipoDeCamara = tiposCamara[position];
	        }
    	 
	        public void onNothingSelected(AdapterView<?> parent) {
	            Toast.makeText(getApplicationContext(), "Debe seleccionar un tipo de cámara", Toast.LENGTH_SHORT).show();
	        }
	        
    	});
		
		Bundle b = this.getIntent().getExtras();
		
		idCamara = 0;
		
		if(b != null) {
			
			int pos = adaptador.getPosition((String)b.getString("TIPO_CAMARA"));
			
			idCamara = b.getInt("ID_CAMARA");
			cmbTipoCamara.setSelection(pos);
			txtNombre.setText(b.getString("NOMBRE_CAMARA"));
			txtIP.setText(b.getString("IP"));
			txtLogin.setText(b.getString("LOGIN"));
			txtPassword.setText(b.getString("PASSWORD"));
			
	        Camara camara = new Camara();
	        camara.setIdCamara(b.getInt("ID_CAMARA"));
	        camara.setTipoCamara(b.getString("TIPO_CAMARA"));
	        camara.setNombreCamara(b.getString("NOMBRE_CAMARA"));
	        camara.setIp(b.getString("IP"));
	        camara.setLogin(b.getString("LOGIN"));
	        camara.setPassword(b.getString("PASSWORD"));
		}
	}
	
	private void guardarCamara() {
		
		if(txtNombre.getText().toString().compareTo("") == 0) {
			Toast.makeText(this, "Debe introducir un nombre!!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(txtIP.getText().toString().compareTo("") == 0) {
			Toast.makeText(this, "Debe introducir una IP o Hostname!!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(tipoDeCamara == null) {
			Toast.makeText(this, "Debe seleccionar un tipo de cámara!!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Camara cam = new Camara();
		cam.setTipoCamara(tipoDeCamara);
		cam.setNombreCamara(txtNombre.getText().toString());
		cam.setIp(txtIP.getText().toString());
		cam.setPassword(txtPassword.getText().toString());
		cam.setLogin(txtLogin.getText().toString());
		cam.setIdCamara(idCamara);
		
		if(idCamara == 0) {
			GestorDBCamaras.getInstance().insertCamara(cam);
			Toast.makeText(this, "Cámara [" + cam.getNombreCamara() + "] añadida...", Toast.LENGTH_LONG).show();
		}
		else {
			GestorDBCamaras.getInstance().updateCamara(cam);
			Toast.makeText(this, "Cámara [" + cam.getNombreCamara() + "] actualizada...", Toast.LENGTH_LONG).show();
		}
		
		inicializarCampos();
	}
	
	private void inicializarCampos() {
		txtNombre.setText("");
		txtIP.setText("");
		txtLogin.setText("");
		txtPassword.setText("");
	}
}
