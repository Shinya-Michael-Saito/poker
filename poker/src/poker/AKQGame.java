package poker;

import java.util.ArrayList;
import java.util.List;

public class AKQGame {
	public static void main(String[] args) {
        //Integer FIRST_STACK=100;
        //Integer PLAYER_NUM=2;
        //Character[] cards = new Character[]{'A', 'K', 'Q'};
        //List cardList = new LinkedList(Arrays.asList(cards));shuffle非対応
        List<Character> cardList =new ArrayList<Character>();
        cardList.add('A');
        cardList.add('K');
        cardList.add('Q');
        Dealer dealer = new Dealer(cardList);
        dealer.playerSeated();
        dealer.playerSeated();

        //現状無限ループ
        while(dealer.getPlayerNum()>=2){
        	//System.out.println("第" + dealer.getGameCount() + "ゲームを開始します。" );
        	dealer.requestBlind();
        	dealer.shuffleCardList();//カードをシャッフル
            dealer.dealCard(dealer.getPlayer(0));
            dealer.dealCard(dealer.getPlayer(1));

            dealer.whoStartFirst("PreFlop");
            
            dealer.nextPlayerAction();
            
            
            //dealer.showResult();


            System.out.println("第" + dealer.getGameCount() + "ゲームを終了しました。\n" );

            dealer.clearGame();
        }
        /*for(Object card:cardList) {
        	System.out.println(card);
        }*/
        System.out.println("end");


    }


}
