import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client_Side {
    public static void main(String[] args){
        BufferedReader in, info = null;
        BufferedWriter out = null;
        Socket socket = null;
        Scanner scanner = new Scanner(System.in);

        // ip address and port number to connect with server
        String ip_address = null;
        int port_number = 0;

        try {
            // read ip address and port number from server_info.dat file
            info = new BufferedReader(new FileReader("src/server_info.dat"));
        }catch (FileNotFoundException e){
            // if the file is missing, set default values
            ip_address = "localhost";
            port_number = 1234;
        }

        try {
            // if the file exists, read information about ip address, port number
            if(info != null){
                ip_address = info.readLine();
                port_number = Integer.parseInt(info.readLine());
            }

            // connect with server
            socket = new Socket(ip_address, port_number);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            while (true) {
                String questionMessage = in.readLine();     // receive question message
                if(questionMessage.equalsIgnoreCase("end")){

                    String final_score = in.readLine();     // receive final score
                    out.write("end");                   // send end message
                    out.flush();

                    System.out.println(final_score + "\n"); // print final score
                    break;
                }
                System.out.println(questionMessage + "\n"); // print question message

                String answerMessage = scanner.nextLine();  // receive answer message from keyboard
                out.write(answerMessage + "\n");        // send answer message
                out.flush();

                String resultMessage = in.readLine();       // receive result message
                System.out.println(resultMessage + "\n");   // print result message
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }finally {
            try {
                scanner.close();
                if(socket != null)
                    // close socket
                    socket.close();
            }catch (IOException e){
                System.out.println("서버와 채팅 중 오류 발생");
            }
        }
    }
}