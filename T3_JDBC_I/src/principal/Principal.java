package principal;

import java.util.Scanner;

public class Principal {

	public static void main(String[] args) {

		// Creamos la variable opc como int para almacenar la opción seleccionada por el
		// usuario.
		int opc;

		// Creamos el Scanner para poder leer datos por consola
		Scanner sc = new Scanner(System.in);

		do {
			// Llamamos a la función menú.
			Menu();
			// Le pedimos al usuario que introduzca la opción que quiere seleccionar en el
			// menú y la leemos.
			System.out.println("Introduce una opción --> ");
			opc = sc.nextInt();

		} while (opc != 0);

		// Cerramos el Scanner
		sc.close();
	}

	public static void Menu() {
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

}
