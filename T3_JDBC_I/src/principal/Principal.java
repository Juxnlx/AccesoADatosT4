package principal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import conexion.Conexion;
import crud.RestauranteCRUD;

public class Principal {

	private static final Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {

		Connection con = Conexion.conexionBD(); // Conexión abierta

		RestauranteCRUD crud = new RestauranteCRUD(); // Instancia del CRUD

		// Creamos la variable opc como int para almacenar la opción seleccionada por el
		// usuario.
		int opc;

		do {
			// Llamamos a la función menú.
			Menu();
			// Le pedimos al usuario que introduzca la opción que quiere seleccionar en el
			// menú y la leemos.
			System.out.println("Introduce una opción --> ");
			opc = sc.nextInt();
			// Limpiamos el buffer
			sc.nextLine();

			switch (opc) {
			case 1 -> gestionCreacionTablas();
			case 2 -> gestionInsentarMesa();
			case 3 -> gestionInsertarProducto();
			case 4 -> gestionInsertarFactura();
			case 5 -> gestionInsertarPedido();
			case 6 -> menuListar();
			case 7 -> modificar();
			case 8 -> borrarDesdeMain(con, crud);
			case 9 -> eliminarTablasDesdeMain(con, crud);
			case 0 -> System.out.println("Saliendo del programa...");
			default -> System.out.println("Opción no válida.");
			}

		} while (opc != 0);

		// Cerramos el Scanner.
		sc.close();
	}

	/**
	 * Muestra por consola el menú principal de opciones del sistema de restaurante.
	 */
	public static void Menu() {

		// Imprimimos el menú principal con todas las opciones disponibles.
		System.out.println("----------MENÚ----------");
		System.out.println("1. Crear Tabla");
		System.out.println("2. Insertar Mesa");
		System.out.println("3. Insertar Producto");
		System.out.println("4. Insertar Factura");
		System.out.println("5. Insertar Pedido");
		System.out.println("6. Listar");
		System.out.println("7. Modificar");
		System.out.println("8. Borrar");
		System.out.println("9. Eliminar Tabla");
		System.out.println("0. Salir");
	}

	/**
	 * Gestiona la creación de tablas en la base de datos según la elección del
	 * usuario.
	 *
	 * Permite al usuario crear todas las tablas de una vez o seleccionar una tabla
	 * específica para crearla, verificando primero si es posible según las
	 * dependencias entre tablas.
	 */
	public static void gestionCreacionTablas() {

		// Variable para la opción del usuario.
		int opc;

		// Variable para almacenar el nombre de la tabla si se elige crear una concreta.
		String nombreTabla = "";

		// Preguntamos al usuario qué desea hacer.
		System.out.println("¿Quieres crear todas las tablas o una tabla concreta?");
		System.out.println("1. Todas las tablas");
		System.out.println("2. Tabla concreta");

		// Leemos la opción.
		opc = sc.nextInt();
		sc.nextLine(); // Limpiamos el buffer

		if (opc == 1) {
			// Crear todas las tablas sin preguntar por dependencias.
			RestauranteCRUD.crearTodasLasTablas();

		} else if (opc == 2) {
			// Crear una tabla concreta: pedimos el nombre.
			System.out.println("Introduce el nombre de la tabla (mesa, producto, factura, pedido):");
			nombreTabla = sc.nextLine().toLowerCase();

			try {
				// Verificamos si se puede crear la tabla según dependencias.
				if (RestauranteCRUD.puedeCrearTabla(nombreTabla)) {

					// Ejecutamos la creación según el nombre de la tabla.
					switch (nombreTabla) {
					case "mesa" -> RestauranteCRUD.crearTablaMesa();
					case "producto" -> RestauranteCRUD.crearTablaProducto();
					case "factura" -> RestauranteCRUD.crearTablaFactura();
					case "pedido" -> RestauranteCRUD.crearTablaPedido();
					default -> System.out.println("Tabla no válida.");
					}

					System.out.println("Tabla " + nombreTabla + " creada correctamente.");

				} else {
					// La tabla depende de otras tablas que no existen.
					System.out.println("No se puede crear la tabla " + nombreTabla
							+ " porque depende de otras tablas que no existen.");
				}

			} catch (SQLException e) {
				System.out.println("Error al crear la tabla: " + e.getMessage());
			}

		} else {
			// Opción no válida
			System.out.println("Opción no válida");
		}
	}

