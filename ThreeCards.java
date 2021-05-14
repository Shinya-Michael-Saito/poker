import java.util.*;

public class ThreeCards {
    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);
        Integer FIRST_STACK=100;
        Integer PLAYER_NUM=2;
        //char[] cards={'A','K','Q'};
        List<Character> cardList = Arrays.asList('A', 'K', 'Q'); 
        List<Player> playerList=new ArrayList<>();
        for(int i=0;i<PLAYER_NUM;i++){
            Player player=new Player(FIRST_STACK,scanner,i);
            playerList.add(player);
            System.out.println("ok");
        }
        
        while(true){
            Collections.shuffle(cardList);//カードをシャッフル
            for(Player player:playerList){
                dealCard(player,cardList);
                cardList.remove(0);
            }
            
            System.out.println( playerList.get(0).getName()+playerList.get(0).getHandList().get(0) );
            break;
            
        }
        

    }

    public static void dealCard(Player player,List<Character> cardList){
        player.getHand(cardList.get(0));
    }


}

class Player{
    private Integer stack;
    private String name;
    private Integer playerId;
    private List<Character> handList=new ArrayList<>();

    Player(Integer FIRST_STACK,Scanner scanner,Integer i){
        this.stack=FIRST_STACK;
        System.out.println("プレイヤー"+(i+1)+"の名前を入力してください");
        this.name=scanner.nextLine();
        this.playerId=i;
        
    }

    public String getName() {
        return name;
    }

    public Integer getStack() {
        return stack;
    }

    public void getHand(Character card){
        handList.add(card);
    }

    public List<Character> getHandList() {
        return handList;
    }

    public void clear(){
        handList.clear();
    }


}
