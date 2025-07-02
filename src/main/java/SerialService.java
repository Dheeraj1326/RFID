package main.java;

import com.fazecast.jSerialComm.SerialPort;


public class SerialService {
    private SerialPort port;

    public SerialService(String portName){
    	
    	int baudrate = Integer.parseInt(MainApp.properties.getProperty("serial.baudrate"));
    	int dataBits = Integer.parseInt(MainApp.properties.getProperty("serial.databits"));
    	int stopBits = Integer.parseInt(MainApp.properties.getProperty("serial.stopbits"));
    	int parity = Integer.parseInt(MainApp.properties.getProperty("serial.parity"));
    	
    	 System.out.println("Trying to connect....");
         System.out.println("Port		: " + portName);
         System.out.println("Baud Rate	: " + baudrate);
         System.out.println("Data Bits	: " + dataBits);
         System.out.println("Stop Bits	: " + stopBits);
         System.out.println("Parity		: " + parity);
    	
        port = SerialPort.getCommPort(portName);
        port.setBaudRate(baudrate);
        port.setNumDataBits(dataBits);
        port.setNumStopBits(stopBits);
        port.setParity(parity);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 1000);
        
       
        
        if (port.openPort()) {
            System.out.println("Serial port opened successfully");
        } else {
            System.err.println("Failed to open serial port: " + portName);
           
        }
        
        
    }

    public String readTag() {
    	
    		
    	    byte[] buffer = new byte[64];
    	    int len = port.readBytes(buffer, buffer.length);

    	    if (len <= 0) {
    	        return "NO TAG"; 
    	    }

    	    return new String(buffer, 0, len).trim();

    }

    public boolean writeTag(String newId) {
        if (newId == null || newId.isEmpty()) return false;

        byte[] data = newId.getBytes();  
        int bytesWritten = port.writeBytes(data, data.length);

        return bytesWritten > 0;
    }

	public void closePort() {
		// TODO Auto-generated method stub
		try {
			port.closePort();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
	}

}

