package simulator.destruction;

import java.awt.Color;
import java.awt.image.BufferedImage;

import simulator.CulturalSimulator;
import simulator.deprecated.AxelrodOld;

/**
 * Based on FlacheExperiment1 this class implements:
 * 1. Homophily
 * 2. Probabilistic change confronting agents homophily and institution homophily 
 * (and an alpha associated)
 * 3. Probabilistic institutional change according to homophily between neighbor's
 * culture and the agent's (and an alpha prime associated)
 * 
 * This corresponds to the PlosOne code corresponding to the Experiments A and C
 * 
 * @author tico
 *
 */
public class Ulloa extends AxelrodOld {
	
	private static final long serialVersionUID = 6739780243602561128L;

	/**
	 * Nationality of each culture
	 */
	protected int institutions[][] = null;
	
	/**
	 * Possible cultures
	 */
	protected int [][] institution_beliefs = null;
	protected int [] institutionsN = null;

	
	/**
	 * Internal variable for the non death traits
	 */
	protected int non_death_traits[];
	
	/**
	 * Metrics for my own implementation
	 */
	private int culturesU;
	private int biggest_clusterU;
	
	/**
	 * Implements a circular doubled linked list with the members of the 
	 * same culture (country men)
	 */
	protected int [][] countryman_right_r = null;
	protected int [][] countryman_right_c = null;
	private int [][] countryman_left_r = null;
	private int [][] countryman_left_c = null;
	
	/**
	 * Keep the votes of the country men
	 */
	protected int [][] votes = null;
	
	/**
	 * Keep track of the agents that had already vote
	 */
	protected boolean [][] votes_flags;
	
	/**
	 * The current flag to indicate an agent has vote
	 */
	protected boolean hasnt_vote_flag = false;
	
	/**
	 * Keep the current max traits
	 */
	protected int max_traits[] = null;
	
	protected int temp_r;
	protected int max_difference_trait_votes;
	protected int max_feature_traitN;
	protected int culture_current_trait_votes;
	
	/**
	 * Keep the current max features
	 */
	protected int max_features[] = null;
	
	/**
	 * Inactive trait main for the instutions
	 */
	protected static int INACTIVE_TRAIT = -1;

	/**
	 * This are variables that I use inside run_iteration, declared
	 * as fields for efficiency, to avoid re-declaration, all of them
	 * are transient in order to avoid them to be saved in the serialized
	 * object. 
	 */
	protected transient int c;
	protected transient int r;
	protected transient int n;
	protected transient int nr;
	protected transient int nc;
	protected transient int institution;
	protected transient int neighbors_institution;
	protected transient int mismatchesN;
	protected transient int institution_overlap;
	protected transient int neighbors_institution_overlap;
	protected transient int selected_feature;
	protected transient int selected_trait;
	protected transient int institution_trait;
	protected transient int neighbors_institution_trait;
	protected transient double institution_resistance;
	protected transient float institutional_factor;
	protected transient int non_death_traitsN;
	protected transient int differences;
	protected transient int rr;
	protected transient int rc;
	protected transient int lr;
	protected transient int lc;
	protected transient int nrr;
	protected transient int nrc;


	

	
	

