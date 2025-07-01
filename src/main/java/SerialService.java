package main.java;


import com.fazecast.jSerialComm.SerialPort;

public class SerialService {
    private SerialPort port;

    public SerialService(String portName) {
        port = SerialPort.getCommPort(portName);
        port.setBaudRate(9600);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 1000);
        port.openPort();
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

        byte[] data = newId.getBytes();  // optionally specify encoding
        int bytesWritten = port.writeBytes(data, data.length);

        return bytesWritten > 0;
    }

}

