/* Copyright 2007 StreetsOfBoston (Anton Spaans)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streetsofboston.smugmug.v1_2_1.db;

//import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
//import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Anton Spaans.
 * 
 */
public class DataBase {

	private static final DataBase DATABASE;
	static {
		DataBase db = null;
		try {
			db = new DataBase();
		} catch (ClassNotFoundException cnfe) {
			db = null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		DATABASE = db;
	}

	public static synchronized DataBase getDB() {
		return DATABASE;
	}

	private Connection mConnection      = null;
	private boolean mHasJustBeenCreated = false;

	private final String mDBLocation;
	private final String mDBName;

	private DataBase() throws ClassNotFoundException, SQLException, IOException {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

		mDBLocation = "D:\\Program_Files\\Development\\Projects\\eclipse\\SmugFig\\";
		mDBName = "SmugFigDB";
		try {
			mConnection = openDatabase(false);
		} catch (SQLException sse) {
			mConnection = openDatabase(true);
		}
	}

	public synchronized void createTables() throws IOException, SQLException {
		final InputStream is = getClass().getClassLoader().getResourceAsStream(
				"com/streetsofboston/smugmug/v1_2_1/db/SmugFigCreateDB.sql");
		if (!mHasJustBeenCreated) {
			dropDatabase();
			mConnection = openDatabase(true);
		}
		// final OutputStream os = new ByteArrayOutputStream();
		org.apache.derby.tools.ij.runScript(mConnection, is, "UTF-8", System.out, "UTF-8");
	}

	public synchronized void dropDatabase() throws SQLException {
		shutdownDatabase();

		File dir = new File(mDBLocation);
		File name = new File(dir, mDBName);

		deleteAllFiles(name);
	}
	
	private static boolean deleteAllFiles(File dir) {
		if (!dir.exists()) {
			return true;
		}
		boolean res = true;
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				res &= deleteAllFiles(files[i]);
			}
			res = dir.delete();// Delete dir itself
		} else {
			res = dir.delete();
		}
		return res;
	}

	private Connection openDatabase(boolean doCreate) throws SQLException {
		mHasJustBeenCreated = doCreate;
		return DriverManager.getConnection("jdbc:derby:" + mDBLocation
				+ mDBName + (doCreate ? ";create=true" : ""));
	}

	private void shutdownDatabase() throws SQLException {
		if (mConnection == null)
			return;

		mHasJustBeenCreated = false;

		mConnection.close(); // releases all locks on db, if any, so that shutdown will work ok.
		mConnection = null;
		
		try {
			DriverManager.getConnection("jdbc:derby:" + mDBLocation + mDBName + ";shutdown=true");
		} catch (SQLException sqle) {
			if (!"08006".equals(sqle.getSQLState()))
				throw sqle;
		}
	}

	public static void main(String[] args) throws IOException, SQLException {
		DataBase db = getDB();
		db.createTables();
	}
}
