package a5;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

public class ClientVelocimetre {
/* Client afegit al grup multicast SrvVelocitats.java que representa un velocímetre */
	
	private boolean continueRunning = true;
    private MulticastSocket socket;
    private InetAddress multicastIP;
    private int port;
    
    
	public ClientVelocimetre(int portValue, String strIp) throws IOException {
		multicastIP = InetAddress.getByName(strIp);
        port = portValue;
        socket = new MulticastSocket(port);
	}

	public void runClient() throws IOException{
        DatagramPacket packet;
        byte [] receivedData = new byte[4];
        
        socket.joinGroup(multicastIP);
        
        while(continueRunning){
           packet = new DatagramPacket(receivedData, 4);
           socket.setSoTimeout(5000);
           try{
                socket.receive(packet);
                continueRunning = getData(packet.getData());
            }catch(SocketTimeoutException e){
                System.out.println("S'ha perdut la connexió amb el servidor.");
                continueRunning = false;
            }
        }

        socket.leaveGroup(multicastIP);
        socket.close();
    }
	
	protected  boolean getData(byte[] data) {
		boolean ret=true;
		
        int v = ByteBuffer.wrap(data).getInt();
        
        //pintem velocimetre
        for(int i=0;i<v;i++) System.out.print("#");
        System.out.print("\n");
        try {
			Runtime.getRuntime().exec("clear");
		} catch (IOException e) {
			e.printStackTrace();
		}
        //if (v==1) ret=false;
        
		return ret;
    }
	
	public static void main(String[] args) throws IOException {
		ClientVelocimetre cvel = new ClientVelocimetre(5557, "224.0.0.1");
		cvel.runClient();
		System.out.println("Parat!");

	}

}
