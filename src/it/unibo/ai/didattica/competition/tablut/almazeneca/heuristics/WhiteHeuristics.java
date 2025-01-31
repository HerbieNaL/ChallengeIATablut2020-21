package it.unibo.ai.didattica.competition.tablut.almazeneca.heuristics;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
public class WhiteHeuristics extends Heuristics {	

	private int countB;
	private int countW;
	private int blackNearKing;
	private int whiteNearKing;
	private int kingFreeWay;
	private int kingOnThrone;
	private int kingNearThrone;
	private int kingOnStar;
	private int kingFromBorder;
	private int blackPawnsOverhanged;
	private int whitePawnsOverhanged;
	private int kingOverhanged;
	private int kingOnFavourite;
	private int guards;
	private int strategy;
	
	//pesi 
	private double WHITE_WEIGHT_COUNT_WHITE_PAWNS= 7.0;
	private double WHITE_WEIGHT_COUNT_BLACK_PAWNS= 5.0;
	private double WHITE_WEIGHT_SINGLE_FREE_WAY_KING= 8.0;
	private double WHITE_WEIGHT_MULTIPLE_FREE_WAY_KING=12.0;
	private double WHITE_WEIGHT_KING_OVERHANGED = 22.0;
	private double WHITE_WEIGHT_KING_ON_BLUE= 15.0;
	private double WHITE_WEIGHT_STRATEGY = 7.0;
	private double WHITE_WEIGHT_KING_ON_THRONE = 4.0;
	private int pawnsB;
	private int pawnsW;

	private Random r;
	private List<String> citadels;
	private List<String> stars;
	private List<String> nearsThrone;
	private List<String> guardsPos;
	private String throne;

	private List<String> blackBarrier;
	
