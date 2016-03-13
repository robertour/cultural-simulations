package simulator;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import simulator.control.events.Distribution;

import javax.swing.JRadioButton;
import javax.swing.UIManager;
import java.awt.Color;

import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

public class DistributionPanel extends JPanel {
	private static final long serialVersionUID = -1571052661129757863L;
	private JRadioButton rdbtnUniformDistribution;
	private JRadioButton rdbtnAproxNormalDistribution;
	private JRadioButton rdbtnNewmannonProbabilistic;
	private JRadioButton rdbtnNone;
	private JSpinner sp_prob;
	private JSpinner sp_row;
	private JSpinner sp_col;
	private JSpinner sp_sd;
	private JLabel label;
	private JSpinner sp_row_newman;
	private JLabel label_1;
	private JSpinner sp_col_newman;
	private JLabel lblWithRadious;
	private JSpinner sp_radious;
	private JRadioButton rdbtnRectangular;
	private JSpinner sp_row1_rect;
	private JSpinner sp_col1_rect;
	private JSpinner sp_row2_rect;
	private JSpinner sp_col2_rect;

	/**
	 * Create the panel.
	 */
	public DistributionPanel(Distribution d, String title) {
		setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), title, TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		setLayout(null);
		
		rdbtnUniformDistribution = new JRadioButton("Uniform Distribution:");
		rdbtnUniformDistribution.setToolTipText("The event occur with Probability p to each agent or feature");
		rdbtnUniformDistribution.setBounds(6, 44, 157, 23);
		add(rdbtnUniformDistribution);
		
		rdbtnAproxNormalDistribution = new JRadioButton("Aprox. Normal Distribution:");
		rdbtnAproxNormalDistribution.setToolTipText("The event occurs with a normal probability distribution to each agent or feature. The normal distribution is center in a particular Row and Column with a given Standard Deviation. Use -1 in Row and/or Columns to indicate that the center of the normal curve can be picked randomly. This distribution is approximate because a grid is discrete, its range is finite, and currently the approximation follow a Newmann's Distance.");
		rdbtnAproxNormalDistribution.setBounds(6, 103, 157, 23);
		add(rdbtnAproxNormalDistribution);

		rdbtnNewmannonProbabilistic = new JRadioButton("Newmann (non probabilistic):");
		rdbtnNewmannonProbabilistic.setToolTipText("The event always occur to the agents in the neighborhood of the specified row and column. Use -1 to specify random selection of rows and columns.");
		rdbtnNewmannonProbabilistic.setBounds(6, 214, 169, 23);
		add(rdbtnNewmannonProbabilistic);
		
		rdbtnNone = new JRadioButton("None");
		rdbtnNone.setToolTipText("This event does not occur");
		rdbtnNone.setBounds(6, 18, 157, 23);
		add(rdbtnNone);
		
