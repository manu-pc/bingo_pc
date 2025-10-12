import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;


// BingoS --> Servidor de bingo.
// Saca las bolas y envía números hasta el fin de la partida.
public class BingoS {
    private final int PORT = Constantes.PORT;
    private GenNumero g = new GenNumero();
    private InetAddress inAdd;
    private MulticastSocket sock;
    volatile boolean bingo = false; //volatile --> si el hilo receptor la actualiza, el hilo emisor ve el cambio inmediatamente

    @SuppressWarnings("deprecation")
    public void servidor() {
        try {
            inAdd = InetAddress.getByName(Constantes.ip);
            sock = new MulticastSocket();
            System.out.println(Constantes.CIAN + "¡Comienza la partida de bingo!" + Constantes.RESET);
            
            // Dos hilos para poder escuchar y enviar simultáneamente
            // hilo emisor --> saca las bolas y envía los mensajes a los clientes
            Thread emisor = new Thread(() -> {
                try {
                    for (int i = 0; i < 90 && !bingo; i++) {
                        sacarBola();
                    }

                } catch (Exception e) {
                    System.out.println("Se ha producido un error fatal.\n");
                    e.printStackTrace();
                }
            });

            // receptor --> recibe mensajes de los clientes, que pueden ser bingo
            Thread receptor = new Thread(() -> {
                try (MulticastSocket receptorSock = new MulticastSocket(Constantes.PORT)) {
                    receptorSock.joinGroup(inAdd);
                    byte[] buf = new byte[256];
                    while (!bingo) {
                        DatagramPacket paquete = new DatagramPacket(buf, buf.length);
                        receptorSock.receive(paquete);
                        String bin = new String(paquete.getData(), 0, paquete.getLength());
                        if (bin.equalsIgnoreCase("bingo")) {
                            System.out.println(Constantes.VERDE + "¡Alguien tiene un bingo! La partida ha terminado."
                                    + Constantes.RESET);
                            bingo = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Se ha producido un error fatal.\n");
                    e.printStackTrace();
                }
            });

            emisor.start();
            receptor.start();

            // cuando finalicen ambos hilos
            emisor.join();
            receptor.join();

            sock.close();
            System.out.println(Constantes.CIAN + "Servidor cerrado. Gracias por jugar!" + Constantes.RESET);
        } catch (Exception e) {
            System.out.println("Se ha producido un error fatal.\n");
            e.printStackTrace();
        }
    }

    // envía mensaje, delega la generación de bola a otra clase
    public void sacarBola() throws IOException, InterruptedException {
        String num = g.sacarNumero();
        byte[] buf = num.getBytes();
        DatagramPacket paquete = new DatagramPacket(buf, buf.length, inAdd, PORT);
        sock.send(paquete);
        System.out.println("Ha salido el número: " + Constantes.CIAN + num + Constantes.RESET + "!\n");
        Thread.sleep(1000);
    }

}
