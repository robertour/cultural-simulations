package simulator;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import simulator.control.Controller;
import simulator.control.ControllerBatch;
import simulator.control.events.ConvertInstitutions;
import simulator.control.events.ConvertTraits;
import simulator.control.events.DestroyInstitutionsContent;
import simulator.control.events.DestroyInstitutionsStructure;
import simulator.control.events.DestroyPartialInstitutionsContent;
import simulator.control.events.Distribution;
import simulator.control.events.Event;
import simulator.control.events.Genocide;
import simulator.control.events.Invasion;

import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import java.awt.Font;
import javax.swing.JTextArea;
import java.awt.GridLayout;

public class BatchMode extends JDialog {

	private static final long serialVersionUID = -6498782807057797553L;
	private JPanel contentPane;
	
	private JButton btn_pause;
	private JButton btn_resume;
	public JTextField tf_results_dir;
	public JTextField tf_experimental_file;
	private String EXPERIMENTAL_FILE = "";
	private String results_dir = "./";
	
	private JFileChooser jfc_experiment = new JFileChooser(Controller.WORKSPACE_DIR + "sample.csv");
	private JFileChooser jfc_results = new JFileChooser(Controller.WORKSPACE_DIR);
	private JFileChooser jfc_disasters = new JFileChooser(Controller.WORKSPACE_DIR + Controller.DISASTERS_DIR);
	private JFileChooser jfc_scenarios = new JFileChooser(Controller.WORKSPACE_DIR);	
	private JFileChooser jfc_configurations = new JFileChooser(Controller.WORKSPACE_DIR + Controller.CONFIGURATIONS_DIR);
	
	private ControllerBatch controller;
	private JButton btn_start;
	private JButton btn_stop;
	private JButton btn_open_file;
	private JButton btn_open_experiment;
	private static OutputArea TA_OUTPUT;
	private JPanel tab_csv;
	private JPanel tab_conf_file;
	private JSpinner sp_repetitions;
	private JButton btnAddConfigurationFile;
	private JButton btnAddFolder;
	private JPanel tab_catastrophic;
	private JButton btnOpen;
	private JTextField tf_scenarios_dir;
	private JLabel lblFolderWithResults;
	private JList<String> sel_conf_files;
	private DefaultListModel<String> conf_list;
	private ArrayList<String> file_list;
	private JTabbedPane tp_batch_mode;
	private JScrollPane scroll_pane;
	private JButton btnLoad;
	private JTextField tf_results_dir_csv;
	
	private ArrayList<Event> events = new ArrayList<Event>();
	private JButton button_1;
	private JButton btnSave;
	private JTextArea ta_set_events;
	private JPanel panel;
	private EventPanel institutionPanel;
	private EventPanel conversionPanel;
	private EventPanel invasionPanel;
	private EventPanel genocidePanel;
	private TripleDistributionDialog triple_dialog;
	private DoubleDistributionDialog double_dialog;
	private SingleDistributionDialog simple_dialog;
	
