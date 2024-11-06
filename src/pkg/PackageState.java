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

    public String getDisplayName() {
        return displayName;
    }

    public static PackageState fromCode(int code) {
        for (PackageState status : PackageState.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return DESCONOCIDO;
    }
}
