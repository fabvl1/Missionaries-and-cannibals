import java.util.*;

/**
 * Contiene implementaciones para BFS, A*, DFS (limitada) y un AG básico.
 * Asume problema 3 misioneros / 3 caníbales con orden Estado(mi,ci,pos,md,cd).
 */
public class AlgoritmoSolucion {
    private final Estado estadoInicial;
    private final Estado estadoObjetivo;
    private final int algoritmo; // 1=BFS, 2=A*, 3=DFS, 4=Genético

    private Tree nodoActualBFS;
    private Tree nodoActualAStar;
    private Tree nodoActualDFS;
    private Cromosoma mejorSolucionAG;

    private int nodosExpandidosBFS = 0;
    private int nodosExpandidosAStar = 0;
    private int nodosExpandidosDFS = 0;

    // AG params
    private static final int POPULATION_SIZE = 100;
    private static final int MAX_GENERATIONS = 500;
    private static final double MUTATION_RATE = 0.12;
    private static final double CROSSOVER_RATE = 0.85;

    // Un único Random para toda la clase (mejor rendimiento / reproducibilidad si se fija la semilla)
    private final Random rnd = new Random();

    public AlgoritmoSolucion(int algoritmo) {
        this.estadoInicial = new Estado(3, 3, Estado.BARCA_IZQ, 0, 0);
        this.estadoObjetivo = new Estado(0, 0, Estado.BARCA_DER, 3, 3);
        this.algoritmo = algoritmo;
    }

    public void resolver() {
        switch (algoritmo) {
            case 1: resolverBFS(); break;
            case 2: resolverAStar(); break;
            case 3: resolverDFS(); break;
            case 4: resolverGenetico(); break;
            default: resolverBFS(); break;
        }
    }

    // -------- BFS --------
    private void resolverBFS() {
        Queue<Tree> open = new LinkedList<>();
        Set<Estado> visited = new HashSet<>();
        nodoActualBFS = null;
        nodosExpandidosBFS = 0;

        Tree inicio = new Tree(estadoInicial, null);
        inicio.setG(0);
        inicio.setF(estadoInicial.heuristicaPersonasIzq());
        open.add(inicio);
        visited.add(estadoInicial);

        while (!open.isEmpty()) {
            Tree actual = open.poll();
            nodosExpandidosBFS++;

            if (actual.getEstado().equals(estadoObjetivo)) {
                nodoActualBFS = actual;
                return;
            }

            for (Tree hijo : generarHijos(actual)) {
                if (!visited.contains(hijo.getEstado())) {
                    visited.add(hijo.getEstado());
                    open.add(hijo);
                }
            }
        }
        nodoActualBFS = null;
    }

    // -------- A* --------
    private void resolverAStar() {
        PriorityQueue<Tree> openSet = new PriorityQueue<>();
        Map<Estado, Integer> gScore = new HashMap<>();
        Set<Estado> closedSet = new HashSet<>();

        nodoActualAStar = null;
        nodosExpandidosAStar = 0;

        Tree inicio = new Tree(estadoInicial, null);
        inicio.setG(0);
        inicio.setF(heuristicViajesRestantes(estadoInicial));
        gScore.put(estadoInicial, 0);
        openSet.add(inicio);

        while (!openSet.isEmpty()) {
            Tree actual = openSet.poll();

            Integer gEnMapa = gScore.get(actual.getEstado());
            if (gEnMapa == null) continue;
            if (actual.getG() != gEnMapa) continue; // nodo obsoleto

            nodosExpandidosAStar++;

            if (actual.getEstado().equals(estadoObjetivo)) {
                nodoActualAStar = actual;
                return;
            }

            closedSet.add(actual.getEstado());

            for (Tree vecino : generarHijos(actual)) {
    Estado estVec = vecino.getEstado();

    int tentativeG = actual.getG() + 1;
    Integer gVec = gScore.get(estVec);

    // Si encontramos un camino mejor (o no hay registro), actualizamos y (re)insertamos en openSet.
    if (gVec == null || tentativeG < gVec) {
        vecino.setPadre(actual);
        vecino.setG(tentativeG);
        int f = tentativeG + heuristicViajesRestantes(estVec);
        vecino.setF(f);
        gScore.put(estVec, tentativeG);
        // permitir reabrir: si estaba en closedSet, quitarlo para que pueda expandirse de nuevo
        closedSet.remove(estVec);
        openSet.add(vecino);
    }
}

        }
        nodoActualAStar = null;
    }

