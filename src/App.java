public class App {
    public static void main(String[] args) {
        Estado inicial = new Estado(3, 3, 0, 0, 0);
        Tree nodo_inicial = new Tree(inicial, null, 0);

        AlgoritmoSolucion solucionador = new AlgoritmoSolucion(nodo_inicial);

        if (solucionador.solucionEncontrada()) {
            System.out.println("Solution found:\n");
            int paso = 0;
            for (Estado estado : solucionador.obtenerSolucion()) {
                System.out.println("Pass " + paso + ":");
                System.out.println(
                    "  bank left  -> Misionariess: " + estado.getMisioneros_isq() +
                    ", cannibals: " + estado.getCanibales_isq()
                );
                System.out.println(
                    "  bank right    -> Misionariess: " + estado.getMisioneros_der() +
                    ", cannibals: " + estado.getCanibales_der()
                );
                System.out.println(
                    "  Position of the boat: " + (estado.getPosicion_barca() == 0 ? "left" : "right")
                );
                System.out.println("-------------------------------------------");
                paso++;
            }
        } else {
            System.out.println("Don't found solution.");
        }
    }
  }