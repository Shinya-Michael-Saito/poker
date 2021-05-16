package poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/*テーブルに座っている(burstしていない)メンバー,>>
各プレイヤーのplay or fold or allin,
ボタンの位置を認識>>done*/
public class Dealer {
	public Integer FIRST_STACK=100;
	private Integer SB=1;
	private Integer BB=1;//AKQはSB=BBだがわからなくなったから一時的に変更

	private Integer playerNum=0;
	private List<Player> playerList=new ArrayList<>();
	private List<Character> cardList;
	private Integer cardCount=0;
	private Integer gameCount=1;
	private Integer pot=0;
	private Integer btnIndex=0;
	private Integer maxBet=0;
	private Integer nextPlayerId;
	private Integer nextPlayerIdMinus;
	private boolean gameEnd=false;//ゲッターが出なかったから直接使うことにした。問題あり？
	private boolean roundEnd=false;
	private String round="PreFlop";
	//private boolean showDown=false;
	private Integer foldedPlayerNum=0;
	
	String[] actions = {"Check", "Bet", "call","fold"};

	Scanner scanner = new Scanner(System.in);

	Dealer(List<Character> cardList){
		this.cardList=new ArrayList<>(cardList);
	}

	public void playerSeated() {
		System.out.println("プレイヤーの名前を入力してください。");
		Player player=new Player(FIRST_STACK,scanner,playerNum);
		playerList.add(player);
		playerNum++;
	}

	public void requestBlind() {
		int sbIndex=(btnIndex+1)%playerNum;
		int bbIndex=(btnIndex+2)%playerNum;
		playerList.get(sbIndex).actionBlindBet("SB",SB,this);
		playerList.get(bbIndex).actionBlindBet("BB",BB,this);;

	}

	//普通のポーカーを想定
	public void whoStartFirst(String round) {
		nextPlayerId=btnIndex+1;
		nextPlayerIdMinus=btnIndex;
		//ヘッズアップの場合は？？？
		if("PreFlop".equals(round)) {
			nextPlayerId+=2;
			nextPlayerIdMinus+=2;
		}
	}

	//もっときれいにできるはず。分岐の中の処理はメソッド化。
	//playerのメソッドにする？
	//再起？
	public void akqNextPlayerAction() {
		nextPlayerId%=playerNum;// - foldedPlayerNum
		Player player=getPlayer(nextPlayerId);
		System.out.println(player.getName() + "さん");
		Integer choice;
		if(player.getLastAction().equals("SB")) {
			System.out.println("アクションを選んでください(1:check,2:bet1)");
			choice=scanner.nextInt();
			if(choice==1) {
				player.actionCheck(this);
			}else if(choice==2) {
				player.actionBet(1,this);
			}
			return;
		}else if(player.getLastAction().equals("BB") && player.getBet()==this.getMaxBet()) {
			//SBがcheck
			System.out.println("アクションを選んでください(1:check,2:bet1)");
			choice=scanner.nextInt();
			if(choice==1) {
				player.actionCheck(this);
				gameEnd=true;//AKQだけ
			}else if(choice==2) {
				player.actionBet(1,this);//変更の結果次で引っかかる。
			}
			return;

		}else if(player.getLastAction().equals("BB") && player.getBet()<this.getMaxBet()) {
			//SBがbet
			System.out.println("アクションを選んでください(1:call,2:fold)");
			choice=scanner.nextInt();
			if(choice==1) {
				player.actionCall(this);
				gameEnd=true;
			}else if(choice==2) {
				player.actionFold(this);
			}

			return;
		}else if(player.getLastAction().equals("Bet") && player.getBet()==this.getMaxBet()){
			//SBがbet>BBがcall
			gameEnd=true;
			return;
		}else if(player.getLastAction().equals("Bet") && player.getBet()<this.getMaxBet()) {
			//bet>someone reraise. AKQでは起こらない。
			System.out.println("AKQでは起こらないはず");
			return;
		}else if(player.getLastAction().equals("Check") && player.getBet()<this.getMaxBet()) {
			System.out.println("アクションを選んでください(1:call,2:fold)");
			choice=scanner.nextInt();
			if(choice==1) {
				player.actionCall(this);
				gameEnd=true;
			}else if(choice==2) {
				player.actionFold(this);
			}
			return;
		}
	}


