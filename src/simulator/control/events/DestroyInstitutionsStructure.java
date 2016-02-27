package simulator.control.events;

import simulator.control.Simulation;

/**
 * Destroy institution structure event
 * 
 * @author tico
 *
 */
public class DestroyInstitutionsStructure extends Event {
	private static final long serialVersionUID = -9186276200796543594L;

	public DestroyInstitutionsStructure(Distribution d) {
		super(d);
	}
	
	@Override
	public void trigger(int r, int c, double p, Simulation simulation) {
		if (simulation.getRand().nextDouble() < p){
			simulation.forget_institution(r, c);
		}
	}
	
	public String toString() {
		return "Structure: " + super.toString();
	}


}