	/**
	 * Gestiona la inserción de una nueva mesa en la base de datos.
	 * 
	 * Solicita al usuario el número de comensales y si la mesa tiene reserva, y
	 * luego llama al método de RestauranteCRUD para insertar la mesa.
	 */
	public static void gestionInsentarMesa() {

		// Variable para el número de comensales
		int numComensales;

		System.out.println("----- INSERTAR MESA -----");

		// Solicitamos al usuario el número de comensales
		System.out.print("Número de comensales: ");
		numComensales = sc.nextInt();
		sc.nextLine(); // Limpiamos el buffer

		// Solicitamos al usuario si la mesa tiene reserva
		System.out.print("¿Tiene reserva? (1 = Sí, 0 = No): ");
		int reserva = sc.nextInt();
		sc.nextLine();

		// Llamamos al método de inserción y mostramos mensaje según el resultado
		if (RestauranteCRUD.insertarMesa(numComensales, reserva)) {
			System.out.println("Mesa insertada correctamente.");
		} else {
			System.out.println("Error al insertar mesa.");
		}
	}

	/**
	 * Gestiona la inserción de un nuevo producto en la base de datos.
	 * 
	 * Solicita al usuario el nombre y precio del producto, y luego llama al método
	 * de RestauranteCRUD para insertar el producto en la tabla.
	 */
	public static void gestionInsertarProducto() {

		// Variables para almacenar los datos del producto
		String nombre = "";
		double precio;

		System.out.println("---- INSERTAR PRODUCTO -----");

		// Solicitamos al usuario el nombre del producto
		System.out.println("Nombre del producto: ");
		nombre = sc.nextLine();

		// Solicitamos al usuario el precio del producto
		System.out.println("Precio del producto: ");
		precio = sc.nextDouble();
		sc.nextLine(); // Limpiamos el buffer

		// Llamamos al método de inserción y mostramos mensaje según el resultado
		if (RestauranteCRUD.insertarProducto(nombre, precio)) {
			System.out.println("Producto insertado correctamente");
		} else {
			System.out.println("Error al insertar producto.");
		}
	}

	/**
	 * Gestiona la inserción de una nueva factura en la base de datos.
	 * 
	 * Solicita al usuario el ID de la mesa, el tipo de pago y el importe, y luego
	 * llama al método de RestauranteCRUD para insertar la factura.
	 */
	public static void gestionInsertarFactura() {

		// Variables para almacenar los datos de la factura
		int idMesa;
		String tipoPago = "";
		double importe;

		System.out.println("----- INSERTAR FACTURA -----");

		// Solicitamos el ID de la mesa asociada a la factura
		System.out.println("ID de mesa: ");
		idMesa = sc.nextInt();
		sc.nextLine(); // Limpiamos el buffer

		// Solicitamos el tipo de pago
		System.out.println("Tipo de pago: ");
		tipoPago = sc.nextLine();

		// Solicitamos el importe de la factura
		System.out.println("Importe de factura: ");
		importe = sc.nextDouble();
		sc.nextLine(); // Limpiamos el buffer

		// Llamamos al método de inserción y mostramos mensaje según el resultado
		if (RestauranteCRUD.insertarFactura(idMesa, tipoPago, importe)) {
			System.out.println("Factura insertada correctamente");
		} else {
			System.out.println("Error al insertar factura.");
		}
	}

	/**
	 * Gestiona la inserción de un nuevo pedido en la base de datos.
	 * 
	 * Solicita al usuario el ID de la factura, el ID del producto y la cantidad, y
	 * luego llama al método de RestauranteCRUD para insertar el pedido.
	 */
	public static void gestionInsertarPedido() {

		// Variables para almacenar los datos del pedido
		int idFactura;
		int idProducto;
		int cantidad;

		System.out.println("----- INSERTAR PEDIDO -----");

		// Solicitamos el ID de la factura asociada al pedido
		System.out.println("ID de Factura: ");
		idFactura = sc.nextInt();
		sc.nextLine(); // Limpiamos el buffer

		// Solicitamos el ID del producto
		System.out.println("ID de Producto: ");
		idProducto = sc.nextInt();
		sc.nextLine(); // Limpiamos el buffer

		// Solicitamos la cantidad del pedido
		System.out.println("Cantidad de pedido: ");
		cantidad = sc.nextInt();
		sc.nextLine(); // Limpiamos el buffer

		// Llamamos al método de inserción y mostramos mensaje según el resultado
		if (RestauranteCRUD.insertarPedido(idFactura, idProducto, cantidad)) {
			System.out.println("Pedido insertado correctamente");
		} else {
			System.out.println("Error al insertar pedido.");
		}
	}

