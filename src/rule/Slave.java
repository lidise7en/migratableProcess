package rule;

public class Slave extends BasicPart{

    protected String hostIpAddr;

    public Slave(int port, String hostIpAddr) {
        this.port = port;
        this.hostIpAddr = hostIpAddr;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

}
