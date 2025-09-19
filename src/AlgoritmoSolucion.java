import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class AlgoritmoSolucion {
    // Estructuras para la búsqueda en anchura
    private Queue<Tree> colaBusqueda = new LinkedList<>();
    private ArrayList<Estado> estadosVisitados = new ArrayList<>();
    private Tree nodoActual;
    private final Estado estado_objetivo = new Estado(0, 0, 1, 3, 3);

    /**
     * Ejecuta el algoritmo de búsqueda en anchura (BFS) para encontrar la solución.
     * El resultado se puede obtener con obtenerSolucion().
     */
    public AlgoritmoSolucion(Tree nodoInicial) {
        inicializarBusqueda(nodoInicial);
        ejecutarBusquedaEnAnchura();
    }

    /**
     * Inicializa las estructuras de datos para la búsqueda.
     */
    private void inicializarBusqueda(Tree nodoInicial) {
        colaBusqueda.offer(nodoInicial);
        estadosVisitados.add(nodoInicial.getEstado());
        nodoActual = nodoInicial;
    }

    /**
     * Ejecuta el ciclo principal de la búsqueda en anchura.
     */
    private void ejecutarBusquedaEnAnchura() {
        while (nodoActual != null && !nodoActual.getEstado().equals(estado_objetivo)) {
            expandirNodoActual();
            nodoActual = colaBusqueda.poll();
        }
    }

    /**
     * Expande el nodo actual generando todos sus hijos válidos.
     */
    private void expandirNodoActual() {
        ArrayList<Estado> movimientosValidos = generarMovimientosValidos(nodoActual.getEstado());
        for (Estado nuevoEstado : movimientosValidos) {
            if (!estadosVisitados.contains(nuevoEstado)) {
                Tree nuevoNodo = new Tree(nuevoEstado, nodoActual, nodoActual.getProfundidad() + 1);
                colaBusqueda.offer(nuevoNodo);
                estadosVisitados.add(nuevoEstado);
            }
        }
    }

    /**
     * Genera todos los movimientos válidos posibles desde un estado dado.
     */
    public ArrayList<Estado> generarMovimientosValidos(Estado estadoActual) {
        ArrayList<Estado> movimientosValidos = new ArrayList<>();
        int[][] movimientosPosibles = {
            {2, 0}, {0, 2}, {1, 1}, {1, 0}, {0, 1}
        };
        for (int[] movimiento : movimientosPosibles) {
            int misionerosMovidos = movimiento[0];
            int canibalesMovidos = movimiento[1];
            Estado nuevoEstado = generarNuevoEstado(estadoActual, misionerosMovidos, canibalesMovidos);
            if (nuevoEstado != null && esEstadoValido(nuevoEstado)) {
                movimientosValidos.add(nuevoEstado);
            }
        }
        return movimientosValidos;
    }

    /**
     * Genera un nuevo estado basado en el movimiento de personas.
     */
    private Estado generarNuevoEstado(Estado estadoActual, int misionerosMovidos, int canibalesMovidos) {
        Estado nuevoEstado = null;
        if (estadoActual.getPosicion_barca() == 0) {
            // Barca en la izquierda, mover a la derecha
            if (hayPersonasSuficientes(estadoActual.getMisioneros_isq(), estadoActual.getCanibales_isq(),
                    misionerosMovidos, canibalesMovidos)) {
                nuevoEstado = new Estado(
                    estadoActual.getMisioneros_isq() - misionerosMovidos,
                    estadoActual.getCanibales_isq() - canibalesMovidos,
                    1,
                    estadoActual.getMisioneros_der() + misionerosMovidos,
                    estadoActual.getCanibales_der() + canibalesMovidos
                );
            }
        } else {
            // Barca en la derecha, mover a la izquierda
            if (hayPersonasSuficientes(estadoActual.getMisioneros_der(), estadoActual.getCanibales_der(),
                    misionerosMovidos, canibalesMovidos)) {
                nuevoEstado = new Estado(
                    estadoActual.getMisioneros_isq() + misionerosMovidos,
                    estadoActual.getCanibales_isq() + canibalesMovidos,
                    0,
                    estadoActual.getMisioneros_der() - misionerosMovidos,
                    estadoActual.getCanibales_der() - canibalesMovidos
                );
            }
        }
        return nuevoEstado;
    }

    /**
     * Verifica si hay suficientes personas en una orilla para realizar el movimiento.
     */
    private boolean hayPersonasSuficientes(int misionerosDisponibles, int canibalesDisponibles,
                                           int misionerosRequeridos, int canibalesRequeridos) {
        return misionerosDisponibles >= misionerosRequeridos &&
               canibalesDisponibles >= canibalesRequeridos;
    }

    /**
     * Valida si un estado cumple con las restricciones del problema.
     */
    private boolean esEstadoValido(Estado estado) {
        int mi = estado.getMisioneros_isq();
        int ci = estado.getCanibales_isq();
        int md = estado.getMisioneros_der();
        int cd = estado.getCanibales_der();
        if (!valoresEnRango(mi, ci, md, cd)) return false;
        if (mi > 0 && ci > mi) return false;
        if (md > 0 && cd > md) return false;
        return true;
    }

    /**
     * Verifica que los valores estén en el rango válido [0,3].
     */
    private boolean valoresEnRango(int... valores) {
        for (int valor : valores) {
            if (valor < 0 || valor > 3) return false;
        }
        return true;
    }

    /**
     * Devuelve la lista de estados desde el inicial al objetivo.
     */
    public ArrayList<Estado> obtenerSolucion() {
        ArrayList<Estado> caminoSolucion = new ArrayList<>();
        Tree nodo = nodoActual;
        while (nodo != null) {
            caminoSolucion.add(0, nodo.getEstado());
            nodo = nodo.getPadre();
        }
        return caminoSolucion;
    }

    /**
     * Indica si se encontró una solución.
     */
    public boolean solucionEncontrada() {
        return nodoActual != null && nodoActual.getEstado().equals(estado_objetivo);
    }
}