    /** Heurística admisible: viajes mínimos restantes = ceil(personasIzq / 2.0) */
    private int heuristicViajesRestantes(Estado e) {
        int personasIzq = e.getMisioneros_isq() + e.getCanibales_isq();
        return (int) Math.ceil(personasIzq / 2.0);
    }

    // -------- DFS (limitada) --------
    private void resolverDFS() {
        Deque<Tree> pila = new ArrayDeque<>();
        Set<Estado> visited = new HashSet<>();
        final int LIMITE_PROF = 20;
        nodoActualDFS = null;
        nodosExpandidosDFS = 0;

        Tree raiz = new Tree(estadoInicial, null);
        raiz.setG(0);
        pila.push(raiz);
        visited.add(raiz.getEstado()); // marcar la raíz

        while (!pila.isEmpty()) {
            Tree actual = pila.pop();
            nodosExpandidosDFS++;

            if (actual.getEstado().equals(estadoObjetivo)) {
                nodoActualDFS = actual;
                return;
            }

            if (actual.getProfundidad() < LIMITE_PROF) {
                for (Tree hijo : generarHijos(actual)) {
                    if (!visited.contains(hijo.getEstado())) {
                        visited.add(hijo.getEstado()); // marcar al pushear
                        pila.push(hijo);
                    }
                }
            }
        }
        nodoActualDFS = null;
    }

    // -------- Algoritmo Genético --------
    private void resolverGenetico() {
        List<Cromosoma> poblacion = generarPoblacionInicial();
        mejorSolucionAG = null;

        for (int gen = 0; gen < MAX_GENERATIONS; gen++) {
            for (Cromosoma c : poblacion) c.fitness = calcularFitness(c);
            poblacion.sort(Comparator.comparingDouble((Cromosoma c) -> c.fitness).reversed());

            if (isPerfect(poblacion.get(0))) {
                mejorSolucionAG = poblacion.get(0);
                return;
            }

            List<Cromosoma> nueva = new ArrayList<>();
            int elite = Math.max(1, (int) (POPULATION_SIZE * 0.1));
            for (int i = 0; i < elite && i < poblacion.size(); i++) nueva.add(poblacion.get(i).clonarShallow());

            while (nueva.size() < POPULATION_SIZE) {
                Cromosoma p1 = seleccionarPorTorneo(poblacion);
                Cromosoma p2 = seleccionarPorTorneo(poblacion);
                Cromosoma hijo;
                if (rnd.nextDouble() < CROSSOVER_RATE) hijo = cruzar(p1, p2);
                else hijo = p1.clonarShallow();
                mutar(hijo);
                nueva.add(hijo);
            }
            poblacion = nueva;
        }

        poblacion.sort(Comparator.comparingDouble((Cromosoma c) -> c.fitness).reversed());
        mejorSolucionAG = poblacion.get(0);
    }

    private boolean isPerfect(Cromosoma c) {
        if (c == null) return false;
        Estado resultado = simularCromosoma(c);
        return resultado.equals(estadoObjetivo);
    }

