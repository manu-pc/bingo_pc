
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class BingoC {
    private String[] carton;
    private InetAddress inAdd;
    private MulticastSocket sock;
    private GenNumero g = new GenNumero();

    @SuppressWarnings("deprecation")
    public void cliente() {
        this.carton = genCarton();
        System.out.println(this.toString());
        try {
            inAdd = InetAddress.getByName(Constantes.ip);
            sock = new MulticastSocket(Constantes.PORT);
            sock.joinGroup(inAdd);
            System.out
                    .println(Constantes.AMARILLO + "Cliente iniciado! Esperando por servidor...\n" + Constantes.RESET);

            byte[] buf = new byte[256];
            while (true) {
                DatagramPacket paquete = new DatagramPacket(buf, buf.length);
                sock.receive(paquete);

                String num = new String(paquete.getData(), 0, paquete.getLength());
                if (num.equalsIgnoreCase("bingo")) {
                    System.out.println("La partida ha acabado. Otro jugador ha cantado bingo.");
                    break;
                } else {
                    System.out.println(Constantes.AMARILLO + "Número recibido: " + Constantes.RESET + num);
                    if (comprobarNum(num)) {
                        System.out.println(Constantes.VERDE + "Acierto!" + Constantes.RESET);
                    }
                    System.out.println(this.toString());

                    if (comprobarCarton()) {
                        System.out
                                .println(Constantes.FONDO_VERDE + "¡Has conseguido bingo!" + Constantes.RESET + "\n");
                        byte[] bin = "bingo".getBytes();
                        paquete = new DatagramPacket(bin, bin.length, inAdd, Constantes.PORT);
                        sock.send(paquete);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Se ha producido un error fatal.\n");
            e.printStackTrace();
        }
    }

    public String[] genCarton() {
        return g.sacarCarton();
    }

    public boolean comprobarNum(String bola) {
        for (int i = 0; i < this.carton.length; i++) {
            if (this.carton[i].equals(bola)) {
                this.carton[i] = "XX";
                return true;
            }
        }
        return false;
    }

    public boolean comprobarCarton() {
        for (int i = 0; i < this.carton.length; i++) {
            if (!this.carton[i].equals("XX")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        int count = 0;
        StringBuilder sb = new StringBuilder("Cartón: ");
        sb.append(Constantes.FONDO_BLANCO + Constantes.NEGRO + "|");
        for (String num : carton) {
            if (num.equals("XX")) {
                sb.append(Constantes.FONDO_ROJO)
                        .append(num)
                        .append(Constantes.FONDO_BLANCO)
                        .append("|");
                count++;
            } else {
                sb.append(Constantes.FONDO_BLANCO)
                        .append(num)
                        .append("|");
            }
        }

        sb.append(Constantes.RESET + "(" + count + "/" + Constantes.TAM_CARTON + ")\n");
        return sb.toString();
    }
}
