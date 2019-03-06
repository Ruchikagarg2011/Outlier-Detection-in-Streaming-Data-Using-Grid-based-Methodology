package Pattern;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

public class P2 {

	/** Created a Point class to store coordinates and timestamp of the point ***/
	
	public static class Point {
		ArrayList<Integer> pointCoordinates;
		int timestamp;
		Point(ArrayList<Integer> arr, int time) {
			pointCoordinates = arr;
			timestamp = time;
		}
	}

	private static Socket client;
	private static InetAddress ipAddress;
	private static String host;
	private static int windowSize;
	private static int port;
	private static Queue<Point> listOfPoints = new LinkedList<>();
	private static int dimension;
	private static double gridSize;
	private static double tau;
	private static HashMap<ArrayList<Integer>, Integer> map = new HashMap<ArrayList<Integer>,Integer>();

	/**Calculating window size and parsing IP address and port***/

	public static void main(String args[]) throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		try {
			String window = input.readLine();
			windowSize = Integer.parseInt(window);
			if (windowSize <= 0 || window == null || window.equals("")) {
				System.out.println("Invalid Window Size");
				System.exit(0);
			}
			String ipAddressAndPort = input.readLine();
			String[] ipPortsplit = ipAddressAndPort.split(":");
			if (ipPortsplit.length != 2) {
				System.out.println("Invalid IP address");
			}
			host = ipPortsplit[0];
			ipAddress = InetAddress.getByName(host);
			port = Integer.parseInt(ipPortsplit[1]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		SocketConnection();
	}
	
	/**Socket connection and reading from the server and adding points to the Queue of window size***/

	@SuppressWarnings("unchecked")
	public static void SocketConnection() {
		try {
			boolean flag1 = false;
			client = new Socket(ipAddress, port);
			System.out.println("Just connected to " + client.getRemoteSocketAddress());
			BufferedReader inputStreamCSV = new BufferedReader(new InputStreamReader(client.getInputStream()));
			while (true) {
				String str = inputStreamCSV.readLine();
				if (str == null) {
					break;
				}
				String pointArray[] = str.split(",");
				int timestamp = Integer.parseInt(pointArray[0]);
				if (timestamp < 0) {
					System.out.println("Invalid timestamp");
					System.exit(0);
				}
				ArrayList<Integer> curPoint = new ArrayList<Integer>();
				for (int i = 1; i < pointArray.length; i++) {
					int coordinate = Integer.parseInt(pointArray[i]);
					if (coordinate >= -32768 && coordinate <= 32767) {
						curPoint.add(coordinate);
					} else {
						System.out.println("Invalid input data");
						System.exit(0);
					}
				}
				if (flag1 == false) {
					dimension = curPoint.size();
					gridSize();
					flag1 = true;
				}
				int dimension1 = curPoint.size();
				if (dimension1 != dimension) {
					System.out.println("Invalid Dimension");
					System.exit(0);
				}
				Point p = new Point(curPoint, timestamp);
				addPointToQueue(p);
			}
		} catch (IOException e) {
			System.out.println("Invalid IP address and Port");
			e.printStackTrace();
		} catch (Exception ex) {
			System.out.println("Invalid Data Provided");
		}
	}

	/**Method to add point to the Queue till window Size and then if queue size is greater than window size then
	 * checking the point for outlier and adding to the queue and grid and removing the first point from
	 * the grid and the queue
	 * ***/
	@SuppressWarnings("unchecked")
	public static void addPointToQueue(Point pt) throws IOException {
		if (listOfPoints.size() < windowSize) {
			listOfPoints.add(pt);
			addToGrid(pt);
		} else {
			isOutlier(pt);
			Point pointRemoved = listOfPoints.poll();
			removeFromGrid(pointRemoved);
			listOfPoints.add(pt);
			addToGrid(pt);
		}
	}
	
	/**Method to compute partition and grid size and threshold density 
	 * ***/
	@SuppressWarnings("unchecked")
	public static void gridSize() {
		int range = 65536;
		int d = dimension + 1;
		double p = Math.pow(windowSize, (double) ((double) (1) / (double) (d)));
		gridSize = (int) (range / p);
		tau = Math.ceil(Math.log10(p));
	}
	
	/**Method to compute grid the of the point to which grid the point belong to
	 * ***/
	@SuppressWarnings("unchecked")
	public static ArrayList<Integer> calculateGrid(Point pt) {
		int gridAddress;
		ArrayList<Integer> pointarray = new ArrayList<Integer>();
		for (int i = 0; i < pt.pointCoordinates.size(); i++) {
			int coordinate = pt.pointCoordinates.get(i);
			 gridAddress = Math.floorDiv(coordinate,(int) gridSize);			
			pointarray.add(gridAddress);
		}
		return pointarray;
	}
	
	/**Method to add the point to the grid: the grid is Map of grid and its count ***/
	@SuppressWarnings("unchecked")
	public static void addToGrid(Point pt) {
		ArrayList<Integer> grid = calculateGrid(pt);
		if (map.containsKey(grid)) {
			map.put(grid, map.get(grid) + 1);
		} else
			map.put(grid, 1);
	}
	
	/**Method to remove the point from the grid when points are greater than window size
	 * ***/
	@SuppressWarnings("unchecked")
	public static void removeFromGrid(Point pt) {
		ArrayList<Integer> grid = calculateGrid(pt);
		if (map.containsKey(grid)) {
			if (map.get(grid) > 1) {
				map.put(grid, map.get(grid) - 1);
			} else {
				map.remove(grid);
			}
		}
	}
	
	/**Method to compute if the point is outlier or not and also check the neighbours for the density
	 * ***/
	@SuppressWarnings("unchecked")
	public static void isOutlier(Point pt) throws IOException {
		String point = "";
		for (int i : pt.pointCoordinates) {
			point += Integer.toString(i) + " ";
		}
		boolean flag = false;
		ArrayList<Integer> currentGrid = calculateGrid(pt);
		if (map.containsKey(currentGrid)) {
			int density = map.get(currentGrid);
			if (density >= tau) {
				flag = true;
			}
		}
		if (flag == false) {
			List<List<Integer>> neighbors = getNeighbors(currentGrid);
			for (List<Integer> neighbor : neighbors) {
				if (map.containsKey(neighbor)) {
					int density = map.get(neighbor);
					if (density >= tau) {
						flag = true;
						break;
					}
				}
			}
		}
		if (flag == false) {
			System.out.println(pt.timestamp + " " + point.trim() + " " + "Outlier Point");
		}
	}
	
	/**Method to compute the neighbors of the point and return the neighbor list to the is outlier method
	 * ***/
	@SuppressWarnings("unchecked")
	public static List<List<Integer>> getNeighbors(List<Integer> list) {
		List<List<Integer>> result = new ArrayList<>();
		if (list.size() == 1) {
			int val = list.get(0);
			List<Integer> list1 = new ArrayList<>();
			List<Integer> list2 = new ArrayList<>();
			List<Integer> list3 = new ArrayList<>();
			list1.add(val - 1);
			list2.add(val + 1);
			list3.add(val);
			result.add(list1);
			result.add(list2);
			result.add(list3);
			return result;
		} else {
			List<List<Integer>> outerList = getNeighbors(list.subList(0, 1));
			for (List<Integer> out : outerList) {
				List<List<Integer>> inList = getNeighbors(list.subList(1, list.size()));
				for (List<Integer> in : inList) {
					List<Integer> resultInner = new ArrayList<Integer>(out);
					resultInner.addAll(in);
					result.add(resultInner);
				}
			}
			return result;
		}
	}
}