    private List<Cromosoma> generarPoblacionInicial() {
        List<Cromosoma> pobl = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            int longitud = 8 + rnd.nextInt(13); // 8..20
            List<int[]> genes = new ArrayList<>();
            for (int j = 0; j < longitud; j++) {
                int m, c;
                do {
                    m = rnd.nextInt(3);
                    c = rnd.nextInt(3);
                } while (m + c < 1 || m + c > 2);
                genes.add(new int[]{m, c});
            }
            pobl.add(new Cromosoma(genes));
        }
        return pobl;
    }

    private double calcularFitness(Cromosoma crom) {
        Estado fin = simularCromosoma(crom);
        if (fin.equals(estadoObjetivo)) {
            double penal = 1.0 - crom.genes.size() * 0.01;
            return Math.max(0.9, penal);
        }
        int d = Math.abs(fin.getMisioneros_der() - estadoObjetivo.getMisioneros_der())
              + Math.abs(fin.getCanibales_der() - estadoObjetivo.getCanibales_der());
        return 1.0 / (1.0 + d);
    }

    private Estado simularCromosoma(Cromosoma crom) {
        Estado actual = new Estado(3, 3, Estado.BARCA_IZQ, 0, 0);
        for (int[] mov : crom.genes) {
            if (mov == null) break;
            int m = mov[0], c = mov[1];
            if (m + c < 1 || m + c > 2) break;
            Estado siguiente = generarNuevoEstado(actual, m, c);
            if (siguiente != null && esEstadoValido(siguiente)) actual = siguiente;
            else break;
        }
        return actual;
    }

    private Cromosoma seleccionarPorTorneo(List<Cromosoma> pobl) {
        int k = 5;
        Cromosoma mejor = null;
        double mejorF = -Double.MAX_VALUE;
        for (int i = 0; i < k; i++) {
            Cromosoma cand = pobl.get(rnd.nextInt(pobl.size()));
            if (cand.fitness > mejorF) { mejor = cand; mejorF = cand.fitness; }
        }
        return mejor;
    }

    private Cromosoma cruzar(Cromosoma p1, Cromosoma p2) {
        int maxLen = Math.max(p1.genes.size(), p2.genes.size());
        int punto = rnd.nextInt(Math.max(1, maxLen));
        List<int[]> genes = new ArrayList<>();
        for (int i = 0; i < maxLen; i++) {
            if (i < punto) {
                genes.add(i < p1.genes.size() ? Arrays.copyOf(p1.genes.get(i), 2) : randomMovimiento());
            } else {
                genes.add(i < p2.genes.size() ? Arrays.copyOf(p2.genes.get(i), 2) : randomMovimiento());
            }
        }
        return new Cromosoma(genes);
    }

    private void mutar(Cromosoma c) {
        for (int i = 0; i < c.genes.size(); i++) {
            if (rnd.nextDouble() < MUTATION_RATE) {
                c.genes.set(i, randomMovimiento());
            }
        }
    }

    private int[] randomMovimiento() {
        int m, cc;
        do {
            m = rnd.nextInt(3);
            cc = rnd.nextInt(3);
        } while (m + cc < 1 || m + cc > 2);
        return new int[]{m, cc};
    }

    // -------- Métodos comunes --------
    private List<Tree> generarHijos(Tree padre) {
        List<Tree> hijos = new ArrayList<>();
        int[][] movimientos = {{1,0},{2,0},{0,1},{0,2},{1,1}};
        for (int[] mov : movimientos) {
            Tree hijo = generarHijo(padre, mov[0], mov[1]);
            if (hijo != null) hijos.add(hijo);
        }
        return hijos;
    }

    private Tree generarHijo(Tree padre, int misionerosMov, int canibalesMov) {
        Estado nuevo = generarNuevoEstado(padre.getEstado(), misionerosMov, canibalesMov);
        if (nuevo != null && esEstadoValido(nuevo)) {
            Tree t = new Tree(nuevo, padre);
            int newG = (padre == null) ? 0 : padre.getG() + 1;
            t.setG(newG);
            t.setF(newG + heuristicViajesRestantes(nuevo));
            return t;
        }
        return null;
    }

    /**
     * Genera nuevo estado con el orden de tu constructor: (mi,ci,pos_barca,md,cd)
     */
    private Estado generarNuevoEstado(Estado actual, int misionerosMov, int canibalesMov) {
        int barca = actual.getPosicion_barca();
        if (barca == Estado.BARCA_IZQ) {
            if (!hayPersonasSuficientes(actual.getMisioneros_isq(), actual.getCanibales_isq(), misionerosMov, canibalesMov)) return null;
            int mi = actual.getMisioneros_isq() - misionerosMov;
            int ci = actual.getCanibales_isq() - canibalesMov;
            int md = actual.getMisioneros_der() + misionerosMov;
            int cd = actual.getCanibales_der() + canibalesMov;
            try {
                return new Estado(mi, ci, Estado.BARCA_DER, md, cd);
            } catch (IllegalArgumentException ex) {
                return null;
            }
        } else {
            if (!hayPersonasSuficientes(actual.getMisioneros_der(), actual.getCanibales_der(), misionerosMov, canibalesMov)) return null;
            int mi = actual.getMisioneros_isq() + misionerosMov;
            int ci = actual.getCanibales_isq() + canibalesMov;
            int md = actual.getMisioneros_der() - misionerosMov;
            int cd = actual.getCanibales_der() - canibalesMov;
            try {
                return new Estado(mi, ci, Estado.BARCA_IZQ, md, cd);
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
    }

    private boolean hayPersonasSuficientes(int mDisponibles, int cDisponibles, int mReq, int cReq) {
        return mDisponibles >= mReq && cDisponibles >= cReq;
    }

    private boolean esEstadoValido(Estado s) {
        int mi = s.getMisioneros_isq(), ci = s.getCanibales_isq(), md = s.getMisioneros_der(), cd = s.getCanibales_der();
        if (!valoresEnRango(mi, ci, md, cd)) return false;
        // no dejar que en una orilla los caníbales superen a misioneros (si hay misioneros)
        if (mi > 0 && ci > mi) return false;
        if (md > 0 && cd > md) return false;
        return true;
    }

    private boolean valoresEnRango(int... vals) {
        for (int v : vals) if (v < 0 || v > Estado.TOTAL) return false;
        return true;
    }

    // -------- Result helpers --------
    public ArrayList<Estado> obtenerSolucion() {
        ArrayList<Estado> camino = new ArrayList<>();
        switch (algoritmo) {
            case 1:
                if (nodoActualBFS == null) return camino;
                Tree n = nodoActualBFS;
                while (n != null) { camino.add(0, n.getEstado()); n = n.getPadre(); }
                break;
            case 2:
                if (nodoActualAStar == null) return camino;
                Tree na = nodoActualAStar;
                while (na != null) { camino.add(0, na.getEstado()); na = na.getPadre(); }
                break;
            case 3:
                if (nodoActualDFS == null) return camino;
                Tree nd = nodoActualDFS;
                while (nd != null) { camino.add(0, nd.getEstado()); nd = nd.getPadre(); }
                break;
            case 4:
                if (mejorSolucionAG == null) return camino;
                Estado actual = new Estado(3, 3, Estado.BARCA_IZQ, 0, 0);
                camino.add(actual);
                for (int[] mov : mejorSolucionAG.genes) {
                    Estado nuevo = generarNuevoEstado(actual, mov[0], mov[1]);
                    if (nuevo != null && esEstadoValido(nuevo)) {
                        actual = nuevo;
                        camino.add(actual);
                        if (actual.equals(estadoObjetivo)) break;
                    } else break;
                }
                break;
        }
        return camino;
    }

    public boolean solucionEncontrada() {
        switch (algoritmo) {
            case 1: return nodoActualBFS != null && nodoActualBFS.getEstado().equals(estadoObjetivo);
            case 2: return nodoActualAStar != null && nodoActualAStar.getEstado().equals(estadoObjetivo);
            case 3: return nodoActualDFS != null && nodoActualDFS.getEstado().equals(estadoObjetivo);
            case 4: return mejorSolucionAG != null && simularCromosoma(mejorSolucionAG).equals(estadoObjetivo);
            default: return false;
        }
    }

    public String obtenerEstadisticas() {
        ArrayList<Estado> sol = obtenerSolucion();
        String alg = obtenerNombreAlgoritmo();
        int movimientos = Math.max(0, sol.size() - 1);
        String extra = "";
        if (alg.equals("BFS")) extra = String.format(" | nodos expandidos=%d", nodosExpandidosBFS);
        if (alg.equals("A*")) extra = String.format(" | nodos expandidos=%d", nodosExpandidosAStar);
        if (alg.equals("DFS")) extra = String.format(" | nodos expandidos=%d", nodosExpandidosDFS);
        return String.format("Algoritmo=%s, movimientos=%d%s", alg, movimientos, extra);
    }

    public String obtenerNombreAlgoritmo() {
        switch (algoritmo) {
            case 1: return "BFS";
            case 2: return "A*";
            case 3: return "DFS";
            case 4: return "Alg. Genético";
            default: return "Desconocido";
        }
    }

    // -------- Aux classes --------
    private static class Cromosoma {
        List<int[]> genes;
        double fitness = 0.0;
        Cromosoma(List<int[]> g) { this.genes = g; }
        Cromosoma clonarShallow() {
            List<int[]> copia = new ArrayList<>();
            for (int[] m : genes) copia.add(Arrays.copyOf(m, 2));
            return new Cromosoma(copia);
        }
    }
}
