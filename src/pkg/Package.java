package pkg;

public class Package {
    
    private String clientId;
    private String packageId;
    private PackageStatus status;

    public Package(String clientId, String packageId, PackageStatus status) {
        this.clientId = clientId;
        this.packageId = packageId;
        this.status = status;
    }

    public String getClientId() {
        return clientId;
    }

    public String getPackageId() {
        return packageId;
    }

    public PackageStatus getStatus() {
        return status;
    }

    public void setStatus(PackageStatus status) {
        this.status = status;
    }

    public String getStatusDisplayName() {
        return status.getDisplayName();
    }
}

