package main.java;

import java.util.Scanner;

import com.fazecast.jSerialComm.SerialPort;

public class RFIDReader {
	public static void main(String[] args) {
		System.out.println("Detecting the RFID Reader...");
		
		SerialPort[] commPorts = SerialPort.getCommPorts();
		
		if(commPorts.length == 0) {
			System.out.println("No Serial Port Found.");
			return;
		}
		
		SerialPort selectedPort = commPorts[0];
		selectedPort.setBaudRate(9600);
		
		if(!selectedPort.openPort()) {
			System.out.println("Failed to open Port: "+selectedPort.getSystemPortName());
			return;
		}
		System.out.println("Port open :"+selectedPort.getSystemPortName());
		System.out.println("Waiting for RFID Tags...");
		
		
		Scanner sc = new Scanner(selectedPort.getInputStream());
		while(true) {
			if(sc.hasNextLine()) {
				String tag = sc.nextLine().trim();
				System.out.println("RFID Detected: "+tag);
				
				
			}
		}
		
	}
}
