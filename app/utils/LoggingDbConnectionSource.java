package utils;

import java.sql.Connection;
import java.sql.SQLException;

import play.db.DB;

import ch.qos.logback.core.db.ConnectionSource;
import ch.qos.logback.core.db.dialect.SQLDialectCode;


public class LoggingDbConnectionSource implements ConnectionSource {

	private boolean started; 
	
	@Override
	public boolean isStarted() {
		return started;
	}

	@Override
	public void start() {
		started = true;
	}

	@Override
	public void stop() {
		started = false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DB.getConnection();
	}

	@Override
	public SQLDialectCode getSQLDialectCode() {
		return SQLDialectCode.POSTGRES_DIALECT;
	}

	@Override
	public boolean supportsBatchUpdates() {
		return false;
	}

	@Override
	public boolean supportsGetGeneratedKeys() {
		return false;
	}

}
