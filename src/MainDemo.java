// MainDemo.java
import java.util.ArrayList;

public class MainDemo {
    public static void main(String[] args) {
        // Ejecuta y compara los 4 algoritmos
        for (int alg = 1; alg <= 4; alg++) {
            System.out.println("=== Ejecutando algoritmo " + alg + " ===");
            AlgoritmoSolucion solver = new AlgoritmoSolucion(alg);
            long t0 = System.currentTimeMillis();
            solver.resolver();
            long t1 = System.currentTimeMillis();

            System.out.println(solver.obtenerEstadisticas());
            System.out.println("Tiempo (ms): " + (t1 - t0));
            ArrayList<Estado> camino = solver.obtenerSolucion();
            if (camino.isEmpty()) {
                System.out.println("No se encontró solución.");
            } else {
                System.out.println("Camino (desde inicial hasta objetivo):");
                for (Estado e : camino) {
                    System.out.println("  " + e);
                }
            }
            System.out.println();
        }
    }
}
