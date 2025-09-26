import java.util.Objects;

public final class Estado {
    public static final int BARCA_IZQ = 0;
    public static final int BARCA_DER = 1;
    public static final int TOTAL = 3; // número total de misioneros (y caníbales)

    private final int misioneros_isq;
    private final int canibales_isq;
    private final int posicion_barca;
    private final int misioneros_der;
    private final int canibales_der;

    public Estado(int misioneros_isq, int canibales_isq, int posicion_barca, int misioneros_der, int canibales_der) {
        if (posicion_barca != BARCA_IZQ && posicion_barca != BARCA_DER)
            throw new IllegalArgumentException("posicion_barca inválida: " + posicion_barca);
        if (misioneros_isq < 0 || canibales_isq < 0 || misioneros_der < 0 || canibales_der < 0)
            throw new IllegalArgumentException("Valores negativos no permitidos");
        if (misioneros_isq + misioneros_der != TOTAL)
            throw new IllegalArgumentException("Suma de misioneros debe ser " + TOTAL);
        if (canibales_isq + canibales_der != TOTAL)
            throw new IllegalArgumentException("Suma de caníbales debe ser " + TOTAL);

        this.misioneros_isq = misioneros_isq;
        this.canibales_isq = canibales_isq;
        this.posicion_barca = posicion_barca;
        this.misioneros_der = misioneros_der;
        this.canibales_der = canibales_der;
    }

    public int getMisioneros_isq() { return misioneros_isq; }
    public int getCanibales_isq() { return canibales_isq; }
    public int getPosicion_barca() { return posicion_barca; }
    public int getMisioneros_der() { return misioneros_der; }
    public int getCanibales_der() { return canibales_der; }

    /** Heurística simple: número de personas en la orilla izquierda */
    public int heuristicaPersonasIzq() {
        return misioneros_isq + canibales_isq;
    }

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
        return Objects.hash(misioneros_isq, canibales_isq, posicion_barca, misioneros_der, canibales_der);
    }

    @Override
    public String toString() {
        return String.format("L(%dM,%dC) B=%d R(%dM,%dC)",
            misioneros_isq, canibales_isq, posicion_barca, misioneros_der, canibales_der);
    }
}
