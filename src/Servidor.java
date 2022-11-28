import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {

    public static void main(String[] args) {
        Panel2 panel = new Panel2();
        panel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class Panel2 extends JFrame implements Runnable {

    public Panel2() {
        setBounds(1200, 300, 280, 350);
        JPanel lamina = new JPanel();
        lamina.setLayout(new BorderLayout());
        field = new JTextArea();
        lamina.add(field, BorderLayout.CENTER);
        add(lamina);
        setTitle("Servidor");
        setVisible(true);
        Thread miHilo = new Thread(this);
        miHilo.start();
    }
    @Override
    public void run() {
        //System.out.println("Escucho");
        try {
            ServerSocket servidor = new ServerSocket(1801);
            String user, ip, mensaje;
            PackageEnviar paqueteRecibido;
            while(true){
            Socket miSocket = servidor.accept();
            ObjectInputStream paqueteDatos = new ObjectInputStream(miSocket.getInputStream());
            paqueteRecibido = (PackageEnviar) paqueteDatos.readObject();
            user = paqueteRecibido.getUser();
            ip = paqueteRecibido.getIp();
            mensaje = paqueteRecibido.getMensaje();
            field.append("\n #" + user + ": " + mensaje + "@" + ip);
            Socket enviaDestinatario = new Socket(ip,1802);
            ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
            paqueteReenvio.writeObject(paqueteRecibido);
            paqueteReenvio.close();
            enviaDestinatario.close();
            miSocket.close();
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    private JTextArea field;

}
