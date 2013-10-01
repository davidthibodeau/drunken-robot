// -------------------------------// Kevin T. Manley// CSE 593// -------------------------------package serversrc.resImpl;import java.util.*;public class Customer extends RMItem{    Customer(int id) {        super();		m_Reservations = new RMHashtable();		m_nID=id;		m_deleted = false;    }		public void setID( int id )		{ m_nID=id; }		public int getID()		{ return m_nID; }		public ReservedItem reserve( String key, String location, int price, ReservedItem.rType rtype )		{			ReservedItem reservedItem = getReservedItem( key );			if( reservedItem == null ) {				// customer doesn't already have a reservation for this resource, so create a new one now				reservedItem = new ReservedItem( key, location, 1, price, rtype );			} else {				reservedItem.setCount( reservedItem.getCount() + 1 );				// NOTE: latest price overrides existing price				reservedItem.setPrice( price );			} // else			m_Reservations.put( reservedItem.getKey(), reservedItem );			return reservedItem;		}				public void unreserve(String key){			m_Reservations.remove(key);		}		public ReservedItem getReservedItem( String key )		{			ReservedItem reservedItem = (ReservedItem) m_Reservations.get( key );			return reservedItem;		}		public String printBill()		{			String s = "Bill for customer " + m_nID + "\n";			Object key = null;			for (Enumeration e = m_Reservations.keys(); e.hasMoreElements(); ) {				key = e.nextElement();				ReservedItem item = (ReservedItem) m_Reservations.get( key );				s = s + item.getCount() + " " + item.getReservableItemKey() + " $" + item.getPrice() + "\n";			}			return s;		}		@Override		public String toString()		{ return "--- BEGIN CUSTOMER key='" + getKey() + "', id='" + getID() + "', reservations=>\n" +						 m_Reservations.toString() + "\n" +						 "--- END CUSTOMER ---"; }		public static String getKey( int customerID )		{			String s = "customer-" + customerID;			return s.toLowerCase();		}		public String getKey()		{			return Customer.getKey( getID() );		}				public boolean isDeleted(){			return m_deleted;		}				public void setDeleted(boolean deleted){			m_deleted = deleted;		}		public RMHashtable getReservations()		{			try {				return m_Reservations;			} catch( Exception e ) {				return null;			} // catch		}		private int m_nID;		private RMHashtable m_Reservations;		private boolean m_deleted;}