package chat;
import java.rmi.*;
import java.util.*;
public interface ChatClientInterface extends Remote
{
	//Author: Somnath Shirkule
	public void takemsg(String msg) throws RemoteException;
	public void takeClientList(Vector<String> clients) throws RemoteException;
	public void pm(String hisName, ChatClientInterface hisRef, String hisMsg) throws RemoteException;
}