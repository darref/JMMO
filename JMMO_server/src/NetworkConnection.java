import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class NetworkConnection {
    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;

    public NetworkConnection(Socket socket) throws IOException {
        this.socket = socket;
        input = socket.getInputStream();
        output = socket.getOutputStream();
        //socket.setSoTimeout(100);
    }

    public void send(String message) throws IOException {
        byte[] bytes = (message + "\n").getBytes();
        output.write(bytes);
        output.flush();
    }

    public String receive() throws IOException {
        byte[] buffer = new byte[1024];
        StringBuilder message = new StringBuilder();
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            String chunk = new String(buffer, 0, bytesRead);
            message.append(chunk);
            if (chunk.contains("\n")) {
                break;
            }
        }
        return message.toString().trim();
    }


    public void close() throws IOException {
        socket.close();
    }

}
