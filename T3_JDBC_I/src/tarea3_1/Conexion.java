package tarea3_1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
	
	private final String URL = "jdbc:mysql://dns11036.phdns11.es:3306./ad2526_juanluis_barrionuevo";
	private final String USUARIO = "juanluis_barrionuevo";
	private final String PASSWORD = "12345";
		
	public static Connection conexionBD() {
		Connection conn = null;
		
		try {
			conn = DriverManager.getConnection(Conexion.URL);
			
		} catch (ClassNotFoundException e){
			System.out.println("Driver JDBC no encontrado: " + e.getMessage());
		} catch (SQLException e) {
            System.err.println("❌ Error al conectar a la base de datos: " + e.getMessage());
        }
		
		return conn;
	}
}
