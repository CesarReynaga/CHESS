import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Stockfish {

    private Process engine;
    private BufferedReader reader;
    private PrintWriter writer;

    private List<String> moveHistory = new ArrayList<>();

    public Stockfish(String pathToStockfish) {
        try {
            engine = Runtime.getRuntime().exec(pathToStockfish);

            reader = new BufferedReader(
                    new InputStreamReader(engine.getInputStream()));

            writer = new PrintWriter(
                    engine.getOutputStream(), true);

            sendCommand("uci");
            waitFor("uciok");

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void sendCommand(String command) {
        writer.println(command);
    }
    public String getWorstMove() {
    return getWorstMoveInternal();
}

public String getWorstMove(String start, String end) {

    // add player's move first
    moveHistory.add(start + end);

    return getWorstMoveInternal();
}

private String getWorstMoveInternal() {

    try {

        StringBuilder moves = new StringBuilder();

        for(String move : moveHistory) {
            moves.append(move).append(" ");
        }

        sendCommand("position startpos moves " + moves);

        sendCommand("setoption name MultiPV value 20");

        sendCommand("go movetime 500");

        String line;

        String worstMove = null;
        int lowestScore = Integer.MAX_VALUE;

        while((line = reader.readLine()) != null) {

            if(line.startsWith("info")
                    && line.contains("score cp")
                    && line.contains("pv")) {

                String[] parts = line.split(" ");

                int score = 0;
                String move = null;

                for(int i=0;i<parts.length;i++) {

                    if(parts[i].equals("cp")) {
                        score = Integer.parseInt(parts[i+1]);
                    }

                    if(parts[i].equals("pv")) {
                        move = parts[i+1];
                        break;
                    }
                }

                if(move != null && score < lowestScore) {
                    lowestScore = score;
                    worstMove = move;
                }
            }

            if(line.startsWith("bestmove")) {
                break;
            }
         }

            if(worstMove != null) {
                moveHistory.add(worstMove);
            }

            return worstMove;

        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getBestMove() {

        try {

            StringBuilder moves = new StringBuilder();

            for(String move : moveHistory) {
                moves.append(move).append(" ");
            }

            sendCommand("position startpos moves " + moves);

            sendCommand("go movetime 200");

            String line;

            while((line = reader.readLine()) != null) {

                if(line.startsWith("bestmove")) {

                    String bestMove = line.split(" ")[1];

                    moveHistory.add(bestMove);

                    return bestMove;
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    private void waitFor(String target) throws IOException {
        String line;

        while((line = reader.readLine()) != null) {
            if(line.contains(target)) {
                break;
            }
        }
    }

    public String getBestMove(String start, String end) {

        try {

            // add player's move to history
            moveHistory.add(start + end);

            // build move string
            StringBuilder moves = new StringBuilder();

            for(String move : moveHistory) {
                moves.append(move).append(" ");
            }

            sendCommand("position startpos moves " + moves);

            sendCommand("go movetime 200");

            String line;

            while((line = reader.readLine()) != null) {

                if(line.startsWith("bestmove")) {

                    String bestMove = line.split(" ")[1];

                    // add Stockfish response to history too
                    moveHistory.add(bestMove);

                    return bestMove;
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void close() {
        sendCommand("quit");
        engine.destroy();
    }
}