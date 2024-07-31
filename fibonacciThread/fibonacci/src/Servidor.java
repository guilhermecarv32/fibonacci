import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.concurrent.*;

public class Servidor {
    private static final int PORT = 12345;
    private static final int MAX_INDICE = 1000;
    private static final ConcurrentMap<Integer, BigInteger> resultadosParciais = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciado na porta " + PORT + ". Aguardando conexões...");

            ExecutorService executor = Executors.newFixedThreadPool(2); // 2 clientes
            for (int i = 0; i < 2; i++) {
                executor.submit(() -> {
                    try (Socket clientSocket = serverSocket.accept();
                        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

                        System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                        while (true) {
                            int indice = in.readInt();
                            BigInteger resultado = (BigInteger) in.readObject();

                            if (indice == -1 && resultado.equals(BigInteger.valueOf(-1))) {
                                System.out.println("Cliente terminou a transmissão.");
                                break;
                            }

                            resultadosParciais.put(indice, resultado);
                            System.out.println("Recebido resultado parcial: Índice " + indice + " = " + resultado);
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);

            System.out.println("Resultado final: Fibonacci de " + MAX_INDICE + " = " + calcularFibonacciTotal());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static BigInteger calcularFibonacciTotal() {
        BigInteger a = BigInteger.ZERO;
        BigInteger b = BigInteger.ONE;
        for (int i = 2; i <= MAX_INDICE; i++) {
            BigInteger temp = b;
            b = resultadosParciais.getOrDefault(i, temp.add(a));
            a = temp;
        }
        return b;
    }

}
