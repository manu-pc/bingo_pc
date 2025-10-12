
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class BingoC {
    private String[] carton;
    private InetAddress inAdd;
    private MulticastSocket sock;
    private GenNumero g = new GenNumero();

    // cliente --> recibe las bolas del servidor y va marcando en su cartón
    @SuppressWarnings("deprecation")
    public void cliente() {
        this.carton = genCarton(); // genera cartón (con GenNumero)
        System.out.println(this.toString()); // imprime su cartón
        try {
            inAdd = InetAddress.getByName(Constantes.ip);
            sock = new MulticastSocket(Constantes.PORT);
            sock.joinGroup(inAdd);
            System.out
                    .println(Constantes.AMARILLO + "Cliente iniciado! Esperando por servidor...\n" + Constantes.RESET);
            // espera a recibir el primer mensaje

            byte[] buf = new byte[256];
            while (true) {
                DatagramPacket paquete = new DatagramPacket(buf, buf.length);
                sock.receive(paquete);

                String num = new String(paquete.getData(), 0, paquete.getLength()); // recibe un mensaje
                if (num.equalsIgnoreCase("bingo")) { // si un cliente cualquiera ha enviado bingo, acaba la partida
                    System.out.println("La partida ha acabado. Otro jugador ha cantado bingo.");
                    break;
                } else { // si no, es un numero
                    System.out.println(Constantes.AMARILLO + "Número recibido: " + Constantes.RESET + num);
                    if (comprobarNum(num)) { // si está en el cartón
                        System.out.println(Constantes.VERDE + "Acierto!" + Constantes.RESET);
                    }
                    System.out.println(this.toString());

                    if (comprobarCarton()) { // si el cartón está completo
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

    public boolean comprobarNum(String bola) { // marca el numero como completo en el cartón y devuelve true
        for (int i = 0; i < this.carton.length; i++) {
            if (this.carton[i].equals(bola)) {
                this.carton[i] = "XX";
                return true;
            }
        }
        return false;
    }

    public boolean comprobarCarton() { // comprueba si todo el cartón está marcado como completo
        for (int i = 0; i < this.carton.length; i++) {
            if (!this.carton[i].equals("XX")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() { // imprime estado actual del cartón
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
