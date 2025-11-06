package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

	private static final String URL = "jdbc:mysql://dns11036.phdns11.es:3306./ad2526_juanluis_barrionuevo";
	private static final String USUARIO = "juanluis_barrionuevo";
	private static final String PASSWORD = "12345";

	public static Connection conexionBD() {

		Connection conn = null;

		try {
			conn = DriverManager.getConnection(Conexion.URL, Conexion.USUARIO, Conexion.PASSWORD);

		} catch (SQLException e) {
			System.err.println("❌ Error al conectar a la base de datos: " + e.getMessage());
		}

		return conn;
	}
}
