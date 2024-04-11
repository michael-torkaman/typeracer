
using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
namespace Server;

public class Server
{
    private int PORT = 3333;

    IPHostEntry ipHostInfo = Dns.GetHostEntry(Dns.GetHostName());

    IPAddress ipAddress;

    IPEndPoint localEndPoint;

    public Server()
    {
        ipAddress = ipHostInfo.AddressList[0];
        localEndPoint = new IPEndPoint(ipAddress, PORT);

        Socket listener = new Socket(ipAddress.AddressFamily,
                SocketType.Stream, ProtocolType.Tcp);
    }

    public void Start()
    {
        try
        {
            listener.Bind(localEndPoint);
            listener.Listen(10);
            Console.WriteLine("Server is listening for connections...");

            while (true)
            {
                // Start listening for connections.
                Socket handler = listener.Accept();
                string data = null;

                // Receive data from the client.
                byte[] bytes = new byte[1024];
                int bytesRec = handler.Receive(bytes);
                data += Encoding.ASCII.GetString(bytes, 0, bytesRec);

                Console.WriteLine($"Received from client: {data}");

                // Echo the data back to the client.
                byte[] msg = Encoding.ASCII.GetBytes(data);
                handler.Send(msg);

                handler.Shutdown(SocketShutdown.Both);
                handler.Close();
            }
        }
        catch (Exception e)
        {
            Console.WriteLine(e.ToString());
        }

        Console.WriteLine("\nPress ENTER to continue...");
        Console.Read();
    }

}