package main.java;

import com.fazecast.jSerialComm.SerialPort;


public class SerialService {
    private SerialPort port;

    public SerialService(String portName){
    	
    	int baudrate = Integer.parseInt(MainApp.properties.getProperty("serial.baudrate"));
    	int dataBits = Integer.parseInt(MainApp.properties.getProperty("serial.databits"));
    	int stopBits = Integer.parseInt(MainApp.properties.getProperty("serial.stopbits"));
    	int parity = Integer.parseInt(MainApp.properties.getProperty("serial.parity"));
    	
        port = SerialPort.getCommPort(portName);
        port.setBaudRate(baudrate);
        port.setNumDataBits(dataBits);
        port.setNumStopBits(stopBits);
        port.setParity(parity);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 1000);
        
        if (port.openPort()) {
            System.out.println("Serial port opened successfully");
            System.out.println("Port: " + portName);
            System.out.println("Baud Rate: " + baudrate);
            System.out.println("Data Bits: " + dataBits);
            System.out.println("Stop Bits: " + stopBits);
            System.out.println("Parity: " + parity);
        } else {
            System.err.println("Failed to open serial port: " + portName);
           
        }
        
        
    }

    public String readTag() {
    	
    		
    	    byte[] buffer = new byte[64];
    	    int len = port.readBytes(buffer, buffer.length);

    	    if (len <= 0) {
    	        return "NO TAG"; // or null, or an error message
    	    }

    	    return new String(buffer, 0, len).trim();

    }

    public boolean writeTag(String newId) {
        if (newId == null || newId.isEmpty()) return false;

        byte[] data = newId.getBytes();  
        int bytesWritten = port.writeBytes(data, data.length);

        return bytesWritten > 0;
    }

}

