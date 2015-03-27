package es.cctvdroid.bd;

import java.util.ArrayList;

import es.cctvdroid.bd.model.Camara;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GestorDBCamaras {

	public static GestorDBCamaras instance = new GestorDBCamaras();
	
	private CamarasSQLiteHelper helper;
	
	private SQLiteDatabase db;
	
	private ArrayList<Camara> listaCamaras = new ArrayList<Camara>();
	
	private String qry = null;
	
	private GestorDBCamaras() {
		super();
	}

	public ArrayList<Camara> leerCamaras() {
		db = helper.getWritableDatabase();
		
		Camara camara; 
		
		if (db != null) {
			Cursor cursor = db.query("Camaras", null, null, null, null, null, null);
			cursor.moveToFirst();
			
			listaCamaras = new ArrayList<Camara>();
			
			while (cursor.isAfterLast() == false) {
				camara = new Camara();
				camara.setIdCamara(Integer.parseInt(cursor.getString(0)));
				camara.setTipoCamara(cursor.getString(1));
				camara.setNombreCamara(cursor.getString(2));
				camara.setIp(cursor.getString(3));
				camara.setLogin(cursor.getString(4));
				camara.setPassword(cursor.getString(5));
				
				listaCamaras.add(camara);
				
				cursor.moveToNext();
			}

			db.close();
		}
		
		return listaCamaras;
	}

	public void insertCamara(Camara cam) {
		db = helper.getWritableDatabase();
		
		qry = String.format("INSERT INTO Camaras (tipoCamara, nombreCamara, ip, login, password) VALUES ('%s', '%s', '%s', '%s', '%s')", cam.getTipoCamara(), cam.getNombreCamara(), cam.getIp(), cam.getLogin(), cam.getPassword());

		if (db != null) {
			db.execSQL(qry);
			db.close();
		}
	}
	
	public void deleteCamara(int idCamara) {
		db = helper.getWritableDatabase();
		
		if(db != null) {
			db.delete("Camaras", "idCamara=?", new String[] {Integer.toString(idCamara)});
			db.close();
		}
	}
	
	public void updateCamara(Camara cam) {
		db = helper.getWritableDatabase();
		
		qry = String.format("UPDATE Camaras SET tipoCamara='%s', nombreCamara='%s', ip='%s', login='%s', password='%s' WHERE idCamara=%d", cam.getTipoCamara(), cam.getNombreCamara(), cam.getIp(), cam.getLogin(), cam.getPassword(), cam.getIdCamara());
		
		if(db != null) {
			db.execSQL(qry);
			db.close();
		}
	}
	
	public static GestorDBCamaras getInstance() {
		return instance;
	}
	
	public CamarasSQLiteHelper getHelper() {
		return helper;
	}
	
	public void setHelper(CamarasSQLiteHelper helper) {
		this.helper = helper;
	}
}
