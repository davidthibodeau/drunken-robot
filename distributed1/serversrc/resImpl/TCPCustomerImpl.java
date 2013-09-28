package serversrc.resImpl;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import serversrc.resInterface.*;

import java.util.Calendar;
import java.util.Enumeration;


public class TCPCustomerImpl extends RMBaseImpl implements RMCustomer{
	
	public static void main(String args[]) {
        // Figure out where server is running
        String server = "localhost";
        int port = 1099;

        if (args.length == 1) {
            server = server + ":" + args[0];
            port = Integer.parseInt(args[0]);
        } else if (args.length != 0 &&  args.length != 1) {
            System.err.println ("Wrong usage");
            System.out.println("Usage: java ResImpl.ResourceManagerImpl [port]");
            System.exit(1);
        }

        try {
            // create a new Server object
            TCPCustomerImpl obj = new TCPCustomerImpl();
            // dynamically generate the stub (client proxy)
            RMCustomer rm = (RMCustomer) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry(port);
            registry.rebind("Group2RMCustomer", rm);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }

        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
    }
	
	public TCPCustomerImpl() throws RemoteException {
		
	}
	
	private RMItem readData( int id, String key )
    {
        synchronized(m_itemHT) {
            return (RMItem) m_itemHT.get(key);
        }
    }

    // Writes a data item
    private void writeData( int id, String key, RMItem value )
    {
        synchronized(m_itemHT) {
            m_itemHT.put(key, value);
        }
    }
	
	@Override
	public int newCustomer(int id) throws RemoteException {
		
		Trace.info("INFO: RM::newCustomer(" + id + ") called" );
        // Generate a globally unique ID for the new customer
        int cid = Integer.parseInt( String.valueOf(id) +
                                String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) +
                                String.valueOf( Math.round( Math.random() * 100 + 1 )));
        Customer cust = new Customer( cid );
        writeData( id, cust.getKey(), cust );
        Trace.info("RM::newCustomer(" + cid + ") returns ID=" + cid );
        return cid;
	}
	
	@Override
	public boolean newCustomer(int id, int cid) throws RemoteException {
	    {
	    	Trace.info("INFO: RM::newCustomer(" + id + ", " + cid + ") called" );
	        Customer cust = (Customer) readData( id, Customer.getKey(cid) );
	        if ( cust == null ) {
	            cust = new Customer(cid);
	            writeData( id, cust.getKey(), cust );
	            Trace.info("INFO: RM::newCustomer(" + id + ", " + cid + ") created a new customer" );
	            return true;
	        } else {
	            Trace.info("INFO: RM::newCustomer(" + id + ", " + cid + ") failed--customer already exists");
	            return false;
	        } // else
		}
	}

	@Override
	/**
	 * Must remove all reserved items of that customer as well. 
	 */
	public RMHashtable deleteCustomer(int id, int customerID) throws RemoteException {
		
		Trace.info("RM::deleteCustomer(" + id + ", " + customerID + ") called" );
        Customer cust = (Customer) readData( id, Customer.getKey(customerID) );
        if ( cust == null ) {
            Trace.warn("RM::deleteCustomer(" + id + ", " + customerID + ") failed--customer doesn't exist" );
            return null;
        } else {            
            // Increase the reserved numbers of all reservable items which the customer reserved. 
            RMHashtable reservationHT = cust.getReservations();
            
            // remove the customer from the storage
            removeData(id, cust.getKey());
            
            Trace.info("RM::deleteCustomer(" + id + ", " + customerID + ") succeeded" );
            return reservationHT;
        } // if

	}

    // Returns data structure containing customer reservation info. Returns null if the
    //  customer doesn't exist. Returns empty RMHashtable if customer exists but has no
    //  reservations.
    public RMHashtable getCustomerReservations(int id, int customerID)
        throws RemoteException
    {
        Trace.info("RM::getCustomerReservations(" + id + ", " + customerID + ") called" );
        Customer cust = (Customer) readData( id, Customer.getKey(customerID) );
        if ( cust == null ) {
            Trace.warn("RM::getCustomerReservations failed(" + id + ", " + customerID + ") failed--customer doesn't exist" );
            return null;
        } else {
            return cust.getReservations();
        } // if
    }

	public Customer getCustomer(int id, int customerID)
		throws RemoteException {
		return (Customer) readData( id, Customer.getKey(customerID) );
	}

    // return a bill
    public String queryCustomerInfo(int id, int customerID)
        throws RemoteException
    {
        Trace.info("RM::queryCustomerInfo(" + id + ", " + customerID + ") called" );
        Customer cust = (Customer) readData( id, Customer.getKey(customerID) );
        if ( cust == null ) {
            Trace.warn("RM::queryCustomerInfo(" + id + ", " + customerID + ") failed--customer doesn't exist" );
            return "";   // NOTE: don't change this--WC counts on this value indicating a customer does not exist...
        } else {
                String s = cust.printBill();
                Trace.info("RM::queryCustomerInfo(" + id + ", " + customerID + "), bill follows..." );
                System.out.println( s );
                return s;
        } // if
    }

    public boolean reserve(int id, Customer cust)
    throws RemoteException {
    	writeData( id, cust.getKey(), cust );
    	return true;
    }


}
