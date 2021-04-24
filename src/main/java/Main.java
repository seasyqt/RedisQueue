
public class Main {

  private static RedisStorage redisStorage;

  public static void main(String[] args) {
    redisStorage = new RedisStorage();
    redisStorage.init();
    redisStorage.addUser(20);

    int sizeSet = redisStorage.getRegClient().size();

    for (; ; ) {
      for (int positionUser = 0; positionUser < sizeSet; positionUser++) {
        String user = redisStorage.getRegClient().entryRange(positionUser, positionUser + 1)
            .iterator().next()
            .getValue();
        System.out.println("На главной странице показываем пользователя " + user);

        if (Math.round(Math.random() * 11) > 9) {
          int rnd = buyPositionUser(sizeSet, positionUser, user);
          if (rnd < positionUser) {
            positionUser--;
          }
        }

      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

    }
  }

  private static int buyPositionUser(int sizeSet, int positionUserFromSet, String nameUser) {
    int random = 0;
    boolean isSameUser = true;
    String luckyMan = "";

    while (isSameUser) {
      random = (int) Math.round(Math.random() * (sizeSet - 1));
      luckyMan = redisStorage.getRegClient().entryRange(random, random + 1).iterator().next()
          .getValue();
      isSameUser = nameUser.equals(luckyMan);
    }
    changePositionUsers(random, positionUserFromSet);
    redisStorage.removeUserFromSet(luckyMan);
    int newPositionLuckyUser =
        random > positionUserFromSet ? positionUserFromSet + 1 : positionUserFromSet;
    redisStorage.addUserInSet(newPositionLuckyUser, luckyMan);
    System.out.printf("< Пользователь - %s оплатил платную услугу%n", luckyMan);
    return random;
  }

  private static void changePositionUsers(int positionLuckyUser, int positionLastUser) {

    if (positionLuckyUser > positionLastUser) {
      for (int shiftUserOnRight = positionLastUser + 1; shiftUserOnRight < positionLuckyUser;
          shiftUserOnRight++) {
        String incrementScoreUser = redisStorage.getRegClient()
            .entryRange(shiftUserOnRight, shiftUserOnRight + 1).iterator()
            .next().getValue();
        redisStorage.incrementScoreUser(incrementScoreUser);
      }
    } else {
      for (int shiftUserOnLeft = positionLuckyUser; shiftUserOnLeft < positionLastUser + 1;
          shiftUserOnLeft++) {
        String decrementScoreUser = redisStorage.getRegClient()
            .entryRange(shiftUserOnLeft, shiftUserOnLeft + 1).iterator()
            .next().getValue();
        redisStorage.decrementScoreUser(decrementScoreUser);
      }
    }
  }

}