	//switchの処理確認
	public void requestAction(Player player) {
		Integer actionNum = scanner.nextInt();
		switch(actionNum) {
		case 1:
			player.actionCheck(this);
			break;
		case 2:
			System.out.println("いくらベットしますか？");
			int betAmount=scanner.nextInt();
			player.actionBet(betAmount,this);
			break;
		case 3:
			player.actionCall(this);
			break;
		case 4:
			player.actionFold(this);
			break;
		}
	}
	
	
	//前回以前foldしていたまたはnextRound()の場合requestAction
	//分岐の中でrequestActionすると別のifに引っかかる恐れがある←ないだろ
	public void nextPlayerAction() {//引数いらない説→なくした
		nextPlayerIdMinus++;//whoStartFirstでbtn or BBの位置に初期化.
		nextPlayerIdMinus%=playerNum;
		Player player=getPlayer(nextPlayerId);
		Integer commandType=0;
		if(player.getLastAction().equals("Fold")) {
			//次のプレイヤー
			//ここでroundやgameが終わることはない
		}else if(player.getBet() < maxBet) {
			commandType=14;
			//bet call fold
			//
		}else if(player.getBet() == maxBet) {
			if(player.getLastAction()==null) {
				commandType=3;
			}else if(player.getLastAction().equals("Bet") || player.getLastAction().equals("Check")) {
				//check or bet してcheck or callだけでまわってきた場合。
				nextRound();
			}else if(player.getLastAction().equals("BB")) {
				commandType=3;
			}else if(player.getLastAction().equals("SB")) {
				//AKQやトーナメントのSB=BBの場合のみ
				commandType=3;
			}
		}
		
		//nextRound()の場合が解決できてない
		if(commandType!=0) {
			actionCommand(commandType,player);
		}

		if(!gameEnd) {
			nextPlayerAction();
			//roundはnextRound()で切り替わる
			//riverでnextRound()するとgameEnd=>true
		}
	}
	//String[] actions = {"Check", "Bet", "call","fold"};
	//2パターンしかないから個別に作ったほうがよさそう。
	public void actionCommand(Integer commandNum,Player player) {
		String ableAction=player.getName() + "さん、アクションを選んでください。(";
		for(int i=0;i<4;i++) {
			if(((commandNum>>i) & 1) == 1) {
				ableAction+=i+1;
				ableAction+=":";
				ableAction+=actions[i];
			}
		}
		ableAction+=")";
		System.out.println(ableAction);
	}

	public void shuffleCardList() {
		Collections.shuffle(cardList);
	}

	public void dealCard(Player player) {
		int index=cardCount%playerNum;
		player.getHand(cardList.get(index));
		cardCount++;
	}

	public void foldWinnerConfirmCheck() {
		if(foldedPlayerNum>=playerNum-1) {
			gameEnd=true;
		}
		return;
	}

	public void addPot(int bet) {
		pot+=bet;
	}

	public void addFoldNum() {
		foldedPlayerNum++;
	}

	public void nextRound() {
		setMaxBet(0);
		for(Player player:playerList) {
			player.clearBet();
		}
		String report = round + "が終わりました。";
		if(round.equals("River")) {
			gameEnd=true;
			return;
		}else if(round.equals("Turn")) {
			setRound("River");
		}else if(round.equals("Flop")) {
			setRound("Turn");
		}else if(round.equals("PreFlop")) {
			setRound("Flop");
		}
		report += round + "に移ります。";
		System.out.println(report);
		whoStartFirst(round);
	}

	public void clearGame() {
		btnIndex++;
		gameEnd=false;
		pot=0;//確認用
		cardCount=0;
		gameCount++;
		clearFoldedPlayerNum();
		setRound("PreFlop");
		for(Player player:playerList) {
			player.clearHand();
		}
	}

	//ゲッターセッター --------
	public Integer getPlayerNum() {
		return playerNum;
	}

	public List<Player> getPlayerList() {
		return playerList;
	}

	public Player getPlayer(int id) {
		return playerList.get(id);
	}

	public Integer getGameCount() {
		return gameCount;
	}

	public Integer getPot() {
		return pot;
	}

	public Integer getMaxBet() {
		return maxBet;
	}


	public void setMaxBet(Integer maxBet) {
		this.maxBet = maxBet;
	}

	public Integer getFoldedPlayerNum() {
		return foldedPlayerNum;
	}

	public void clearFoldedPlayerNum() {
		foldedPlayerNum = 0;
	}
	
	public String getRound() {
		return round;
	}
	
	public void setRound(String round) {
		this.round = round;
	}
	
	




}
