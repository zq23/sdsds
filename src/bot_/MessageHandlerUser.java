package bot_;


import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import sun.dc.pr.PRError;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageHandlerUser implements Runnable{
    private VKAPI vkapi = new VKAPI();
    private DataBase db = vkapi.getDB();
    private long id;
    private String message;

    public MessageHandlerUser(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public void handler(long id, String message) throws ClientException, ApiException {
        if (!db.isExistPlyer(id)) {
            db.addPlayerToMap(id, new Player(id, 1337, "player", "new_user"));
            db.addIdToList(id);
            System.out.println("NEW PLAYER: \n" + db.getPlayer(id).toString());
            sendMeassage("Новый персонаж создан!\n\n" + db.getPlayer(id).toString() + "\n\n" + showCommand(), id);
        }

        if (db.getPlayer(id).getStatus().equals("seek_combat")) {
            if (message.toLowerCase().equals("да")) {
                if (isWin(db.getPlayer(id).getPower(), db.getPlayer(db.getPlayer(id).getIdEnemy()).getPower())) {
                    sendMeassage("Победа!\n Вам начислено " + db.getPlayer(db.getPlayer(id).getIdEnemy()).getPower() / 2 + " очков силы", id);
                    int upPowerEnemy = db.getPlayer(db.getPlayer(id).getIdEnemy()).getPower() - db.getPlayer(db.getPlayer(id).getIdEnemy()).getPower() / 10;
                    int upPowerPlayer = db.getPlayer(id).getPower() + calculationWin(db.getPlayer(db.getPlayer(id).getIdEnemy()).getPower());
                    db.getPlayer(db.getPlayer(id).getIdEnemy()).setPower(upPowerEnemy);
                    db.getPlayer(id).setPower(upPowerPlayer);
                } else {
                    sendMeassage("Провал! Вы потеряли " + calculationLoss(db.getPlayer(id).getPower()) + " очков силы", id);
                    int upPowerEnemy = db.getPlayer(db.getPlayer(id).getIdEnemy()).getPower() + db.getPlayer(id).getPower() / 10;
                    int upPowerPlayer = db.getPlayer(id).getPower() - calculationLoss(db.getPlayer(id).getPower());
                    db.getPlayer(db.getPlayer(id).getIdEnemy()).setPower(upPowerEnemy);
                    db.getPlayer(id).setPower(upPowerPlayer);
                }
                sendMeassage("Ваша сила: " + db.getPlayer(id).getPower(), id);
                db.getPlayer(id).setStatus("default");
            }else if (message.toLowerCase().equals("нет")) {
                sendMeassage(seekCombat(), id);
            }else if (message.toLowerCase().equals("стоп")) {
                sendMeassage("Поиск прекращен",id);
                db.getPlayer(id).setStatus("default");
            }
        }

        if (!db.getPlayer(id).getStatus().equals("seek_combat")) {
           if(message.toLowerCase().equals("бой")) {
               db.getPlayer(id).setStatus("seek_combat");
               sendMeassage(seekCombat(), id);
           }
        }

        if(message.toLowerCase().equals("инфа")){
            sendMeassage(db.getPlayer(id).toString(),id);
        } else if(checkedCommand(message,1)[0].toLowerCase().equals("инфа")) {
            int idPlayer = Integer.parseInt(checkedCommand(message,3)[0]);
            String mes = db.getPlayer(Long.parseLong(checkedCommand(message,3)[0])).toString();
            if(db.isExistPlyer(idPlayer)) sendMeassage(mes,id);
        }

        if(isAdmin(id)) {
            if(checkedCommand(message,1)[0].toLowerCase().equals("+игрок")) {
                String[] massStr = checkedCommand(message, 2);
                db.addPlayerToMap(Long.parseLong(massStr[2]), new Player(Long.parseLong(massStr[2]), Integer.parseInt(massStr[1]), massStr[0], massStr[3]));
                db.addIdToList(Long.parseLong(massStr[2]));
                sendMeassage("Игрок создан\n" + db.getPlayer(Integer.parseInt(massStr[2])).toString(), id);
            }
            if(checkedCommand(message,1)[0].toLowerCase().equals("-игрок")) {
                long idRem = Long.parseLong(checkedCommand(message,3)[0]);
                db.getPlayers().remove(idRem);
                db.getIdsPlayers().remove(idRem);
            }
        }

    }

    public String showCommand() {
        return "Команды\n" +
                "бой - начать бой\n" +
                "при поиске боя\n" +
                "| да - начать бой\n" +
                "| нет - продолжить поиск\n" +
                "| стоп - прекратить поиск\n" +
                "инфа - инфа о вашем персе\n" +
                "инфа [игрвоовой id пользователя] - инфа о конкретном персе\n" +
                "для админов\n" +
                "+игрок [имя] [сила] [id] [status] - создать игрока, пример +игрок Игрок 2000 12 default\n" +
                "-игрок [id] - удалить игрока";
    }

    public String[] checkedCommand(String mess, long idCommand) {
        if (idCommand == 1) {
            String[] massStr1 = mess.split(" ");
            System.out.println(massStr1[0]);
            return new String[]{massStr1[0]};
        } else if (idCommand == 2) {
            String[] massStr2 = mess.split(" ");
            return new String[]{
                    //name
                    massStr2[1],
                    //power
                    massStr2[2],
                    //id
                    massStr2[3],
                    //status
                    massStr2[4]
            };
        }else if(idCommand == 3) {
            String[] massStr2 = mess.split(" ");
            return new String[] {massStr2[1]};
        }
        return null;
    }

    public String seekCombat() throws ClientException, ApiException {
        Player randomPlayer;
        while (true) {
            randomPlayer = db.getPlayer(db.getIdsPlayers().get(new Random().nextInt(db.getIdsPlayers().size())));
            if (randomPlayer.getUserId() != id) break;
        }
        db.getPlayer(id).setIdEnemy(randomPlayer.getUserId());
        return "Игрок найден\n\n"
                + randomPlayer.toString()
                + "\nваш шанс победить состовляет " + calculationChanceToWin(db.getPlayer(id)) + "%"
                + "\nв случае выигрыша вы получите " + calculationWin(randomPlayer.getPower()) + " очков силы"
                + "\nпри поражении вы потеряете " + calculationLoss(db.getPlayer(id).getPower()) + " очков силы"
                + "\nначать бой? (да/нет)";
    }



    public Integer calculationLoss(int power) {
        if(power - 600 <= 0) return 0;
        if(power / 4 > 150 && power - 600 <= 150) return power / 4;
        return power / 4;
    }

    public Integer calculationWin(int power) {
        return power / 2;
    }

    public Integer calculationChanceToWin(Player player) {
        return  player.getPower() / ((player.getPower() + db.getPlayer(player.getIdEnemy()).getPower()) / 100);
    }

    public boolean isWin (int player, int enemy) {
        int randomNum = new Random().nextInt(player + enemy);
        if(randomNum <= player) return true;
        return false;
    }

    public boolean isAdmin(long id) {
        ArrayList<Integer> idsAdmin = vkapi.getIdsAdmin();
        for (Integer idAdmin : idsAdmin) {
            if (idAdmin == id)return true;
        }
        return false;
    }

    public void sendMeassage(String message,long id) throws ClientException, ApiException {
        vkapi.getVk().messages().send(vkapi.getGroupActor()).message(message).userId((int) id).execute();
    }


    @Override
    public void run() {
        try {
            handler(id, message);
        } catch (ClientException | ApiException e) {
            e.printStackTrace();
        }
    }
}
