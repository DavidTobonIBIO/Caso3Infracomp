package pkg;

public class Package {

    private int clientId;
    private int packageId;
    private PackageState status;

    public Package(int clientId, int packageId, PackageState status) {
        this.clientId = clientId;
        this.packageId = packageId;
        this.status = status;
    }

    public int getClientId() {
        return clientId;
    }

    public int getPackageId() {
        return packageId;
    }

    public PackageState getStatus() {
        return status;
    }

    public void setStatus(PackageState status) {
        this.status = status;
    }

    public String getStatusDisplayName() {
        return status.getDisplayName();
    }
}