	public WhiteHeuristics(State state) {
		super(state);
		this.pawnsB = 16;
		this.pawnsW = 9;
		this.r = new Random(System.currentTimeMillis());

		this.citadels = Arrays.asList("a4", "a5", "a6", "b5", "d1", "e1", "f1", "e2", "i4", "i5", "i6", "h5", "d9",
				"e9", "f9", "e8");

		this.stars = Arrays.asList("a2", "a3", "a7", "a8", "b1", "b9", "c1", "c9", "g1", "g9", "h1", "h9", "i2", "i3",
				"i7", "i8");

		this.nearsThrone = Arrays.asList("e4", "e6", "d5", "f5");
		this.throne = "e5";

		this.blackBarrier = Arrays.asList("b3", "b7", "c2", "c8", "g2", "g8", "h3", "h7");

		this.guardsPos = Arrays.asList("a1", "a2", "b1", "h1", "i1", "i2", "i8", "i9", "h9", "b9", "a9", "a8");

	}
	@Override
	public double evaluateState() {
		// TODO Auto-generated method stub
		//inizializza&resetta
		this.resetValues();
		
		//extractValues 
		this.extractValues(state);
		
		//calcolo euristica
		double result= 0;
		
		//controllo se il vincitore � il nero
		if (state.getTurn().equalsTurn(Turn.BLACKWIN.toString())) {
			return Double.NEGATIVE_INFINITY; //valore -infinito?
		}
		//re inesperto  
				if(this.kingOverhanged>0) {
					result -= WHITE_WEIGHT_KING_OVERHANGED * this.kingOverhanged;
				}else {
					if (this.kingFreeWay == 1) {
						result += WHITE_WEIGHT_SINGLE_FREE_WAY_KING * this.kingFreeWay;
					} else {
						if (this.kingFreeWay > 1) {
							result += WHITE_WEIGHT_MULTIPLE_FREE_WAY_KING * (this.kingFreeWay);
						}
					}
				}
		
		result -= WHITE_WEIGHT_KING_ON_THRONE * this.kingOnThrone;
		result += WHITE_WEIGHT_KING_ON_BLUE * this.kingOnStar;

		//peso delle pedine nere
		if(this.countB< this.pawnsB) {
			result+= this.WHITE_WEIGHT_COUNT_BLACK_PAWNS*(this.pawnsB-this.countB);
		}
		//peso delle pedine bianche
		if(this.countW< this.pawnsW) {
			//sottraggo il peso?
		
			result -= WHITE_WEIGHT_COUNT_WHITE_PAWNS * (this.pawnsW - this.countW);
		}
		
		
		return result;
	}
	
	
	private void extractValues(State state) {

		//calcolo della strategia
		for (int i = 0; i < state.getBoard().length; i++) {
			for (int j = 0; j < state.getBoard().length; j++) {
				// conto le pedine bianche
				if (state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())
						|| state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
					this.countW++;

				}

				// conto le pedine nere
				if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
					this.countB++;

				}
		//conto delle pedine nere con una bianca o un accampamento o il trono vicino
				if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())
						&& !this.citadels.contains(state.getBox(i, j).toString())) {
					if (i > 0
							&& (state.getPawn(i - 1, j).equalsPawn(State.Pawn.WHITE.toString())
									|| state.getPawn(i - 1, j).equalsPawn(State.Pawn.KING.toString())
									|| this.citadels.contains(state.getBox(i - 1, j))
									|| state.getBox(i - 1, j).equals(this.throne))
							&& i < state.getBoard().length - 1
							&& (state.getPawn(i + 1, j).equalsPawn(State.Pawn.EMPTY.toString()))) {

						this.blackPawnsOverhanged++;
				}else if (i < state.getBoard().length - 1
						&& (state.getPawn(i + 1, j).equalsPawn(State.Pawn.WHITE.toString())
								|| state.getPawn(i + 1, j).equalsPawn(State.Pawn.KING.toString())
								|| this.citadels.contains(state.getBox(i + 1, j))
								|| state.getBox(i + 1, j).equals(this.throne))
						&& i > 0 && (state.getPawn(i - 1, j).equalsPawn(State.Pawn.EMPTY.toString()))) {
					this.blackPawnsOverhanged++;

				}else if (j > 0
						&& (state.getPawn(i, j - 1).equalsPawn(State.Pawn.WHITE.toString())
								|| state.getPawn(i, j - 1).equalsPawn(State.Pawn.KING.toString())
								|| this.citadels.contains(state.getBox(i, j - 1))
								|| state.getBox(i, j - 1).contentEquals(this.throne))
						&& j < state.getBoard().length - 1
						&& (state.getPawn(i, j + 1).equalsPawn(State.Pawn.EMPTY.toString()))) {
					this.blackPawnsOverhanged++;

				}else if (j < state.getBoard().length - 1
						&& (state.getPawn(i, j + 1).equalsPawn(State.Pawn.WHITE.toString())
								|| state.getPawn(i, j + 1).equalsPawn(State.Pawn.KING.toString())
								|| this.citadels.contains(state.getBox(i, j + 1))
								|| state.getBox(i, j - 1).contentEquals(this.throne))
						&& j > 0 && (state.getPawn(i, j - 1).equalsPawn(State.Pawn.EMPTY.toString()))) {
					this.blackPawnsOverhanged++;

					}
				}
					
		// conto le pedine bianche con una nera o un accampamento o il trono vicino
			
				if (state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())) {

					if (i > 0
							&& (state.getPawn(i - 1, j).equalsPawn(State.Pawn.BLACK.toString())
									|| this.citadels.contains(state.getBox(i - 1, j))
									|| state.getBox(i - 1, j).equals(this.throne))
							&& i < state.getBoard().length - 1
							&& (state.getPawn(i + 1, j).equalsPawn(State.Pawn.EMPTY.toString()))) {

						this.whitePawnsOverhanged++;

					}else if (i < state.getBoard().length - 1
							&& (state.getPawn(i + 1, j).equalsPawn(State.Pawn.BLACK.toString())
									|| this.citadels.contains(state.getBox(i + 1, j))
									|| state.getBox(i + 1, j).equals(this.throne))
							&& i > 0 && (state.getPawn(i - 1, j).equalsPawn(State.Pawn.EMPTY.toString()))) {

						this.whitePawnsOverhanged++;

					}

					else if (j > 0
							&& (state.getPawn(i, j - 1).equalsPawn(State.Pawn.BLACK.toString())
									|| this.citadels.contains(state.getBox(i, j - 1))
									|| state.getBox(i, j - 1).contentEquals(this.throne))
							&& j < state.getBoard().length - 1
							&& (state.getPawn(i, j + 1).equalsPawn(State.Pawn.EMPTY.toString()))) {

						this.whitePawnsOverhanged++;

					}

					else if (j < state.getBoard().length - 1
							&& (state.getPawn(i, j + 1).equalsPawn(State.Pawn.BLACK.toString())
									|| this.citadels.contains(state.getBox(i, j + 1))
									|| state.getBox(i, j + 1).contentEquals(this.throne))
							&& j > 0 && (state.getPawn(i, j - 1).equalsPawn(State.Pawn.EMPTY.toString()))) {

						this.whitePawnsOverhanged++;

					}

				}

		// controllo se il re ha pedine nere intorno o accampamenti o il trono
				
				if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {

					if (i > 0 && (state.getPawn(i - 1, j).equalsPawn(State.Pawn.BLACK.toString())
							|| this.citadels.contains(state.getBox(i - 1, j))
							|| state.getBox(i - 1, j).equals(this.throne))) {
						this.blackNearKing++;
					}

					if (i < state.getBoard().length - 1
							&& (state.getPawn(i + 1, j).equalsPawn(State.Pawn.BLACK.toString())
									|| this.citadels.contains(state.getBox(i + 1, j))
									|| state.getBox(i + 1, j).equals(this.throne))) {
						this.blackNearKing++;
					}

					if (j > 0 && (state.getPawn(i, j - 1).equalsPawn(State.Pawn.BLACK.toString())
							|| this.citadels.contains(state.getBox(i, j - 1))
							|| state.getBox(i, j - 1).contentEquals(this.throne))) {
						this.blackNearKing++;
					}

					if (j < state.getBoard().length - 1
							&& (state.getPawn(i, j + 1).equalsPawn(State.Pawn.BLACK.toString())
									|| this.citadels.contains(state.getBox(i, j + 1))
									|| state.getBox(i, j + 1).contentEquals(this.throne))) {
						this.blackNearKing++;
					}

				}
				
		// controllo se il re ha pedine bianche intorno
		
				if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {

					if (i > 0 && (state.getPawn(i - 1, j).equalsPawn(State.Pawn.WHITE.toString()))) {
						this.whiteNearKing++;
					}

					if (i < state.getBoard().length - 1
							&& (state.getPawn(i + 1, j).equalsPawn(State.Pawn.WHITE.toString()))) {
						this.whiteNearKing++;
					}

					if (j > 0 && (state.getPawn(i, j - 1).equalsPawn(State.Pawn.WHITE.toString())
							|| this.citadels.contains(state.getBox(i, j - 1))
							|| state.getBox(i, j - 1).contentEquals(this.throne))) {
						this.whiteNearKing++;

					}

					if (j < state.getBoard().length - 1
							&& (state.getPawn(i, j + 1).equalsPawn(State.Pawn.WHITE.toString()))) {
						this.whiteNearKing++;
					}

				}
		// controllo se il re ha vie libere per vincere
				if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString()) && (i == 1 || i == 2 || i == 6 || i == 7)
						&& (j == 1 || j == 2 || j == 6 || j == 7)) {
					boolean free = true;
					for (int w = 0; w < i; w++) {

						if (!state.getPawn(w, j).equalsPawn(State.Pawn.EMPTY.toString())
								|| this.citadels.contains(state.getBox(w, j))) {
							free = false;
							break;

						}

					}

					if (free) {
						this.kingFreeWay++;
					}

					free = true;

					for (int w = i + 1; w < state.getBoard().length; w++) {

						if (!state.getPawn(w, j).equalsPawn(State.Pawn.EMPTY.toString())
								|| this.citadels.contains(state.getBox(w, j))) {
							free = false;
							break;

						}

					}

					if (free) {
						this.kingFreeWay++;
					}

					free = true;
					for (int w = 0; w < j; w++) {

						if (!state.getPawn(i, w).equalsPawn(State.Pawn.EMPTY.toString())
								|| this.citadels.contains(state.getBox(i, w))) {
							free = false;
							break;
						}

					}

					if (free) {
						this.kingFreeWay++;
					}

					free = true;

					for (int w = i + 1; w < state.getBoard().length; w++) {

						if (!state.getPawn(i, w).equalsPawn(State.Pawn.EMPTY.toString())
								|| this.citadels.contains(state.getBox(i, w))) {
							free = false;
							break;

						}

					}

					if (free) {
						this.kingFreeWay++;
					}

		// controllo se il re � sul trono
					if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())
							&& state.getBox(i, j).equals(this.throne)) {
						this.kingOnThrone = 1;
					}
		// controllo se il re � vicino al trono
					if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())
							&& this.nearsThrone.contains(state.getBox(i, j))) {
						this.kingNearThrone = 1;
					}
		
		// controllo se il re � su una stella(caselle blu)
					if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())
							&& this.stars.contains(state.getBox(i, j))) {
						this.kingOnStar = 1;
					}
		// controllo se il re � vicino al bordo
					if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
						this.kingFromBorder = Math.min(state.getBoard().length - 1 - i, state.getBoard().length - 1 - j);
					}
				
		// controllo se il re � minacciato
					if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
						// ho una nera sotto e controllo sopra-destra-sinistra
						if (i + 1 < state.getBoard().length - 1
								&& (state.getPawn(i + 1, j).equalsPawn(State.Pawn.BLACK.toString())
										|| citadels.contains(state.getBox(i + 1, j)))) {
							boolean minacciato = false;
							for (int itemp = i - 1; itemp >= 0 && !minacciato; itemp--) {
								if (state.getPawn(itemp, j).equalsPawn(State.Pawn.BLACK.toString()))
									minacciato = true;
								if (state.getPawn(itemp, j).equalsPawn(State.Pawn.THRONE.toString())
										|| citadels.contains(state.getBox(itemp, j))
										|| state.getPawn(itemp, j).equalsPawn(State.Pawn.WHITE.toString()))
									break;
							}
							for (int jtemp = j - 1; jtemp >= 0 && !minacciato; jtemp--) {
								if (state.getPawn(i - 1, jtemp).equalsPawn(State.Pawn.BLACK.toString()))
									minacciato = true;
								if (state.getPawn(i - 1, jtemp).equalsPawn(State.Pawn.THRONE.toString())
										|| citadels.contains(state.getBox(i - 1, jtemp))
										|| state.getPawn(i - 1, jtemp).equalsPawn(State.Pawn.WHITE.toString()))
									break;
							}
							for (int jtemp = j + 1; jtemp < state.getBoard().length - 1 && !minacciato; jtemp++) {
								if (state.getPawn(i - 1, jtemp).equalsPawn(State.Pawn.BLACK.toString()))
									minacciato = true;
								if (state.getPawn(i - 1, jtemp).equalsPawn(State.Pawn.THRONE.toString())
										|| citadels.contains(state.getBox(i - 1, jtemp))
										|| state.getPawn(i - 1, jtemp).equalsPawn(State.Pawn.WHITE.toString()))
									break;
							}
							if (minacciato) {
								kingOverhanged++;
							}
							
						}
					}
				}
			}
		}
					
						
						
	}
	
		private void resetValues() {
			this.countB = 0;
			this.countW = 0;
			this.blackNearKing = 0;
			this.whiteNearKing = 0;
			this.kingFreeWay = 0;
			this.kingOnThrone = 0;
			this.kingOnStar = 0;
			this.kingNearThrone = 0;
			this.kingFromBorder = 0;
			this.blackPawnsOverhanged = 0;
			this.whitePawnsOverhanged = 0;
			this.kingOverhanged = 0;
			this.kingOnFavourite = 0;
			this.guards = 0;

		}

}