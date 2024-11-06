package pkg;

public class Package {

    private int clientId;
    private int packageId;
    private PackageState state;

    public Package(int clientId, int packageId, PackageState state) {
        this.clientId = clientId;
        this.packageId = packageId;
        this.state = state;
    }

    public int getClientId() {
        return clientId;
    }

    public int getPackageId() {
        return packageId;
    }

    public PackageState getState() {
        return state;
    }

    public void setState(PackageState state) {
        this.state = state;
    }

    public String getStateName() {
        return state.getStateName();
    }
}
