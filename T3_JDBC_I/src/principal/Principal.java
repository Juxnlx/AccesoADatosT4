package principal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import conexion.Conexion;
import crud.RestauranteCRUD;

public class Principal {

	private static final Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {

		// Variable para almacenar la opción seleccionada por el usuario.
		int opc;

		do {
			// Llamamos a la función menú.
			Menu();
			// Le pedimos al usuario que introduzca la opción que quiere seleccionar.
			System.out.print("Introduce una opción --> ");
			opc = sc.nextInt();
			// Limpiamos el buffer
			sc.nextLine();

			switch (opc) {
			case 1:
				gestionCreacionTablas();
				break;
			case 2:
				gestionInsertarMesa();
				break;
			case 3:
				gestionInsertarProducto();
				break;
			case 4:
				gestionInsertarFactura();
				break;
			case 5:
				gestionInsertarPedido();
				break;
			case 6:
				menuListar();
				break;
			case 7:
				modificar();
				break;
			case 8:
				borrarDesdeMain();
				break;
			case 9:
				eliminarTablasDesdeMain();
				break;
			case 0:
				System.out.println("Saliendo del programa...");
				break;
			default:
				System.out.println("Opción no válida.");
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
		System.out.println("\n----------MENÚ----------");
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
	 */
	public static void gestionCreacionTablas() {

		// Variable para la opción del usuario.
		int opc;

		// Variable para almacenar el nombre de la tabla si se elige crear una concreta.
		String nombreTabla = "";

		// Preguntamos al usuario qué desea hacer.
		System.out.println("\n¿Quieres crear todas las tablas o una tabla concreta?");
		System.out.println("1. Todas las tablas");
		System.out.println("2. Tabla concreta");
		System.out.print("Opción: ");

		// Leemos la opción.
		opc = sc.nextInt();
		sc.nextLine(); // Limpiamos el buffer

		if (opc == 1) {
			// Crear todas las tablas.
			RestauranteCRUD.crearTodasLasTablas();

		} else if (opc == 2) {
			// Crear una tabla concreta: pedimos el nombre.
			System.out.print("Introduce el nombre de la tabla (mesa, producto, factura, pedido): ");
			nombreTabla = sc.nextLine().toLowerCase();

			// Verificamos si se puede crear la tabla según dependencias.
			if (RestauranteCRUD.puedeCrearTabla(nombreTabla)) {

				// Abrimos conexión para crear la tabla.
				try (Connection con = Conexion.conexionBD()) {

					// Ejecutamos la creación según el nombre de la tabla.
					if (nombreTabla.equals("mesa")) {
						con.createStatement().executeUpdate(RestauranteCRUD.crearTablaMesa());
						System.out.println("Tabla Mesa creada correctamente.");

					} else if (nombreTabla.equals("producto")) {
						con.createStatement().executeUpdate(RestauranteCRUD.crearTablaProducto());
						System.out.println("Tabla Producto creada correctamente.");

					} else if (nombreTabla.equals("factura")) {
						con.createStatement().executeUpdate(RestauranteCRUD.crearTablaFactura());
						System.out.println("Tabla Factura creada correctamente.");

					} else if (nombreTabla.equals("pedido")) {
						con.createStatement().executeUpdate(RestauranteCRUD.crearTablaPedido());
						System.out.println("Tabla Pedido creada correctamente.");

					} else {
						System.out.println("Tabla no válida.");
					}

				} catch (SQLException e) {
					System.out.println("Error al crear la tabla: " + e.getMessage());
				}

			} else {
				// La tabla depende de otras tablas que no existen.
				System.out.println("No se puede crear la tabla " + nombreTabla
						+ " porque depende de otras tablas que no existen.");
			}

		} else {
			// Opción no válida
			System.out.println("Opción no válida");
		}
	}

	/**
	 * Gestiona la inserción de una nueva mesa en la base de datos.
	 */
	public static void gestionInsertarMesa() {

		// Variable para el número de comensales
		int numComensales;
		int reserva;

		System.out.println("\n----- INSERTAR MESA -----");

		// Solicitamos al usuario el número de comensales
		System.out.print("Número de comensales: ");
		numComensales = sc.nextInt();
		sc.nextLine(); // Limpiamos el buffer

		// Solicitamos al usuario si la mesa tiene reserva
		System.out.print("¿Tiene reserva? (1 = Sí, 0 = No): ");
		reserva = sc.nextInt();
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
	 */
	public static void gestionInsertarProducto() {

		// Variables para almacenar los datos del producto
		String nombre = "";
		double precio;

		System.out.println("\n----- INSERTAR PRODUCTO -----");

		// Solicitamos al usuario el nombre del producto
		System.out.print("Nombre del producto: ");
		nombre = sc.nextLine();

		// Solicitamos al usuario el precio del producto
		System.out.print("Precio del producto: ");
		precio = sc.nextDouble();
		sc.nextLine(); // Limpiamos el buffer

		// Llamamos al método de inserción y mostramos mensaje según el resultado
		if (RestauranteCRUD.insertarProducto(nombre, precio)) {
			System.out.println("Producto insertado correctamente.");
		} else {
			System.out.println("Error al insertar producto.");
		}
	}

	/**
	 * Gestiona la inserción de una nueva factura en la base de datos.
	 */
	public static void gestionInsertarFactura() {

		// Variables para almacenar los datos de la factura
		int idMesa;
		String tipoPago = "";
		double importe;

		System.out.println("\n----- INSERTAR FACTURA -----");

		// Primero mostramos las mesas disponibles
		System.out.println("Mesas disponibles:");
		System.out.println(RestauranteCRUD.listarMesa());

		// Solicitamos el ID de la mesa asociada a la factura
		System.out.print("ID de mesa: ");
		idMesa = sc.nextInt();
		sc.nextLine(); // Limpiamos el buffer

		// Solicitamos el tipo de pago
		System.out.print("Tipo de pago (Efectivo/Tarjeta): ");
		tipoPago = sc.nextLine();

		// Solicitamos el importe de la factura
		System.out.print("Importe de factura: ");
		importe = sc.nextDouble();
		sc.nextLine(); // Limpiamos el buffer

		// Llamamos al método de inserción y mostramos mensaje según el resultado
		if (RestauranteCRUD.insertarFactura(idMesa, tipoPago, importe)) {
			System.out.println("Factura insertada correctamente.");
		} else {
			System.out.println("Error al insertar factura. Verifica que la mesa existe.");
		}
	}

	/**
	 * Gestiona la inserción de un nuevo pedido en la base de datos.
	 */
	public static void gestionInsertarPedido() {

		// Variables para almacenar los datos del pedido
		int idFactura;
		int idProducto;
		int cantidad;

		System.out.println("\n----- INSERTAR PEDIDO -----");

		// Mostramos las facturas disponibles
		System.out.println("Facturas disponibles:");
		System.out.println(RestauranteCRUD.listarFactura());

		// Solicitamos el ID de la factura asociada al pedido
		System.out.print("ID de Factura: ");
		idFactura = sc.nextInt();
		sc.nextLine(); // Limpiamos el buffer

		// Mostramos los productos disponibles
		System.out.println("Productos disponibles:");
		System.out.println(RestauranteCRUD.listarProducto());

		// Solicitamos el ID del producto
		System.out.print("ID de Producto: ");
		idProducto = sc.nextInt();
		sc.nextLine(); // Limpiamos el buffer

		// Solicitamos la cantidad del pedido
		System.out.print("Cantidad: ");
		cantidad = sc.nextInt();
		sc.nextLine(); // Limpiamos el buffer

		// Llamamos al método de inserción y mostramos mensaje según el resultado
		if (RestauranteCRUD.insertarPedido(idFactura, idProducto, cantidad)) {
			System.out.println("Pedido insertado correctamente.");
		} else {
			System.out.println("Error al insertar pedido. Verifica que la factura y producto existen.");
		}
	}

	/**
	 * Muestra un menú para listar registros de diferentes tablas.
	 */
	public static void menuListar() {

		// Preguntamos al usuario qué tabla desea listar
		System.out.println("\n¿Qué tabla quieres listar?");
		System.out.println("1. Mesa");
		System.out.println("2. Producto");
		System.out.println("3. Factura");
		System.out.println("4. Pedido");
		System.out.print("Opción: ");

		int opcTabla = sc.nextInt();
		sc.nextLine(); // Limpiamos el buffer

		if (opcTabla == 1) {
			// Listar Mesa
			menuListarMesa();

		} else if (opcTabla == 2) {
			// Listar Producto
			menuListarProducto();

		} else if (opcTabla == 3) {
			// Listar Factura
			menuListarFactura();

		} else if (opcTabla == 4) {
			// Listar Pedido
			menuListarPedido();

		} else {
			System.out.println("Opción no válida.");
		}
	}

	/**
	 * Menú para listar registros de la tabla Mesa.
	 */
	public static void menuListarMesa() {

		System.out.println("\n¿Cómo quieres listar las mesas?");
		System.out.println("1. Todas las mesas");
		System.out.println("2. Por número de comensales");
		System.out.println("3. Por estado de reserva");
		System.out.print("Opción: ");

		int opc = sc.nextInt();
		sc.nextLine();

		if (opc == 1) {
			// Listar todas las mesas
			System.out.println("\n----- MESAS -----");
			System.out.println(RestauranteCRUD.listarMesa());

		} else if (opc == 2) {
			// Listar por número de comensales
			System.out.print("Número de comensales: ");
			int numComensales = sc.nextInt();
			sc.nextLine();

			System.out.println("\n----- MESAS -----");
			System.out.println(RestauranteCRUD.listarMesaPorComensales(numComensales));

		} else if (opc == 3) {
			// Listar por estado de reserva
			System.out.print("Estado de reserva (1 = Reservada, 0 = Libre): ");
			int reserva = sc.nextInt();
			sc.nextLine();

			System.out.println("\n----- MESAS -----");
			System.out.println(RestauranteCRUD.listarMesaPorReserva(reserva));

		} else {
			System.out.println("Opción no válida.");
		}
	}

	/**
	 * Menú para listar registros de la tabla Producto.
	 */
	public static void menuListarProducto() {

		System.out.println("\n¿Cómo quieres listar los productos?");
		System.out.println("1. Todos los productos");
		System.out.println("2. Por nombre");
		System.out.println("3. Por precio menor que...");
		System.out.println("4. Por precio mayor que...");
		System.out.print("Opción: ");

		int opc = sc.nextInt();
		sc.nextLine();

		if (opc == 1) {
			// Listar todos los productos
			System.out.println("\n----- PRODUCTOS -----");
			System.out.println(RestauranteCRUD.listarProducto());

		} else if (opc == 2) {
			// Listar por nombre
			System.out.print("Nombre del producto: ");
			String nombre = sc.nextLine();

			System.out.println("\n----- PRODUCTOS -----");
			System.out.println(RestauranteCRUD.listarProductoPorNombre(nombre));

		} else if (opc == 3) {
			// Listar por precio menor
			System.out.print("Precio máximo: ");
			double precio = sc.nextDouble();
			sc.nextLine();

			System.out.println("\n----- PRODUCTOS -----");
			System.out.println(RestauranteCRUD.listarProductoPorPrecioMenor(precio));

		} else if (opc == 4) {
			// Listar por precio mayor
			System.out.print("Precio mínimo: ");
			double precio = sc.nextDouble();
			sc.nextLine();

			System.out.println("\n----- PRODUCTOS -----");
			System.out.println(RestauranteCRUD.listarProductoPorPrecioMayor(precio));

		} else {
			System.out.println("Opción no válida.");
		}
	}

	/**
	 * Menú para listar registros de la tabla Factura.
	 */
	public static void menuListarFactura() {

		System.out.println("\n¿Cómo quieres listar las facturas?");
		System.out.println("1. Todas las facturas");
		System.out.println("2. Por tipo de pago");
		System.out.println("3. Por importe menor que...");
		System.out.println("4. Por importe mayor que...");
		System.out.print("Opción: ");

		int opc = sc.nextInt();
		sc.nextLine();

		if (opc == 1) {
			// Listar todas las facturas
			System.out.println("\n----- FACTURAS -----");
			System.out.println(RestauranteCRUD.listarFactura());

		} else if (opc == 2) {
			// Listar por tipo de pago
			System.out.print("Tipo de pago: ");
			String tipoPago = sc.nextLine();

			System.out.println("\n----- FACTURAS -----");
			System.out.println(RestauranteCRUD.listarFacturaPorTipoPago(tipoPago));

		} else if (opc == 3) {
			// Listar por importe menor
			System.out.print("Importe máximo: ");
			double importe = sc.nextDouble();
			sc.nextLine();

			System.out.println("\n----- FACTURAS -----");
			System.out.println(RestauranteCRUD.listarFacturaPorImporteMenor(importe));

		} else if (opc == 4) {
			// Listar por importe mayor
			System.out.print("Importe mínimo: ");
			double importe = sc.nextDouble();
			sc.nextLine();

			System.out.println("\n----- FACTURAS -----");
			System.out.println(RestauranteCRUD.listarFacturaPorImporteMayor(importe));

		} else {
			System.out.println("Opción no válida.");
		}
	}

	/**
	 * Menú para listar registros de la tabla Pedido.
	 */
	public static void menuListarPedido() {

		System.out.println("\n¿Cómo quieres listar los pedidos?");
		System.out.println("1. Todos los pedidos");
		System.out.println("2. Por cantidad");
		System.out.print("Opción: ");

		int opc = sc.nextInt();
		sc.nextLine();

		if (opc == 1) {
			// Listar todos los pedidos
			System.out.println("\n----- PEDIDOS -----");
			System.out.println(RestauranteCRUD.listarPedido());

		} else if (opc == 2) {
			// Listar por cantidad
			System.out.print("Cantidad: ");
			int cantidad = sc.nextInt();
			sc.nextLine();

			System.out.println("\n----- PEDIDOS -----");
			System.out.println(RestauranteCRUD.listarPedidoPorCantidad(cantidad));

		} else {
			System.out.println("Opción no válida.");
		}
	}

	/**
	 * Gestiona la modificación de un registro en la base de datos.
	 */
	public static void modificar() {

		System.out.println("\n----- MODIFICAR -----");

		// Solicitamos los datos necesarios para identificar el registro a modificar
		System.out.print("Tabla: ");
		String tabla = sc.nextLine();

		System.out.print("Campo filtro: ");
		String campoFiltro = sc.nextLine();

		System.out.print("Valor del filtro: ");
		String valorFiltro = sc.nextLine();

		System.out.print("Campo a modificar: ");
		String campoModificar = sc.nextLine();

		System.out.print("Nuevo valor: ");
		String nuevoValor = sc.nextLine();

		// Abrimos conexión para la transacción
		try (Connection con = Conexion.conexionBD()) {

			// Llamamos al método modificar del CRUD
			boolean modificado = RestauranteCRUD.modificar(con, tabla, campoFiltro, valorFiltro, campoModificar,
					nuevoValor);

			// Si no se modificó ningún registro, avisamos y salimos
			if (!modificado) {
				System.out.println("No se modificó ningún registro.");
				con.rollback();
				return;
			}

			// Mostramos los datos modificados
			System.out.println("\nDatos después de la modificación:");
			listarTabla(con, tabla);

			// Preguntamos al usuario si desea confirmar los cambios
			System.out.print("\n¿Confirmar cambios? (s/n): ");
			String resp = sc.nextLine();

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
			System.out.println("Error al modificar: " + e.getMessage());
		}
	}

	/**
	 * Lista todos los registros de una tabla específica en la base de datos.
	 *
	 * @param con   Conexión a la base de datos.
	 * @param tabla Nombre de la tabla que se desea listar.
	 */
	public static void listarTabla(Connection con, String tabla) {

		// Elegimos el método apropiado según la tabla
		if (tabla.equalsIgnoreCase("Mesa")) {
			System.out.println(RestauranteCRUD.listarMesa());

		} else if (tabla.equalsIgnoreCase("Producto")) {
			System.out.println(RestauranteCRUD.listarProducto());

		} else if (tabla.equalsIgnoreCase("Factura")) {
			System.out.println(RestauranteCRUD.listarFactura());

		} else if (tabla.equalsIgnoreCase("Pedido")) {
			System.out.println(RestauranteCRUD.listarPedido());

		} else {
			System.out.println("Tabla no válida.");
		}
	}

	/**
	 * Gestiona la eliminación de registros de una tabla.
	 */
	public static void borrarDesdeMain() {

		System.out.println("\n----- BORRAR -----");

		// Preguntamos qué tabla desea borrar el usuario
		System.out.print("¿Qué tabla quieres borrar? ");
		String tabla = sc.nextLine();

		// Preguntamos si desea borrar toda la tabla
		System.out.print("¿Quieres borrar toda la tabla? (s/n): ");
		boolean borrarTodo = sc.nextLine().equalsIgnoreCase("s");

		// Variables para filtro en caso de borrado parcial
		String campo = "";
		String valor = "";

		if (!borrarTodo) {
			// Solicitamos campo y valor para filtrar los registros a borrar
			System.out.print("Introduce el campo por el que borrar: ");
			campo = sc.nextLine();

			System.out.print("Introduce el valor que debe tener: ");
			valor = sc.nextLine();
		}

		// Abrimos conexión para la transacción
		try (Connection con = Conexion.conexionBD()) {

			// Llamamos al método borrar del CRUD
			boolean exito = RestauranteCRUD.borrar(con, tabla, campo, valor, borrarTodo);

			if (!exito) {
				// Si no se pudo borrar, avisamos y hacemos rollback
				System.out.println("No se pudo borrar. Revisa las claves foráneas.");
				con.rollback();
				return;
			}

			// Mostramos los datos restantes tras el borrado
			System.out.println("\nDatos tras el borrado:");
			listarTabla(con, tabla);

			// Solicitamos confirmación de los cambios
			System.out.print("\n¿Confirmar cambios? (s/n): ");
			if (sc.nextLine().equalsIgnoreCase("s")) {
				con.commit();
				System.out.println("Cambios guardados.");
			} else {
				con.rollback();
				System.out.println("Cambios deshechos.");
			}

		} catch (SQLException e) {
			System.out.println("Error al borrar: " + e.getMessage());
		}
	}

	/**
	 * Gestiona la eliminación de tablas.
	 */
	public static void eliminarTablasDesdeMain() {

		System.out.println("\n----- ELIMINAR TABLA -----");

		// Preguntamos si el usuario desea eliminar todas las tablas
		System.out.print("¿Quieres eliminar todas las tablas? (s/n): ");
		boolean eliminarTodo = sc.nextLine().equalsIgnoreCase("s");

		// Variable para la tabla específica a eliminar si no es "todas"
		String tabla = "";
		if (!eliminarTodo) {
			System.out.print("Introduce el nombre de la tabla a eliminar: ");
			tabla = sc.nextLine();
		}

		// Abrimos conexión para la transacción
		try (Connection con = Conexion.conexionBD()) {

			// Llamamos al método eliminarTabla del CRUD
			boolean exito = RestauranteCRUD.eliminarTabla(con, tabla, eliminarTodo);

			if (!exito) {
				// Si no se pudo eliminar, avisamos y hacemos rollback
				System.out.println("No se pudo eliminar la tabla. Puede haber restricciones de integridad.");
				con.rollback();
				return;
			}

			System.out.println("Tabla(s) eliminada(s) correctamente.");

			// Confirmar cambios con commit o rollback según respuesta del usuario
			System.out.print("¿Confirmar cambios? (s/n): ");
			if (sc.nextLine().equalsIgnoreCase("s")) {
				con.commit();
				System.out.println("Cambios guardados.");
			} else {
				con.rollback();
				System.out.println("Cambios deshechos.");
			}

		} catch (SQLException e) {
			System.out.println("Error al eliminar tablas: " + e.getMessage());
		}
	}
}