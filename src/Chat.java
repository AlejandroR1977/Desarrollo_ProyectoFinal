
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
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
        addWindowListener(new EnviarOnline());
    }
}

class EnviarOnline extends WindowAdapter {

    public void windowOpened(WindowEvent ev) {
        try {
            Socket miSocket = new Socket("192.168.56.1", 1801);
            PackageEnviar datos = new PackageEnviar();
            datos.setMensaje(" *Se ha conectado*");
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

class Lamina extends JPanel implements Runnable {

    public Lamina() {
        String userNameConectar = JOptionPane.showInputDialog(null, "Selecciona tu nombre de usuario: ", "Conectarse", JOptionPane.PLAIN_MESSAGE);
        JLabel userName = new JLabel("User: ");
        add(userName);
        user = new JLabel();
        user.setText(userNameConectar);
        add(user);
        JLabel conectados = new JLabel("Conectados: ");
        add(conectados);
        ip = new JComboBox();
        add(ip);
        area = new JTextArea(12, 20);
        add(area);
        field = new JTextField(20);
        add(field);
        JButton boton = new JButton("Enviar");
        EnviaTexto evento1 = new EnviaTexto();
        boton.addActionListener(evento1);
        add(boton);
        JButton boton2 = new JButton("Desconectarse");
        Disconnect evento2 = new Disconnect();
        boton2.addActionListener(evento2);
        add(boton2);
        Thread miHilo = new Thread(this);
        miHilo.start();
    }

    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {
        if (field.getText().length() >= 140) {
            evt.consume();
        }
    }

    private class Disconnect implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Socket miSocket = new Socket("192.168.56.1", 1801);
                PackageEnviar datos = new PackageEnviar();
                datos.setMensaje(" *Se ha desconectado*");
                ObjectOutputStream paqueteDatos = new ObjectOutputStream(miSocket.getOutputStream());
                paqueteDatos.writeObject(datos);
                miSocket.close();
                System.exit(0);
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private class EnviaTexto implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            area.append("\n" + field.getText());
            try {
                Socket miSocket = new Socket("192.168.56.1", 1801);
                PackageEnviar datos = new PackageEnviar();
                datos.setUser(user.getText());
                datos.setIp(ip.getSelectedItem().toString());
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
            ServerSocket servidorCliente = new ServerSocket(1802);
            Socket cliente;
            PackageEnviar paqueteRecibido;
            while (true) {
                cliente = servidorCliente.accept();
                ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());
                paqueteRecibido = (PackageEnviar) entrada.readObject();
                if (!paqueteRecibido.getMensaje().equals(" *Se ha conectado*")&&!paqueteRecibido.getMensaje().equals(" *Se ha desconectado*")) {
                    area.append("\n#" + paqueteRecibido.getMensaje() + "@" + paqueteRecibido.getUser());
                } else {
                    //Aqui en lugar de un ArrayList se podria usar un HashMap para que salieran los usuarios en lugar de las ip, pero por motivos de tiempo y otras materias no puedo implementarlo.
                    ArrayList<String> IPsBox = new ArrayList<String>();
                    IPsBox = paqueteRecibido.getIPs();
                    ip.removeAllItems();
                    for (String a : IPsBox) {
                        ip.addItem(a);
                    }
                }
                if (paqueteRecibido.getMensaje().equals(" *Se ha desconectado*")) {
                    ArrayList<String> IPsBox = new ArrayList<String>();
                    IPsBox = paqueteRecibido.getIPs();
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    private JTextField field;
    private JComboBox ip;
    private JLabel user;
    private JButton boton, boton2;
    private JTextArea area;

}

class PackageEnviar implements Serializable {

    private String user, ip, mensaje;
    private ArrayList<String> IPs;

    public ArrayList<String> getIPs() {
        return IPs;
    }

    public void setIPs(ArrayList<String> ips) {
        IPs = ips;
    }

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
