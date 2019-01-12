package Pattern;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class P2 {
	
	public static class Point{		
	 static ArrayList<Integer> pointCoordinates;
	 static int timestamp; 
	 Point(ArrayList<Integer> arr,int time) {
			pointCoordinates = arr;
			timestamp = time;
		}
	}
	
	private static Socket client;
	private static InetAddress ipAddress;
	private static int windowSize;
	private static int port;
	private static ArrayList<Point> listOfPoints = new ArrayList<Point>();
	private static int dimension;
			
	 public static void main(String args[]) throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		 windowSize = Integer.parseInt(input.readLine());
		if(windowSize <= 0) { 
			System.out.println("Invalid Window Size"); 
			System.exit(0);
			}
		String ipAddressAndPort = input.readLine();
		String[] ipPortsplit = ipAddressAndPort.split(":");
		String host = ipPortsplit[0];  
		port = Integer.parseInt(ipPortsplit[1]);
		ipAddress = InetAddress.getByName(host); 
		System.out.println(windowSize + " "+host+" "+port);
		SocketConnection();		 
	 }
	 
	 public static void SocketConnection(){
			 try {
				client = new Socket(ipAddress,port);
				System.out.println("Just connected to " + client.getRemoteSocketAddress());
				BufferedReader inputStreamCSV = new BufferedReader(new InputStreamReader(client.getInputStream()));
				while(true) {
					String str = inputStreamCSV.readLine();
					String pointArray[] = str.split(",");
					int timestamp = Integer.parseInt(pointArray[0]);
					ArrayList<Integer> curPoint = new ArrayList<Integer>();
					dimension = curPoint.size();
					for(int i = 1; i < pointArray.length ;i++) {						
						curPoint.add(Integer.parseInt(pointArray[i]));
					}
					Point p = new Point(curPoint,timestamp);
					for(int i =0 ; i < windowSize;i++) {
						listOfPoints.add(p);
					}
					if(listOfPoints.size() > windowSize) {
						listOfPoints.remove(0);
						listOfPoints.add(p);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}		 
	 }
	 
	 public static void isOutlier (){
			 int d = dimension+1;
			 int gridSize = (int) Math.pow(windowSize, 1/d);
			 HashMap<ArrayList<Integer>,Integer> map = new HashMap();
			 ArrayList<Integer> arr= new ArrayList();
			 	for(int i =0; i < dimension;i++) {
			 	}
		 }	 
}
