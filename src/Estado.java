public class Estado {
    private int misioneros_isq;
     private int canibales_isq;
    private int posicion_barca; // 0 para isquierda  y 1 para derecha
     private int misioneros_der;
    private int canibales_der;


    public Estado(int misioneros_isq, int canibales_isq, int posicion_barca, int misioneros_der, int canibales_der) {
        this.misioneros_isq = misioneros_isq;
        this.canibales_isq = canibales_isq;
        this.posicion_barca = posicion_barca;
        this.misioneros_der = misioneros_der;
        this.canibales_der = canibales_der;
    }
    

    public int getMisioneros_isq() {
        return this.misioneros_isq;
    }

    public void setMisioneros_isq(int misioneros_isq) {
        this.misioneros_isq = misioneros_isq;
    }

    public int getCanibales_isq() {
        return this.canibales_isq;
    }

    public void setCanibales_isq(int canibales_isq) {
        this.canibales_isq = canibales_isq;
    }

    public int getPosicion_barca() {
        return this.posicion_barca;
    }

    public void setPosicion_barca(int posicion_barca) {
        this.posicion_barca = posicion_barca;
    }

    public int getMisioneros_der() {
        return this.misioneros_der;
    }

    public void setMisioneros_der(int misioneros_der) {
        this.misioneros_der = misioneros_der;
    }

    public int getCanibales_der() {
        return this.canibales_der;
    }

    public void setCanibales_der(int canibales_der) {
        this.canibales_der = canibales_der;
    }
    public int heuristica() {
    // Heurística admisible: misioneros + caníbales en el lado izquierdo
    return this.misioneros_isq + this.canibales_isq;
}

    // ...existing code...
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Estado other = (Estado) obj;
    return misioneros_isq == other.misioneros_isq &&
           canibales_isq == other.canibales_isq &&
           posicion_barca == other.posicion_barca &&
           misioneros_der == other.misioneros_der &&
           canibales_der == other.canibales_der;
}

@Override
public int hashCode() {
    return java.util.Objects.hash(misioneros_isq, canibales_isq, posicion_barca, misioneros_der, canibales_der);
}
// ...existing code...
}