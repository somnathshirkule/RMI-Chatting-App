package chat;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class RMIChatClient extends JFrame implements ActionListener,ChatClientInterface
{
	//Author: Somnath Shirkule
	Hashtable<String,MiniWindow> ht;
	Container c;
	JTextArea jta;
	JList list;
	JTextField t1;
	JButton b1;
	JScrollPane jsp1,jsp2;
	DefaultListModel model;
	String key="rmi://localhost:1099/chatapp";
	ChatServerInterface csi;
	String myname;
	public RMIChatClient(String name)
	{
		super("Chat Client "+name);
		ht=new Hashtable<String,MiniWindow>();
		myname=name;
		setSize(500,777);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		c=getContentPane();
		c.setLayout(null);
		jta=new JTextArea();
		jsp1=new JScrollPane(jta);
		jsp1.setBounds(5,5,500,400);
		c.add(jsp1);
		model=new DefaultListModel();
		list=new JList(model);
		jsp2=new JScrollPane(list);
		jsp2.setBounds(510,5,200,400);
		c.add(jsp2);
		t1=new JTextField();
		t1.setBounds(5,410,500,25);
		c.add(t1);
		b1=new JButton("Send");
		b1.setBounds(510,410,200,25);
		c.add(b1);
		t1.addActionListener(this);
		b1.addActionListener(this);
		list.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount()==2)
				{
					String hisName=(String)list.getSelectedValue();
					MiniWindow mw=ht.get(hisName);
					if(mw==null)
					{
						try
						{
							ChatClientInterface hisRef=csi.giveRef(hisName);
							mw=new MiniWindow(hisName,hisRef);
							ht.put(hisName,mw);
						}
						catch (Exception e3)
						{
							System.out.println(e3);
						}
					}
				}
			}
		});
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				try
				{
					csi.logout(myname);
				}
				catch (Exception e1)
				{
					System.out.println(e1);
				}
			}
		});
		try
		{
			csi=(ChatServerInterface)Naming.lookup(key);
			UnicastRemoteObject.exportObject(this);
			csi.login(myname,this);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
	public void takemsg(String msg)
	{
		jta.append(msg+"\n");
	}
	public void takeClientList(final Vector<String> clients)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				model.removeAllElements();
				Enumeration<String> en=clients.elements();
				while(en.hasMoreElements())
				{
					String s=en.nextElement();
					model.addElement(s);
				}
			}
		});
	}
	public void actionPerformed(ActionEvent e)
	{
		String msg=t1.getText();
		msg=myname+" : "+msg;
		try
		{
			csi.takemsg(msg);
		}
		catch (Exception e1)
		{
			System.out.println(e1);
		}
		t1.setText("");
	}
	
	public void pm(String hisName,ChatClientInterface hisRef, String hisMsg)
	{
		MiniWindow mw=ht.get(hisName);
		if(mw==null)
		{
			mw=new MiniWindow(hisName,hisRef);
			ht.put(hisName,mw);
		}
		mw.jta.append(hisMsg+"\n");
	}
	class MiniWindow extends JFrame implements ActionListener
	{
		Container c;
		JTextArea jta;
		JTextField jtf;
		JButton jbtn;
		JScrollPane jsp1;
		String hisName;
		ChatClientInterface hisRef;
		public MiniWindow(String name,ChatClientInterface ref)
		{
			super("From "+myname+" to "+name);
			hisName=name;
			hisRef=ref;
			setResizable(true);
			setSize(340,280);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			c=getContentPane();
			c.setLayout(null);
			jta=new JTextArea();
			jsp1=new JScrollPane(jta);
			jsp1.setBounds(5,5,325,200);
			c.add(jsp1);
			jtf=new JTextField();
			jtf.setBounds(5,210,85,25);
			c.add(jtf);
			jbtn=new JButton("Send");
			jbtn.setBounds(245,210,85,25);
			c.add(jbtn);
			addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					ht.remove(hisName);
				}
			});
			jtf.addActionListener(this);
			jbtn.addActionListener(this);
			jta.setEditable(false);
			setVisible(true);
		}
		public void actionPerformed(ActionEvent ae)
		{
			String myMsg=jtf.getText();
			myMsg=myname+" : "+myMsg;
			try
			{
				hisRef.pm(myname,RMIChatClient.this,myMsg);
			}
			catch (Exception ex)
			{
				System.out.println(ex);
			}
			jta.append(myMsg+"\n");
			jtf.setText("");
		}
	};
	public static void main(String args[])
	{
		new RMIChatClient(args[0]);
	}
};