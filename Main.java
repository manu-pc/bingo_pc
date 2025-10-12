public class Main {
    // mismo main para ejecutar un cliente o servidor, se diferencia con arugmentos
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java Main [servidor|cliente]");
            return;
        }

        String modo = args[0].toLowerCase();
        switch (modo) {
            case "servidor":
                BingoS servidor = new BingoS();
                servidor.servidor();
                break;

            case "cliente":
                BingoC cliente = new BingoC();
                cliente.cliente();
                break;

            default:
                System.out.println("Opción no válida. Usa 'servidor' o 'cliente'.");
                break;
        }
    }
}
