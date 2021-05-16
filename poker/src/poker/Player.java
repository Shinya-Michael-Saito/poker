package poker;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Player{
    private Integer stack;
    private String name;
    private Integer playerId;
    private List<Character> handList=new ArrayList<>();
    private Integer bet=0;
    private String lastAction=null;
    //ファーストアクションプレイヤーの判定前に初期化.foldはそのまま
    //ゲーム開始時に初期化
    //判定、ブラインド支払いの初期化はAKQと異なる。

    Player(Integer FIRST_STACK,Scanner scanner,Integer i){
        this.stack=FIRST_STACK;
        this.name=scanner.nextLine();
        this.playerId=i;

    }

    public void burst(Dealer dealer) {
    	dealer.getPlayerList().remove(playerId);

    }

    public void getHand(Character card){
        handList.add(card);
    }

    public void clearHand() {
    	handList.clear();
    }

    public void clearBet() {
    	bet=0;
    }

    public void actionBlindBet(String blindType,Integer blindBet,Dealer dealer) {
    	bet+=blindBet;
    	stack-=blindBet;
    	dealer.addPot(blindBet);
    	if(blindType.equals("BB")) {
    		dealer.setMaxBet(blindBet);
    	}
    	lastAction=blindType;
    }

    public void actionBet(int betAmount,Dealer dealer) {
    	bet+=betAmount;
    	stack-=betAmount;
    	dealer.addPot(betAmount);
    	if(bet>dealer.getMaxBet()) {
    		dealer.setMaxBet(bet);
    	}
    	lastAction="Bet";
    	System.out.println(getName() + "さんが" + bet + "チップ" +lastAction + "しました。");
    }

    public void actionCheck(Dealer dealer) {
    	lastAction="Check";
    	System.out.println(getName() + "さんが" + lastAction + "しました。");
    }

    public void actionCall(Dealer dealer) {
    	int needChip=dealer.getMaxBet()-bet;
    	bet+=needChip;
    	stack-=needChip;
    	dealer.addPot(needChip);
    	lastAction="Call";
    	System.out.println(getName() + "さんが" + lastAction + "しました。");
    }

    public void actionFold(Dealer dealer) {
    	dealer.addFoldNum();
    	dealer.foldWinnerConfirmCheck();
    	lastAction="Fold";
    	System.out.println(getName() + "さんが" + lastAction + "しました。");
    }



    /*
    public void clear(){
        handList.clear();
    }*/

    //ゲッター---------------------
    public Integer getPlayerId() {
		return playerId;
	}

    public String getName() {
        return name;
    }

    public Integer getStack() {
        return stack;
    }

    public List<Character> getHandList() {
        return handList;
    }


    public Integer getBet() {
		return bet;
	}

    public String getLastAction() {
		return lastAction;
	}


}