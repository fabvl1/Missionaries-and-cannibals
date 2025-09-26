import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AlgoritmoSolucion {
    private final Estado estado_objetivo = new Estado(0, 0, 1, 3, 3);
    private int algoritmo;  // 1 para BFS, 2 para A*

    // ==================== SECCIÓN BFS ====================
    private Queue<Tree> colaBusqueda = new LinkedList<>();
    private ArrayList<Estado> estadosVisitados = new ArrayList<>();
    private Tree nodoActualBFS;

    // ==================== SECCIÓN A* ====================
    private PriorityQueue<Tree> colaPrioridad = new PriorityQueue<>(Comparator.comparingInt(n -> calcularF(n)));
    private Map<Estado, Integer> gScore = new HashMap<>();
    private Map<Estado, Tree> vinoDe = new HashMap<>();
    private Set<Estado> explorados = new HashSet<>();
    private Tree nodoActualAStar;

    /**
     * Constructor: Inicializa y ejecuta el algoritmo seleccionado.
     */
    public AlgoritmoSolucion(Tree nodoInicial, int algoritmo) {
        this.algoritmo = algoritmo;
        switch (algoritmo) {
            case 1:
                inicializarBFS(nodoInicial);
                ejecutarBFS();
                break;
            case 2:
                inicializarAStar(nodoInicial);
                ejecutarAStar();
                break;
            default:
                throw new IllegalArgumentException("Algoritmo no soportado: " + algoritmo);
        }
    }

    // ==================== MÉTODOS BFS ====================
    private void inicializarBFS(Tree nodoInicial) {
        colaBusqueda.offer(nodoInicial);
        estadosVisitados.add(nodoInicial.getEstado());
        nodoActualBFS = nodoInicial;
    }

    private void ejecutarBFS() {
        while (nodoActualBFS != null && !nodoActualBFS.getEstado().equals(estado_objetivo)) {
            expandirNodoBFS();
            nodoActualBFS = colaBusqueda.poll();
        }
    }

    private void expandirNodoBFS() {
        ArrayList<Estado> movimientosValidos = generarMovimientosValidos(nodoActualBFS.getEstado());
        for (Estado nuevoEstado : movimientosValidos) {
            if (!estadosVisitados.contains(nuevoEstado)) {
                Tree nuevoNodo = new Tree(nuevoEstado, nodoActualBFS, nodoActualBFS.getProfundidad() + 1);
                colaBusqueda.offer(nuevoNodo);
                estadosVisitados.add(nuevoEstado);
            }
        }
    }

    // ==================== MÉTODOS A* ====================
    private void inicializarAStar(Tree nodoInicial) {
        gScore.put(nodoInicial.getEstado(), 0);
        colaPrioridad.add(nodoInicial);
        vinoDe.put(nodoInicial.getEstado(), null);
        nodoActualAStar = nodoInicial;
    }

    private void ejecutarAStar() {
        while (!colaPrioridad.isEmpty() && !nodoActualAStar.getEstado().equals(estado_objetivo)) {
            nodoActualAStar = colaPrioridad.poll();
            explorados.add(nodoActualAStar.getEstado());
            expandirNodoAStar();
        }
    }

    private void expandirNodoAStar() {
        ArrayList<Estado> movimientosValidos = generarMovimientosValidos(nodoActualAStar.getEstado());
        for (Estado nuevoEstado : movimientosValidos) {
            if (explorados.contains(nuevoEstado)) continue;
            
            int gTentativo = gScore.get(nodoActualAStar.getEstado()) + 1;
            Tree nuevoNodo = new Tree(nuevoEstado, nodoActualAStar, nodoActualAStar.getProfundidad() + 1);
            
            if (!gScore.containsKey(nuevoEstado) || gTentativo < gScore.get(nuevoEstado)) {
                vinoDe.put(nuevoEstado, nodoActualAStar);
                gScore.put(nuevoEstado, gTentativo);
                colaPrioridad.add(nuevoNodo);
            }
        }
    }

    private int calcularF(Tree nodo) {
        int g = gScore.get(nodo.getEstado());
        int h = nodo.getEstado().heuristica();
        return g + h;
    }

    // ==================== MÉTODOS COMUNES ====================
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
     * Devuelve la lista de estados desde el inicial al objetivo, basado en el algoritmo.
     */
    public ArrayList<Estado> obtenerSolucion() {
        ArrayList<Estado> caminoSolucion = new ArrayList<>();
        if (algoritmo == 1) {  // BFS
            Tree nodo = nodoActualBFS;
            while (nodo != null) {
                caminoSolucion.add(0, nodo.getEstado());
                nodo = nodo.getPadre();
            }
        } else if (algoritmo == 2) {  // A*
            Tree nodo = nodoActualAStar;
            while (nodo != null) {
                caminoSolucion.add(0, nodo.getEstado());
                nodo = vinoDe.get(nodo.getEstado());
            }
        }
        return caminoSolucion;
    }

    /**
     * Indica si se encontró una solución, basado en el algoritmo.
     */
    public boolean solucionEncontrada() {
        if (algoritmo == 1) {
            return nodoActualBFS != null && nodoActualBFS.getEstado().equals(estado_objetivo);
        } else if (algoritmo == 2) {
            return nodoActualAStar != null && nodoActualAStar.getEstado().equals(estado_objetivo);
        }
        return false;
    }
}