		rdbtnRectangular = new JRadioButton("Rectangular (non probabilistic):");
		rdbtnRectangular.setToolTipText("The event always occur to the agents in the neighborhood of the specified row and column. Use -1 to specify random selection of rows and columns.");
		rdbtnRectangular.setBounds(6, 326, 169, 23);
		add(rdbtnRectangular);
	
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnUniformDistribution);
		group.add(rdbtnAproxNormalDistribution);
		group.add(rdbtnNewmannonProbabilistic);
		group.add(rdbtnRectangular);
		group.add(rdbtnNone);
		
		if (d == null) {
			rdbtnNone.setSelected(true);
			d = new Distribution(0.1);
		} else if (d.getType() == Distribution.UNIFORM)
			rdbtnUniformDistribution.setSelected(true);
		else if (d.getType() == Distribution.NORMAL)
			rdbtnAproxNormalDistribution.setSelected(true);
		else if (d.getType() == Distribution.NEWMANN)
			rdbtnNewmannonProbabilistic.setSelected(true);
		else if (d.getType() == Distribution.RECTANGULAR)
			rdbtnRectangular.setSelected(true);
		
		JLabel lblNewLabel = new JLabel("Probability:");
		lblNewLabel.setToolTipText("The probability of the event occuring to any agent or feature given.");
		lblNewLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNewLabel.setBounds(16, 74, 99, 14);
		add(lblNewLabel);
		
		sp_prob = new JSpinner();
		sp_prob.setToolTipText("The probability of the event occuring to any agent or feature given.");
		sp_prob.setModel(new SpinnerNumberModel(new Double(d.getProbability()), new Double(0.0), new Double(1.0), new Double(0.1)));
		sp_prob.setBounds(123, 69, 52, 20);
		add(sp_prob);
		
		JLabel lblCenterAtRow = new JLabel("Center at Row:");
		lblCenterAtRow.setToolTipText("Row where the aproximate normal distribution is centered, given as a ration of the existent rows");
		lblCenterAtRow.setHorizontalAlignment(SwingConstants.TRAILING);
		lblCenterAtRow.setBounds(16, 136, 99, 14);
		add(lblCenterAtRow);
		
		sp_row = new JSpinner();
		sp_row.setToolTipText("Row where the aproximate normal distribution is centered, given as a ration of the existent rows");
		sp_row.setModel(new SpinnerNumberModel(new Double(d.getRow_ratio()), new Double(-1.0), new Double(1.0), new Double(0.1)));
		sp_row.setBounds(123, 133, 52, 20);
		add(sp_row);
		
		JLabel lblAndColumn = new JLabel("and Column:");
		lblAndColumn.setToolTipText("Column where the aproximate normal distribution is centered, given as a ratio of the existent columns");
		lblAndColumn.setHorizontalAlignment(SwingConstants.TRAILING);
		lblAndColumn.setBounds(26, 161, 88, 14);
		add(lblAndColumn);
		
		sp_col = new JSpinner();
		sp_col.setToolTipText("Column where the aproximate normal distribution is centered, given as a ratio of the existent columns");
		sp_col.setModel(new SpinnerNumberModel(new Double(d.getCol_ratio()), new Double(-1.0), new Double(1.0), new Double(0.1)));
		sp_col.setBounds(123, 158, 52, 20);
		add(sp_col);
		
		JLabel lblWithSd = new JLabel("with SD:");
		lblWithSd.setToolTipText("Standard Deviation of the distribution, given as a ratio of the existent Rows or Columns");
		lblWithSd.setHorizontalAlignment(SwingConstants.TRAILING);
		lblWithSd.setBounds(16, 186, 98, 14);
		add(lblWithSd);
		
		sp_sd = new JSpinner();
		sp_sd.setToolTipText("Standard Deviation of the distribution, given as a ratio of the existent Rows or Columns");
		sp_sd.setModel(new SpinnerNumberModel(new Double(d.getSd()), new Double(0.0), new Double(1.0), new Double(0.1)));
		sp_sd.setBounds(123, 183, 52, 20);
		add(sp_sd);
		
		label = new JLabel("Center at Row:");
		label.setToolTipText("Row where the aproximate normal distribution is centered, given as a ration of the existent rows");
		label.setHorizontalAlignment(SwingConstants.TRAILING);
		label.setBounds(16, 247, 99, 14);
		add(label);
		
		sp_row_newman = new JSpinner();
		sp_row_newman.setToolTipText("Row where the aproximate normal distribution is centered, given as a ratio of the existent rows");
		sp_row_newman.setModel(new SpinnerNumberModel(new Double(d.getRow_ratio()), new Double(-1.0), new Double(1.0), new Double(0.1)));
		sp_row_newman.setBounds(123, 244, 52, 20);
		add(sp_row_newman);
		
		label_1 = new JLabel("and Column:");
		label_1.setToolTipText("Column where the aproximate normal distribution is centered, given as a ratio of the existent columns");
		label_1.setHorizontalAlignment(SwingConstants.TRAILING);
		label_1.setBounds(26, 272, 88, 14);
		add(label_1);
		
		sp_col_newman = new JSpinner();
		sp_col_newman.setToolTipText("Column where the aproximate normal distribution is centered, given as a ratio of the existent columns");
		sp_col_newman.setModel(new SpinnerNumberModel(new Double(d.getCol_ratio()), new Double(-1.0), new Double(1.0), new Double(0.1)));
		sp_col_newman.setBounds(123, 269, 52, 20);
		add(sp_col_newman);
		
		lblWithRadious = new JLabel("with Radious:");
		lblWithRadious.setToolTipText("The radio (of the Newman's neighborhood) of the attack");
		lblWithRadious.setHorizontalAlignment(SwingConstants.TRAILING);
		lblWithRadious.setBounds(16, 297, 98, 14);
		add(lblWithRadious);
		
		sp_radious = new JSpinner();
		sp_radious.setModel(new SpinnerNumberModel(new Integer(d.getRadious()), null, null, new Integer(2)));
		sp_radious.setToolTipText("The radio (of the Newman's neighborhood) of the attack");
		
		sp_radious.setBounds(123, 294, 52, 20);
		add(sp_radious);
		
		
		
		JLabel lblRow = new JLabel("Row 1:");
		lblRow.setToolTipText("Row where the aproximate normal distribution is centered, given as a ration of the existent rows");
		lblRow.setHorizontalAlignment(SwingConstants.TRAILING);
		lblRow.setBounds(16, 359, 99, 14);
		add(lblRow);
		
		sp_row1_rect = new JSpinner();
		sp_row1_rect.setModel(new SpinnerNumberModel(new Double(d.getRow_ratio()), new Double(-1.0), new Double(1.0), new Double(0.1)));
		sp_row1_rect.setToolTipText("First row of the rectangle, given as a ratio of the existent rows");
		sp_row1_rect.setBounds(123, 356, 52, 20);
		add(sp_row1_rect);
		
		JLabel lblColumn = new JLabel("Column 1:");
		lblColumn.setToolTipText("Column where the aproximate normal distribution is centered, given as a ratio of the existent columns");
		lblColumn.setHorizontalAlignment(SwingConstants.TRAILING);
		lblColumn.setBounds(26, 384, 88, 14);
		add(lblColumn);
		
		sp_col1_rect = new JSpinner();
		sp_col1_rect.setModel(new SpinnerNumberModel(new Double(d.getCol_ratio()), new Double(-1.0), new Double(1.0), new Double(0.1)));
		sp_col1_rect.setToolTipText("Column where the aproximate normal distribution is centered, given as a ratio of the existent columns");
		sp_col1_rect.setBounds(123, 381, 52, 20);
		add(sp_col1_rect);
		
		JLabel lblRow_1 = new JLabel("Row 2:");
		lblRow_1.setToolTipText("The radio (of the Newman's neighborhood) of the attack");
		lblRow_1.setHorizontalAlignment(SwingConstants.TRAILING);
		lblRow_1.setBounds(16, 409, 98, 14);
		add(lblRow_1);
		
		sp_row2_rect = new JSpinner();
		sp_row2_rect.setModel(new SpinnerNumberModel(new Double(d.getRow2_ratio()), new Double(-1.0), new Double(1.0), new Double(0.1)));
		sp_row2_rect.setToolTipText("The radio (of the Newman's neighborhood) of the attack");
		sp_row2_rect.setBounds(123, 406, 52, 20);
		add(sp_row2_rect);
		
		JLabel lblCol = new JLabel("Column 2:");
		lblCol.setToolTipText("The radio (of the Newman's neighborhood) of the attack");
		lblCol.setHorizontalAlignment(SwingConstants.TRAILING);
		lblCol.setBounds(17, 434, 98, 14);
		add(lblCol);
		
		sp_col2_rect = new JSpinner();
		sp_col2_rect.setModel(new SpinnerNumberModel(new Double(d.getCol2_ratio()), new Double(-1.0), new Double(1.0), new Double(0.1)));
		sp_col2_rect.setToolTipText("The radio (of the Newman's neighborhood) of the attack");
		sp_col2_rect.setBounds(123, 431, 52, 20);
		add(sp_col2_rect);
		
	}
	
	public Distribution get_distribution(){
		Distribution d = null;
		if (rdbtnUniformDistribution.isSelected()){
			d = new Distribution((double) sp_prob.getValue());
		}else if (rdbtnAproxNormalDistribution.isSelected()){
			d = new Distribution((double) sp_row.getValue(), (double) sp_col.getValue(), (double) sp_sd.getValue() );
		}else if (rdbtnNewmannonProbabilistic.isSelected()){
			d = new Distribution((double) sp_row_newman.getValue(), (double) sp_col_newman.getValue(), (int) sp_radious.getValue() );
		}else if (rdbtnRectangular.isSelected()){
			d = new Distribution((double) sp_row1_rect.getValue(), (double) sp_col1_rect.getValue(), (double) sp_row2_rect.getValue(), (double) sp_col2_rect.getValue());
		}
		return d;
	}
}

