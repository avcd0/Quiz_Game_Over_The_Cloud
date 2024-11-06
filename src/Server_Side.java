
import java.io.*;
import java.net.*;
import java.util.*;

// due to the sub-package not being recognized,I specified it explicitly
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server_Side {
    public static void main(String[] args) throws Exception{
        ServerSocket listener = null;
        Socket socket = null;

        // set port number
        listener = new ServerSocket(9999);
        System.out.println("waiting...");

        // thread pool (max : 10 clients)
        ExecutorService pool = Executors.newFixedThreadPool(10);
        while(true){
            // wait and response
            socket = listener.accept();

            // handle each client (start quiz)
            pool.execute(new Quiz(socket));
        }
    }

    // runnable interface
    private static class Quiz implements Runnable {
        private Socket socket;

        Quiz(Socket socket){
            this.socket = socket;
        }

        // question array
        String[] question = {
                "In which year did World War II end?",
                "What's the name of company which logo is like bitten apple?",
                "Who's the Gachon university's president?(korean name)",
                "What is the name of the substance that covers 71% of the Earth's surface?"
        };
        // counts client's correct score
        int correct_cnt = 0;

        // answer array
        String[] answer = {
                "1945",
                "apple",
                "이길여",
                "water"
        };

        // run function
        @Override
        public void run() {
            System.out.println("Connected: " + socket);

            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                for(int i=0;i<question.length;i++){
                    // send question to client
                    out.write(question[i] + "\n");
                    out.flush();

                    // recieve answer message from client
                    String answerMessage = in.readLine();

                    // send result message

                    // if answer is right, send "Correct!"
                    if(answerMessage.equalsIgnoreCase(answer[i])){
                        out.write("Correct!\n");
                        out.flush();
                        correct_cnt++;
                    }
                    // else, send "Incorrect"
                    else{
                        out.write("Incorrect\n");
                        out.flush();
                    }
                }

                // if all questions were sent, send end message and final score message
                out.write("end\n");
                out.flush();
                out.write("correct: " + correct_cnt + ", incorrect: " + (question.length - correct_cnt) + "\n");
                out.flush();

                // wating for client's end message
                while(true){
                    String endMessage = in.readLine();
                    // receive client's end message
                    if(endMessage.equalsIgnoreCase("end"))
                        break;
                }
            }catch (IOException e){
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (socket != null)
                        // close socket
                        socket.close();
                }catch (IOException e){
                    System.out.println("Error");
                }
                System.out.println("Closed: " + socket);
            }
        }
    }
}