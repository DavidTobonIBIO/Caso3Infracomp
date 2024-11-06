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
            int clientId = i;
            int packageId = i;
            Package pkg = new Package(clientId, packageId, getRandomState());
            packageMap.put(generateKey(clientId, packageId), pkg);
        }
    }

    private String generateKey(int clientId, int packageId) {
        return "client:" + clientId + "|package:" + packageId;
    }

    private PackageState getRandomState() {
        PackageState[] states = PackageState.values();
        return states[random.nextInt(states.length - 1)];
    }

    public PackageState getPackageState(int clientId, int packageId) {
        Package pkg = packageMap.get(generateKey(clientId, packageId));
        return (pkg != null) ? pkg.getState() : PackageState.DESCONOCIDO;
    }
}
