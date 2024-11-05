package pkg;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PackageTable {
    private static final int NUM_PACKAGES = 32;

    private Map<String, Package> packageMap;
    private Random random;

    public PackageTable() {
        packageMap = new HashMap<>();
        random = new Random();
        initializePackages();
    }

    private void initializePackages() {
        for (int i = 0; i < NUM_PACKAGES; i++) {
            String clientId = "client:" + i;
            String packageId = "package:" + i;
            Package pkg = new Package(clientId, packageId, getRandomStatus());
            packageMap.put(generateKey(clientId, packageId), pkg);
        }
    }

    private String generateKey(String clientId, String packageId) {
        return clientId + "|" + packageId;
    }

    private PackageStatus getRandomStatus() {
        PackageStatus[] statuses = PackageStatus.values();
        return statuses[random.nextInt(statuses.length - 1)];
    }

    public PackageStatus getPackageStatus(String clientId, String packageId) {
        Package pkg = packageMap.get(generateKey(clientId, packageId));
        return (pkg != null) ? pkg.getStatus() : PackageStatus.DESCONOCIDO;
    }

    public void updatePackageStatus(String clientId, String packageId, PackageStatus newStatus) {
        Package pkg = packageMap.get(generateKey(clientId, packageId));
        if (pkg != null) {
            pkg.setStatus(newStatus);
        }
    }
}
