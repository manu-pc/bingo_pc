import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class BingoS {
    private final int PORT = Constantes.PORT;
    private GenNumero g = new GenNumero();
    private InetAddress inAdd;
    private MulticastSocket sock;
    volatile boolean bingo = false;

    @SuppressWarnings("deprecation")
    public void servidor() {
        try {
            inAdd = InetAddress.getByName(Constantes.ip);
            sock = new MulticastSocket();
            System.out.println(Constantes.CIAN + "¡Comienza la partida de bingo!" + Constantes.RESET);

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

            emisor.join();
            receptor.join();

            sock.close();
            System.out.println(Constantes.CIAN + "Servidor cerrado. Gracias por jugar!" + Constantes.RESET);
        } catch (Exception e) {
            System.out.println("Se ha producido un error fatal.\n");
            e.printStackTrace();
        }
    }

    public void sacarBola() throws IOException, InterruptedException {
        String num = g.sacarNumero();
        byte[] buf = num.getBytes();
        DatagramPacket paquete = new DatagramPacket(buf, buf.length, inAdd, PORT);
        sock.send(paquete);
        System.out.println("Ha salido el número: " + Constantes.CIAN + num + Constantes.RESET + "!\n");
        Thread.sleep(1000);
    }

}
