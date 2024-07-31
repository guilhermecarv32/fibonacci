import java.io.*;
import java.math.BigInteger;
import java.net.*;

public class Cliente1 extends Thread {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final int MAX_INDICE = 999;

    public static void main(String[] args) {
        Cliente1 cliente1 = new Cliente1();
        cliente1.start();
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            for (int i = 2; i <= MAX_INDICE; i += 2) {
                BigInteger resultado = calcularFibonacci(i);

                out.writeInt(i);
                out.writeObject(resultado);
                System.out.println("Resultado parcial enviado: Índice " + i + " = " + resultado);
            }

            out.writeInt(-1);
            out.writeObject(BigInteger.valueOf(-1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BigInteger calcularFibonacci(int n) {
        if (n <= 1) return BigInteger.valueOf(n);
        BigInteger a = BigInteger.ZERO, b = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            BigInteger temp = a;
            a = b;
            b = temp.add(b);
        }
        return b;
    }
}
