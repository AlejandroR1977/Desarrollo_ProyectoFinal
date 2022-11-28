import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.JFrame;
import javax.swing.*;

public class Chat {

    public static void main(String[] args) {
        Panel1 panel = new Panel1();
        panel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class Panel1 extends JFrame {

    public Panel1() {
        setBounds(600, 300, 280, 350);
        Lamina lamina = new Lamina();
        add(lamina);
        setTitle("Chat");
        setVisible(true);
    }
}

class Lamina extends JPanel implements Runnable {

    public Lamina() {
        user = new JTextField(5);
        add(user);
        ip = new JTextField(8);
        add(ip);
        area = new JTextArea(12, 20);
        add(area);
        field = new JTextField(20);
        add(field);
        JButton boton = new JButton("Enviar");
        EnviaTexto evento = new EnviaTexto();
        boton.addActionListener(evento);
        add(boton);
        Thread miHilo = new Thread(this);
        miHilo.start();
    }

    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {
        if (field.getText().length() >= 140) {
            evt.consume();
        }
    }

    private class EnviaTexto implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Socket miSocket = new Socket("192.168.56.1", 18);
                PackageEnviar datos = new PackageEnviar();
                datos.setUser(user.getText());
                datos.setIp(ip.getText());
                datos.setMensaje(field.getText());
                ObjectOutputStream paqueteDatos = new ObjectOutputStream(miSocket.getOutputStream());
                paqueteDatos.writeObject(datos);
                miSocket.close();
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    @Override
    public void run() {
        try {
            ServerSocket servidorCliente = new ServerSocket(18);
            Socket cliente;
            PackageEnviar paqueteRecibido;
            while (true) {
                cliente = servidorCliente.accept();
                ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());
                paqueteRecibido = (PackageEnviar) entrada.readObject();
                area.append("\n #" + paqueteRecibido.getUser() + ": " + paqueteRecibido.getMensaje());
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    private JTextField field, user, ip;
    private JButton boton;
    private JTextArea area;

}

class PackageEnviar implements Serializable {

    private String user, ip, mensaje;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
