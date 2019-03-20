package a5;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ClientVelocimetre2 {
	/*Client que s'afegeix al Multicast SrvVelocitats.java que treu mitjanaes de velocitat */
	
	private boolean continueRunning = true;
	private MulticastSocket socket;
    private InetAddress multicastIP;
    private int port;
    private List<Integer> velocitats;
    private Integer sumVel;
    
	public ClientVelocimetre2(int portValue, String strIp) throws IOException {
		multicastIP = InetAddress.getByName(strIp);
        port = portValue;
        socket = new MulticastSocket(port);
        sumVel = 0;
        velocitats = new ArrayList();
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
        velocitats.add(v);
        //int ti = (int) System.currentTimeMillis();
        
        if(velocitats.size() % 5 == 0) {
        	for (Integer mark : velocitats) {
                sumVel += mark;
            }
        //	int te = (int) System.currentTimeMillis();
        	System.out.println("Velocitat mitjana: " + sumVel.doubleValue() / velocitats.size());
        	sumVel = 0;
        	velocitats.clear();
        }
        
        //if (v==0) ret=false;
                   
		return ret;
    }
	
	public static void main(String[] args) throws IOException {
		ClientVelocimetre2 cvel = new ClientVelocimetre2(5557, "224.0.0.1");
		cvel.runClient();
		System.out.println("Parat!");

	}

}
