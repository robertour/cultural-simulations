package simulator.control;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.zip.GZIPOutputStream;

import simulator.CulturalSimulator;
import simulator.control.events.Event;

/**
 * TODO comment this class. Describe the general model. 
 * 
 * @author Roberto Ulloa
 *
 */
public abstract class Simulation implements Callable<String>, Serializable {

	private static final long serialVersionUID = 8793729684553266527L;

	/**
	 * String text to identify the type of simulation
	 */
	public String TYPE = null;
	/**
	 * Buffered size to avoid writing each time.
	 */
	public int BUFFERED_SIZE = 512;
	/**
	 * Identify the current simulation object
	 */
	public int IDENTIFIER = 0;
	/**
	 * Indicate if the initialization is meant to be random or uniform
	 */
	public boolean RANDOM_INITIALIZATION = true;
	/**
	 * ROWS of the world
	 */
	public int ROWS = 32;
	/**
	 * COLS of the World
	 */
	public int COLS = 32;
	/**
	 * Total agents in the world
	 */
	protected int TOTAL_AGENTS = ROWS * COLS;
	/**
	 * Number of FEATURES of the cultural space
	 */
	public int FEATURES = 6;
	/**
	 * Number of FEATURES of the cultural space
	 */
	public int TRAITS = 14;
	/**
	 * Alpha value for cultural resilience
	 */
	public float ALPHA = 0.85f;
	protected float BETA = 0.5f;
	/**
	 * Alpha prime value for cultural loyalty
	 */
	public float ALPHA_PRIME = 0.5f;
	protected float BETA_PRIME = 0.5f;

	/**
	 * Frequency of democratic process
	 */
	public int FREQ_DEM = 0;

	/**
	 * Frequency of propaganda process
	 */
	public int FREQ_PROP = 0;

	/**
	 * Define the RADIOUS of the Newmann neighborhood
	 */
	public int RADIUS = 6;
	/**
	 * Number of NEIGHOURS Of the cultural space
	 */
	protected int NEIGHBOURS = RADIUS * RADIUS + (RADIUS + 1) * (RADIUS + 1) - 1;
	/**
	 * Define X coordinates of the neighbors
	 */
	protected int[][][] neighboursX = null;
	/**
	 * Define Y coordinates of the neighbors
	 */
	protected int[][][] neighboursY = null;
	/**
	 * Define the total number of neighbors. It is different per agent because
	 * of the non-toroidal configuration.
	 */
	protected int[][] neighboursN = null;
	/**
	 * Define the MUTATION error. This is when the agent changes one trait
	 * randomly
	 */
	public float MUTATION = 0.001f;
	/**
	 * Define SELECTION_ERROR. This is when the agent randomly changes its
	 * decision to interact or not with an specific agent
	 */
	public float SELECTION_ERROR = 0.001f;