	/**
	 * Muestra un menú para listar registros de una tabla de la base de datos.
	 * 
	 * Permite al usuario elegir la tabla y, opcionalmente, aplicar un filtro
	 * especificando campo, operación y valor. Luego llama al método
	 * RestauranteCRUD.listar para obtener los resultados.
	 */
	public static void menuListar() {

		// Preguntamos al usuario qué tabla desea listar
		System.out.println("¿Qué tabla quieres listar?");
		System.out.println("1. Mesa");
		System.out.println("2. Producto");
		System.out.println("3. Factura");
		System.out.println("4. Pedido");
		System.out.print("Opción: ");

		int opcTabla = sc.nextInt();
		sc.nextLine(); // Limpiamos el buffer

		String tabla = "";
		switch (opcTabla) {
		case 1 -> tabla = "Mesa";
		case 2 -> tabla = "Producto";
		case 3 -> tabla = "Factura";
		case 4 -> tabla = "Pedido";
		default -> {
			// Si la opción no es válida, terminamos la función
			System.out.println("Tabla no válida.");
			return;
		}
		}

		// Preguntamos si desea aplicar un filtro
		System.out.println("¿Quieres filtrar?");
		System.out.println("1. Sí");
		System.out.println("2. No");
		System.out.print("Opción: ");

		int opcFiltro = sc.nextInt();
		sc.nextLine(); // Limpiamos el buffer

		String campo = "";
		String operacion = "";
		String valor = "";

		if (opcFiltro == 1) {
			// Solicitamos datos del filtro
			System.out.print("Campo por el que filtrar: ");
			campo = sc.nextLine();

			System.out.print("Operación (=, <, >, LIKE): ");
			operacion = sc.nextLine().toUpperCase();

			System.out.print("Valor: ");
			valor = sc.nextLine();
		}

		// Llamamos al método listar del CRUD
		String resultado = RestauranteCRUD.listar(tabla, campo, operacion, valor);

		// Mostramos los resultados por pantalla
		System.out.println("----- RESULTADO -----");
		System.out.println(resultado);
	}

