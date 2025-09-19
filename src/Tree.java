public class Tree {
        private Estado estado;
    private Tree padre;
    private int profundidad;


    public Tree(Estado estado, Tree padre, int profundidad) {
        this.estado = estado;
        this.padre = padre;
        this.profundidad = profundidad;
    }
    public Estado getEstado() {
        return this.estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Tree getPadre() {
        return this.padre;
    }

    public void setPadre(Tree padre) {
        this.padre = padre;
    }

    public int getProfundidad() {
        return this.profundidad;
    }

    public void setProfundidad(int profundidad) {
        this.profundidad = profundidad;
    }
}