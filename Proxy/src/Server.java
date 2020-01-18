import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(16666); // 监听指定端口
        System.out.println("server is running...");
        for (;;) {
            Socket sock = ss.accept();
            System.out.println("connected from " + sock.getRemoteSocketAddress());
            Thread t = new Handler(sock);
            t.start();
        }
    }
}

class Handler extends Thread {
    Socket sock;

    public Handler(Socket sock) {
        this.sock = sock;
    }

    @Override
    public void run() {
        try (InputStream input = this.sock.getInputStream()) {
            try (OutputStream output = this.sock.getOutputStream()) {
                handle(input, output);
            }
        } catch (Exception e) {
            try {
                this.sock.close();
            } catch (IOException ioe) {
            }
            System.out.println("client disconnected.");
        }
    }

    private void handle(InputStream input, OutputStream output) throws IOException {
        var writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
//        writer.write("hello\n");
//        writer.flush();
        String line;
        String url;
        String host = "";
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            sb.append(line + "\r\n");
            if (line.length() == 0) {
                break;
            } else {
                String[] tmp = line.split(" ");
                if (tmp[0].contains("Host")) {
                    host = tmp[1];
                }
            }
        }
        String all = sb.toString();
        String type = sb.substring(0, sb.indexOf(" "));
        url = all.split(" ")[1];
        System.out.println(all);
//        System.out.println(type);
        Socket ps = new Socket("liaoqing.me", 80);
        try (InputStream prxyInput = ps.getInputStream()) {
            try (OutputStream prxyOutput = ps.getOutputStream()) {
                prxyOutput.write(all.getBytes(StandardCharsets.UTF_8));
                prxyOutput.flush();
                new PrxyHandle(input, prxyOutput).start();
                while (true) {
                    output.write(prxyInput.read());
                    output.flush();
                }
            }
        } catch (Exception e) {
            try {
                ps.close();
            } catch (IOException ioe) {
            }
            System.out.println("client disconnected.");
        }
    }

    class PrxyHandle extends Thread {
        private InputStream input;
        private OutputStream output;

        public PrxyHandle(InputStream input, OutputStream output) {
            this.input = input;
            this.output = output;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    output.write(input.read());
                    output.flush();
                }
//                int n;
//                while ((n = input.read()) != -1) {
//                    output.write(n);
//                    output.flush();
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
