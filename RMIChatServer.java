package chat;
import java.util.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
public class RMIChatServer extends UnicastRemoteObject implements ChatServerInterface
{
	//Author: Somnath Shirkule
	Vector<ClientInfo> v;
	String identity="rmi://localhost:1099/chatapp";
	public RMIChatServer() throws RemoteException
	{
		v=new Vector<ClientInfo>();
		try
		{
			LocateRegistry.createRegistry(1099);
			Naming.rebind(identity,this);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
	public void login(String name, ChatClientInterface ref)
	{
		v.add(new ClientInfo(name,ref));
		broadcastMsg(name+" has logged in.");
		broadcastList();
	}
	public void takemsg(String msg)
	{
		broadcastMsg(msg);
	}
	class ClientInfo
	{
		String name;
		ChatClientInterface ref;
		public ClientInfo(String name, ChatClientInterface ref)
		{
			this.name=name;
			this.ref=ref;
		}
		public boolean equals(Object o)
		{
			if(o instanceof ClientInfo)
			{
				return ((ClientInfo)o).name.equals(name);
			}
			else
				return false;
		}
	};
	public void logout(String name)
	{
		v.remove(new ClientInfo(name,null));
		broadcastMsg(name+" is logged out.");
		broadcastList();
	}
	public void broadcastMsg(String msg)
	{
		Enumeration<ClientInfo> en=v.elements();
		while (en.hasMoreElements())
		{
			ClientInfo ci=en.nextElement();
			try
			{
				ci.ref.takemsg(msg);
			}
			catch (Exception e)
			{
				System.out.println(e);			}
		}
	}
	public ChatClientInterface giveRef(String hisName)
	{
		ClientInfo ci=new ClientInfo(hisName,null);
		if(v.contains(ci))
		{
			int index=v.indexOf(ci);
			ci=v.get(index);
		}
		return ci.ref;
	}
	public void broadcastList()
	{
		Vector<String> v2=new Vector<>();
		Enumeration<ClientInfo> en=v.elements();
		while (en.hasMoreElements())
		{
			ClientInfo ci=en.nextElement();
			v2.add(ci.name);
		}
		Enumeration<ClientInfo> en2=v.elements();
		while(en2.hasMoreElements())
		{
			ClientInfo ci=en2.nextElement();
			try
			{
				ci.ref.takeClientList(v2);
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
		}
	}
	public static void main(String args[])
	{
		try
		{
			new RMIChatServer();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
};