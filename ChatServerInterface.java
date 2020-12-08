package chat;
import java.rmi.*;
public interface ChatServerInterface extends Remote
{
	//Author: Somnath Shirkule
	public void login(String name,ChatClientInterface ref) throws RemoteException;
	public void takemsg(String msg) throws RemoteException;
	public void logout(String name) throws RemoteException;
	public ChatClientInterface giveRef(String hisName) throws RemoteException;
}