	/**
	 * Individual belief space
	 */
	protected int[][][] beliefs = null;
	/**
	 * Institutional belief space
	 */
	protected int[][] institution_beliefs = null;
	/**
	 * Size of the Nth institution
	 */
	protected int[] institutionsN = null;
	/**
	 * Number of iterations of the simulation
	 */
	public int ITERATIONS = 100000;
	/**
	 * Save and calculate results each checkpoint iterations
	 */
	public int CHECKPOINT = 100;
	/**
	 * Random number generator
	 */
	protected Random rand = new Random();
	/**
	 * This indicates how many times the generation have been saved and restarted
	 */
	protected int epoch = 0;
	/**
	 * Internal iteration of the simulation counter
	 */
	protected int iteration = 0;
	/**
	 * Internal iteration of the simulation counter
	 */
	protected int generation = 0;
	/**
	 * Flag for recursion
	 */
	private boolean flag_mark = true;
	/**
	 * Controls recursion
	 */
	private boolean[][] flags;
	/**
	 * Internal recursion matrix to indicate culture
	 */
	private int[][] cultures;
	/**
	 * Size of the current cluster
	 */
	private int cluster_size = 0;
	/**
	 * Average row of the present culture in the recursion
	 */
	private double ave_row = -1;
	/**
	 * Average row of the present culture in the recursion
	 */
	private double ave_col = -1;
	/**
	 * Number of members of the biggest cluster
	 */
	private int biggest_cluster = 0;
	/**
	 * Number of clusters
	 */
	private int cultureN;
	/**
	 * Number of alife institutions
	 */
	protected int alife_institutions = 0;
	/**
	 * Members of the biggest institution
	 */
	protected int biggest_institution = 0;
	/**
	 * Number of members of the biggest Newmann cluster
	 */
	protected int biggest_newmann_cluster = 0;
	/**
	 * Number of Newmann clusters
	 */
	protected int culture_newmannN;
	/**
	 * Energy of the System
	 */
	protected int energy = 0;
	/**
	 * Spread of the foreigners traits
	 */
	protected int foreiners_traits = 0;
	/**
	 * Number of alive traits
	 */
	protected int alife_traits = 0;
	/**
	 * Number of destroyed institutions
	 */
	protected int destoyed_institutions = 0;
	/**
	 * Number of agents who went into stateless state because their institution
	 * was destroyed
	 */
	protected int stateless = 0;
	/**
	 * Number of agents that abandoned the institutions
	 */
	protected int apostates = 0;
	/**
	 * Number of removed institutions (full content)
	 */
	protected int removed_institutions = 0;
	/**
	 * Number of removed traits (partial content)
	 */
	protected int removed_traits = 0;
	/**
	 * Number of converted institutions (full conversion)
	 */
	protected int converted_institutions = 0;
	/**
	 * Number of converted traits (partial conversion)
	 */
	protected int converted_traits = 0;
	/**
	 * Number of individuals that has been killed
	 */
	protected int casualties = 0;
	/**
	 * Number of invaders that have been introduced to the system
	 */
	protected int invaders = 0;
	/**
	 * Pixel similarity with the initial state
	 */
	protected int pixel_similarity;
	/**
	 * Pixel similarity with the initial state
	 */
	protected int pixel_institution_similarity;
	/**
	 * Defines the index of the similarity vectors. There is 4 values, the first
	 * one being the interaction of the following three: position, size and
	 * beliefs
	 */
	protected static final int FULL_SIM = 0;
	protected static final int POS_SIM = 1;
	protected static final int SIZ_SIM = 2;
	protected static final int BEL_SIM = 3;
	/**
	 * Newmann's culture similarity with the initial state
	 */
	private double[] newmann_similarity = new double[4];
	/**
	 * Culture similarity with the initial state
	 */
	private double[] culture_similarity = new double[4];
	/**
	 * Average center of the culture, number of people per culture, and beliefs
	 */
	protected List<CultureStatistics> culture_stats;
	/**
	 * Average center of the culture, number of people per culture, and beliefs
	 * (calculated as Newmann's)
	 */
	protected List<CultureStatistics> newmann_stats;
	/**
	 * Indicates if the thread should be running. If not, it would stop or
	 * suspend as soon as it can if not. This is when a checkpoint is finalized.
	 */
	private volatile boolean playing = false;
	/**
	 * Indicates if the thread was suspended. It would suspend as soon as it
	 * can. This is when a checkpoint is finalized.
	 */
	private volatile boolean suspended = false;
	/**
	 * Indicates if the thread was cancelled. It would be cancelled as soon as
	 * it can. This is when a checkpoint is finalized.
	 */
	private volatile boolean stopped = false;
	/**
	 * Indicates if the simulation ended completely, without any interruptions.
	 */
	private boolean is_finished = false;
	/**
	 * Activate this when something went wrong in the simulation
	 */
	private boolean failed = false;
	/**
	 * Indicates if there are any catastrophic events scheduled
	 */
	protected volatile ArrayList<Event> events = new ArrayList<Event>();
	protected volatile boolean executing_events = false;
	/**
	 * This object is necessary in order to suspend the thread
	 */
	protected transient Object monitor;
	/**
	 * Register the time when the experiment started
	 */
	protected long startTime;
	/**
	 * Register the time when the experiment finished
	 */
	protected long endTime = 0l;
	/**
	 * Number that represent a TRAIT of a death agent
	 */
	protected static int DEAD_TRAIT = -2;

	/**
	 * # that represent an empty institution
	 */
	protected static int EMPTY = -3;
	/**
	 * This is the starting state of the simulation to calculate
	 * similarity/change of worlds.
	 */
	protected Simulation starter = null;
	/**
	 * This will be where the log is printed
	 */
	public transient Printable log = null;
	/**
	 * The results directory
	 */
	protected transient String results_dir = null;
	/**
	 * The constructor just loads the class TYPE. The rest of the things are
	 * decided in the setup() which is safer.
	 */

	public Simulation() {
		TYPE = this.getClass().getSimpleName().toUpperCase();
		monitor = new Object();
		save_state();
	}

