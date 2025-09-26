import java.util.Objects;

public class Tree implements Comparable<Tree> {
    private Estado estado;
    private Tree padre;
    private int profundidad;

    // g = coste desde inicio, f = g + h (para A*)
    private int g;
    private int f;

    public Tree(Estado estado, Tree padre, int profundidad) {
        this.estado = estado;
        this.padre = padre;
        this.profundidad = profundidad;
        this.g = Integer.MAX_VALUE / 4;
        this.f = Integer.MAX_VALUE / 2;
    }

    // Constructor que calcula profundidad a partir del padre
    public Tree(Estado estado, Tree padre) {
        this.estado = estado;
        this.padre = padre;
        this.profundidad = (padre == null) ? 0 : padre.getProfundidad() + 1;
        this.g = Integer.MAX_VALUE / 4;
        this.f = Integer.MAX_VALUE / 2;
    }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public Tree getPadre() { return padre; }
    public void setPadre(Tree padre) {
        this.padre = padre;
        this.profundidad = (padre == null) ? 0 : padre.getProfundidad() + 1;
    }

    public int getProfundidad() { return profundidad; }
    public void setProfundidad(int profundidad) { this.profundidad = profundidad; }

    public int getG() { return g; }
    public void setG(int g) { this.g = g; }

    public int getF() { return f; }
    public void setF(int f) { this.f = f; }

@Override
public int compareTo(Tree o) {
    int cmp = Integer.compare(this.f, o.f);
    if (cmp != 0) return cmp;
    cmp = Integer.compare(this.g, o.g);
    if (cmp != 0) return cmp;
    cmp = Integer.compare(this.profundidad, o.profundidad);
    if (cmp != 0) return cmp;
    return Integer.compare(Objects.hashCode(this.estado), Objects.hashCode(o.estado));
}

    @Override
    public String toString() {
        return String.format("Tree[%s | p=%d g=%d f=%d]", estado, profundidad, g, f);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tree)) return false;
        Tree t = (Tree) o;
        return Objects.equals(this.estado, t.estado);
    }

    @Override
    public int hashCode() {
        return estado != null ? estado.hashCode() : 0;
    }
}
