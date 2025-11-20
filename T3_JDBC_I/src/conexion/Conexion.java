package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

	/**
	 * URL de conexión a la base de datos MySQL.
	 */
	private static final String URL = "jdbc:mysql://dns11036.phdns11.es:3306/ad2526_juanluis_barrionuevo";

	/**
	 * Nuestro usuario con el que accederemos a la base de datos.
	 */
	private static final String USUARIO = "ad2526_juanluis_barrionuevo";

	/**
	 * Contraseña con la que accederemos a la base de datos.
	 */
	private static final String PASSWORD = "12345";

	/**
	 * Establecemos una conexión con la base de datos MySQL utilizando la URL, el
	 * usuario y la contraseña definidos en esta clase.
	 * 
	 * @return un objeto conexión si se realiza correctamente, o un mensaje de error
	 *         si hay algun problema al conectarse.
	 */
	public static Connection conexionBD() {

		// Inicializamos la conexión como null por si ocurre algun error.
		Connection conn = null;

		try {
			// Establecemos conexión usando DriverManager.
			conn = DriverManager.getConnection(URL, USUARIO, PASSWORD);

		} catch (SQLException e) {

			// Si ocurre algun error, mostramos este mensaje por consola.
			System.err.println(" Error al conectar a la base de datos: " + e.getMessage());
		}

		return conn;
	}
}
