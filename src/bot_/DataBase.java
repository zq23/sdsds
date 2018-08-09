package bot_;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class DataBase {

    private ArrayList<Long> idsPlayers = new ArrayList<>();
    private HashMap<Long,Player> players = new HashMap<>();

    public ArrayList<Long> getIdsPlayers() {
        return idsPlayers;
    }

    public void addIdToList(Long user_id){
        idsPlayers.add(user_id);

    }

    public void addPlayerToMap(Long id, Player player) {
        players.put(id,player);
    }

    public Player getPlayer(long user_id) {
        return players.get(user_id);
    }

    public HashMap<Long, Player> getPlayers() {
        return players;
    }

    public boolean isExistPlyer(long user_id) {
        if(!players.isEmpty() && players.get(user_id) != null) {
            return true;
        }
        return false;
    }
}
