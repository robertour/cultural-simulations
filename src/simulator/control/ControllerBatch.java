package simulator.control;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import simulator.destruction.Flache;
import simulator.control.events.Event;
import simulator.destruction.Axelrod;
import simulator.destruction.Flache2;
import simulator.destruction.Ulloa;
 
/**
 * This controller creates the tasks base on a CSV configuration file and it
 * can provide an interface for the user to interrupt, suspend or resume the 
 * simulation, or run in a console mode
 * 
 * @author tico
 */
public class ControllerBatch extends Controller
{

	/**
	 * The area to append the results (log)
	 */
	protected static Printable log = null;
	
	/**
	 * Directory to write results
	 */
	protected static String RESULTS_DIR = null;
	
	// Keep the tasks in a list. Don't start until the entire file is read.
	protected ArrayList<Simulation> tasks = null;
	
	public ControllerBatch(Printable output) {
		log = output;
	}

	public void set_RESULTS_DIR(String results_dir) {
		RESULTS_DIR = results_dir;
	}

	/** 
	 * Load a simulation object
	 * @throws IOException 
	 */
	public Simulation load_simulation(String simfile){
		Simulation s = null;
		try {
			ObjectInputStream inFile = new ObjectInputStream(new FileInputStream(simfile));
			s = (Simulation) inFile.readObject();
			inFile.close();
		}  catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * Open the files and creates the tasks for the experiments
	 * @throws FileNotFoundException
	 */
    public void load_tasks(ArrayList<String> sim_files, int repetitions) {
    	tasks = new ArrayList<Simulation>();

    	for (Iterator<String> iterator = sim_files.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			for (int j = 0; j < repetitions; j++) {
    			tasks.add(this.load_simulation(string));	
			}	
		}
    }
    
	/**
	 * Open the files and creates the tasks for the experiments
	 * @throws FileNotFoundException
	 */
    public void load_tasks_with_events(ArrayList<String> sim_files, ArrayList<Event> events, int repetitions) {
    	tasks = new ArrayList<Simulation>();

    	for (Iterator<String> iterator = sim_files.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			for (int j = 0; j < repetitions; j++) {
				Simulation s = this.load_simulation(string);
				s.events(events);
    			tasks.add(s);	
			}	
		}
    }
	
	/**
	 * Open the files and creates the tasks for the experiments
	 * @throws FileNotFoundException
	 */
    public void load_tasks(String csv_file) throws FileNotFoundException {
    	
    	// This is used to randomize the experiment.
    	Random rand = new Random();
    	
    	//Get scanner instance        
    	Scanner scanner = new Scanner(new File(csv_file));
        
    	//skip the column titles
        scanner.nextLine();
        scanner.useDelimiter(",");
         
        tasks = new ArrayList<Simulation>();
        
        //Start reading the file
        while (scanner.hasNext())
        {
        	int repetitions = Integer.parseInt(scanner.next());
        	if (repetitions > 0) {
        		Simulation simulation = null;
        		String type = scanner.next();
		    		switch (type) {
		    		case "Flache": simulation = new Flache(); break;
		    		case "Ulloa": simulation = new Ulloa(); break;		    		
        			case "FLACHE1":	simulation = new Axelrod();	break;
        			case "FLACHE2":	simulation = new Flache2();	break;
        		}
	        	simulation.ITERATIONS = Integer.parseInt(scanner.next());
	        	simulation.CHECKPOINT = Integer.parseInt(scanner.next());
	        	simulation.BUFFERED_SIZE = Integer.parseInt(scanner.next());
	        	simulation.ROWS = Integer.parseInt(scanner.next());
	        	simulation.COLS = Integer.parseInt(scanner.next());
	        	simulation.FEATURES = Integer.parseInt(scanner.next());
	        	simulation.TRAITS = Integer.parseInt(scanner.next());
	        	simulation.RADIUS = Integer.parseInt(scanner.next());
	        	simulation.ALPHA = Float.parseFloat(scanner.next());
	        	simulation.ALPHA_PRIME = Float.parseFloat(scanner.next());
	        	simulation.FREQ_DEM =  Integer.parseInt(scanner.next());
	        	simulation.FREQ_PROP =  Integer.parseInt(scanner.next());
	        	simulation.MUTATION = Float.parseFloat(scanner.next());
	        	simulation.SELECTION_ERROR = Float.parseFloat(scanner.next());
	        	tasks.add(rand.nextInt(tasks.size()+1), simulation);
	        	
	        	// Generate tasks per repetitions
	        	for (int r = 1; r < repetitions; r++) {
		        	tasks.add( rand.nextInt(tasks.size()+1), simulation.clone());
	        	}
	        	
	        	if (scanner.hasNextLine()) {
	        		scanner.nextLine();
	        	}
        	}
        }
         
        //Do not forget to close the scanner 
        scanner.close();
    }
    
    /**
     * Write the final or intermediate (when interrupted) results
     * @throws IOException
     */
    public void write_results() throws IOException {
    	// Write the results to the file
    	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(RESULTS_DIR + "results.csv"), "utf-8"));
        writer.write(Simulation.header());
        for(Simulation w : tasks) {
			writer.write(w.get_results());
        }
        writer.close();    	
    }
    
    /**
     * Load the file and starts the simulation
     * @throws FileNotFoundException
     */
    public void start() 
    {
    	/**
    	 * Load static variables that the simulation is going to access
    	 */
    	IS_BATCH = true;
    	
    	// This is a pool of threads of the size of the cores of the computer
    	exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    	
    	int id = 0;
    	for(Simulation w : tasks) {
    		w.IDENTIFIER = id++;
    		exec.submit(w);
    	} 
    
    	log.print("All Tasks Submitted\n");
    	
    	exec.shutdown();
    	
    	(new SimulationExecuter()).start();
        
    }

    /** 
     * Cancel all threads
     * @throws IOException
     */
    public void clean_all() {
    	if (tasks != null) {
	    	for(Simulation w : tasks) {
	    		w.clean();
	    	} 
    	}
    }
    
    /** 
     * Cancel all threads
     * @throws IOException
     */
    public void cancel_all() {
    	if (tasks != null){
	    	for(Simulation w : tasks) {
	    		w.cancel();
	    	} 
	    	exec.shutdownNow();
    	}
    }
    
    /**
     * Suspend all threads
     */
    public void suspend_all() {
    	for(Simulation w : tasks) {
    		w.suspend();
    	} 
    }
    
    /**
     * Resume all threads
     */
    public void resume_all(){
    	for(Simulation w : tasks) {
    		w.resume();
    	}
    }
    
    /**
     * Handles the finalization of the experiments and its possible interruptions
     * 
     * @author tico
     */
    private class SimulationExecuter extends Thread {
    	
    	public void run (){
    		log.print("Simulation Executor Started\n");
    		try {
				exec. awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
				log.print("The experiments where finished without errors\n");
			} catch (InterruptedException e) {
				log.print("Simulation interrupted\n");
			}
	    	
	    	try {
				write_results();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
   
}