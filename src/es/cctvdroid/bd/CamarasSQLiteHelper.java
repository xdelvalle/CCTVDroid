package es.cctvdroid.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CamarasSQLiteHelper extends SQLiteOpenHelper {

    String sqlCreate = "create table Camaras (idCamara integer primary key autoincrement, tipoCamara text, nombreCamara text, ip text, login text, password text);";
    
	public CamarasSQLiteHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sqlCreate);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Camaras");
        db.execSQL(sqlCreate);
	}
}
