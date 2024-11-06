package pkg;

public enum PackageState {
    ENOFICINA(0, "ENOFICINA"),
    RECOGIDO(1, "RECOGIDO"),
    ENCLASIFICACION(2, "ENCLASIFICACION"),
    DESPACHADO(3, "DESPACHADO"),
    ENENTREGA(4, "ENENTREGA"),
    ENTREGADO(5, "ENTREGADO"),
    DESCONOCIDO(6, "DESCONOCIDO");

    private final int code;
    private final String displayName;

    PackageState(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public int getCode() {
        return code;
    }

    public String getStateName() {
        return displayName;
    }

    public static PackageState fromCode(int code) {
        for (PackageState state : PackageState.values()) {
            if (state.getCode() == code) {
                return state;
            }
        }
        return DESCONOCIDO;
    }
}
