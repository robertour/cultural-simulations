package simulator.control;

/**
 * Destroy institution content event
 * 
 * @author tico
 *
 */
public class DestroyPartialInstitutionsContent extends Event {
	private static final long serialVersionUID = 6865761072879604679L;

	public DestroyPartialInstitutionsContent(Distribution d) {
		super(d);
	}

	@Override
	public void trigger(int r, int c, double p, Simulation simulation) {
		simulation.remove_partial_institution_content(r * simulation.COLS + c, p);
	}
	
	public String toString() {
		return "Full Content: " + super.toString();
	}


}