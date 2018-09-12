package com.github.rmee.jpa.schemagen.internal;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlUtils {

	public static void executeSql(
			Connection connection, String sqlScript, String statementSeparator) throws SQLException {
		try (Statement stmt = connection.createStatement()) {
			String[] sqls = sqlScript.split(statementSeparator);
			String sql = "";

			try {
				for (int i = 0; i < sqls.length; i++) {
					sql = sqls[i].trim();
					if (!sql.isEmpty()) {
						stmt.executeUpdate(sql);
					}
				}
			}
			catch (SQLException e) {
				throw new SQLException("Failed to execute '" + sql + "'", e);
			}
		}
	}
}