	/**
	 * Performs a deep cloning of the simulation taking advantage of Java
	 * serialization
	 */
	protected void save_state() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);

			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			starter = (Simulation) ois.readObject();

			if (log != null) {
				log.print(IDENTIFIER, "Current state has been saved.\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starting point of execution
	 * 
	 * @returns the last line of results
	 */
	public String call() {
		monitor = new Object();
		playing = true;
		suspended = false;
		stopped = false;
		is_finished = false;
		failed = false;
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(results_dir
					+ Controller.ITERATIONS_DIR + IDENTIFIER + "_" + TYPE + "_" + ROWS + "x" + COLS + ".csv"), "utf-8"),
					BUFFERED_SIZE);
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}

		if (generation == 0) {
			try {
				simulation_setup();
				log.print(IDENTIFIER, "Simulation setup is ready. \n");
			} catch (Exception e1) {
				e1.printStackTrace();
				log.print(IDENTIFIER, "simulation_setup() failed");
				failed = true;
			}

			try {
				setup();
				log.print(IDENTIFIER, TYPE + " setup ready. \n");
			} catch (Exception e1) {
				e1.printStackTrace();
				log.print(IDENTIFIER,  TYPE + " setup() failed");
				failed = true;
			}
			try {
				save_state();
				log.print(IDENTIFIER, "Initial state has been saved. \n");
			} catch (Exception e1) {
				e1.printStackTrace();
				log.print(IDENTIFIER, "save_state() failed");
				failed = true;
			}

			try {
				writer.write(results());
				log.print(IDENTIFIER, "Initial results were written. \n");
			} catch (IOException e) {
				e.printStackTrace();
				log.print(IDENTIFIER, "writer.write(results()); failed");
				failed = true;
			}
		} else {
			epoch++;
		}

		String r = "";
		log.print(IDENTIFIER, "Starting the experiment... \n");
		startTime = System.currentTimeMillis();
		try {
			if (Controller.IS_BATCH) {
				r = run_experiment_batch(writer);
			} else {
				r = run_experiment_single(writer);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.print(IDENTIFIER, "run_experiment(); failed");
			failed = true;
		}
		endTime = System.currentTimeMillis();

		try {
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.print(IDENTIFIER, "system.gc(); failed");
			failed = true;
		}

		if (is_finished) {
			log.print(IDENTIFIER, "Finished: " + get_identification() + "\n");
		} else if (failed) {
			log.print(IDENTIFIER, "Failed: " + get_identification() + "\n");
		} else {
			log.print(IDENTIFIER, "Stopped: " + get_identification() + "\n");
		}

		/**
		 * Always make sure to save and clear memory in batch mode
		 */
		if (Controller.IS_BATCH) {
			save_state();
			save_simulation();
			clean();
		}

		return r;
	}

	/**
	 * Save the simulation object
	 */
	private void save_simulation() {

		try {
			File f = new File(results_dir + Controller.SIMULATIONS_DIR + IDENTIFIER + ".simfile");
			FileOutputStream fos = new FileOutputStream(f);
			GZIPOutputStream gos = new GZIPOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(gos);
			oos.writeObject(this);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Setups the object in order to run the experiment. Initialize all the
	 * variables
	 */
	private void simulation_setup() {

		TYPE = this.getClass().getSimpleName().toUpperCase();
		NEIGHBOURS = RADIUS * RADIUS + (RADIUS + 1) * (RADIUS + 1) - 1;
		TOTAL_AGENTS = ROWS * COLS;
		BETA = 1 - ALPHA;
		BETA_PRIME = 1 - ALPHA_PRIME;

		beliefs = new int[ROWS][COLS][FEATURES];
		neighboursX = new int[ROWS][COLS][NEIGHBOURS];
		neighboursY = new int[ROWS][COLS][NEIGHBOURS];
		neighboursN = new int[ROWS][COLS];

		flags = new boolean[ROWS][COLS];
		cultures = new int[ROWS][COLS];

		int middle_trait = (int) Math.round(TRAITS / 2.0 - 0.01);

		int n = 0;
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				for (int f = 0; f < FEATURES; f++) {
					if (RANDOM_INITIALIZATION) {
						beliefs[r][c][f] = rand.nextInt(TRAITS);
					} else {
						beliefs[r][c][f] = middle_trait;
					}
				}
				n = 0;
				for (int i = 0; i <= RADIUS; i++) {
					for (int j = 0; j <= RADIUS; j++) {
						if (j + i + 2 <= RADIUS) {
							if (r + i + 1 < ROWS && c + j + 1 < COLS) {
								neighboursX[r][c][n] = r + i + 1;
								neighboursY[r][c][n] = c + j + 1;
								n++;
							}
							if (r - i - 1 >= 0 && c - j - 1 >= 0) {
								neighboursX[r][c][n] = r - i - 1;
								neighboursY[r][c][n] = c - j - 1;
								n++;
							}
						}
						if (j + i <= RADIUS && (j != 0 || i != 0)) {
							if (r - i >= 0 && c + j < COLS) {
								neighboursX[r][c][n] = r - i;
								neighboursY[r][c][n] = c + j;
								n++;
							}
							if (r + i < ROWS && c - j >= 0) {
								neighboursX[r][c][n] = r + i;
								neighboursY[r][c][n] = c - j;
								n++;
							}
						}
					}
					neighboursN[r][c] = n;
				}
			}

			culture_stats = new ArrayList<CultureStatistics>();
			newmann_stats = new ArrayList<CultureStatistics>();

		}

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(results_dir
					+ Controller.ITERATIONS_DIR + IDENTIFIER + "_" + TYPE + "_" + ROWS + "x" + COLS + ".csv"), "utf-8"),
					BUFFERED_SIZE);
			writer.write(header());
			writer.flush();
			writer.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Setup the variables for the simulation subclasses
	 */
	protected abstract void setup();

	/**
	 * It is a sort of destructor to help the garbage collector
	 */
	protected void reset() {
		beliefs = null;
		neighboursX = null;
		neighboursY = null;
		neighboursN = null;
		flags = null;
		cultures = null;
		culture_stats = null;
		newmann_stats = null;
	}

	/**
	 * Run experiments in batch mode.
	 * 
	 * @param writer
	 * @return the results of the simulations
	 */
	private String run_experiment_batch(BufferedWriter writer) {
		String r = "";
		check_for_events();
		for (iteration = 0; iteration < ITERATIONS; iteration += CHECKPOINT) {
			run_iterations();
			generation += CHECKPOINT;

			r = results();

			// write results of the current checkpoint
			try {
				writer.write(r);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// check if the user hasn't cancelled or suspended the thread
			if (!playing) {
				if (suspended) {
					set_suspended();
				}
				if (stopped) {
					playing = false;
					return r;
				}
			} // END of !playing

		} // END of iterations

		if (iteration == ITERATIONS) {
			is_finished = true;
			playing = false;
		}

		return r;
	}

	/**
	 * Run experiments in single mode.
	 * 
	 * @param writer
	 * @return
	 */
	private String run_experiment_single(BufferedWriter writer) {
		log.print(IDENTIFIER, "Executed in single mode (no multi-thread). \n");

		String r = "";
		for (iteration = 0; iteration < ITERATIONS; iteration += CHECKPOINT) {

			check_for_events();
			update_gui();
			run_iterations();
			generation += CHECKPOINT;

			r = results();

			// write results of the current checkpoint
			try {
				writer.write(r);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// check if the user hasn't cancelled or suspended the thread
			if (!playing) {
				if (suspended) {
					set_suspended();
				}
				if (stopped) {
					playing = false;
					break;
				}
			} // END of !playing

		} // END of iterations

		if (iteration == ITERATIONS) {
			is_finished = true;
			playing = false;
		}

		update_gui();

		return r;
	}

	/**
	 * Calculate institutions geographical center based on the individuals
	 * that belongs to it
	 */
	protected void calculate_institutions_centers() {};

	/**
	 * Start the execution of iterations, as many as the checkpoints parameter
	 * indicates. Please read the subclass documentation for specific details 
	 * regarding implementation. The subclasses define the way the agents interact
	 * with each other.
	 */
	protected abstract void run_iterations();

	/**
	 * Continue the execution of the thread
	 */
	public void resume() {
		playing = true;
		suspended = false;
		if (monitor != null) {
			synchronized (monitor) {
				monitor.notifyAll();
			}
		}
	}

	/**
	 * Set a suspended state. Just wait until the thread is resumed.
	 */
	protected void set_suspended() {
		while (suspended) {
			synchronized (monitor) {
				try {
					monitor.wait();
				} catch (InterruptedException e) {
					log.print(IDENTIFIER, "Error while trying to wait" + "\n");
				}
			}
		}

	}

	/**
	 * Suspend this thread
	 */
	public void suspend() {
		playing = false;
		suspended = true;
	}

	/**
	 * Cancel this thread
	 */
	public void cancel() {
		playing = false;
		stopped = true;
	}

	/**
	 * Clean memory structures, and call the garbage collector
	 */
	public void clean() {
		try {
			reset();
		} catch (Exception e) {
			e.printStackTrace();
			log.print(IDENTIFIER, "reset(); failed");
			failed = true;
		}
		try {
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
			log.print(IDENTIFIER, "system.gc(); failed");
			failed = true;
		}
	}

	/**
	 * Clone the configuration of this object, this is not a deep cloning method
	 * 
	 * @return a clone of this object
	 */
	public Simulation clone() {
		Simulation clone = null;
		try {
			clone = this.getClass().newInstance();
			clone.RANDOM_INITIALIZATION = this.RANDOM_INITIALIZATION;
			clone.ITERATIONS = this.ITERATIONS;
			clone.CHECKPOINT = this.CHECKPOINT;
			clone.TYPE = this.TYPE;
			clone.ROWS = this.ROWS;
			clone.COLS = this.COLS;
			clone.FEATURES = this.FEATURES;
			clone.TRAITS = this.TRAITS;
			clone.RADIUS = this.RADIUS;
			clone.ALPHA = this.ALPHA;
			clone.ALPHA_PRIME = this.ALPHA_PRIME;
			clone.FREQ_DEM = this.FREQ_DEM;
			clone.FREQ_PROP = this.FREQ_PROP;
			clone.MUTATION = this.MUTATION;
			clone.SELECTION_ERROR = this.SELECTION_ERROR;
			clone.log = this.log;
			clone.results_dir = this.results_dir;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return clone;
	}

	/**
	 * Return a CSV header for the output
	 * 
	 * @return header fo the output files
	 */
	public static String header() {
		return "id,timestamp,duration,iterations,checkpoint," + "type,initialization,"
				+ "rows,cols,features,traits,radius," + "alpha,alpha_prime,freq_dem,freq_prop,mutation,selection_error,"
				+ "epoch,generation,iteration," + "energy," + "cultures,biggest_cluster,full_sim,"
				+ "pos_sim,siz_sim,bel_sim," + "newmann_cultures,biggest_newmann_culture,newman_full_sim,"
				+ "newmann_pos_sim,newmann_siz_sim,newmann_bel_sim,"
				+ "institutions,biggest_institution,pixel_institution_similarity," + "alife,foreign,pixel_similarity,"
				+ "destroyed_institutions,stateless,apostates," + "removed_institutions,removed_traits,"
				+ "converted_institutions,converted_traits," + "invaders,casualties," + "\n";

	}

	/**
	 * Create a CSV line for the results
	 * 
	 * @return a CSV line with current results
	 */
	public String get_results() {
		return IDENTIFIER + "," + new java.sql.Timestamp(startTime) + ","
				+ ((endTime == 0) ? (System.currentTimeMillis() - startTime) : (endTime - startTime)) + "," + ITERATIONS
				+ "," + CHECKPOINT + "," + TYPE + "," + RANDOM_INITIALIZATION + "," + ROWS + "," + COLS + "," + FEATURES
				+ "," + TRAITS + "," + RADIUS + "," + ALPHA + "," + ALPHA_PRIME + "," + FREQ_DEM + "," + FREQ_PROP + ","
				+ MUTATION + "," + SELECTION_ERROR + "," + epoch + "," + generation + "," + iteration + "," + energy
				+ "," + cultureN + "," + biggest_cluster + "," + culture_similarity[FULL_SIM] + ","
				+ culture_similarity[POS_SIM] + "," + culture_similarity[SIZ_SIM] + "," + culture_similarity[BEL_SIM]
				+ "," + culture_newmannN + "," + biggest_newmann_cluster + "," + newmann_similarity[FULL_SIM] + ","
				+ newmann_similarity[POS_SIM] + "," + newmann_similarity[SIZ_SIM] + "," + newmann_similarity[BEL_SIM]
				+ "," + alife_institutions + "," + biggest_institution + "," + pixel_institution_similarity + ","
				+ alife_traits + "," + foreiners_traits + "," + pixel_similarity + "," + destoyed_institutions + ","
				+ stateless + "," + apostates + "," + removed_institutions + "," + removed_traits + ","
				+ converted_institutions + "," + converted_traits + "," + invaders + "," + casualties + "\n";
	}

	/**
	 * Generates an identification of the system in its current state
	 * 
	 * @return an identification of the system
	 */
	public String get_identification() {
		return TYPE + "(" + (RANDOM_INITIALIZATION ? "R" : "S") + ") " + ROWS + "x" + COLS + "(" + RADIUS + "): "
				+ "F/T:" + FEATURES + "/" + TRAITS + " | " + "M/S:" + MUTATION + "/" + SELECTION_ERROR + " | "
				+ "a/a\':" + ALPHA + "/" + ALPHA_PRIME + " | " + "D/P:" + FREQ_DEM + "/" + FREQ_PROP + " @ " + epoch
				+ "|" + generation + " (" + "Energy: " + energy + " | " + "Cultures: " + cultureN + "/"
				+ biggest_cluster + "/" + String.format("%.1g", culture_similarity[FULL_SIM]) + "="
				+ String.format("%.1g", culture_similarity[POS_SIM]) + "*"
				+ String.format("%.1g", culture_similarity[SIZ_SIM]) + "*"
				+ String.format("%.1g", culture_similarity[BEL_SIM]) + " | " + "Newmann's: " + culture_newmannN + "/"
				+ biggest_newmann_cluster + "/" + String.format("%.1g", newmann_similarity[FULL_SIM]) + "="
				+ String.format("%.1g", newmann_similarity[POS_SIM]) + "*"
				+ String.format("%.1g", newmann_similarity[SIZ_SIM]) + "*"
				+ String.format("%.1g", newmann_similarity[BEL_SIM]) + " | " + "Inst: " + alife_institutions + "/"
				+ biggest_institution + "/" + pixel_institution_similarity + " | " + "Pixel: " + foreiners_traits + "/"
				+ alife_institutions + "/" + pixel_similarity + ")";
	}

	/**
	 * Count the cluster and returns a CSV line with the results
	 * 
	 * @return a CSV line with the results
	 */
	protected String results() {
		calculate_stats();
		calculate_newmann_stats();
		calculate_responses();
		return this.get_results();
	}

	/**
	 * Count clusters size, number of cultures and calculate some
	 * cluster statistics
	 */
	private void calculate_stats() {
		biggest_cluster = 0;
		cultureN = 0;
		CultureStatistics cs = null;
		culture_stats.clear();

		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				if (flags[r][c] != flag_mark) {

					cluster_size = 0;
					ave_row = 0;
					ave_col = 0;

					calculate_stats_rec(r, c);
					if (cluster_size > biggest_cluster) {
						biggest_cluster = cluster_size;
					}

					if (cluster_size > 2) {

						cs = new CultureStatistics(cluster_size, ave_row / cluster_size, ave_col / cluster_size,
								beliefs[r][c], this);
						culture_stats.add(cs);
					}

					cultureN++;
				}
			}
		}
		flag_mark = !flag_mark;

	}

	/**
	 * Auxiliar (recursive) method to count cultures
	 * 
	 * @param r current row
	 * @param c current column
	 */
	private void calculate_stats_rec(int r, int c) {
		flags[r][c] = flag_mark;
		cultures[r][c] = cultureN;
		cluster_size++;
		ave_row += r;
		ave_col += c;

		int nr = r - 1;
		int nc = c;
		if (nr >= 0 && flags[nr][nc] != flag_mark && this.is_same_culture(beliefs[r][c], beliefs[nr][nc])) {
			calculate_stats_rec(nr, nc);
		}
		nr = r + 1;
		if (nr < ROWS && flags[nr][nc] != flag_mark && this.is_same_culture(beliefs[r][c], beliefs[nr][nc])) {
			calculate_stats_rec(nr, nc);
		}
		nr = r;
		nc = c - 1;
		if (nc >= 0 && flags[nr][nc] != flag_mark && this.is_same_culture(beliefs[r][c], beliefs[nr][nc])) {
			calculate_stats_rec(nr, nc);
		}
		nc = c + 1;
		if (nc < COLS && flags[nr][nc] != flag_mark && this.is_same_culture(beliefs[r][c], beliefs[nr][nc])) {
			calculate_stats_rec(nr, nc);
		}
	}

	/**
	 * Count the number of cultures considering neighborhoods of the size of the
	 * radius and calculate some cluster statistics
	 */
	private void calculate_newmann_stats() {
		biggest_newmann_cluster = 0;
		culture_newmannN = 0;
		CultureStatistics cs = null;
		newmann_stats.clear();

		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				if (flags[r][c] != flag_mark) {

					cluster_size = 0;
					ave_row = 0;
					ave_col = 0;

					calculate_newmann_stats_rec(r, c);
					if (cluster_size > biggest_newmann_cluster) {
						biggest_newmann_cluster = cluster_size;
					}

					if (cluster_size > 2) {
						cs = new CultureStatistics(cluster_size, ave_row / cluster_size, ave_col / cluster_size,
								beliefs[r][c], this);
						newmann_stats.add(cs);

					}
					culture_newmannN++;
				}
			}
		}
		flag_mark = !flag_mark;

	}

	/**
	 * Auxiliar (recursive) method of calculate newmann stats.
	 * 
	 * @param r current row
	 * @param c current column
	 */
	private void calculate_newmann_stats_rec(int r, int c) {
		flags[r][c] = flag_mark;
		cultures[r][c] = culture_newmannN;
		cluster_size++;
		ave_row += r;
		ave_col += c;

		int nr = 0;
		int nc = 0;

		for (int n = 0; n < neighboursN[r][c]; n++) {
			nr = neighboursX[r][c][n];
			nc = neighboursY[r][c][n];
			if (flags[nr][nc] != flag_mark && this.is_same_culture(beliefs[r][c], beliefs[nr][nc])) {
				calculate_newmann_stats_rec(nr, nc);
			}
		}

	}

	/**
	 * Compare if two cultures are equivalent
	 * 
	 * @param c1 array of the culture to compare
	 * @param c2 array of another culture to compare
	 * @return
	 */
	private boolean is_same_culture(int[] c1, int[] c2) {
		boolean fellow = true;
		for (int f = 0; f < FEATURES; f++) {
			if (c1[f] != c2[f]) {
				fellow = false;
				f = FEATURES;
			}
		}
		return fellow;
	}

	/**
	 * Calculate the energy of the system, the amount of foreign and alife traits, 
	 * and the similarity with the starter state
	 */
	private void calculate_responses() {
		pixel_similarity = 0;
		energy = 0;
		alife_traits = 0;
		foreiners_traits = 0;
		pixel_institution_similarity = 0;
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				if (institutionsN != null && institution_beliefs != null){
					if (institutionsN[r * ROWS + c] > 0 && starter.institutionsN[r * ROWS + c] > 0) {
						for (int f = 0; f < FEATURES; f++) {
							if (institution_beliefs[r * ROWS + c][f] == starter.institution_beliefs[r * ROWS + c][f]) {
								pixel_institution_similarity++;
							}
						}
					}
				}

				for (int f = 0; f < FEATURES; f++) {
					if (beliefs[r][c][f] == starter.beliefs[r][c][f]) {
						pixel_similarity++;
					}
					if (beliefs[r][c][f] == TRAITS) {
						foreiners_traits++;
					}
					if (beliefs[r][c][f] != DEAD_TRAIT) {
						alife_traits++;
					}
					if (c + 1 < COLS && (beliefs[r][c][f] != beliefs[r][c + 1][f])) {
						energy++;
					}
					if (r + 1 < COLS && (beliefs[r + 1][c][f] != beliefs[r + 1][c][f])) {
						energy++;
					}
				}
			}
		}

		compare_stats(newmann_stats, starter.newmann_stats, newmann_similarity);
		compare_stats(culture_stats, starter.culture_stats, culture_similarity);

	}

	/**
	 * Compare the statistics of two instants of the cultural evolution
	 *  
	 * @param stats1 list of statistics of one state
	 * @param stats2 list of statistics of another state
	 * @param sims the results of the comparison
	 */
	private void compare_stats(List<CultureStatistics> stats1, List<CultureStatistics> stats2, double[] sims) {

		if (stats1.size() == 0 || stats2.size() == 0) {
			sims[FULL_SIM] = 0.0;
			sims[POS_SIM] = 0.0;
			sims[SIZ_SIM] = 0.0;
			sims[BEL_SIM] = 0.0;
			return;
		}

		sims[FULL_SIM] = 0.0;
		sims[POS_SIM] = 0.0;
		sims[SIZ_SIM] = 0.0;
		sims[BEL_SIM] = 0.0;
		double full_sim = 0;
		double temp_full_sim = 0;
		double pos_sim = 0;
		double temp_pos_sim = 0;
		double size_sim = 0;
		double temp_size_sim = 0;
		double bel_sim = 0;
		double temp_bel_sim = 0;

		for (Iterator<CultureStatistics> i1 = stats1.iterator(); i1.hasNext();) {
			CultureStatistics cs1 = (CultureStatistics) i1.next();
			full_sim = 0;
			pos_sim = 0;
			size_sim = 0;
			bel_sim = 0;

			for (Iterator<CultureStatistics> i2 = stats2.iterator(); i2.hasNext();) {
				CultureStatistics cs2 = (CultureStatistics) i2.next();
				temp_pos_sim = cs1.compare_positions(cs2);
				if (pos_sim < temp_pos_sim) {
					pos_sim = temp_pos_sim;
				}
				temp_size_sim = cs1.compare_size(cs2);
				if (size_sim < temp_size_sim) {
					size_sim = temp_size_sim;
				}
				temp_bel_sim = cs1.compare_beliefs(cs2);
				if (bel_sim < temp_bel_sim) {
					bel_sim = temp_bel_sim;
				}
				temp_full_sim = temp_pos_sim * temp_size_sim * temp_bel_sim;
				if (full_sim < temp_full_sim) {
					full_sim = temp_full_sim;
				}
			}

			sims[FULL_SIM] += full_sim;
			sims[POS_SIM] += pos_sim;
			sims[SIZ_SIM] += size_sim;
			sims[BEL_SIM] += bel_sim;
		}

		for (Iterator<CultureStatistics> i1 = stats2.iterator(); i1.hasNext();) {
			CultureStatistics cs1 = (CultureStatistics) i1.next();
			full_sim = 0;
			pos_sim = 0;
			size_sim = 0;
			bel_sim = 0;

			for (Iterator<CultureStatistics> i2 = stats1.iterator(); i2.hasNext();) {
				CultureStatistics cs2 = (CultureStatistics) i2.next();
				temp_pos_sim = cs1.compare_positions(cs2);
				if (pos_sim < temp_pos_sim) {
					pos_sim = temp_pos_sim;
				}
				temp_size_sim = cs1.compare_size(cs2);
				if (size_sim < temp_size_sim) {
					size_sim = temp_size_sim;
				}
				temp_bel_sim = cs1.compare_beliefs(cs2);
				if (bel_sim < temp_bel_sim) {
					bel_sim = temp_bel_sim;
				}
				temp_full_sim = temp_pos_sim * temp_size_sim * temp_bel_sim;
				if (full_sim < temp_full_sim) {
					full_sim = temp_full_sim;
				}
			}

			sims[FULL_SIM] += full_sim;
			sims[POS_SIM] += pos_sim;
			sims[SIZ_SIM] += size_sim;
			sims[BEL_SIM] += bel_sim;
		}

		double size = stats1.size() + stats2.size();
		sims[FULL_SIM] = sims[FULL_SIM] / size;
		sims[POS_SIM] = sims[POS_SIM] / size;
		sims[SIZ_SIM] = sims[SIZ_SIM] / size;
		sims[BEL_SIM] = sims[BEL_SIM] / size;

	}

	/**
	 * Check if there is events that need to occur
	 */
	private void check_for_events() {
		if (events.size() > 0) {
			calculate_institutions_centers();
			executing_events = true;
			for (Iterator<Event> iterator = events.iterator(); iterator.hasNext();) {
				Event event = (Event) iterator.next();
				event.execute(this);
			}
			events.clear();
			executing_events = false;
		}
	}

	/**
	 * Execute events if it is not running, otherwise add them to the list
	 * 
	 * @param events list of events that need to be executed
	 */
	public void events(ArrayList<Event> events) {
		if (playing) {
			boolean loop = true;
			while (loop) {
				if (!executing_events) {
					this.events.addAll(events);
					loop = false;
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
		} else {
			if (generation > 0 && !Controller.IS_BATCH) {
				for (Iterator<Event> iterator = events.iterator(); iterator.hasNext();) {
					Event event = (Event) iterator.next();
					event.execute(this);
				}
				update_gui();
			} else {
				this.events.addAll(events);
			}
		}
	}

	/**
	 * Remove partial information from an institution
	 * 
	 * @param r row of the institution (averaged over the agents that belongs to it)
	 * @param c column of the institution (averaged over the agents that belongs to it)
	 * @param prob probability of a trait of being deleted
	 */
	public void remove_partial_institution_content(int r, int c, double prob) {}

	/**
	 * Remove complete information from an institution
	 * 
	 * @param r row of the institution (averaged over the agents that belongs to it)
	 * @param c column of the institution (averaged over the agents that belongs to it)
	 */
	public void remove_full_institution_content(int r, int c) {}

	/**
	 * Quit to the institution the agent belongs to
	 * 
	 * @param r row of the individual
	 * @param c column of the individual
	 */
	public void apostasy(int r, int c) {}

	/**
	 * Convert an institution towards the invader TRAITS
	 * 
	 * @param r row of the institution (averaged over the agents that belongs to it)
	 * @param c column of the institution (averaged over the agents that belongs to it)
	 */
	public void convert_full_institution(int r, int c) {}

	/**
	 * Convert some of the traits  towards the invader TRAITS
	 * 
	 * @param r row of the institution (averaged over the agents that belongs to it)
	 * @param c column of the institution (averaged over the agents that belongs to it)
	 * @param prob probability of a trait of being converted
	 */
	public void convert_partial_institution(int r, int c, double prob) {}

	/**
	 * Destroy an institution, all agents become stateless
	 * 
	 * @param r row of the institution (averaged over the agents that belongs to it)
	 * @param c column of the institution (averaged over the agents that belongs to it)
	 */
	public void destoy_institution(int r, int c) {}

	/**
	 * Prepare elements before an invasion. Before the invasion, a free 
	 * institution has to be taken as the representation of the invaders, 
	 * so they can refer to it as they representative. In this case, it 
	 * would be finding a free institution near the r, c coordinates.
	 * 
	 * @param r row of the institution (averaged over the agents that belongs to it)
	 * @param c column of the institution (averaged over the agents that belongs to it)
	 * @return  the free institution that was found
	 */
	public int pre_invasion(int r, int c) {
		return -999;
	}

	/**
	 * Invade a cell, replace the individual in the cell with an invader
	 *  
	 * @param r row of the individual
	 * @param c column of the individual
	 */
	public void invade(int r, int c, int nr, int nc) {
		for (int f = 0; f < FEATURES; f++) {
			beliefs[r][c][f] = TRAITS;
		}
	}

	/**
	 * Kill an individual in an specified cell
	 * 
	 * @param r row of the individual
	 * @param c column of the individual
	 */
	public void kill_individual(int r, int c) {
		this.casualties++;
		for (int f = 0; f < FEATURES; f++) {
			beliefs[r][c][f] = DEAD_TRAIT;
		}
	}

	/**
	 * Update the interface
	 */
	protected void update_gui() {
		if (!Controller.IS_BATCH) {
			print_belief_spaces();
			update_culture_graphs();
			CulturalSimulator.l_start_identification.setText("S: " + starter.get_identification());
			CulturalSimulator.l_current_identification.setText("C: " + get_identification());
		}
	}

	/**
	 * Update the graphs of the interface
	 */
	private void update_culture_graphs() {

		int total_features = TOTAL_AGENTS * FEATURES;
		CulturalSimulator.graphEnergy.scores.add((double) energy / total_features);
		CulturalSimulator.lblEnergy.setText(energy + "");
		CulturalSimulator.graphEnergy.update();

		CulturalSimulator.graphCultures.scores.add((double) cultureN / TOTAL_AGENTS);
		CulturalSimulator.graphCultures.scores2.add((double) biggest_cluster / TOTAL_AGENTS);
		CulturalSimulator.graphCultures.scores3.add(culture_similarity[FULL_SIM]);
		CulturalSimulator.lblCultures
				.setText(cultureN + "/" + biggest_cluster + "/" + String.format("%.2g", culture_similarity[FULL_SIM]));
		CulturalSimulator.graphCultures.update();

		CulturalSimulator.graphNeumann.scores.add((double) culture_newmannN / TOTAL_AGENTS);
		CulturalSimulator.graphNeumann.scores2.add((double) biggest_newmann_cluster / TOTAL_AGENTS);
		CulturalSimulator.graphNeumann.scores3.add(newmann_similarity[FULL_SIM]);
		CulturalSimulator.lblNeumann.setText(culture_newmannN + "/" + biggest_newmann_cluster + "/"
				+ String.format("%.2g", newmann_similarity[FULL_SIM]));
		CulturalSimulator.graphNeumann.update();

		CulturalSimulator.graphCultureSimilarity.scores.add(culture_similarity[POS_SIM]);
		CulturalSimulator.graphCultureSimilarity.scores2.add(culture_similarity[SIZ_SIM]);
		CulturalSimulator.graphCultureSimilarity.scores3.add(culture_similarity[BEL_SIM]);
		CulturalSimulator.lblCultureSimilarity.setText(String.format("%.2g", culture_similarity[POS_SIM]) + "/"
				+ String.format("%.2g", culture_similarity[SIZ_SIM]) + "/"
				+ String.format("%.2g", culture_similarity[BEL_SIM]));
		CulturalSimulator.graphCultureSimilarity.update();

		CulturalSimulator.graphNeumannSimilarity.scores.add(newmann_similarity[POS_SIM]);
		CulturalSimulator.graphNeumannSimilarity.scores2.add(newmann_similarity[SIZ_SIM]);
		CulturalSimulator.graphNeumannSimilarity.scores3.add(newmann_similarity[BEL_SIM]);
		CulturalSimulator.lblNeumannSimilarity.setText(String.format("%.2g", newmann_similarity[POS_SIM]) + "/"
				+ String.format("%.2g", newmann_similarity[SIZ_SIM]) + "/"
				+ String.format("%.2g", newmann_similarity[BEL_SIM]));
		CulturalSimulator.graphNeumannSimilarity.update();

		CulturalSimulator.graphInstitutions.scores.add((double) alife_institutions / TOTAL_AGENTS);
		CulturalSimulator.graphInstitutions.scores2.add((double) biggest_institution / TOTAL_AGENTS);
		CulturalSimulator.graphInstitutions.scores3
				.add((double) pixel_institution_similarity / (alife_institutions * FEATURES));
		CulturalSimulator.lblInstitutions
				.setText(alife_institutions + "/" + biggest_institution + "/" + pixel_institution_similarity);
		CulturalSimulator.graphInstitutions.update();

		CulturalSimulator.graphPixels.scores.add((double) alife_traits / total_features);
		CulturalSimulator.graphPixels.scores2.add((double) foreiners_traits / total_features);
		CulturalSimulator.graphPixels.scores3.add((double) pixel_similarity / total_features);
		CulturalSimulator.lblPixels.setText(alife_traits + "/" + foreiners_traits + "/" + pixel_similarity);
		CulturalSimulator.graphPixels.update();

	}

	/**
	 * Print belief spaces in the screen
	 */
	private void print_belief_spaces() {
		BufferedImage image_belief_space = new BufferedImage(ROWS, COLS, BufferedImage.TYPE_INT_RGB);

		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				String belief_space_ohex = "";
				for (int f = 0; f < Math.min(FEATURES, 6); f++) {
					belief_space_ohex += get_color_for_trait(beliefs[r][c][f]);
				}
				belief_space_ohex = "#" + belief_space_ohex;

				image_belief_space.setRGB(r, c, Color.decode(belief_space_ohex).getRGB());
			}
		}

		CulturalSimulator.set_belief_space(image_belief_space);

	}

	/**
	 * Return a color according to a trait number and the posssible traits
	 * 
	 * @param t get a color for 
	 * @return a String representing the color
	 */
	 protected String get_color_for_trait(int t) {
		String color = "0";
		if (t == TRAITS) {
			color = "f";
		} else if (t >= 0) {
			if (TRAITS <= 1) {
				color = "8";
			} else {
				color = Integer.toHexString((int) Math.round(14 * t / ((double) TRAITS) - 1) + 1);
			}
		}
		return color;
	}

	/**
	 * Return the random generator. Don't change but use it in order to generate 
	 * events related to the simulation, so it keeps a unique seed associated to
	 * the simulation. 
	 * 
	 * @return the generator.
	 */
	public Random getRand() {
		return rand;
	}

}