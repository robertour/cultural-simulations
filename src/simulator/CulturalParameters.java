package simulator;

import java.awt.BorderLayout;

import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSpinner;
import javax.swing.JLabel;

import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTextField;


public class CulturalParameters extends JDialog {

	private static final long serialVersionUID = 1L;
	
	public static ControllerSingle controller = CulturalSimulator.controller;
	private final JPanel contentPanel = new JPanel();
	public static JComboBox<String> classSelector;
	public static ArrayList<Class> classes;
	public static ArrayList<String> prettyNames;
	public static JSpinner sp_influence;
	public static JSpinner sp_loyalty;
	public static JSpinner sp_democracy;
	public static JSpinner sp_propaganda;
	public static JSpinner sp_mutation;
	public static JSpinner sp_sel_error;
	public static JSpinner sp_iterations;
	public static JSpinner sp_checkpoints;
	public static JSpinner sp_buffer;
	public static JSpinner sp_features;
	public static JSpinner sp_radious;
	public static JSpinner sp_rows;
	public static JSpinner sp_cols;
	public static JSpinner sp_traits;
	private String [] packs = {"simulator.previous", "simulator.destruction"};
	
	private JFileChooser jfc_load = new JFileChooser("./configurations/");
	private JFileChooser jfc_results = new JFileChooser("./");