	CulturalSimulator cultural_simulator;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CulturalSimulator frame = new CulturalSimulator();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public BatchMode(CulturalSimulator cultural_simulator) {
		super (cultural_simulator);
		setTitle("Batch Mode");
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
           		controller.cancel_all();
           		dispose();                
            }
        });
		
		setBounds(100, 100, 618, 551);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		jfc_experiment.setDialogTitle("Select an Experimental Design File");
		jfc_experiment.setSelectedFile(new File("sample.csv"));
		jfc_results.setDialogTitle("Select a Results Folder");
		jfc_results.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc_results.setAcceptAllFileFilterUsed(false);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 270, 582, 231);
		contentPane.add(scrollPane);
		
		TA_OUTPUT = new OutputArea();
		TA_OUTPUT.setFont(new Font("Tahoma", Font.PLAIN, 9));
		TA_OUTPUT.setLineWrap(true);
		TA_OUTPUT.setEditable(false);
		scrollPane.setViewportView(TA_OUTPUT);
		
		controller = new ControllerBatch(TA_OUTPUT);
		
		tp_batch_mode = new JTabbedPane(JTabbedPane.TOP);
		tp_batch_mode.setBounds(10, 11, 582, 220);
		contentPane.add(tp_batch_mode);
		
		conf_list = new DefaultListModel<String>();
		file_list = new ArrayList<String>();
		
		tab_conf_file = new JPanel();
		tp_batch_mode.addTab("Configuration Files", null, tab_conf_file, null);
		tab_conf_file.setLayout(null);
		
		sp_repetitions = new JSpinner();
		sp_repetitions.setModel(new SpinnerNumberModel(new Integer(10), null, null, new Integer(1)));
		sp_repetitions.setBounds(78, 108, 55, 20);
		tab_conf_file.add(sp_repetitions);
		
		JLabel lblRepetitions = new JLabel("Repetitions:");
		lblRepetitions.setBounds(10, 111, 70, 14);
		tab_conf_file.add(lblRepetitions);
		
		btnAddConfigurationFile = new JButton("Add File");
		btnAddConfigurationFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jfc_configurations.setFileSelectionMode( JFileChooser.FILES_ONLY );
				if (jfc_configurations.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
						conf_list.addElement(jfc_configurations.getSelectedFile().getAbsolutePath());
						file_list.add(jfc_configurations.getSelectedFile().getAbsolutePath());						
				}
			}
		});
		btnAddConfigurationFile.setBounds(467, 107, 100, 25);
		tab_conf_file.add(btnAddConfigurationFile);
		
		File[] directoryListing = jfc_configurations.getCurrentDirectory().listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				conf_list.addElement(child.getAbsolutePath());
				file_list.add(child.getAbsolutePath());
			}
		}
		
		btnAddFolder = new JButton("Add Folder");
		btnAddFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				jfc_configurations.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				if (jfc_configurations.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
					File[] directoryListing = jfc_configurations.getSelectedFile().listFiles();
					if (directoryListing != null) {
						for (File child : directoryListing) {
							conf_list.addElement(child.getAbsolutePath());
							file_list.add(child.getAbsolutePath());
						}
					}
				}
			}
		});
		btnAddFolder.setBounds(357, 107, 100, 25);
		tab_conf_file.add(btnAddFolder);
		
		JButton btn_clearlist = new JButton("Clear");
		btn_clearlist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				conf_list.clear();
				file_list.clear();
			}
		});
		btn_clearlist.setBounds(247, 107, 100, 25);
		tab_conf_file.add(btn_clearlist);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 11, 557, 85);
		tab_conf_file.add(scrollPane_1);
		
		sel_conf_files = new JList<String>();
		scrollPane_1.setViewportView(sel_conf_files);
		sel_conf_files.setModel(conf_list);
		
		JLabel lblResultsFolder = new JLabel("Results Folder:");
		lblResultsFolder.setBounds(10, 153, 80, 15);
		tab_conf_file.add(lblResultsFolder);
		
		tf_results_dir = new JTextField();
		tf_results_dir.setBounds(88, 148, 369, 25);
		tab_conf_file.add(tf_results_dir);
		tf_results_dir.setEditable(false);
		tf_results_dir.setColumns(10);
		tf_results_dir.setText(jfc_results.getCurrentDirectory().getAbsolutePath() + "\\");
		btn_open_file = new JButton("Select");
		btn_open_file.setBounds(467, 148, 100, 25);
		tab_conf_file.add(btn_open_file);
		btn_open_file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (jfc_results.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
					tf_results_dir.setText(jfc_results.getSelectedFile().getAbsolutePath() + "\\");
					tf_results_dir_csv.setText(jfc_results.getSelectedFile().getAbsolutePath() + "\\");
				}
			}
		});
		
		tab_csv = new JPanel();
		tp_batch_mode.addTab("CSV Configuration", null, tab_csv, null);
		tab_csv.setLayout(null);
		
		JLabel lblExperimentalDesignFile = new JLabel("Experimental Design File (CSV):");
		lblExperimentalDesignFile.setBounds(10, 10, 166, 15);
		tab_csv.add(lblExperimentalDesignFile);
		
		tf_experimental_file = new JTextField();
		tf_experimental_file.setBounds(10, 40, 430, 25);
		tab_csv.add(tf_experimental_file);
		tf_experimental_file.setEditable(false);
		tf_experimental_file.setColumns(10);
		tf_experimental_file.setText(jfc_experiment.getSelectedFile().getAbsolutePath());
		
		btn_open_experiment = new JButton("Select");
		btn_open_experiment.setBounds(450, 40, 117, 25);
		tab_csv.add(btn_open_experiment);
		
		JLabel label = new JLabel("Results Folder:");
		label.setBounds(10, 105, 80, 15);
		tab_csv.add(label);
		
		tf_results_dir_csv = new JTextField();
		tf_results_dir_csv.setEditable(false);
		tf_results_dir_csv.setColumns(10);
		tf_results_dir_csv.setText(jfc_results.getCurrentDirectory().getAbsolutePath() + "\\");
		tf_results_dir_csv.setBounds(10, 131, 430, 25);
		tab_csv.add(tf_results_dir_csv);
		
		JButton button = new JButton("Select");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (jfc_results.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
					tf_results_dir.setText(jfc_results.getSelectedFile().getAbsolutePath());
					tf_results_dir_csv.setText(jfc_results.getSelectedFile().getAbsolutePath());
				}
			}
		});
		button.setBounds(450, 131, 117, 25);
		tab_csv.add(button);
		
		tab_catastrophic = new JPanel();
		tp_batch_mode.addTab("Catastrophic Setup", null, tab_catastrophic, null);
		tab_catastrophic.setLayout(null);
		
		btnOpen = new JButton("Select");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				jfc_scenarios.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				if (jfc_scenarios.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
					tf_scenarios_dir.setText(jfc_scenarios.getSelectedFile().getAbsolutePath() + "\\");
				}
			}
		});
		btnOpen.setBounds(484, 10, 83, 25);
		tab_catastrophic.add(btnOpen);		
		
		tf_scenarios_dir = new JTextField();
		tf_scenarios_dir.setText(jfc_scenarios.getCurrentDirectory().getAbsolutePath() + "\\");
		tf_scenarios_dir.setEditable(false);
		tf_scenarios_dir.setColumns(10);
		tf_scenarios_dir.setBounds(75, 10, 399, 25);
		tab_catastrophic.add(tf_scenarios_dir);
		
		lblFolderWithResults = new JLabel("Scenarios:");
		lblFolderWithResults.setToolTipText("These scenarios serve as a starting point to apply the catastrophes. Basically, a folder with the results obtained in the other two tabs \"Configuration Files\" or \"CSV Configuration\".");
		lblFolderWithResults.setBounds(10, 15, 83, 15);
		tab_catastrophic.add(lblFolderWithResults);
		
		scroll_pane = new JScrollPane();
		scroll_pane.setBounds(405, 46, 160, 104);
		tab_catastrophic.add(scroll_pane);
		
		ta_set_events = new JTextArea();
		ta_set_events.setFont(new Font("Tahoma", Font.PLAIN, 9));
		ta_set_events.setEditable(false);
		scroll_pane.setViewportView(ta_set_events);
		
		btnLoad = new JButton("Load");
		btnLoad.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnLoad.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				if (jfc_disasters.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
					String dis_file = jfc_disasters.getSelectedFile().getAbsolutePath();
					try {
						ObjectInputStream inFile = new ObjectInputStream(new FileInputStream(dis_file));
						events = (ArrayList<Event>) inFile.readObject();
						inFile.close();
						update_event_set();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		btnLoad.setBounds(460, 161, 50, 25);
		tab_catastrophic.add(btnLoad);
		
		button_1 = new JButton("Clear");
		button_1.setBorder(new EmptyBorder(0, 0, 0, 0));
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				events.clear();
				update_event_set();
			}
		});
		button_1.setBounds(405, 161, 50, 25);
		tab_catastrophic.add(button_1);
		
		btnSave = new JButton("Save");
		btnSave.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jfc_disasters.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
					String dis_file = jfc_disasters.getSelectedFile().getAbsolutePath();
					try {
						ObjectOutputStream write = new ObjectOutputStream (new FileOutputStream(dis_file));				
						write.writeObject(events);
						write.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				}
			}
		});
		btnSave.setBounds(515, 161, 50, 25);
		tab_catastrophic.add(btnSave);
		
		panel = new JPanel();
		panel.setBounds(10, 41, 385, 145);
		tab_catastrophic.add(panel);
		panel.setLayout(new GridLayout(0, 2, 5, 0));
		
		
		this.cultural_simulator = cultural_simulator; 
		triple_dialog = cultural_simulator.destructionDialog;
		institutionPanel = new EventPanel("Institutions destruction", triple_dialog);
		institutionPanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TripleDistributionDialog triple_dialog = BatchMode.this.cultural_simulator.destructionDialog;
				if (triple_dialog.get_distribution1() != null){
					events.add(new DestroyInstitutionsStructure(triple_dialog.get_distribution1()));
					update_event_set();
				}
				if (triple_dialog.get_distribution2() != null){
					events.add(new DestroyInstitutionsContent(triple_dialog.get_distribution2()));
					update_event_set();
				}
				if (triple_dialog.get_distribution3() != null){
					events.add(new DestroyPartialInstitutionsContent(triple_dialog.get_distribution3()));
					update_event_set();
				}
				
			}
		});
		triple_dialog.addNotifiable(institutionPanel);		
		panel.add(institutionPanel);
		
		double_dialog = cultural_simulator.conversionDialog;
		conversionPanel = new EventPanel("Institutional conversion", double_dialog);
		conversionPanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DoubleDistributionDialog double_dialog = BatchMode.this.cultural_simulator.conversionDialog;
				if (double_dialog.get_distribution1() != null){
					events.add(new ConvertInstitutions(double_dialog.get_distribution1()));
					update_event_set();
				}
				if (double_dialog.get_distribution2() != null){
					events.add(new ConvertTraits(double_dialog.get_distribution2()));
					update_event_set();
				}

			}
		});
		double_dialog.addNotifiable(conversionPanel);
		panel.add(conversionPanel);
		
		simple_dialog = cultural_simulator.invasionDialog;
		invasionPanel = new EventPanel("Invasion", simple_dialog);
		invasionPanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Distribution d = BatchMode.this.cultural_simulator.invasionDialog.get_distribution();
				if (d != null){
					events.add(new Invasion(d));
					update_event_set();
				}
			}
		});
		simple_dialog.addNotifiable(invasionPanel);
		panel.add(invasionPanel);
		
		simple_dialog = cultural_simulator.genocideDialog;
		genocidePanel = new EventPanel("Genocide", simple_dialog);
		genocidePanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Distribution d = BatchMode.this.cultural_simulator.genocideDialog.get_distribution();
				if (d != null){
					events.add(new Genocide(d));
					update_event_set();
				}
			}
		});
		simple_dialog.addNotifiable(genocidePanel);
		panel.add(genocidePanel);
		
		btn_open_experiment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (jfc_experiment.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
					tf_experimental_file.setText(jfc_experiment.getSelectedFile().getAbsolutePath());
					jfc_results.setCurrentDirectory(jfc_experiment.getCurrentDirectory());
					tf_results_dir.setText(jfc_experiment.getCurrentDirectory().getAbsolutePath() + "\\");
					tf_results_dir_csv.setText(jfc_experiment.getCurrentDirectory().getAbsolutePath() + "\\");
					EXPERIMENTAL_FILE = tf_experimental_file.getText();
					try {
						controller.load_tasks(EXPERIMENTAL_FILE);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		btn_start = new JButton("Start");
		btn_start.setBounds(242, 235, 80, 25);
		contentPane.add(btn_start);
		
		btn_pause = new JButton("Pause");
		btn_pause.setBounds(332, 235, 80, 25);
		contentPane.add(btn_pause);
		btn_pause.setEnabled(false);
		
		btn_resume = new JButton("Resume");
		btn_resume.setBounds(422, 235, 80, 25);
		contentPane.add(btn_resume);
		btn_resume.setEnabled(false);
		
		btn_stop = new JButton("Stop");
		btn_stop.setBounds(512, 235, 80, 25);
		contentPane.add(btn_stop);
		btn_stop.setEnabled(false);
		btn_stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.cancel_all();
				btn_start.setEnabled(true);
				btn_pause.setEnabled(false);
				btn_resume.setEnabled(false);
				btn_stop.setEnabled(false);
				btn_open_experiment.setEnabled(true);
				btn_open_file.setEnabled(true);
			}
		});
		btn_resume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.resume_all();
				btn_start.setEnabled(false);
				btn_pause.setEnabled(true);
				btn_resume.setEnabled(false);
				btn_stop.setEnabled(true);
			}
		});
		btn_pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.suspend_all();
				btn_start.setEnabled(false);
				btn_pause.setEnabled(false);
				btn_resume.setEnabled(true);
				btn_stop.setEnabled(false);
			}
		});
		btn_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.clean_all();
				
				if (tp_batch_mode.getSelectedComponent() == tab_conf_file || 
						tp_batch_mode.getSelectedComponent() == tab_csv ) {
					
					if (tp_batch_mode.getSelectedComponent() == tab_conf_file) {
						controller.load_tasks(file_list, (int) sp_repetitions.getValue());
					} else if (tp_batch_mode.getSelectedComponent() == tab_csv) {
						EXPERIMENTAL_FILE = tf_experimental_file.getText();
						
						try {
							controller.load_tasks(EXPERIMENTAL_FILE);
							btn_start.setEnabled(true);
						} catch (FileNotFoundException e) {
							System.out.println("WARNING: Sample experimental file not found (" + EXPERIMENTAL_FILE + ")");
							e.printStackTrace();
						}
					}
					
					// Look for an non-existent directory to save the results
					File dir = new File(tf_results_dir.getText() + Controller.RESULTS_DIR);
					String relative_dir = Controller.RESULTS_DIR.substring(0, Controller.RESULTS_DIR.length() - 1);
					for( int i = 0; dir.exists(); i++) {
						dir = new File(tf_results_dir.getText() + relative_dir + i + "/");	
					}
					results_dir = dir.getAbsolutePath() + "\\";
					controller.set_RESULTS_DIR(results_dir);
					
			    } else if (tp_batch_mode.getSelectedComponent() == tab_catastrophic) {
			    	ArrayList<String> sim_list = new ArrayList<String>();  			    	
			    	
			    	File simulations_dir = new File (tf_scenarios_dir.getText() + 
			    			ControllerBatch.SIMULATIONS_DIR);
			    	if (simulations_dir.exists() && simulations_dir.isDirectory()){
						File[] directoryListing = simulations_dir.listFiles();
						if (directoryListing != null) {
							for (File child : directoryListing) {
								sim_list.add(child.getAbsolutePath());
							}
						} else {
							TA_OUTPUT.print(-1, "The " + ControllerBatch.SIMULATIONS_DIR + " directory didn't contain " +
										"any simulations. Please make sure you are providing a directory with " +
										"the results of a Batch process (either CSV or Configuration Files).");
							return;
						}
					} else {
						TA_OUTPUT.print(-1, "The " + ControllerBatch.SIMULATIONS_DIR + " directory was not found on " +
									"the provided directory. Please make sure you are providing a directory with " +
									"the results of a Batch process (either CSV or Configuration Files).");
						return;
					}
			    	controller.load_tasks_with_events(sim_list, events, 1);
			    	
			    	// Look for an non-existent directory inside the scenarios to save the results
			    	File dir = new File(tf_scenarios_dir.getText() + Controller.RESULTS_DIR);
			    	String relative_dir = Controller.RESULTS_DIR.substring(0, Controller.RESULTS_DIR.length() - 1);			    	
					for( int i = 0; dir.exists(); i++) {
						dir = new File(tf_scenarios_dir.getText() + relative_dir + i + "/");	
					}
					results_dir = dir.getAbsolutePath() + "/";
					controller.set_RESULTS_DIR(results_dir);
			    }
			
				// Create the  subdirectories for the results inside the corresponding results directory
				(new File(results_dir + Controller.ITERATIONS_DIR)).mkdirs();
				(new File(results_dir + Controller.SIMULATIONS_DIR)).mkdirs();
				
				btn_start.setEnabled(false);
				btn_pause.setEnabled(true);
				btn_resume.setEnabled(false);
				btn_stop.setEnabled(true);
				btn_open_experiment.setEnabled(false);
				btn_open_file.setEnabled(false);
				
				controller.start();
				
			}
		});

		DefaultCaret caret = (DefaultCaret)TA_OUTPUT.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}
	
	private void update_event_set(){
		int i = 1;
		ta_set_events.setText("");
		for (Iterator<Event> iterator = events.iterator(); iterator.hasNext();) {
			Event event = (Event) iterator.next();
			ta_set_events.append(i + ". " + event + "\n");
			i++;			
		}
	}
}
