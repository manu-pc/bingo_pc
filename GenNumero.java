public class GenNumero {

    // clase que genera en formato string [01] un cartón de numeros para o cliente
    // ou unha bola para o servidor

    private boolean numSalido[]; // impide que un servidor bingo saque duas veces a misma bola,
                                 // e que un cliente saque duas veces o mesmo numero no cartón
    
    public GenNumero() {
        numSalido = new boolean[Constantes.MAX_NUM + 1];
    }
    public String sacarNumero() {
        int num;
        do {
            num = (int) (Math.random() * (Constantes.MAX_NUM+1));
        } while (numSalido[num]);
        numSalido[num] = true;
        return String.format("%02d", num);

    }

    public String[] sacarCarton() {
        String[] carton = new String[Constantes.TAM_CARTON];
        for (int i = 0; i < Constantes.TAM_CARTON; i++) {
            carton[i] = sacarNumero();
        }
        return carton;
    }
}
