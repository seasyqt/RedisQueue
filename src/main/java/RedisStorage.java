
import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.redisson.config.Config;

public class RedisStorage {

  private RedissonClient redissonClient;

  private RKeys keys;

  private RScoredSortedSet<String> regClient;

  private final static String KEY = "USERS";

  void init() {
    Config config = new Config();
    config.useSingleServer().setAddress("redis://127.0.0.1:6379");
    try {
      redissonClient = Redisson.create(config);
    } catch (RedisConnectionException e) {
      System.out.println("Не удалось подклчится к БД");
      e.printStackTrace();
    }
    keys = redissonClient.getKeys();
    regClient = redissonClient.getScoredSortedSet(KEY);
    keys.delete(KEY);
  }

  public void addUser(int countUser) {
    for (int i = 1; i <= countUser; i++) {
      regClient.add(i, String.valueOf(i));
    }
  }

  public void decrementScoreUser(String nameUser) {
    regClient.addScore(nameUser, -1);
  }

  public void incrementScoreUser(String nameUser) {
    regClient.addScore(nameUser, 1);
  }

  public void removeUserFromSet(String nameUser) {
    regClient.remove(nameUser);
  }

  public void addUserInSet(int newPositionSet, String nameUser) {
    regClient.add(newPositionSet + 1, nameUser);
  }

  public RScoredSortedSet<String> getRegClient() {
    return regClient;
  }
}