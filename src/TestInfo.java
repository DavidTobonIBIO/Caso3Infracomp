public class TestInfo {

    private String encryptionAlgorithm;
    private int numClients;
    private double avgTimeS;
    private double avgTimeMs;
    private long avgTimeNs;

    public TestInfo(String cipherMethod, int numClients) {
        this.encryptionAlgorithm = cipherMethod;
        this.numClients = numClients;
    }

    /**
     * @return the cipherMethod
     */
    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    /**
     * @return the numClients
     */
    public int getNumClients() {
        return numClients;
    }

    /**
     * @return the avgTimeS
     */
    public double getAvgTimeS() {
        return avgTimeS;
    }

    /**
     * @param avgTimeS the avgTimeS to set
     */
    public void setAvgTimeS(double avgTimeS) {
        this.avgTimeS = avgTimeS;
    }

    /**
     * @return the avgTimeMs
     */
    public double getAvgTimeMs() {
        return avgTimeMs;
    }

    /**
     * @param avgTimeMs the avgTimeMs to set
     */
    public void setAvgTimeMs(double avgTimeMs) {
        this.avgTimeMs = avgTimeMs;
    }

    /**
     * @return the avgTimeNs
     */
    public long getAvgTimeNs() {
        return avgTimeNs;
    }

    /**
     * @param avgTimeNs the avgTimeNs to set
     */
    public void setAvgTimeNs(long avgTimeNs) {
        this.avgTimeNs = avgTimeNs;
    }
}
