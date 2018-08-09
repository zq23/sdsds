package bot_;

public class Player {
    private long user_id;
    private int power;
    private long idEnemy;
    private String name;
    private String status;

    public void setPower(int power) {
        this.power = power;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setIdEnemy(long idEnemy) {
        this.idEnemy = idEnemy;
    }

    public long getUserId() {
        return user_id;
    }

    public long getIdEnemy() {
        return idEnemy;
    }

    public String getStatus() {
        return status;
    }

    public int getPower() {
        return power;
    }

    public String getName() {
        return name;
    }

    public Player(long user_id, int power, String name, String status) {
        this.user_id = user_id;
        this.power = power;
        this.name = name;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Имя: " + name +
                "\nСила: " + power +
                "\nuser_id: " + user_id;
    }
}
