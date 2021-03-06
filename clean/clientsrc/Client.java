package clientsrc;

import serversrc.resImpl.Crash;
import serversrc.resImpl.InvalidTransactionException;
import serversrc.resImpl.TMimpl;
import serversrc.resImpl.TransactionAbortedException;
import serversrc.resInterface.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.io.*;


public class Client
{
	static String message = "blank";
	static ResourceManager rm = null;

	public static void main(String args[])
	{
		Client obj = new Client();
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		String command = "";
		Vector<String> arguments  = new Vector<String>();
		int Id, Cid;
		int flightNum;
		int flightPrice;
		int flightSeats;
		boolean Room;
		boolean Car;
		int price;
		int numRooms;
		int numCars;
		String location;


		String server = "localhost";
		int port = 1099;
		if (args.length > 0)
		{
			server = args[0];
		}
		if (args.length > 1)
		{
			port = Integer.parseInt(args[1]);
		}
		if (args.length > 2)
		{
			System.out.println ("Usage: java client [rmihost [rmiport]]");
			System.exit(1);
		}

		try 
		{
			// get a reference to the rmiregistry
			Registry registry = LocateRegistry.getRegistry(server, port);
			// get the proxy and the remote reference by rmiregistry lookup
			rm = (ResourceManager) registry.lookup("Group2Middleware");
			if(rm!=null)
			{
				System.out.println("Successful");
				System.out.println("Connected to RM");
			}
			else
			{
				System.out.println("Unsuccessful");
			}
			// make call on remote method
		} 
		catch (Exception e) 
		{    
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}



		if (System.getSecurityManager() == null) {
			//System.setSecurityManager(new RMISecurityManager());
		}


		System.out.println("\n\n\tClient Interface");
		System.out.println("Type \"help\" for list of supported commands");
		while(true){
			System.out.print("\n>");
			try{
				//read the next command
				command =stdin.readLine();
			}
			catch (IOException io){
				System.out.println("Unable to read from standard in");
				System.exit(1);
			}
			//remove heading and trailing white space
			command=command.trim();
			arguments=obj.parse(command);
			try{
				//decide which of the commands this was
				switch(obj.findChoice(arguments.elementAt(0))){
				case 1: //help section
					if(arguments.size()==1)   //command was "help"
						obj.listCommands();
					else if (arguments.size()==2)  //command was "help <commandname>"
						obj.listSpecific(arguments.elementAt(1));
					else  //wrong use of help command
						System.out.println("Improper use of help command. Type help or help, <commandname>");
					break;

				case 2:  //new flight
					if(arguments.size()!=5){
						obj.wrongNumber();
						break;
					}
					System.out.println("Adding a new Flight using id: "+arguments.elementAt(1));
					System.out.println("Flight number: "+arguments.elementAt(2));
					System.out.println("Add Flight Seats: "+arguments.elementAt(3));
					System.out.println("Set Flight Price: "+arguments.elementAt(4));

						Id = obj.getInt(arguments.elementAt(1));
						flightNum = obj.getInt(arguments.elementAt(2));
						flightSeats = obj.getInt(arguments.elementAt(3));
						flightPrice = obj.getInt(arguments.elementAt(4));
						if(rm.addFlight(Id,flightNum,flightSeats,flightPrice))
							System.out.println("Flight added");
						else
							System.out.println("Flight could not be added");
					break;

				case 3:  //new Car
					if(arguments.size()!=5){
						obj.wrongNumber();
						break;
					}
					System.out.println("Adding a new Car using id: "+arguments.elementAt(1));
					System.out.println("Car Location: "+arguments.elementAt(2));
					System.out.println("Add Number of Cars: "+arguments.elementAt(3));
					System.out.println("Set Price: "+arguments.elementAt(4));
						Id = obj.getInt(arguments.elementAt(1));
						location = arguments.elementAt(2);
						numCars = obj.getInt(arguments.elementAt(3));
						price = obj.getInt(arguments.elementAt(4));
						if(rm.addCars(Id,location,numCars,price))
							System.out.println("Cars added");
						else
							System.out.println("Cars could not be added");
					break;

				case 4:  //new Room
					if(arguments.size()!=5){
						obj.wrongNumber();
						break;
					}
					System.out.println("Adding a new Room using id: "+arguments.elementAt(1));
					System.out.println("Room Location: "+arguments.elementAt(2));
					System.out.println("Add Number of Rooms: "+arguments.elementAt(3));
					System.out.println("Set Price: "+arguments.elementAt(4));
						Id = obj.getInt(arguments.elementAt(1));
						location = arguments.elementAt(2);
						numRooms = obj.getInt(arguments.elementAt(3));
						price = obj.getInt(arguments.elementAt(4));
						if(rm.addRooms(Id,location,numRooms,price))
							System.out.println("Rooms added");
						else
							System.out.println("Rooms could not be added");
					break;

				case 5:  //new Customer
					if(arguments.size()!=2){
						obj.wrongNumber();
						break;
					}
					System.out.println("Adding a new Customer using id:"+arguments.elementAt(1));
						Id = obj.getInt(arguments.elementAt(1));
						int customer=rm.newCustomer(Id);
						System.out.println("new customer id:"+customer);
					break;

				case 6: //delete Flight
					if(arguments.size()!=3){
						obj.wrongNumber();
						break;
					}
					System.out.println("Deleting a flight using id: "+arguments.elementAt(1));
					System.out.println("Flight Number: "+arguments.elementAt(2));
						Id = obj.getInt(arguments.elementAt(1));
						flightNum = obj.getInt(arguments.elementAt(2));
						if(rm.deleteFlight(Id,flightNum))
							System.out.println("Flight Deleted");
						else
							System.out.println("Flight could not be deleted");
					break;

				case 7: //delete Car
					if(arguments.size()!=3){
						obj.wrongNumber();
						break;
					}
					System.out.println("Deleting the cars from a particular location  using id: "+arguments.elementAt(1));
					System.out.println("Car Location: "+arguments.elementAt(2));
						Id = obj.getInt(arguments.elementAt(1));
						location = arguments.elementAt(2);

						if(rm.deleteCars(Id,location))
							System.out.println("Cars Deleted");
						else
							System.out.println("Cars could not be deleted");
					break;

				case 8: //delete Room
					if(arguments.size()!=3){
						obj.wrongNumber();
						break;
					}
					System.out.println("Deleting all rooms from a particular location  using id: "+arguments.elementAt(1));
					System.out.println("Room Location: "+arguments.elementAt(2));
						Id = obj.getInt(arguments.elementAt(1));
						location = arguments.elementAt(2);
						if(rm.deleteRooms(Id,location))
							System.out.println("Rooms Deleted");
						else
							System.out.println("Rooms could not be deleted");
					break;

				case 9: //delete Customer
					if(arguments.size()!=3){
						obj.wrongNumber();
						break;
					}
					System.out.println("Deleting a customer from the database using id: "+arguments.elementAt(1));
					System.out.println("Customer id: "+arguments.elementAt(2));
						Id = obj.getInt(arguments.elementAt(1));
						customer = obj.getInt(arguments.elementAt(2));
						if(rm.deleteCustomer(Id,customer))
							System.out.println("Customer Deleted");
						else
							System.out.println("Customer could not be deleted");
					break;

				case 10: //querying a flight
					if(arguments.size()!=3){
						obj.wrongNumber();
						break;
					}
					System.out.println("Querying a flight using id: "+arguments.elementAt(1));
					System.out.println("Flight number: "+arguments.elementAt(2));
						Id = obj.getInt(arguments.elementAt(1));
						flightNum = obj.getInt(arguments.elementAt(2));
						int seats=rm.queryFlight(Id,flightNum);
						System.out.println("Number of seats available:"+seats);
					break;

				case 11: //querying a Car Location
					if(arguments.size()!=3){
						obj.wrongNumber();
						break;
					}
					System.out.println("Querying a car location using id: "+arguments.elementAt(1));
					System.out.println("Car location: "+arguments.elementAt(2));
						Id = obj.getInt(arguments.elementAt(1));
						location = arguments.elementAt(2);
						numCars=rm.queryCars(Id,location);
						System.out.println("number of Cars at this location:"+numCars);
					break;

				case 12: //querying a Room location
					if(arguments.size()!=3){
						obj.wrongNumber();
						break;
					}
					System.out.println("Querying a room location using id: "+arguments.elementAt(1));
					System.out.println("Room location: "+arguments.elementAt(2));
						Id = obj.getInt(arguments.elementAt(1));
						location = arguments.elementAt(2);
						numRooms=rm.queryRooms(Id,location);
						System.out.println("number of Rooms at this location:"+numRooms);
					break;

				case 13: //querying Customer Information
					if(arguments.size()!=3){
						obj.wrongNumber();
						break;
					}
					System.out.println("Querying Customer information using id: "+arguments.elementAt(1));
					System.out.println("Customer id: "+arguments.elementAt(2));
						Id = obj.getInt(arguments.elementAt(1));
						customer = obj.getInt(arguments.elementAt(2));
						String bill=rm.queryCustomerInfo(Id,customer);
						System.out.println("Customer info:"+bill);

					break;               

				case 14: //querying a flight Price
					if(arguments.size()!=3){
						obj.wrongNumber();
						break;
					}
					System.out.println("Querying a flight Price using id: "+arguments.elementAt(1));
					System.out.println("Flight number: "+arguments.elementAt(2));
						Id = obj.getInt(arguments.elementAt(1));
						flightNum = obj.getInt(arguments.elementAt(2));
						price=rm.queryFlightPrice(Id,flightNum);
						System.out.println("Price of a seat:"+price);
					break;

				case 15: //querying a Car Price
					if(arguments.size()!=3){
						obj.wrongNumber();
						break;
					}
					System.out.println("Querying a car price using id: "+arguments.elementAt(1));
					System.out.println("Car location: "+arguments.elementAt(2));
						Id = obj.getInt(arguments.elementAt(1));
						location = arguments.elementAt(2);
						price=rm.queryCarsPrice(Id,location);
						System.out.println("Price of a car at this location:"+price);              
					break;

				case 16: //querying a Room price
					if(arguments.size()!=3){
						obj.wrongNumber();
						break;
					}
					System.out.println("Querying a room price using id: "+arguments.elementAt(1));
					System.out.println("Room Location: "+arguments.elementAt(2));
						Id = obj.getInt(arguments.elementAt(1));
						location = arguments.elementAt(2);
						price=rm.queryRoomsPrice(Id,location);
						System.out.println("Price of Rooms at this location:"+price);
					break;

				case 17:  //reserve a flight
					if(arguments.size()!=4){
						obj.wrongNumber();
						break;
					}
					System.out.println("Reserving a seat on a flight using id: "+arguments.elementAt(1));
					System.out.println("Customer id: "+arguments.elementAt(2));
					System.out.println("Flight number: "+arguments.elementAt(3));
						Id = obj.getInt(arguments.elementAt(1));
						customer = obj.getInt(arguments.elementAt(2));
						flightNum = obj.getInt(arguments.elementAt(3));
						if(rm.reserveFlight(Id,customer,flightNum))
							System.out.println("Flight Reserved");
						else
							System.out.println("Flight could not be reserved.");
					break;

				case 18:  //reserve a car
					if(arguments.size()!=4){
						obj.wrongNumber();
						break;
					}
					System.out.println("Reserving a car at a location using id: "+arguments.elementAt(1));
					System.out.println("Customer id: "+arguments.elementAt(2));
					System.out.println("Location: "+arguments.elementAt(3));

						Id = obj.getInt(arguments.elementAt(1));
						customer = obj.getInt(arguments.elementAt(2));
						location = arguments.elementAt(3);

						if(rm.reserveCar(Id,customer,location))
							System.out.println("Car Reserved");
						else
							System.out.println("Car could not be reserved.");
					break;

				case 19:  //reserve a room
					if(arguments.size()!=4){
						obj.wrongNumber();
						break;
					}
					System.out.println("Reserving a room at a location using id: "+arguments.elementAt(1));
					System.out.println("Customer id: "+arguments.elementAt(2));
					System.out.println("Location: "+arguments.elementAt(3));
						Id = obj.getInt(arguments.elementAt(1));
						customer = obj.getInt(arguments.elementAt(2));
						location = arguments.elementAt(3);

						if(rm.reserveRoom(Id,customer,location))
							System.out.println("Room Reserved");
						else
							System.out.println("Room could not be reserved.");
					break;

				case 20:  //reserve an Itinerary
					if(arguments.size()<7){
						obj.wrongNumber();
						break;
					}
					System.out.println("Reserving an Itinerary using id:"+arguments.elementAt(1));
					System.out.println("Customer id:"+arguments.elementAt(2));
					for(int i=0;i<arguments.size()-6;i++)
						System.out.println("Flight number"+arguments.elementAt(3+i));
					System.out.println("Location for Car/Room booking:"+arguments.elementAt(arguments.size()-3));
					System.out.println("Car to book?:"+arguments.elementAt(arguments.size()-2));
					System.out.println("Room to book?:"+arguments.elementAt(arguments.size()-1));
						Id = obj.getInt(arguments.elementAt(1));
						customer = obj.getInt(arguments.elementAt(2));
						Vector<String> flightNumbers = new Vector<String>();
						for(int i=0;i<arguments.size()-6;i++)
							flightNumbers.addElement(arguments.elementAt(3+i));
						location = arguments.elementAt(arguments.size()-3);
						Car = obj.getBoolean(arguments.elementAt(arguments.size()-2));
						Room = obj.getBoolean(arguments.elementAt(arguments.size()-1));

						if(rm.itinerary(Id,customer,flightNumbers,location,Car,Room))
							System.out.println("Itinerary Reserved");
						else
							System.out.println("Itinerary could not be reserved.");
					break;

				case 21:  //quit the client
					if(arguments.size()!=1){
						obj.wrongNumber();
						break;
					}
					System.out.println("Quitting client.");
					System.exit(1);


				case 22:  //new Customer given id
					if(arguments.size()!=3){
						obj.wrongNumber();
						break;
					}
					System.out.println("Adding a new Customer using id:"+arguments.elementAt(1) + " and cid " +arguments.elementAt(2));
						Id = obj.getInt(arguments.elementAt(1));
						Cid = obj.getInt(arguments.elementAt(2));
						rm.newCustomer(Id,Cid);
						System.out.println("new customer id:"+Cid);
					break;

				case 23: //Start a new transaction
					if(arguments.size()!=1){
						obj.wrongNumber();
						break;
					}
					System.out.println("Starting new transaction");
						int tid = rm.start();
						System.out.println("new transaction id:"+tid);
					break;

				case 24: //Commit a transaction
					if(arguments.size()!=2){
						obj.wrongNumber();
						break;
					}
					System.out.println("Committing transaction with id: "+arguments.elementAt(1));
						Id = obj.getInt(arguments.elementAt(1));
						tid = obj.getInt(arguments.elementAt(1));
						if(rm.commit(Id))
							System.out.println("Commit successful.");
						else 
							System.out.println("Commit failed");
					break;

				case 25: //Abort a transaction
					if(arguments.size()!=2){
						obj.wrongNumber();
						break;
					}
					System.out.println("Aborting transaction with id: "+arguments.elementAt(1));
						Id = obj.getInt(arguments.elementAt(1));
						rm.abort(Id);
					break;
				case 26: //shutdown server and client
					if(arguments.size()!=1){
						obj.wrongNumber();
						break;
					}
					System.out.println("Initiating server shutdown.");
					if(rm.shutdown()){
						System.out.println("Server has successfully shutdown. Client is shutting down.");
						System.exit(0);
					} else
						System.out.println("Server shutdown has failed. Client shutdown has been cancelled.");
					break;
					
				case 27: //Start an autocommitted sequence of transactions
					if(arguments.size()!=1){
						obj.wrongNumber();
						break;
					}
					System.out.println("Obtaining id for autocommitted operations.");
						tid = rm.autocommit();
						System.out.println("new transaction id:"+tid);
					break;
				
				case 28: //Crash a server
					if(arguments.size()!=2){
						obj.wrongNumber();
						break;
					}
					String serverName = arguments.elementAt(1);
					System.out.println("Crashing " + serverName);
					rm.crash(serverName);
					break;
					
				case 29: //crash a server at a time
					if(arguments.size()!=3){
						obj.wrongNumber();
						break;
					}
					String serverLocation = arguments.elementAt(1);
					String crashType = arguments.elementAt(2);
					if(!rm.testCrash(serverLocation, crashType)){
						System.out.println("Invalid Selection, No crash will occur");;
					}
					break;
				case 30:
					if(arguments.size()!=1){
						obj.wrongNumber();
						break;
					}
					rm.toggleHeartbeatVerbosity();
					break;
				
				default:
					System.out.println("The interface does not support this command.");
					break;
					
				}//end of switch
			} catch(InvalidTransactionException e){
				System.out.println("The transaction you asked for does not exists. Maybe it was aborted because of inactivity.");
			} catch (TransactionAbortedException e) {
				System.out.println("The request you made for this transaction could not be completed and the transaction has been aborted.");
			} catch (RemoteException e){
				System.out.println("Remote server crashed unexpectedly");
				System.out.println("attempting to reconnect");
			
					boolean connected = false;
					while(!connected){
						try {
						Thread.sleep(1000);
						Registry registry = LocateRegistry.getRegistry(server,port);
						// get the proxy and the remote reference by rmiregistry
						// lookup
						rm = (ResourceManager) registry.lookup("Group2Middleware");
						if (rm != null) connected = rm.isConnected();
						} catch (InterruptedException e1) {
							//try again
						}
						catch (RemoteException e1){
							//try again
						} catch (NotBoundException e1) {
							//try again
						}
				}
				}
		
			catch (Exception e) {
				System.out.println("EXCEPTION:");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}//end of while(true)
	}

	public Vector<String> parse(String command)
	{
		Vector<String> arguments = new Vector<String>();
		StringTokenizer tokenizer = new StringTokenizer(command,",");
		String argument ="";
		while (tokenizer.hasMoreTokens())
		{
			argument = tokenizer.nextToken();
			argument = argument.trim();
			arguments.add(argument);
		}
		return arguments;
	}
	public int findChoice(String argument)
	{
		if (argument.compareToIgnoreCase("help")==0)
			return 1;
		else if(argument.compareToIgnoreCase("newflight")==0)
			return 2;
		else if(argument.compareToIgnoreCase("newcar")==0)
			return 3;
		else if(argument.compareToIgnoreCase("newroom")==0)
			return 4;
		else if(argument.compareToIgnoreCase("newcustomer")==0)
			return 5;
		else if(argument.compareToIgnoreCase("deleteflight")==0)
			return 6;
		else if(argument.compareToIgnoreCase("deletecar")==0)
			return 7;
		else if(argument.compareToIgnoreCase("deleteroom")==0)
			return 8;
		else if(argument.compareToIgnoreCase("deletecustomer")==0)
			return 9;
		else if(argument.compareToIgnoreCase("queryflight")==0)
			return 10;
		else if(argument.compareToIgnoreCase("querycar")==0)
			return 11;
		else if(argument.compareToIgnoreCase("queryroom")==0)
			return 12;
		else if(argument.compareToIgnoreCase("querycustomer")==0)
			return 13;
		else if(argument.compareToIgnoreCase("queryflightprice")==0)
			return 14;
		else if(argument.compareToIgnoreCase("querycarprice")==0)
			return 15;
		else if(argument.compareToIgnoreCase("queryroomprice")==0)
			return 16;
		else if(argument.compareToIgnoreCase("reserveflight")==0)
			return 17;
		else if(argument.compareToIgnoreCase("reservecar")==0)
			return 18;
		else if(argument.compareToIgnoreCase("reserveroom")==0)
			return 19;
		else if(argument.compareToIgnoreCase("itinerary")==0)
			return 20;
		else if (argument.compareToIgnoreCase("quit")==0)
			return 21;
		else if (argument.compareToIgnoreCase("newcustomerid")==0)
			return 22;
		else if (argument.compareToIgnoreCase("start")==0)
			return 23;
		else if (argument.compareToIgnoreCase("commit")==0)
			return 24;
		else if (argument.compareToIgnoreCase("abort")==0)
			return 25;
		else if (argument.compareToIgnoreCase("shutdown")==0)
			return 26;
		else if (argument.compareToIgnoreCase("autocommit")==0)
			return 27;
		else if (argument.compareToIgnoreCase("crash")==0)
			return 28;
		else if (argument.compareToIgnoreCase("crashtime")==0)
			return 29;
		else if (argument.compareToIgnoreCase("toggleverbose")==0)
			return 30;
		
		else
			return 666;

	}

	public void listCommands()
	{
		System.out.println("\nWelcome to the client interface provided to test your project.");
		System.out.println("Commands accepted by the interface are:");
		System.out.println("help");
		System.out.println("newflight\nnewcar\nnewroom\nnewcustomer\nnewcusomterid\ndeleteflight\ndeletecar\ndeleteroom");
		System.out.println("deletecustomer\nqueryflight\nquerycar\nqueryroom\nquerycustomer");
		System.out.println("queryflightprice\nquerycarprice\nqueryroomprice");
		System.out.println("reserveflight\nreservecar\nreserveroom\nitinerary");
		System.out.println("start\nautocommit\ncommit\nabort\nshutdown");
		System.out.println("crash\ncrashTime\ntoggleverbose");
		System.out.println("\nquit");
		System.out.println("\ntype help, <commandname> for detailed info(NOTE the use of comma).");
	}


	public void listSpecific(String command)
	{
		System.out.print("Help on: ");
		switch(findChoice(command))
		{
		case 1:
			System.out.println("Help");
			System.out.println("\nTyping help on the prompt gives a list of all the commands available.");
			System.out.println("Typing help, <commandname> gives details on how to use the particular command.");
			break;

		case 2:  //new flight
			System.out.println("Adding a new Flight.");
			System.out.println("Purpose:");
			System.out.println("\tAdd information about a new flight.");
			System.out.println("\nUsage:");
			System.out.println("\tnewflight,<id>,<flightnumber>,<flightSeats>,<flightprice>");
			break;

		case 3:  //new Car
			System.out.println("Adding a new Car.");
			System.out.println("Purpose:");
			System.out.println("\tAdd information about a new car location.");
			System.out.println("\nUsage:");
			System.out.println("\tnewcar,<id>,<location>,<numberofcars>,<pricepercar>");
			break;

		case 4:  //new Room
			System.out.println("Adding a new Room.");
			System.out.println("Purpose:");
			System.out.println("\tAdd information about a new room location.");
			System.out.println("\nUsage:");
			System.out.println("\tnewroom,<id>,<location>,<numberofrooms>,<priceperroom>");
			break;

		case 5:  //new Customer
			System.out.println("Adding a new Customer.");
			System.out.println("Purpose:");
			System.out.println("\tGet the system to provide a new customer id. (same as adding a new customer)");
			System.out.println("\nUsage:");
			System.out.println("\tnewcustomer,<id>");
			break;


		case 6: //delete Flight
			System.out.println("Deleting a flight");
			System.out.println("Purpose:");
			System.out.println("\tDelete a flight's information.");
			System.out.println("\nUsage:");
			System.out.println("\tdeleteflight,<id>,<flightnumber>");
			break;

		case 7: //delete Car
			System.out.println("Deleting a Car");
			System.out.println("Purpose:");
			System.out.println("\tDelete all cars from a location.");
			System.out.println("\nUsage:");
			System.out.println("\tdeletecar,<id>,<location>,<numCars>");
			break;

		case 8: //delete Room
			System.out.println("Deleting a Room");
			System.out.println("\nPurpose:");
			System.out.println("\tDelete all rooms from a location.");
			System.out.println("Usage:");
			System.out.println("\tdeleteroom,<id>,<location>,<numRooms>");
			break;

		case 9: //delete Customer
			System.out.println("Deleting a Customer");
			System.out.println("Purpose:");
			System.out.println("\tRemove a customer from the database.");
			System.out.println("\nUsage:");
			System.out.println("\tdeletecustomer,<id>,<customerid>");
			break;

		case 10: //querying a flight
			System.out.println("Querying flight.");
			System.out.println("Purpose:");
			System.out.println("\tObtain Seat information about a certain flight.");
			System.out.println("\nUsage:");
			System.out.println("\tqueryflight,<id>,<flightnumber>");
			break;

		case 11: //querying a Car Location
			System.out.println("Querying a Car location.");
			System.out.println("Purpose:");
			System.out.println("\tObtain number of cars at a certain car location.");
			System.out.println("\nUsage:");
			System.out.println("\tquerycar,<id>,<location>");        
			break;

		case 12: //querying a Room location
			System.out.println("Querying a Room Location.");
			System.out.println("Purpose:");
			System.out.println("\tObtain number of rooms at a certain room location.");
			System.out.println("\nUsage:");
			System.out.println("\tqueryroom,<id>,<location>");        
			break;

		case 13: //querying Customer Information
			System.out.println("Querying Customer Information.");
			System.out.println("Purpose:");
			System.out.println("\tObtain information about a customer.");
			System.out.println("\nUsage:");
			System.out.println("\tquerycustomer,<id>,<customerid>");
			break;               

		case 14: //querying a flight for price 
			System.out.println("Querying flight.");
			System.out.println("Purpose:");
			System.out.println("\tObtain price information about a certain flight.");
			System.out.println("\nUsage:");
			System.out.println("\tqueryflightprice,<id>,<flightnumber>");
			break;

		case 15: //querying a Car Location for price
			System.out.println("Querying a Car location.");
			System.out.println("Purpose:");
			System.out.println("\tObtain price information about a certain car location.");
			System.out.println("\nUsage:");
			System.out.println("\tquerycarprice,<id>,<location>");        
			break;

		case 16: //querying a Room location for price
			System.out.println("Querying a Room Location.");
			System.out.println("Purpose:");
			System.out.println("\tObtain price information about a certain room location.");
			System.out.println("\nUsage:");
			System.out.println("\tqueryroomprice,<id>,<location>");        
			break;

		case 17:  //reserve a flight
			System.out.println("Reserving a flight.");
			System.out.println("Purpose:");
			System.out.println("\tReserve a flight for a customer.");
			System.out.println("\nUsage:");
			System.out.println("\treserveflight,<id>,<customerid>,<flightnumber>");
			break;

		case 18:  //reserve a car
			System.out.println("Reserving a Car.");
			System.out.println("Purpose:");
			System.out.println("\tReserve a given number of cars for a customer at a particular location.");
			System.out.println("\nUsage:");
			System.out.println("\treservecar,<id>,<customerid>,<location>,<nummberofCars>");
			break;

		case 19:  //reserve a room
			System.out.println("Reserving a Room.");
			System.out.println("Purpose:");
			System.out.println("\tReserve a given number of rooms for a customer at a particular location.");
			System.out.println("\nUsage:");
			System.out.println("\treserveroom,<id>,<customerid>,<location>,<nummberofRooms>");
			break;

		case 20:  //reserve an Itinerary
			System.out.println("Reserving an Itinerary.");
			System.out.println("Purpose:");
			System.out.println("\tBook one or more flights.Also book zero or more cars/rooms at a location.");
			System.out.println("\nUsage:");
			System.out.println("\titinerary,<id>,<customerid>,<flightnumber1>....<flightnumberN>,<LocationToBookCarsOrRooms>,<NumberOfCars>,<NumberOfRoom>");
			break;


		case 21:  //quit the client
			System.out.println("Quitting client.");
			System.out.println("Purpose:");
			System.out.println("\tExit the client application.");
			System.out.println("\nUsage:");
			System.out.println("\tquit");
			break;

		case 22:  //new customer with id
			System.out.println("Create new customer providing an id");
			System.out.println("Purpose:");
			System.out.println("\tCreates a new customer with the id provided");
			System.out.println("\nUsage:");
			System.out.println("\tnewcustomerid, <id>, <customerid>");
			break;

		case 23: //start a transaction
			System.out.println("Start a new transaction");
			System.out.println("Purpose:");
			System.out.println("\tCreate a new transaction to be committed or aborted at once.");
			System.out.println("\nUsage:");
			System.out.println("\tstart");
			break;

		case 24: //commit a transaction
			System.out.println("Commit a transaction");
			System.out.println("Purpose:");
			System.out.println("\tRegister the transaction to be committed.");
			System.out.println("\nUsage:");
			System.out.println("\tcommit, <id>");
			break;

		case 25: //abort a transaction
			System.out.println("Abort a new transaction");
			System.out.println("Purpose:");
			System.out.println("\tRequest the aborting of the transaction.");
			System.out.println("\nUsage:");
			System.out.println("\tstart, <id>");
			break;
		case 26: //shutdown a transaction
			System.out.println("Shutdown the server and the client");
			System.out.println("Purpose:");
			System.out.println("\tWill shutdown both the client and the server.");
			System.out.println("\nUsage:");
			System.out.println("\tshutdown");
			break;
			
		case 27: //start an autocommitted sequence of operations
			System.out.println("Start a sequence of autocommitted transactions");
			System.out.println("Purpose:");
			System.out.println("\tProvides an id for operations that will be autocommitted.");
			System.out.println("\nUsage:");
			System.out.println("\tstart");
			break;
		
		case 28: //Crash a server
			System.out.println("Simulates a crash on a server");
			System.out.println("Purpose:");
			System.out.println("Crashes the server specified in the parameter");
			System.out.println("\nUsage:");
			System.out.println("\tcrash,[middleware,car,hotel,flight,customer]");
			break;
		
		case 29: //Crash a server during commit
			System.out.println("Simulates a crash on a server during a 2PC");
			System.out.println("Purpose:");
			System.out.println("Crashes the server specified in the parameter, at the time in the other");
			System.out.println("\nUsage:");
			System.out.println("\tcrashtime,[crashtype],[middleware,car,hotel,flight,customer]");
			System.out.println("\tcrashtypes: before_vote during_decision_send after_decisions during_reply\n" +
			"before_decision before_decision_sent before_reply");
			break;
		default:
			System.out.println(command);
			System.out.println("The interface does not support this command.");
			break;
		}
	}

	public void wrongNumber() {
		System.out.println("The number of arguments provided in this command are wrong.");
		System.out.println("Type help, <commandname> to check usage of this command.");
	}



	public int getInt(String temp) throws NumberFormatException{
			return Integer.parseInt(temp);
	}

	public boolean getBoolean(String temp){
			return Boolean.getBoolean(temp);
	}

}