	/**
	 * Gestiona la modificación de un registro en la base de datos.
	 * 
	 * Solicita al usuario la tabla, el campo y valor del filtro, el campo a
	 * modificar y el nuevo valor. Luego llama al método RestauranteCRUD.modificar
	 * para aplicar los cambios, y permite confirmar o deshacer la transacción.
	 */
	public static void modificar() {

		// Solicitamos los datos necesarios para identificar el registro a modificar
		System.out.println("Tabla:");
		String tabla = sc.nextLine();

		System.out.println("Campo filtro:");
		String campoFiltro = sc.nextLine();

		System.out.println("Valor del filtro:");
		String valorFiltro = sc.nextLine();

		System.out.println("Campo a modificar:");
		String campoModificar = sc.nextLine();

		System.out.println("Nuevo valor:");
		String nuevoValor = sc.nextLine();

		// Llamamos al método modificar del CRUD
		boolean modificado = RestauranteCRUD.modificar(tabla, campoFiltro, valorFiltro, campoModificar, nuevoValor);

		// Si no se modificó ningún registro, avisamos y salimos
		if (!modificado) {
			System.out.println("No se modificó ningún registro.");
			return;
		}

		// Preguntamos al usuario si desea confirmar los cambios
		System.out.println("¿Confirmar cambios? (s/n)");
		String resp = sc.nextLine();

		try (Connection con = Conexion.conexionBD()) {
			if (resp.equalsIgnoreCase("s")) {
				// Confirmamos la transacción
				con.commit();
				System.out.println("Cambios guardados.");
			} else {
				// Revertimos la transacción
				con.rollback();
				System.out.println("Cambios deshechos.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lista todos los registros de una tabla completa en la base de datos. Muestra
	 * todas las columnas y sus valores por consola. Se requiere una conexión válida
	 * a la base de datos.
	 *
	 * @param con   Conexión a la base de datos.
	 * @param tabla Nombre de la tabla que se desea listar.
	 */
	public static void listarTablaCompleta(Connection con, String tabla) {

		// Comprobamos que la conexión no sea null
		if (con == null) {
			System.out.println("ERROR: la conexión es null.");
			return;
		}

		try {
			String sql = "SELECT * FROM " + tabla;
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);

			// Recorremos cada registro
			while (rs.next()) {
				int cols = rs.getMetaData().getColumnCount();

				// Recorremos todas las columnas de cada registro
				for (int i = 1; i <= cols; i++) {
					System.out.print(rs.getMetaData().getColumnName(i) + ": " + rs.getString(i) + "  ");
				}
				System.out.println(); // Salto de línea entre registros
			}

		} catch (SQLException e) {
			// Mostramos el error si ocurre algún problema con la consulta
			System.out.println("Error al listar tabla completa: " + e.getMessage());
		}
	}

	/**
	 * Gestiona la eliminación de registros de una tabla desde el main.
	 * 
	 * Permite borrar toda la tabla o solo registros que cumplan un filtro. Después
	 * de la operación, se muestran los datos restantes y se solicita confirmación
	 * de los cambios para commit o rollback.
	 *
	 * @param con  Conexión a la base de datos.
	 * @param crud Instancia de RestauranteCRUD para usar sus métodos de borrado.
	 */
	public static void borrarDesdeMain(Connection con, RestauranteCRUD crud) {

		// Preguntamos qué tabla desea borrar el usuario
		System.out.println("¿Qué tabla quieres borrar?");
		String tabla = sc.nextLine();

		// Preguntamos si desea borrar toda la tabla
		System.out.println("¿Quieres borrar toda la tabla? (s/n)");
		boolean borrarTodo = sc.nextLine().equalsIgnoreCase("s");

		// Variables para filtro en caso de borrado parcial
		String campo = "";
		String valor = "";

		if (!borrarTodo) {
			// Solicitamos campo y valor para filtrar los registros a borrar
			System.out.println("Introduce el campo por el que borrar:");
			campo = sc.nextLine();

			System.out.println("Introduce el valor que debe tener:");
			valor = sc.nextLine();
		}

		// Llamamos al método borrar del CRUD
		boolean exito = crud.borrar(con, tabla, campo, valor, borrarTodo);

		if (!exito) {
			// Si no se pudo borrar, avisamos y hacemos rollback
			System.out.println("No se pudo borrar. Revisa claves foráneas.");
			try {
				con.rollback();
			} catch (SQLException e) {
			}
			return;
		}

		// Mostramos los datos restantes tras el borrado
		System.out.println("Datos tras el borrado:");
		listarTabla(con, tabla);

		// Solicitamos confirmación de los cambios
		System.out.println("¿Confirmar cambios? (s/n)");
		if (sc.nextLine().equalsIgnoreCase("s")) {
			try {
				con.commit();
				System.out.println("Cambios guardados.");
			} catch (SQLException e) {
			}
		} else {
			try {
				con.rollback();
				System.out.println("Cambios deshechos.");
			} catch (SQLException e) {
			}
		}
	}

	/**
	 * Lista todos los registros de una tabla específica en la base de datos.
	 * 
	 * Muestra todas las columnas y sus valores por consola usando
	 * PreparedStatement.
	 *
	 * @param con   Conexión a la base de datos.
	 * @param tabla Nombre de la tabla que se desea listar.
	 */
	public static void listarTabla(Connection con, String tabla) {
		try {
			// Preparar la sentencia SQL para obtener todos los registros de la tabla
			String sql = "SELECT * FROM " + tabla;
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			// Obtenemos la información de las columnas
			ResultSetMetaData meta = rs.getMetaData();
			int columnas = meta.getColumnCount();

			// Recorremos cada registro
			while (rs.next()) {
				for (int i = 1; i <= columnas; i++) {
					// Imprimimos nombre de columna y valor
					System.out.print(meta.getColumnName(i) + ": " + rs.getString(i) + "  ");
				}
				System.out.println(); // Salto de línea entre registros
			}

		} catch (SQLException e) {
			// Mostramos error si ocurre algún problema
			System.out.println("Error al listar.");
		}
	}

	/**
	 * Gestiona la eliminación de tablas desde el main.
	 * 
	 * Permite eliminar todas las tablas o solo una tabla concreta. Después de la
	 * operación, se solicita confirmación de los cambios para realizar commit o
	 * rollback.
	 *
	 * @param con  Conexión a la base de datos.
	 * @param crud Instancia de RestauranteCRUD para usar sus métodos de
	 *             eliminación.
	 */
	public static void eliminarTablasDesdeMain(Connection con, RestauranteCRUD crud) {

		// Preguntamos si el usuario desea eliminar todas las tablas
		System.out.println("¿Quieres eliminar todas las tablas? (s/n)");
		boolean eliminarTodo = sc.nextLine().equalsIgnoreCase("s");

		// Variable para la tabla específica a eliminar si no es "todas"
		String tabla = "";
		if (!eliminarTodo) {
			System.out.println("Introduce el nombre de la tabla a eliminar:");
			tabla = sc.nextLine();
		}

		// Llamamos al método eliminarTabla del CRUD
		boolean exito = crud.eliminarTabla(con, tabla, eliminarTodo);

		if (!exito) {
			// Si no se pudo eliminar, avisamos y hacemos rollback
			System.out.println("No se pudo eliminar la tabla. Puede haber restricciones de integridad.");
			try {
				con.rollback();
			} catch (SQLException e) {
			}
			return;
		}

		System.out.println("Tabla(s) eliminada(s) correctamente.");

		// Confirmar cambios con commit o rollback según respuesta del usuario
		System.out.println("¿Confirmar cambios? (s/n)");
		if (sc.nextLine().equalsIgnoreCase("s")) {
			try {
				con.commit();
				System.out.println("Cambios guardados.");
			} catch (SQLException e) {
			}
		} else {
			try {
				con.rollback();
				System.out.println("Cambios deshechos.");
			} catch (SQLException e) {
			}
		}
	}
}