	@Override
	public void setup() {
		super.setup();
		
		institutions = new int[ROWS][COLS];
		
		institution_beliefs = new int[ROWS*COLS][FEATURES];
		institutionsN = new int[ROWS*COLS];
		non_death_traits = new int [FEATURES];

		
		countryman_right_r = new int[ROWS][COLS];
		countryman_right_c = new int[ROWS][COLS];
		countryman_left_r = new int[ROWS][COLS];
		countryman_left_c = new int[ROWS][COLS];
		
		// Initialize cultures and nationalities
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				institutions[r][c] = r * COLS + c;
				institutionsN[r * COLS + c] = 1;
				for (int f = 0; f < FEATURES; f++) {
					institution_beliefs[r * COLS + c][f] = -1;
				}
				
				countryman_right_r[r][c] = r;
				countryman_right_c[r][c] = c;
				countryman_left_r[r][c] = r;
				countryman_left_c[r][c] = c;
			}
		}


		votes_flags = new boolean[ROWS][COLS];
		votes = new int[FEATURES][TRAITS+1];
		max_traits = new int[TRAITS];
		
		max_traits = new int[FEATURES*TRAITS];
		max_features = new int[FEATURES*TRAITS];
		
	}

	@Override
	protected void reset(){
		super.reset();
		institutions = null;
		institution_beliefs = null;
		institutionsN = null;
		countryman_right_r = null;
		countryman_right_c = null;
		countryman_left_r = null;
		countryman_left_c = null;

		votes_flags = null;
		votes = null;
		max_traits = null;
		
		max_traits = null;
		max_features = null;
	}

	@Override
	public void run_iteration() {

		for (int ic = 0; ic < CHECKPOINT; ic++) {
			for (int i = 0; i < TOTAL_AGENTS; i++) {
				
				// select the agent
				r = rand.nextInt(ROWS);
				c = rand.nextInt(COLS);

				// select the neighbor that might influence the agent
				n = rand.nextInt(neighboursN[r][c]);
				nr = neighboursX[r][c][n];
				nc = neighboursY[r][c][n];
				
				// select the agent's and neighbor's institution
				institution = institutions[r][c];					
				neighbors_institution = institutions[nr][nc];

				// get the number of mismatches between the two agents					
				mismatchesN = 0;
				differences = 0;
				non_death_traitsN = 0;
				
				// get the number of identical traits between the agent and its institution					
				institution_overlap = 0;		
				
				// get the number of identical traits between the agent 
				// and its neighbors's institution					
				neighbors_institution_overlap = 0;
				
				for (int f = 0; f < FEATURES; f++) {
					if (beliefs[nr][nc][f] != DEAD_TRAIT){
						non_death_traits[non_death_traitsN] = f;
						non_death_traitsN++;
						if (beliefs[r][c][f] != beliefs[nr][nc][f]) {
							mismatches[mismatchesN] = f;
							mismatchesN++;
							if (beliefs[r][c][f] != DEAD_TRAIT){
								differences++;
							}
						}
					} else {
						differences++;
					}
					if (beliefs[r][c][f] == institution_beliefs[neighbors_institution][f]) {
						neighbors_institution_overlap++;
					}
					if (beliefs[r][c][f] == institution_beliefs[institution][f]) {
						institution_overlap++;
					}
				}
				
				// the agent cannot interact with a dead individual
				if (non_death_traitsN > 0){

					// if arrive by selection error, there is still 
					// a chance of influencing the institution
					if (mismatchesN == 0 ) {  
						selected_feature = non_death_traits[rand.nextInt(non_death_traitsN)];
					} else {
						selected_feature = mismatches[rand.nextInt(mismatchesN)];
					}
					
					// select the traits in play (agent's, neighbor's and institution's)
					selected_trait = beliefs[nr][nc][selected_feature];
					institution_trait = institution_beliefs[institution][selected_feature];
					neighbors_institution_trait = institution_beliefs[neighbors_institution][selected_feature];
					
					// When there is no institutional conflict because the institution favors the selected trait
					// or because the trait is already different, then just use homophily
					if (selected_trait == institution_trait || 
							institution_trait != -1 && 
							beliefs[r][c][selected_feature] != institution_trait){
						
						// check if there is actual interaction checking against the homophily and 
						// considering the selection error. The present formula integrates homophily
						// and selection error into one probability. 
						if (rand.nextFloat() <  
								( ((FEATURES - differences) / (float) FEATURES) * (1-SELECTION_ERROR) + 
								(              differences  / (float) FEATURES) * SELECTION_ERROR  ) ) {
	
							//////////////////////
							// CHANGE THE TRAIT //
							//////////////////////
							beliefs[r][c][selected_feature] = selected_trait;
						}
							
					} else {
						
						// Probability of interaction taking into account selection error
						float prob_int = ( ((FEATURES - differences) / (float) FEATURES) * (1-SELECTION_ERROR) + 
										   (            differences  / (float) FEATURES) * SELECTION_ERROR  );
						
						// Calculate the institutional resistance
						institution_resistance = ALPHA * institution_overlap / (float) FEATURES;
						
						// check if there is actual interaction 
						if (rand.nextFloat() >= institution_resistance / (prob_int * BETA + institution_resistance) ) {
						
							
							// at this point, we are sure that the agent is losing similarity with the institution
							// if the new trait is the same of the institution trait, and the current 
							// agent's trait is different from the institution then the cultural overlap 
							// will increase
							if (institution_trait == selected_trait && 
									beliefs[r][c][selected_feature] != institution_trait) {
								 institution_overlap++;
							} 
							//otherwise, if the new trait is different from the institution trait, and 
							// the current agent's trait is the same of the institution then the cultural 
							// overlap will decrease
							else if (institution_trait != selected_trait && 
									beliefs[r][c][selected_feature] == institution_trait) {
								institution_overlap--;
							}
														
							
							// if the new trait is the same of the institution trait, and the current 
							// agent's trait is different from the institution then the institutional overlap 
							// will increase
							if (neighbors_institution_trait == selected_trait && 
									beliefs[r][c][selected_feature] != neighbors_institution_trait) {
								 neighbors_institution_overlap++;
							} 
							//otherwise, if the new trait is different from the institution trait, and 
							// the current agent's trait is the same of the institution then the institutional 
							// overlap will decrease
							else if (neighbors_institution_trait != selected_trait && 
									beliefs[r][c][selected_feature] == neighbors_institution_trait) {
								neighbors_institution_overlap--;
							}
							
							// if the selected feature of the neighbor's institution hasn't been
							// assigned yet, then it is an opportunity to be more similar
							if (institution_beliefs[neighbors_institution][selected_feature] == -1) {
								neighbors_institution_overlap++;
							}						
							
	
							//////////////////////
							// CHANGE THE TRAIT //
							//////////////////////
							// we change the trait after adjusting the overlaps //
							beliefs[r][c][selected_feature] = selected_trait;
						
							// when the agent doesn't have any similarity with the institutions then
							// he loses his identity and accept the trait. This also avoid divisions 
							// by 0 in the next condition.
							if (institution_overlap == 0 && neighbors_institution_overlap == 0) {
								// Nothing happen when the agent is not similar to any of the two institutions
							}						
							// if there is no institutional shock (current trait is different to its institution's), 
							// accept the change
							else {
								institutional_factor = institution_overlap * ALPHA_PRIME;
							
								if (rand.nextFloat() >= institutional_factor / 
										(float) neighbors_institution_overlap * BETA_PRIME +  
										institutional_factor) {
										
									// if the institutions are different, then institution change to its neighbors
									if (institution != neighbors_institution) {
										// its institution lost a citizen
										institutionsN[institution]--;
										institutions[r][c] = neighbors_institution;
										institutionsN[neighbors_institution]++;	
										
										// temporal variables of the agent
										rr = countryman_right_r[r][c];
										rc = countryman_right_c[r][c];
										lr = countryman_left_r[r][c];
										lc = countryman_left_c[r][c];
										
										// Remove the agent from the current culture asking my country men
										// to grab each other
										countryman_left_r[rr][rc] = lr;
										countryman_left_c[rr][rc] = lc;
										countryman_right_r[lr][lc] = rr;
										countryman_right_c[lr][lc] = rc;
										
										// temporal variables of the neighbor
										nrr = countryman_right_r[nr][nc];
										nrc = countryman_right_c[nr][nc];
										
										// The agent grab the neighbor with the left and the right's neighbor 
										// country man with the right
										countryman_right_r[r][c] = nrr;
										countryman_right_c[r][c] = nrc;
										countryman_left_r[r][c] = nr;
										countryman_left_c[r][c] = nc;
										
										// The neighbor and its right countryman grab the agent 
										countryman_right_r[nr][nc] = r;
										countryman_right_c[nr][nc] = c;
										countryman_left_r[nrr][nrc] = r;
										countryman_left_c[nrr][nrc] = c;
										
										
									} // END of different institution
									
									// if there is no trait selected for the selected feature, then make the
									// selected trait part of the institution
									if (institution_beliefs[neighbors_institution][selected_feature] == -1) {
										institution_beliefs[neighbors_institution][selected_feature] = selected_trait;
									} // END of add a institutional trait to institution
			
								} // END of institution change
								
							} // END of else
							
						} // END of institutional shock
						
					} // END of else

				} // END of not grieving 
				
				
				// mutation
				if ( rand.nextFloat() >= 1 - MUTATION ) {
					// TODO after the first invasion TRAITS should be bigger?
					beliefs[r][c][rand.nextInt(FEATURES)] = rand.nextInt(TRAITS);
				}
				
			} // END of total agents
			
			if (FREQ_DEM > 0 &&(iteration * CHECKPOINT + ic + 1) % FREQ_DEM == 0){
				
				// Democratic Process
				// traverse rows
				for (r = 0; r < ROWS; r++) {
					
					// traverse columns
					for (c = 0; c < COLS; c++) {
						
						// if it hasn't vote
						if (votes_flags[r][c] == hasnt_vote_flag) {
													
							// select the institution
							institution = institutions[r][c];
							
														
							// clean the votes of the features
							for (int f = 0; f < FEATURES; f++) {
								for (int t = 0; t < TRAITS; t++) {
									votes[f][t] = 0;
								}
							}
							
							// include my votes
							nr = r;
							nc = c;
							temp_r = nr;
							
							// country-men votes
							do {
								
								// let the agent vote on all the active features
								for (int f = 0; f < FEATURES; f++) {
									if (beliefs[nr][nc][f] != DEAD_TRAIT){
										votes[f][beliefs[nr][nc][f]]++;
									}
								}
								
								votes_flags[nr][nc] = !hasnt_vote_flag;
								
								// avoid overwriting the nr before time
								temp_r = nr;
								
								// look for the next country man on the right
								nr = countryman_right_r[nr][nc];
								nc = countryman_right_c[temp_r][nc];
								
								
								if (votes_flags[nr][nc] != hasnt_vote_flag && !(nr == r && nc == c)) {
									System.out.println("Circular list Kaputt!!! Somebody already voted.");
								}
								
								// while the next agent hasn't vote (nr == r && nc == c)
							} while (votes_flags[nr][nc] == hasnt_vote_flag ) ;
							// END of country men votes
							
							// set winner traits for the current culture
							max_difference_trait_votes = 0;
							max_feature_traitN = 0;
							culture_current_trait_votes = 0;
							
							// iterate over the active features
							for (int f = 0; f < FEATURES; f++) {
								culture_current_trait_votes = 0;
								if (institution_beliefs[institution][f] != -1){
									culture_current_trait_votes = votes[f][institution_beliefs[institution][f]];
								}
								// search for the traits with most votes
								for (int t = 0; t < TRAITS; t++){									
									if ( max_difference_trait_votes < votes[f][t] - culture_current_trait_votes ){
										max_difference_trait_votes = votes[f][t] - culture_current_trait_votes;
										max_feature_traitN = 0;										
										max_traits[max_feature_traitN] = t;
										max_features[max_feature_traitN++] = f;
									} else if ( max_difference_trait_votes == votes[f][t] - culture_current_trait_votes  ){
										max_traits[max_feature_traitN] = t;
										max_features[max_feature_traitN++] = f;
									}
								} // END of search for the traits with most votes
							
							} // END of the iteration over the active features
							
							// if there is maximal group
							if (max_feature_traitN > 0){
								int feature_trait_index = rand.nextInt(max_feature_traitN);
								selected_feature = max_features[feature_trait_index];
	
								// if there was actually a trait that got more (and only more) votes
								// then randomly select one out of the winners and change the trait
								if (institution_beliefs[institution][selected_feature] == -1 || max_difference_trait_votes > 0){
									institution_beliefs[institution][selected_feature] = max_traits[feature_trait_index];
								}
							}
							
						} // END of it hasn't vote
						
					} // END of cols
					
				} // END Democratic Process
					
				// change the flag
				hasnt_vote_flag = !hasnt_vote_flag;
			}
			
			if (FREQ_PROP > 0  && (iteration * CHECKPOINT + ic + 1) % FREQ_PROP == 0){
				
				// Propaganda Process
				// traverse rows
				for (r = 0; r < ROWS; r++) {
					
					// traverse columns
					for (c = 0; c < COLS; c++) {
						
						// if it hasn't vote
						if (votes_flags[r][c] == hasnt_vote_flag) {
													
							// select the institution
							institution = institutions[r][c];
							
							// include my votes
							nr = r;
							nc = c;
							temp_r = nr;
							
                            do {
                            	
                            	// reset the mismatches counter
                            	mismatchesN = 0;
                            	
                            	// count mismatches between the agent and the institution 
                            	for (int f = 0; f < FEATURES; f++) {
            						if (institution_beliefs[institution][f] != beliefs[nr][nc][f]) {
            							mismatchesN++;
            						}
            					}
                            	
                            	for (int f = 0; f < FEATURES; f++) {
	                            	// check if the propaganda has effect by measuring the similarity with the institution 
            						if (institution_beliefs[institution][f] != -1 
            								&& beliefs[nr][nc][f] != institution_beliefs[institution][f] 
            										&& rand.nextFloat() > mismatchesN  / (float) FEATURES ) {
            							beliefs[nr][nc][f] = institution_beliefs[institution][f];
            						}
                            	}
                            	            	
                            	votes_flags[nr][nc] = !hasnt_vote_flag;
								
								// avoid overwriting the nr before time
								temp_r = nr;
								
								// look for the next country man on the right
								nr = countryman_right_r[nr][nc];
								nc = countryman_right_c[temp_r][nc];
								
								
								if (votes_flags[nr][nc] != hasnt_vote_flag && !(nr == r && nc == c)) {
									System.out.println("Circular list Kaputt!!! Somebody already voted.");
								}
								
								// while the next agent hasn't vote (nr == r && nc == c)
							} while (votes_flags[nr][nc] == hasnt_vote_flag ) ;

							
						} // END of it hasn't vote
						
					} // END of cols
					
				} // END Propaganda Process
					
				// change the flag
				hasnt_vote_flag = !hasnt_vote_flag;
			
			}

			
		} // END of checkpoint
			

	} // END of run_experiment
	
	
	protected void check_circular_list() {
		boolean stop = false;
		for (int r = 0; r < ROWS; r++) {
			
			for (int c = 0; c < COLS; c++) {

				// include my votes
				int nr = r;
				int nc = c;
				int temp_r;
				int nationality = institutions[r][c];
				/*System.out.print( "Nationality(" + culturesN[nationality] + "): " + nationality + 
						" Members: (" + countryman_left_r[nr][nc] + ", " + countryman_left_c[nr][nc] + 
						" ) <-(" + nr + "," + nc + 
						") -> (" + countryman_right_r[nr][nc] + "," + countryman_right_c[nr][nc] + ") - ");
				*/
				// country-men votes
				 while (votes_flags[nr][nc] == hasnt_vote_flag ) {
					
					
											
					votes_flags[nr][nc] = !hasnt_vote_flag;
					
					if (institutions[nr][nc] != nationality) {
						System.out.println("kaput!!! Different nationalities: (" + nationality + "," + institutions[nr][nc] + ")");
					}
					
					// avoid overwriting the nr before time
					temp_r = nr;
					
					// look for the next country man on the right
					nr = countryman_right_r[nr][nc];
					nc = countryman_right_c[temp_r][nc];
					
					/*System.out.print("(" + countryman_left_r[nr][nc] + "," + countryman_left_c[nr][nc] + 
						" ) <-(" + nr + "," + nc + 
						") -> (" + countryman_right_r[nr][nc] + "," + countryman_right_c[nr][nc] + ") - ");*/
					
					
					if (votes_flags[nr][nc] != hasnt_vote_flag && !(nr == r && nc == c)) {
						System.out.println("Super NEW Circular list Kaput!!! Somebody already voted.");
						 stop = true;
					}
					
					// while the next agent hasn't vote (nr == r && nc == c)
				}
				//System.out.println();
			}
			
			
		}
		
		if (stop){
			System.out.println();
		}
		
		hasnt_vote_flag = !hasnt_vote_flag;
	}


	
	/**
	 * Search for the biggest culture and counts the number of cultures so far
	 */
	private void count_clustersU(){
		biggest_clusterU = 0;
		culturesU = 0;
		for (int i = 0; i < institutionsN.length; i++) {
			if (institutionsN[i] > 0) {
				culturesU++;
				if (institutionsN[i] > biggest_clusterU){
					biggest_clusterU = institutionsN[i];					
				}
			}
		}
	}

	@Override
	protected String results() {
		count_clustersU();
		return super.results();
	}

	@Override
	public String get_results() {
		return  IDENTIFIER + "," +
				new java.sql.Timestamp(startTime) + "," +
				((endTime == 0) ? (System.currentTimeMillis() - startTime) : (endTime - startTime)) + "," +
				ITERATIONS + "," +  
				CHECKPOINT + "," +  
				TYPE + "," +  
				ROWS + "," +
				COLS + "," +  
				FEATURES + "," +  
				TRAITS + "," +  
				RADIUS + "," +  
				ALPHA + "," +
				ALPHA_PRIME + "," +
				FREQ_DEM + "," +
				FREQ_PROP + "," +
				MUTATION + "," +  
				SELECTION_ERROR + "," +
				iteration * CHECKPOINT+ "," +
				cultureN  + "," +
				(float) cultureN / TOTAL_AGENTS + "," +
				biggest_cluster + "," +
				(float) biggest_cluster / TOTAL_AGENTS + "," + 
				culturesU  + "," +
				(float) culturesU / TOTAL_AGENTS + "," +
				biggest_clusterU + "," +
				(float) biggest_clusterU / TOTAL_AGENTS +  "\n";				
	}
	
	protected void update_gui(){
		super.update_gui();
		update_institutions_graph();
		print_alife_institutional_beliefs_space();
		print_alife_institutions();
		print_institutional_beliefs_association();
	}
	
	protected void update_institutions_graph(){
		CulturalSimulator.graph_institutions.scores.add((double) culturesU / TOTAL_AGENTS);
		CulturalSimulator.graph_institutions.scores2.add((double) biggest_clusterU / TOTAL_AGENTS);
		CulturalSimulator.graph_institutions.update();	
	}
	

	protected void print_institutional_beliefs_association(){
		BufferedImage image = new BufferedImage(ROWS, COLS, BufferedImage.TYPE_INT_RGB);
		int institution = 0;
		String ohex = "";
		
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				
				institution = institutions[r][c];
				ohex = "";
				for (int f = 0; f < FEATURES; f++) {
					if (institution_beliefs[institution][f] == -1){
						ohex += Integer.toHexString(15);
					} else if (institution_beliefs[institution][f] == -2){
						ohex += Integer.toHexString(0);
					} else {
						ohex += Integer.toHexString(institution_beliefs[institution][f]+1);
					}
				}
				ohex = "#" + ohex;

				image.setRGB(r, c, Color.decode(ohex).getRGB());
			}
		}
		
		
		
		CulturalSimulator.set_institutional_beliefs_association(image);
	}
	
	
	protected void print_alife_institutional_beliefs_space(){
		BufferedImage image = new BufferedImage(ROWS, COLS, BufferedImage.TYPE_INT_RGB);
		int institution = 0;
		String ohex = "";
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				ohex = "";
				institution = r * COLS + c;
				if (institutionsN[institution] == 0){
					ohex = "#000000";
				} else {
					for (int f = 0; f < FEATURES; f++) {
						if (institution_beliefs[institution][f] == -1){
							ohex += Integer.toHexString(15);
						} else {
							ohex += Integer.toHexString(institution_beliefs[r*COLS+c][f]+1);	
						}
					}
					ohex = "#" + ohex;
				}
				
								
				image.setRGB(r, c, Color.decode(ohex).getRGB());
			}
		}
		
		CulturalSimulator.set_alife_institutional_beliefs_space(image);
	}
	
	
	protected void print_alife_institutions(){
		BufferedImage image = new BufferedImage(ROWS, COLS, BufferedImage.TYPE_INT_RGB);
		int institution = 0;
		String ohex;
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				ohex = "";
				institution = r*COLS+c;
				
				if (institutionsN[institution] == 0){
					ohex = "#000000";
				} else {
					ohex = "#ffffff";
				}
				
				image.setRGB(r, c, Color.decode(ohex).getRGB());
			}
		}
		
		CulturalSimulator.set_alife_institutions(image);
		
	}
	
	
	private int search_free_institution(int r, int c) {
	    int x=0, y=0, dx = 0, dy = -1;
	    int t = Math.max(ROWS,COLS);
	    int maxI = t*t;

	    for (int i=0; i < maxI; i++){
	        if ((-1 < r+x) && (r+x < ROWS) && (-1 < c+y) && (c+y < COLS)) {
	            if (institutionsN[(r+x) * COLS+(c+y)] == 0 || 
	            		institutionsN[(r+x) * COLS+(c+y)] == 1 
	            		&& institutions[r+x][c+y] == (r+x) * COLS +(c+y)){
	            	return (r+x) * COLS + (c+y);
	            }
	        }

	        if( (x == y) || ((x < 0) && (x == -y)) || ((x > 0) && (x == 1-y))) {
	            t=dx; dx=-dy; dy=t;
	        }   
	        x+=dx; y+=dy;
	    }
	    
	    for (int r1 = 0; r1 < ROWS; r1++){
	    	for (int c1 = 0; c1 < COLS; c1++){
	    		if (institutionsN[r1 * COLS + c1] == 0 || 
	    				institutionsN[r1 * COLS + c1] == 1 &&
	    				r1*COLS+c1 == institutions[r1][c1]){
	    			return r1*COLS+c1;
	    		}
	    	}
	    	
	    }

	    return -99999;
	}

	/**
	 * Invasion inserts a group of foreigners with its own 
	 * institution associated. All of them, including the
	 * institution have the same traits.
	 */
	protected void invasion (int radius){
		int r = ROWS/2;
		int c = COLS/2;
		
		int institution = search_free_institution(r, c);
		
		for (int f=0; f < FEATURES; f++){
			beliefs[r][c][f] = TRAITS;
			institution_beliefs[institution][f] = TRAITS;
		}
		
		// found its  own institution in free space
		abandon_institution(r, c);
		institutions[r][c] = institution;
		institutionsN[institution] = 1;
		
		int r2;
		int c2;
		
		for (int i = 0; i <= radius; i++) {
			for (int j = 0; j <= radius; j++) {
				if ( j + i + 2 <= radius ) {
					if ( r + i + 1 < ROWS && c + j + 1 < COLS ) {
						r2 = r + i + 1;
						c2 = c + j + 1;
						move_to_institution(r2, c2, r, c);
						for (int f=0; f < FEATURES; f++){
							beliefs[r2][c2][f]=TRAITS;
						}
						
					}
					if ( r - i - 1 >= 0 && c - j - 1 >= 0 ) {
						r2 = r - i - 1;
						c2 = c - j - 1;
						move_to_institution(r2, c2, r, c);
						for (int f=0; f < FEATURES; f++){
							beliefs[r2][c2][f]=TRAITS;
						}
					}
				}
				if ( j + i <= radius && ( j != 0 || i != 0 ) ) {
					if ( r - i >= 0 && c + j < COLS ) {
						r2 = r - i;
						c2 = c + j;
						move_to_institution(r2, c2, r, c);
						for (int f=0; f < FEATURES; f++){
							beliefs[r2][c2][f]=TRAITS;
						}
					}
					if ( r + i < ROWS && c - j >= 0 ) {
						r2 = r + i;
						c2 = c - j;
						move_to_institution(r2, c2, r, c);
						for (int f=0; f < FEATURES; f++){
							beliefs[r2][c2][f]=TRAITS;
						}
					}								
				}
			}
		}

	}

	
	/**
	 * Destroy the institutions structure by resetting the 
	 * 1 to 1 relationship between institutions and citizens
	 */
	protected void destroy_institutions_structure(){

		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
			
				institutions[r][c] = r * COLS + c;
				institutionsN[r * COLS + c] = 1;
				
				countryman_right_r[r][c] = r;
				countryman_right_c[r][c] = c;
				countryman_left_r[r][c] = r;
				countryman_left_c[r][c] = c;
				

			}
		}
		
		check_circular_list();
	}
	
	/**
	 * Convert entire institution with certain probability
	 * @param prob
	 */
	protected void institutional_conversion(double prob){
		for (int i = 0; i < ROWS*COLS; i++ ){
			if (institutionsN[i] > 0){
				if (Math.random() < prob) {
					for (int f = 0; f < FEATURES; f++) {
						institution_beliefs[i][f] = -1;
					}
				}
			}			
		}		
	}
	
	/**
	 * Convert traits of institutions with certain probability
	 * @param prob
	 */
	protected void institutional_trait_conversion(double prob){
		for (int i = 0; i < ROWS*COLS; i++ ){
			if (institutionsN[i] > 0){
				for (int f = 0; f < FEATURES; f++) {
					if (Math.random() < prob) {
						institution_beliefs[i][f] = -1;
					}
				}

			}			
		}		
	}

	/**
	 * Destroy the institutions content by resetting the content of
	 * the institutions
	 */
	protected void destroy_institutions_content(){
		// Initialize cultures and nationalities
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				for (int f = 0; f < FEATURES; f++) {
					institution_beliefs[r * COLS + c][f] = -1;
				}
			}
		}
		
	}
	
	/**
	 * A genocide would indicate traits as dead.
	 * 
	 * @param probability
	 */
	protected void genocide(double prob){
		int institution = 0;
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				institution = institutions[r][c];
				if (institutionsN[institution] > 1){
					abandon_institution(r, c);
					institution = search_free_institution(r,c);
					institutions[r][c] = institution;
					institutionsN[institution] = 1;
				}
				
				for (int f = 0; f < FEATURES; f++) {
					if (prob > Math.random()){
						beliefs[r][c][f] = DEAD_TRAIT;
						institution_beliefs[institution][f] = INACTIVE_TRAIT;						
					}
				}
			}
		}
	}
	
	/**
	 * Abandon my institution
	 * @param r
	 * @param c
	 * @param institution
	 */
	private void abandon_institution (int r, int c){
		// its institution lost a citizen
		institutionsN[institutions[r][c]]--;
		institutions[r][c] = -1;
		
		// temporal variables of the agent
		rr = countryman_right_r[r][c];
		rc = countryman_right_c[r][c];
		lr = countryman_left_r[r][c];
		lc = countryman_left_c[r][c];
		
		// Remove the agent from the current culture asking my country men
		// to grab each other
		countryman_left_r[rr][rc] = lr;
		countryman_left_c[rr][rc] = lc;
		countryman_right_r[lr][lc] = rr;
		countryman_right_c[lr][lc] = rc;
		
		// Grab myself since I don't have any institution associated and
		// I am by myself
		countryman_right_r[r][c] = r;
		countryman_right_c[r][c] = c;
		countryman_left_r[r][c] = r;
		countryman_left_c[r][c] = c;
		
	}
	
	/** 
	 * Move from my institution to my neighbors
	 * 
	 * @param r
	 * @param c
	 * @param nr
	 * @param nc
	 */
	private void move_to_institution (int r, int c, int nr, int nc){
		institution = institutions[r][c];
		neighbors_institution = institutions[nr][nc];
		
		// its institution lost a citizen
		institutionsN[institution]--;
		institutions[r][c] = neighbors_institution;
		institutionsN[neighbors_institution]++;	
		
		// temporal variables of the agent
		rr = countryman_right_r[r][c];
		rc = countryman_right_c[r][c];
		lr = countryman_left_r[r][c];
		lc = countryman_left_c[r][c];
		
		// Remove the agent from the current culture asking my country men
		// to grab each other
		countryman_left_r[rr][rc] = lr;
		countryman_left_c[rr][rc] = lc;
		countryman_right_r[lr][lc] = rr;
		countryman_right_c[lr][lc] = rc;
		
		// temporal variables of the neighbor
		nrr = countryman_right_r[nr][nc];
		nrc = countryman_right_c[nr][nc];
		
		// The agent grab the neighbor with the left and the right's neighbor 
		// country man with the right
		countryman_right_r[r][c] = nrr;
		countryman_right_c[r][c] = nrc;
		countryman_left_r[r][c] = nr;
		countryman_left_c[r][c] = nc;
		
		// The neighbor and its right countryman grab the agent 
		countryman_right_r[nr][nc] = r;
		countryman_right_c[nr][nc] = c;
		countryman_left_r[nrr][nrc] = r;
		countryman_left_c[nrr][nrc] = c;
		
	}
	
}