	private JComboBox<String> cb_presets;
	public JTextField tf_results_dir;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			CulturalParameters dialog = new CulturalParameters(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public CulturalParameters(JFrame owner) {
		super (owner);
		
		setTitle("Simulation Parameters");
		setBounds(100, 100, 687, 380);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setToolTipText("How often an agent confuses the selection of an agent that can be influnce by or not?");
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		jfc_results.setDialogTitle("Select a Results Folder");
		jfc_results.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc_results.setAcceptAllFileFilterUsed(false);
		

		{
			JPanel panel_1 = new JPanel();
			panel_1.setBorder(new TitledBorder(null, "Configuration", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panel_1.setBounds(10, 11, 655, 256);
			contentPanel.add(panel_1);
			panel_1.setLayout(null);
			{
				JPanel panel_1_1 = new JPanel();
				panel_1_1.setBounds(308, 56, 176, 78);
				panel_1.add(panel_1_1);
				panel_1_1.setLayout(null);
				panel_1_1.setBorder(new TitledBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Noise", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
				{
					JLabel lblMutation = new JLabel("Mutation:");
					lblMutation.setToolTipText("How often a random change in a feature occurs?");
					lblMutation.setBounds(10, 23, 74, 14);
					panel_1_1.add(lblMutation);
				}
				{
					JLabel lblSelectionError = new JLabel("Selection Error:");
					lblSelectionError.setToolTipText("How often do you want to save results, update graphs, check for Pause/Stop/Resume states?");
					lblSelectionError.setBounds(10, 48, 85, 14);
					panel_1_1.add(lblSelectionError);
				}
				{
					sp_mutation = new JSpinner();
					
					sp_mutation.setModel(new SpinnerNumberModel(new Float(0.00002), new Float(0), new Float(1), new Float(0.000001)));
					((JSpinner.NumberEditor) sp_mutation.getEditor()).getFormat().setMinimumFractionDigits(6);
					sp_mutation.setValue(0.000001f);
					
					sp_mutation.setToolTipText("How often a random change in a feature occurs?");
					sp_mutation.setBounds(94, 20, 70, 20);
					panel_1_1.add(sp_mutation);
				}
				{
					sp_sel_error = new JSpinner();
					sp_sel_error.setToolTipText("How often an agent confuses the selection of an agent that can be influnce by or not?");
					sp_sel_error.setModel(new SpinnerNumberModel(new Float(0.00002), new Float(0), new Float(1), new Float(0.000001)));
					((JSpinner.NumberEditor) sp_sel_error.getEditor()).getFormat().setMinimumFractionDigits(6);
					sp_sel_error.setValue(0.000001f);
					sp_sel_error.setBounds(94, 45, 70, 20);
					panel_1_1.add(sp_sel_error);
				}
			}
			
			JPanel panel = new JPanel();
			panel.setBounds(173, 56, 125, 149);
			panel_1.add(panel);
			panel.setBorder(new TitledBorder(null, "World", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panel.setLayout(null);
			{
				JLabel lblFeatures = new JLabel("Features:");
				lblFeatures.setToolTipText("Number of features of the culture");
				lblFeatures.setBounds(10, 98, 57, 14);
				panel.add(lblFeatures);
			}
			{
				JLabel lblTraits = new JLabel("Traits:");
				lblTraits.setToolTipText("Number of traits available per features ");
				lblTraits.setBounds(10, 123, 46, 14);
				panel.add(lblTraits);
			}
			{
				sp_features = new JSpinner();
				sp_features.setToolTipText("Number of features of the culture");
				sp_features.setModel(new SpinnerNumberModel(new Integer(6), null, null, new Integer(1)));
				sp_features.setBounds(64, 95, 53, 20);
				panel.add(sp_features);
			}
			{
				JLabel lblRows = new JLabel("Rows:");
				lblRows.setToolTipText("Number of rows of the World Grid");
				lblRows.setBounds(10, 23, 46, 14);
				panel.add(lblRows);
			}
			{
				JLabel lblColumns = new JLabel("Columns:");
				lblColumns.setToolTipText("Number of columns of the World Grid");
				lblColumns.setBounds(10, 48, 46, 14);
				panel.add(lblColumns);
			}
			{
				JLabel lblRadius = new JLabel("Radius:");
				lblRadius.setToolTipText("The radious of the neighbors that can reach each agent");
				lblRadius.setBounds(10, 73, 46, 14);
				panel.add(lblRadius);
			}
			{
				sp_radious = new JSpinner();
				sp_radious.setToolTipText("The radious of the neighbors that can reach each agent");
				sp_radious.setModel(new SpinnerNumberModel(new Integer(6), null, null, new Integer(1)));
				sp_radious.setBounds(64, 70, 53, 20);
				panel.add(sp_radious);
			}
			{
				sp_rows = new JSpinner();
				sp_rows.setToolTipText("Number of rows of the World Grid");
				sp_rows.setModel(new SpinnerNumberModel(new Integer(100), null, null, new Integer(1)));
				sp_rows.setBounds(64, 20, 53, 20);
				panel.add(sp_rows);
			}
			{
				sp_cols = new JSpinner();
				sp_cols.setToolTipText("Number of columns of the World Grid");
				sp_cols.setModel(new SpinnerNumberModel(new Integer(100), null, null, new Integer(1)));
				sp_cols.setBounds(64, 45, 53, 20);
				panel.add(sp_cols);
			}
			{
				sp_traits = new JSpinner();
				sp_traits.setToolTipText("Number of traits available per features ");
				sp_traits.setModel(new SpinnerNumberModel(new Integer(14), null, null, new Integer(1)));
				sp_traits.setBounds(64, 120, 53, 20);
				panel.add(sp_traits);
			}
			{
				JPanel panel_2 = new JPanel();
				panel_2.setBounds(10, 56, 150, 100);
				panel_1.add(panel_2);
				panel_2.setBorder(new TitledBorder(null, "Controls", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				panel_2.setLayout(null);
				{
					JLabel lblNewLabel = new JLabel("Iterations: ");
					lblNewLabel.setToolTipText("How many checkpoints would you like to register? The total number of generations will be multiplied by the checkpoints!");
					lblNewLabel.setBounds(10, 23, 64, 14);
					panel_2.add(lblNewLabel);
				}
				{
					JLabel lblCheckpoint = new JLabel("Checkpoints:");
					lblCheckpoint.setToolTipText("How often do you want to save results, update graphs, check for Pause/Stop/Resume states?");
					lblCheckpoint.setBounds(10, 48, 64, 14);
					panel_2.add(lblCheckpoint);
				}
				{
					JLabel lblBufferSize = new JLabel("Buffer Size:");
					lblBufferSize.setToolTipText("Size of the buffer to send results to hard drive? Small values constantly send to hard drive (slow), high values avoid sending to hard drive often but you could lost all the data if the simulation crashes (Risky)");
					lblBufferSize.setBounds(10, 73, 64, 14);
					panel_2.add(lblBufferSize);
				}
				
				sp_iterations = new JSpinner();
				sp_iterations.setToolTipText("checkpoints!");
				sp_iterations.setModel(new SpinnerNumberModel(new Integer(1000), null, null, new Integer(1)));
				sp_iterations.setBounds(84, 20, 56, 20);
				panel_2.add(sp_iterations);
				
				sp_checkpoints = new JSpinner();
				sp_checkpoints.setToolTipText("How often do you want to save results, update graphs, check for Pause/Stop/Resume states?");
				sp_checkpoints.setModel(new SpinnerNumberModel(new Integer(100), null, null, new Integer(1)));
				sp_checkpoints.setBounds(84, 45, 56, 20);
				panel_2.add(sp_checkpoints);
				
				sp_buffer = new JSpinner();
				sp_buffer.setToolTipText("Size of the buffer to send results to hard drive? Small values constantly send to hard drive (slow), high values avoid sending to hard drive often but you could lost all the data if the simulation crashes (Risky)");
				sp_buffer.setModel(new SpinnerNumberModel(new Integer(512), null, null, new Integer(1)));
				sp_buffer.setBounds(84, 70, 56, 20);
				panel_2.add(sp_buffer);
			}
			{
				JPanel panel_1_1 = new JPanel();
				panel_1_1.setBounds(494, 55, 150, 124);
				panel_1.add(panel_1_1);
				panel_1_1.setLayout(null);
				panel_1_1.setBorder(new TitledBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Controls4", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), "Institutions", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
				{
					JLabel lblInfluence = new JLabel("Influence:");
					lblInfluence.setToolTipText("Institutional influence over the agent");
					lblInfluence.setBounds(10, 23, 64, 14);
					panel_1_1.add(lblInfluence);
				}
				{
					JLabel lblLoyalty = new JLabel("Loyalty:");
					lblLoyalty.setToolTipText("Agent's loyalty towards the institution");
					lblLoyalty.setBounds(10, 48, 64, 14);
					panel_1_1.add(lblLoyalty);
				}
				{
					JLabel lblDemocracy = new JLabel("Democracy:");
					lblDemocracy.setToolTipText("How often a democratic process occurs?");
					lblDemocracy.setBounds(10, 73, 64, 14);
					panel_1_1.add(lblDemocracy);
				}
				{
					sp_influence = new JSpinner();
					sp_influence.setModel(new SpinnerNumberModel(new Float(0.85), new Float(0), new Float(1), new Float(0.05)));
					((JSpinner.NumberEditor) sp_influence.getEditor()).getFormat().setMinimumFractionDigits(3);
					sp_influence.setValue(0.85f);
					sp_influence.setToolTipText("Institutional influence over the agent");
					sp_influence.setBounds(84, 20, 54, 20);
					panel_1_1.add(sp_influence);
				}
				{
					sp_loyalty = new JSpinner();
					sp_loyalty.setModel(new SpinnerNumberModel(new Float(0.5), new Float(0), new Float(1), new Float(0.05)));
					((JSpinner.NumberEditor) sp_loyalty.getEditor()).getFormat().setMinimumFractionDigits(3);
					sp_loyalty.setValue(0.5f);
					sp_loyalty.setToolTipText("Agent's loyalty towards the institution");
					sp_loyalty.setBounds(84, 45, 54, 20);
					panel_1_1.add(sp_loyalty);
				}
				{
					sp_democracy = new JSpinner();
					sp_democracy.setModel(new SpinnerNumberModel(new Integer(10), null, null, new Integer(1)));
					sp_democracy.setToolTipText("How often a democratic process occurs?");
					sp_democracy.setBounds(84, 70, 54, 20);
					panel_1_1.add(sp_democracy);
				}
				{
					JLabel lblPropaganda = new JLabel("Propaganda:");
					lblPropaganda.setToolTipText("How often a propaganda process occurs?");
					lblPropaganda.setBounds(10, 98, 64, 14);
					panel_1_1.add(lblPropaganda);
				}
				{
					sp_propaganda = new JSpinner();
					sp_propaganda.setModel(new SpinnerNumberModel(new Integer(3), null, null, new Integer(1)));
					sp_propaganda.setToolTipText("How often a propaganda process occurs?");
					sp_propaganda.setBounds(84, 95, 54, 20);
					panel_1_1.add(sp_propaganda);
				}
			}
			{
				JLabel lblModel = new JLabel("Model:");
				lblModel.setBounds(17, 21, 46, 14);
				panel_1.add(lblModel);
			}
			
			classSelector = new JComboBox<String>();
			classSelector.setBounds(90, 18, 554, 20);
			panel_1.add(classSelector);
			

			try {
				classSelector.setModel(new DefaultComboBoxModel<String>(getClasses(packs)));
				{
					JLabel lblLoadExample = new JLabel("Load Preset:");
					lblLoadExample.setBounds(10, 221, 101, 23);
					panel_1.add(lblLoadExample);
				}
				{
					cb_presets = new JComboBox<String>();
					cb_presets.setBounds(101, 221, 320, 23);
					panel_1.add(cb_presets);
					cb_presets.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (CulturalSimulator.want_to_continue(CulturalParameters.this)){
								if (cb_presets.getSelectedIndex() != 0){
									controller.load_parameters("./presets/" + (String) cb_presets.getSelectedItem());
								}
							} 
						}
					});
					cb_presets.setModel(new DefaultComboBoxModel<String>(listFiles("./presets/")));
				}
				{
					JButton btnNewButton = new JButton("Save To File");
					btnNewButton.setBounds(431, 221, 101, 23);
					panel_1.add(btnNewButton);
					{
						JButton btnNewButton_1 = new JButton("Load from File");
						btnNewButton_1.setBounds(542, 221, 101, 23);
						panel_1.add(btnNewButton_1);
						{
							JLabel lblResultsDirectory = new JLabel("Results Directory:");
							lblResultsDirectory.setBounds(10, 278, 104, 20);
							contentPanel.add(lblResultsDirectory);
						}
						
						tf_results_dir = new JTextField();
						tf_results_dir.setEditable(false);
						tf_results_dir.setBounds(109, 278, 453, 20);
						contentPanel.add(tf_results_dir);
						tf_results_dir.setColumns(10);
						tf_results_dir.setText(jfc_results.getCurrentDirectory().getAbsolutePath());
						
						JButton btnBrowse = new JButton("Browse");
						btnBrowse.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								if (jfc_results.showOpenDialog(contentPanel) == JFileChooser.APPROVE_OPTION) {
									tf_results_dir.setText(jfc_results.getSelectedFile().getAbsolutePath());
								}
							}
						});
						btnBrowse.setBounds(572, 277, 89, 23);
						contentPanel.add(btnBrowse);
						btnNewButton_1.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								if (jfc_load.showOpenDialog(contentPanel) == JFileChooser.APPROVE_OPTION) {
									if (CulturalSimulator.want_to_continue(jfc_load)){

										String conf_file = jfc_load.getSelectedFile().getAbsolutePath();
										controller.load_parameters(conf_file);
									} 					
								
								}
							}
						});
					}
					btnNewButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if (jfc_load.showOpenDialog(contentPanel) == JFileChooser.APPROVE_OPTION) {
								if (CulturalSimulator.want_to_continue(jfc_load)){
									String conf_file = jfc_load.getSelectedFile().getAbsolutePath();
									controller.load_parameters_from_interface();
									controller.save_simulation(conf_file);
								}
							}
						}
					});
				}
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (CulturalSimulator.want_to_continue(CulturalParameters.this)){
							controller.load_parameters_from_interface();
							CulturalParameters.this.setVisible(false);
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						controller.restore_parameters_to_interface();
						CulturalParameters.this.setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}



