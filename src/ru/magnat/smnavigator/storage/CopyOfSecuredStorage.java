package ru.magnat.smnavigator.storage;

import java.sql.SQLException;

import ru.magnat.smnavigator.model.Branch;
import ru.magnat.smnavigator.model.Department;
import ru.magnat.smnavigator.model.Manager;
import android.content.Context;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.db.H2DatabaseType;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class CopyOfSecuredStorage {
	
	private JdbcPooledConnectionSource mConnection;
	
	public CopyOfSecuredStorage(Context context) {
		String dbFile = context.getDir("data", Context.MODE_PRIVATE).getPath() + "/test";
		
		try {
			mConnection = new JdbcPooledConnectionSource("jdbc:h2:" + dbFile); 

			DatabaseType databaseType = new H2DatabaseType();
			
			mConnection.setDatabaseType(databaseType);
			
			TableUtils.createTableIfNotExists(mConnection, Manager.class);
			TableUtils.createTableIfNotExists(mConnection, Branch.class);
			TableUtils.createTableIfNotExists(mConnection, Department.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void closeConnection() {
		try {
			mConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