	 /**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static String[] getClasses(String [] packageNames)
	        throws ClassNotFoundException, IOException {
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    classes = new ArrayList<Class>();
	    prettyNames = new ArrayList<String>();
	    
	    assert classLoader != null;
	    
	    for (String packageName : packageNames){
		    String path = packageName.replace('.', '/');
		    Enumeration<URL> resources = classLoader.getResources(path);
		    List<File> dirs = new ArrayList<File>();
		    while (resources.hasMoreElements()) {
		        URL resource = resources.nextElement();
		        dirs.add(new File(resource.getFile()));
		    }
		    
		    for (File directory : dirs) {
		    	findClasses(directory, packageName);
		    }
	    }
	    
	    return prettyNames.toArray(new String[prettyNames.size()]);
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static void findClasses(File directory, String packageName) throws ClassNotFoundException {
	    
	    
	    if (directory.exists()) {
	        File[] files = directory.listFiles();
		    for (File file : files) {
		        if (file.isDirectory()) {
		            assert !file.getName().contains(".");
		            findClasses(file, packageName + "." + file.getName());
		        } else if (file.getName().endsWith(".class")) {
		        	String name = file.getName().substring(0, file.getName().length() - 6);
		        	prettyNames.add(name + " (" + packageName + ")");
		            classes.add(Class.forName(packageName + '.' + name));
		        }
		    }
		}
	}
	
    /**

     * List all the files under a directory

     * @param directoryName to be listed

     */

    private static String[] listFiles(String directoryName){
    	
    	ArrayList<String> filenames = new ArrayList<String>();

        File directory = new File(directoryName);

        //get all the files from a directory

        File[] fList = directory.listFiles();
        
        filenames.add("None");

        for (File file : fList){

            if (file.isFile()){
            	filenames.add(file.getName());
            }

        }
        
	    return filenames.toArray(new String[filenames.size()]);

    }
}